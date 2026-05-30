-- 订单主表
CREATE TABLE IF NOT EXISTS order_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id VARCHAR(64) NOT NULL,
    total_amount DECIMAL(18,4) NOT NULL,
    pay_amount DECIMAL(18,4) NOT NULL,
    coupon_id BIGINT,
    discount_amount DECIMAL(18,4) DEFAULT 0,
    status INT NOT NULL DEFAULT 0,
    pay_status INT NOT NULL DEFAULT 0,
    pay_type VARCHAR(32),
    pay_time TIMESTAMP,
    logistics_no VARCHAR(64),
    receiver_name VARCHAR(128),
    receiver_phone VARCHAR(32),
    receiver_address VARCHAR(512),
    remark VARCHAR(512),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    item_type INT DEFAULT 1,
    item_id VARCHAR(64) NOT NULL,
    item_name VARCHAR(256) NOT NULL,
    package_id VARCHAR(64),
    image_id VARCHAR(64),
    region_code VARCHAR(32),
    billing_type INT DEFAULT 1,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(18,4) NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    remark VARCHAR(512),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 订单状态流水表
CREATE TABLE IF NOT EXISTS order_status_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    before_status INT,
    after_status INT NOT NULL,
    before_pay_status INT,
    after_pay_status INT,
    change_desc VARCHAR(256),
    operation_type VARCHAR(32),
    operator_id VARCHAR(64),
    operator_name VARCHAR(128),
    remark VARCHAR(512),
    external_no VARCHAR(64),
    client_ip VARCHAR(64),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_ms BIGINT
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_order_info_user_id ON order_info(user_id);
CREATE INDEX IF NOT EXISTS idx_order_info_status ON order_info(status);
CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_order_status_flow_order_id ON order_status_flow(order_id);
CREATE INDEX IF NOT EXISTS idx_order_status_flow_order_no ON order_status_flow(order_no);