
> - 시퀀스 다이어그램에서 Controller, Repository와 같은 세부 사항은 생략하였습니다. <br>
> - Facade에서 도메인 간 협력을 나타내었습니다.

# 상품 목록 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  participant PF as ProductFacade
  participant P as Product
  participant L as ProductLike
  participant U as User

  opt 페이지 사이즈 초과
  Note right of A: Controller 내 페이지 유효성 검사
  A->>A: 400 Bad Request
  end
  A->>PF: 상품 목록 조회 요청
  activate PF
  PF->>P: 조회 조건에 해당하는 상품 목록 요청
  activate P
  P-->>PF: 페이지 내 상품 목록 반환
  deactivate P
  PF->>L: 상품 별 좋아요 수 조회
  activate L
  L-->>PF: 상품 별 좋아요 수 반환
  deactivate L
  PF->>U: 사용자 정보 확인
  activate U
  U-->>PF: 로그인 정보 반환
  deactivate U
  opt 로그인한 사용자
    PF->>L: 상품 별 좋아요 여부 조회
    activate L
    L-->>PF: 상품 별 좋아요 여부 반환
  end
  deactivate L
  PF->>A: 상품 목록 반환
  deactivate PF
```

# 상품 상세

```mermaid
sequenceDiagram
  actor A as Actor
  participant PF as ProductFacade
  participant P as Product
  participant S as Stock
  participant L as ProductLike
  participant U as User
  
  A->>PF: 상품 상세 조회 요청
  activate PF
  PF->>P: 상품 정보 조회
  activate P
  P-->>PF: 상품 정보 반환
  deactivate P
  PF->>S: 상품 재고 조회
  activate S
  S-->>PF: 상품 재고 반환
  deactivate S
  PF->>L: 상품 좋아요 수 조회
  activate L
  L-->>PF: 상품 좋아요 수 반환
  deactivate L
  PF->>U: 사용자 정보 확인
  activate U
  U-->>PF: 로그인 정보 반환
  deactivate U
  opt 로그인한 사용자
    PF->>L: 상품 좋아요 여부 조회
    activate L
    L-->>PF: 상품 좋아요 여부 반환
  end
  deactivate L
  PF->>A: 상품 상세 정보 반환
  deactivate PF
```

# 브랜드 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant BF as BrandFacade
  Participant B as Brand
  
  A->>BF: 브랜드 상세 조회 요청
  activate BF
  BF->>B: 브랜드 정보 요청
  activate B
  opt 존재하지 않는 브랜드
    B-->>A: 404 Not Found
  end
  B -->> BF: 브랜드 정보 반환
  deactivate B
  BF -->> A: 브랜드 정보 응답 
  deactivate BF
  
```

# 상품 좋아요 등록/취소

## 좋아요 등록

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant LF as LikeFacade
  Participant U as User
  Participant L as ProductLike
  
    A->>LF: 좋아요 등록 요청
    activate LF
    LF->>U: 사용자 정보 확인
    activate U
    opt 미로그인 / 유효하지 않은 사용자
      U-->>A: 401 Unauthroized
    end
    U-->>LF: 사용자 정보 반환
    deactivate U
    LF->>L: 좋아요 생성 요청
    activate L
    opt 좋아요 이미 존재
      Note right of L: 좋아요가 이미 존재하면,<br> 아무 작업도 하지 않음 (멱등성 보장)
      L-->>LF: 기존 좋아요 반환
    end
    L-->>LF: 생성된 좋아요 반환
    deactivate L
    LF-->>A: 좋아요 등록 응답
    deactivate LF
  
```

## 좋아요 취소

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant LF as LikeFacade
  Participant U as User
  Participant L as ProductLike
    
    A->>LF:좋아요 취소 요청
    activate LF
    LF-->>U: 사용자 정보 확인
    activate U
    opt 미로그인 / 유효하지 않은 사용자
      U-->>A: 401 Unauthroized
    end
    U-->>LF: 사용자 정보 반환
    deactivate U
    LF->>L: 좋아요 삭제 요청
    activate L
    opt 좋아요 미존재
       Note right of L: 좋아요가 없으면,<br> 아무 작업도 하지 않음 (멱등성 보장)
    end
    L-->>LF: 좋아요 삭제 완료
    deactivate L
    LF-->>A: 좋아요 취소 완료
    deactivate LF
```

# 내가 좋아요한 상품 목록 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant PF as ProductFacade
  Participant U as User
  Participant L as ProductLike
  Participant P as Product

  opt 페이지 사이즈 초과
  Note right of A: Controller 내 페이지 유효성 검사
  A->>A: 400 Bad Request
  end
  A->>PF: 좋아요한 상품 목록 요청
  activate PF
  PF->>U: 사용자 정보 확인
  activate U
  opt 미로그인 / 유효하지 않은 사용자
    U-->>A: 401 Unauthorized
  end
    U-->>PF: 사용자 정보 반환
    deactivate U
    PF->>L: 페이지에 해당하는 좋아요 조회
    activate L
    L-->>PF: 페이지에 해당하는 좋아요 반환
    deactivate L
    PF->>P: 좋아요한 상품 정보 조회
    activate P
    P-->>PF: 좋아요한 상품 정보 반환
    deactivate P
    PF-->>A: 좋아요한 상품 목록 반환
    deactivate PF
```

# 주문 생성 및 결제 흐름

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant OF as OrderFacade
  Participant U as User
  Participant Pd as Product
  Participant O as Order
  Participant Pay as Payment
  Participant P as Point
  Participant S as Stock
  
  A->>OF: 주문 요청
  activate OF
  OF->>U: 사용자 정보 확인
  activate U
  
  opt 미로그인 / 유효하지 않은 사용자
    U-->>A: 401 Unauthorized
  end
  
  U-->>OF: 사용자 정보 반환
  deactivate U
  OF->>Pd: 주문 상품 조회 요청
  activate Pd
  opt 상품 미존재
    Pd-->>A: 404 Not Found 
  end
  opt 상품 구매 불가
    Pd-->>A: 409 Conflict
  end
  Pd-->>OF: 상품 정보 반환
  deactivate Pd     
  OF->>O: 주문 생성
  activate O
  O-->>OF: 주문 반환
  deactivate O
    
  OF->>Pay: 결제 요청
  activate Pay
  opt 중복 결제
    Pay-->>OF: 중복 결제 응답
    OF->>O: 주문 취소 처리
    activate O
    O-->>OF: 주문 취소 완료
    deactivate O
    OF-->>A: 409 Conflict
  end
  opt 결제 잔액 부족
    Pay-->>OF: 잔액 부족 응답
    OF->>O: 주문 취소 처리
    activate O
    O-->>OF: 주문 취소 완료
    deactivate O
    OF-->>A: 403 Forbidden
  end
  opt 결제 실패
    Pay-->>OF: 결제 실패 응답
    OF->>O: 주문 취소 처리
    activate O
    O-->>OF: 주문 취소 완료
    deactivate O
    OF-->>A: 409 Conflict
  end
  Pay-->>OF: 결제 성공
  deactivate Pay
  opt 포인트 사용
    OF->>P: 사용 포인트 차감
    activate P
    opt 포인트 부족
      P-->>OF: 차감 실패
      OF->>Pay: 결제 환불 요청
   activate Pay
   Pay-->>OF: 결제 환불 성공
   deactivate Pay
   OF->>O: 주문 취소 처리
   activate O
   O-->>OF: 주문 취소 완료
   deactivate O
   OF-->>A: 409 Conflict
  end
  P-->>OF: 포인트 차감 결과 반환
  deactivate P
  end
  OF->>S: 재고 차감 시도
  activate S
  opt 재고 차감 실패
    S-->>OF: 차감 실패 응답
    opt 주문 시 포인트 사용
    OF->>P: 사용 포인트 반환 요청
    activate P
    P-->>OF: 포인트 반환 성공
    deactivate P
    end
    OF->>Pay: 결제 환불 요청
    activate Pay
    Pay-->>OF: 결제 환불 성공
    deactivate Pay
    OF->>O: 주문 취소 처리
    activate O
    O-->>OF: 주문 취소 완료
    deactivate O
    OF->>A: 409 Conflict
  end
  S-->>OF: 재고 차감 결과 반환
  deactivate S
  OF-->>A: 주문 결과 반환
  deactivate OF
```

# 유저의 주문 목록 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant OF as OrderFacade
  Participant U as User
  Participant O as Order

  opt 페이지 사이즈 초과
  Note right of A: Controller 내 페이지 유효성 검사
  A->>A: 400 Bad Request
  end
  A->>OF: 주문 목록 조회 요청
  activate OF
  
  OF->>U: 사용자 정보 확인
  activate U
  opt 미로그인 / 유효하지 않은 사용자
    U-->>A: 401 Unauthroized
  end
  U-->>OF: 사용자 정보 반환
  deactivate U
  
  OF->>O: 주문 목록 조회
  activate O
  O-->>OF: 주문 목록 반환
  deactivate O
  OF-->>A: 주문 목록 반환
  deactivate OF
  
```

# 단일 주문 상세 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  participant OF as OrderFacade
  participant U as User
  participant O as Order
  participant Pay as Payment
  
  A->>OF: 주문 상세 조회 요청
  activate OF
  
  OF->>U: 사용자 정보 확인
  activate U
  alt 미로그인 / 유효하지 않은 사용자 
    U-->>A: 401 Unauthorized
  end
  U-->>OF: 사용자 정보 반환
  deactivate U
  
  OF->>O: 단일 주문 조회
  activate O
  opt 로그인 사용자의 주문 X
    O-->>A: 403 Forbidden
  end
  opt 존재하지 않는 주문
    O-->>A: 404 Not Found
  end
  O-->>OF: 단일 주문 정보 반환
  deactivate O
  
  OF->>Pay: 결제 정보 요청
  activate Pay
  Pay-->>OF: 결제 정보 반환
  deactivate Pay
  OF-->>A: 주문 상세 정보 반환
  deactivate OF
```