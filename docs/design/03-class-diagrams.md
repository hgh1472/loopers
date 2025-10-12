> 클래스 간 연관에 집중하기 위해, 모든 연관관계는 객체로 표현하였습니다.
> 

```mermaid
classDiagram
    %% 사용자 및 포인트 관련
    class Point {
        - User
        - Amount
        + use(amount) : PointHistory
        + charge(amount) : PointHistory
        + canAfford(amount) : boolean
    }
    class PointHistory {
        - Point
        - amount
        - Type
        - createdAt
        + chargeOf(pointId, amount) : PointHistory
        + useOf(pointId, amount) : PointHistory
    }
    class Amount {
        - value
        + charge(amount) : void
        + use(amount) : void
        + isGreaterThanOrEqual(amount) : boolean
    }
    class Type {
        CHARGED, USED
    }
    class User {
        - Id
        - LoginId
        - Email
        - Gender
    }

    %% 상품 관련
    class Brand {
        - name
        - description
    }
    class ProductStatus {
        ON_SALE, OUT_OF_STOCK, HOLD, DELETED
    }
    class Stock {
        - Product
        - quantity
        + hasStock(quantity)
        + deduct(quantity)
    }
    class Product {
        - brand
        - name
        - Price
        - Stock
        - ProductStatus
        - createdAt
        + isPurchasable() : boolean
        + isOutOfStock() : boolean
    }
    class ProductLike {
        - Product
        - User
        - createdAt
    }

    %% 주문 및 결제 관련
    class Orderer {
        - User
    }
    class OrderStatus {
        PENDING_PAYMENT, PAID, PRODUCT_PREPARING,
        DELIVERING, COMPLETED, CANCELED
    }
    class OrderLine {
        - Order
        - Product
        - quantity
        - amount
        - createdAt
        + calculateLineAmount() : BigDecimal
    }
    class OrderDelivery {
        - receiverName
        - phoneNumber
        - baseAddress
        - detailAddress
        - requirements
    }
    class PaymentStatus {
        PENDING, COMPLETED, INSUFFICIENT_FUNDS,
        DUPLICATED, TIMEOUT, FAILED, REFUNDED
    }
    class Payment {
        - transactionKey
        - Order
        - amount
        - PaymentStatus
        - reason
        + refund()
        + success()
        + fail(reason)
    }
    class OrderPayment {
        - originalAmount
        - discountAmount
        - pointAmount
        - paymentAmount
    }
    class Order {
        - List<OrderLine> orderLines
        - DeliveryInfo
        - OrderStatus
        - Orderer
        - PaymentInfo
        - Coupon
        + cancel() : void
        + calculateTotalAmount() : BigDecimal
    }
    
    class Coupon {
        - name
        - discountPolicy
        - minumumOrderAmount
        - expireHours
        - remainingQuantity
        - issuedQuantity
        + issue(User, LocalDateTime) : UserCoupon
    }
    
    class UserCoupon {
        - User
        - Coupon
        - discountPolicy
        + use()
        + restore()
    }

    %% 관계 정의
    Point -- User
    Point -- Amount
    PointHistory "N" -- Point
    PointHistory -- Type

    Product -- Brand
    Product -- Stock
    Product -- ProductStatus
    ProductLike "N" -- Product
    ProductLike "N" -- User

    Orderer -- User
    Order -- Orderer
    Order -- "N" OrderLine
    Order -- OrderStatus
    Order -- OrderDelivery
    Order -- OrderPayment
    OrderLine "N" -- Product

    OrderPayment -- Payment
    Payment -- PaymentStatus
    Payment -- Order
    
    Coupon -- "N" UserCoupon
    Order "N" -- Coupon
    UserCoupon "N" -- User
```