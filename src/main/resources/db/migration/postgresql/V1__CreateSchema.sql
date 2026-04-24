-- PostgreSQL 15 schema — used for production and Testcontainers integration tests

CREATE TABLE customers (
    id              UUID         NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    tier            VARCHAR(20)  NOT NULL DEFAULT 'STANDARD'
                        CHECK (tier IN ('STANDARD', 'PREMIUM', 'VIP')),
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE TABLE orders (
    id              UUID          NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID          NOT NULL REFERENCES customers(id),
    status          VARCHAR(30)   NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED','CANCELLED','REFUNDED')),
    total           NUMERIC(10,2) NOT NULL DEFAULT 0,
    correlation_id  UUID,
    is_deleted      BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE TABLE order_items (
    id              UUID          NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id      UUID          NOT NULL,
    product_sku     VARCHAR(100)  NOT NULL,
    product_name    VARCHAR(255)  NOT NULL,
    quantity        INT           NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(10,2) NOT NULL CHECK (unit_price >= 0),
    is_deleted      BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE TABLE inventory_items (
    id                  UUID          NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id          UUID          NOT NULL UNIQUE,
    product_sku         VARCHAR(100)  NOT NULL UNIQUE,
    product_name        VARCHAR(255)  NOT NULL,
    quantity_on_hand    INT           NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
    quantity_reserved   INT           NOT NULL DEFAULT 0 CHECK (quantity_reserved >= 0),
    reorder_threshold   INT           NOT NULL DEFAULT 10,
    unit_cost           NUMERIC(10,2) NOT NULL,
    warehouse_location  VARCHAR(100),
    is_deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100)
);

CREATE TABLE inventory_audit_log (
    id                  UUID         NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id   UUID         NOT NULL,
    product_sku         VARCHAR(100) NOT NULL,
    change_type         VARCHAR(20)  NOT NULL
                            CHECK (change_type IN ('RECEIVED','RESERVED','RELEASED','ADJUSTED','SHIPPED','RETURNED','DAMAGED')),
    quantity_before     INT          NOT NULL,
    quantity_after      INT          NOT NULL,
    delta               INT          NOT NULL,
    reference_id        UUID,
    reference_type      VARCHAR(50),
    notes               TEXT,
    recorded_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100)
);

CREATE TABLE payments (
    id                  UUID          NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID          NOT NULL REFERENCES orders(id),
    amount              NUMERIC(10,2) NOT NULL,
    status              VARCHAR(30)   NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING','PROCESSING','COMPLETED','FAILED','REFUNDED','PARTIALLY_REFUNDED')),
    payment_method      VARCHAR(50)   NOT NULL,
    gateway_reference   VARCHAR(100),
    failure_reason      TEXT,
    retry_count         INT           NOT NULL DEFAULT 0,
    correlation_id      UUID,
    is_deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100)
);

CREATE INDEX idx_orders_customer_id    ON orders(customer_id);
CREATE INDEX idx_orders_status         ON orders(status);
CREATE INDEX idx_order_items_order_id  ON order_items(order_id);
CREATE INDEX idx_payments_order_id     ON payments(order_id);
CREATE INDEX idx_payments_status       ON payments(status);
CREATE INDEX idx_inventory_product_sku ON inventory_items(product_sku);
CREATE INDEX idx_inventory_low_stock   ON inventory_items((quantity_on_hand - quantity_reserved))
    WHERE is_deleted = FALSE;
