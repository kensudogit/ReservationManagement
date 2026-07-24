-- 債権管理システム 初期スキーマ（参考用）
-- 実運用のテーブル作成は Hibernate ddl-auto=update が担当します。
-- PostgreSQL 初回起動時に docker-entrypoint-initdb.d 経由で実行されます。

CREATE TABLE IF NOT EXISTS customers (
    id            BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(32)  NOT NULL UNIQUE,
    name          VARCHAR(200) NOT NULL,
    contact_name  VARCHAR(100),
    email         VARCHAR(200),
    phone         VARCHAR(50),
    credit_limit  NUMERIC(18,0) DEFAULT 0,
    status        VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS receivables (
    id            BIGSERIAL PRIMARY KEY,
    invoice_no    VARCHAR(40)  NOT NULL UNIQUE,
    customer_id   BIGINT       NOT NULL REFERENCES customers(id),
    invoice_date  DATE         NOT NULL,
    due_date      DATE         NOT NULL,
    amount        NUMERIC(18,2) NOT NULL,
    balance       NUMERIC(18,2) NOT NULL,
    currency      VARCHAR(3)   NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    description   VARCHAR(500),
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
    id             BIGSERIAL PRIMARY KEY,
    receivable_id  BIGINT       NOT NULL REFERENCES receivables(id),
    payment_date   DATE         NOT NULL,
    amount         NUMERIC(18,2) NOT NULL,
    method         VARCHAR(30)  NOT NULL,
    reference_no   VARCHAR(80),
    note           VARCHAR(500),
    created_at     TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_receivables_customer ON receivables(customer_id);
CREATE INDEX IF NOT EXISTS idx_receivables_status ON receivables(status);
CREATE INDEX IF NOT EXISTS idx_payments_receivable ON payments(receivable_id);
