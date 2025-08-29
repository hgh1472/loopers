package com.loopers.infrastructure.platform;

import com.loopers.domain.platform.PlatformCommand;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class LoopersPlatformSender {

    public void send(PlatformCommand.Order order) {
        try {
            Random random = new Random();
            int ms = random.nextInt(300, 3000);
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    public void send(PlatformCommand.Payment payment) {
        try {
            Random random = new Random();
            int ms = random.nextInt(300, 3000);
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }
}
