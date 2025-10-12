```mermaid
erDiagram
  user {
    bigint id PK
    varchar login_id UNIQUE
    varchar email UNIQUE
    date birth_date
    varchar gender
  }

  point {
    bigint id PK
    bigint amount
    bigint ref_user_id
  }

  point_history {
    bigint id PK
    bigint ref_point_id
    bigint amount
    varchar type
  }

  brand {
    bigint id PK
    varchar name
    varchar description
  }

  product {
    bigint id PK
    bigint ref_brand_id
    varchar name
    decimal price
    varchar product_status
  }

  stock {
    bigint id PK
    bigint ref_product_id
    bigint quantity
    }

  product_like {
    bigint id PK
    bigint ref_product_id
    bigint ref_user_id
  }

  orders {
    varchar id PK
    bigint ref_user_id
    bigint ref_coupon_id
    varchar order_status
    varchar receiver_name
    varchar phone_number
    varchar base_address
    varchar detail_address
    varchar requirements
    decimal original_amount
  }

  order_line {
    bigint id PK
    bigint ref_order_id
    bigint ref_product_id
    bigint quantity
    decimal amount
  }

  payment {
    bigint id PK
    varchar transaction_key
    varchar ref_order_id
    decimal amount
    varchar payment_status
    varchar reason
  }

  coupon {
    bigint id PK
    varchar name
    varchar value
    varchar type
    decimal minimum_order_amount
    int expire_hours
    bigint remaining_quantity
    bigint issued_quantity
  }

  user_coupon {
    bigint id PK
    bigint ref_user_id
    bigint ref_coupon_id
    varchar value
    varchar type
    datetime used_at
    datetime expired_at
  }

  %% 관계 정의
  user ||--|{ point : ""
  point ||--o{ point_history : ""
  user ||--o{ product_like : ""
  brand ||--o{ product : ""
  product ||--|{ stock : ""
  product ||--o{ product_like : ""
  user ||--o{ orders : ""
  orders ||--|{ order_line : ""
  order_line }o--|| product : ""
  orders ||--|| payment : ""
  coupon ||--o{ user_coupon : ""
  user ||--o{ user_coupon : ""
  orders ||--o| coupon : ""
