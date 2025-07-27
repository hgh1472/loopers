```mermaid
erDiagram
  user {
    bigint id PK
    varchar email
    varchar login_id
    varchar gender
  }

  brand {
    bigint id PK
    varchar name
    varchar description
  }

  category {
    bigint id PK
    varchar name
  }

  product {
    bigint id PK
    bigint ref_brand_id
    varchar name
    int price
    varchar product_status
    datetime created_at
  }

  product_category {
    bigint id PK
    bigint ref_product_id
    bigint ref_category_id
  }

  product_like {
    bigint id PK
    bigint ref_user_id
    bigint ref_product_id
    datetime created_at
  }

  stock {
    bigint id PK
    bigint ref_product_id
    int quantity
  }

  order {
    bigint id PK
    bigint ref_user_id FK
    varchar order_status
    varchar receiver_name
    varchar receiver_phone_number
    varchar base_address
    varchar detail_address
    varchar delivery_requirement
    decimal original_amount
    decimal used_point_amount
    decimal discount_amount
    decimal payment_amountdd
  }

  order_line {
    bigint id PK
    bigint ref_order_id
    bigint ref_product_id
    int quantity
    decimal amount
    datetime created_at
  }

  payment {
    bigint id PK
    varchar payment_key
    bigint ref_order_id
    decimal total_amount
    varchar payment_status
    localtime requested_at
    localtime approved_at
  }

  point {
    bigint id PK
    bigint ref_user_id
    int amount
  }

  point_history {
    bigint id PK
    bigint ref_point_id
    int amount
    varchar point_type
    datetime created_at
  }

  %% 관계 정의
  user ||--o{ product_like : ""
  user ||--o{ order : ""
  user ||--|| point : ""
  brand ||--o{ product : ""
  category ||--o{ product_category : ""
  product ||--o{ product_category : ""
  product ||--o{ product_like : ""
  product ||--o{ order_line : ""
  product ||--o{ stock : ""
  order ||--o{ order_line : ""
  order ||--|| payment : ""
  point ||--o{ point_history : ""