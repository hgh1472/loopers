import http from "k6/http";
import {check, group, sleep} from "k6";

const numUsers = 100; // the number of vusers
const serverName = "loopers"
const testId = new Date().getTime();
export const options = {
    scenarios: {
        open_model: {
            // option details : <https://k6.io/docs/using-k6/scenarios/executors/ramping-arrival-rate/#options>
            executor: 'ramping-arrival-rate',
            startRate: 100,
            timeUnit: '20s', // 요청이 만들어지는 기본 단위
            preAllocatedVUs: 10,
            maxVUs: numUsers,
            stages: [
                // 초기 부하: 첫 20초 동안 진행되며, 매 20초 동안 요청이 60개 만들어짐 → 3 rps
                {target: 60, duration: '20s'},

                // 점진적 증가: 1분 동안 20초당 요청 수를 60 → 200으로 늘림 → 10 rps
                {target: 200, duration: '1m'},

                // 최대 부하 유지: 1분 동안 20초당 요청 200개 유지 → 10 rps
                {target: 200, duration: '1m'},

                // 점진적 감소: 마지막 20초 동안 20초당 요청 수를 200 → 60으로 줄임 → 3 rps
                {target: 60, duration: '20s'},
            ]
        },
    },
    tags: {
        testid: `${serverName}-${testId}`
    },
    thresholds: {
        http_req_duration: ['p(99)<1000'], // 99% of requests must complete below 1s
    },
};

const BASE_URL = "http://localhost:8080";
// Sleep duration between successive requests.
// You might want to edit the value of this variable or remove calls to the sleep function on the script.
const SLEEP_DURATION = 0.1;
// Global variables should be initialized.

let userId = "10";

export default function () {
    group("/api/v1/products", () => {
        let url = BASE_URL + `/api/v1/products`;
        let params = {
            headers: {"X-USER-ID": userId},
            params: {
                query: {
                    page: 10,
                    size: 10
                }
            }
        };

        // Request No. 1
        let request = http.get(url, params);
        check(request, {
            "OK": (r) => r.status === 200
        });
        sleep(SLEEP_DURATION);
    });
}
