-- 支付流水表
CREATE TABLE IF NOT EXISTS payment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(64) NOT NULL UNIQUE,
    order_no VARCHAR(64) NOT NULL,
    order_id BIGINT NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    pay_channel VARCHAR(32) NOT NULL,
    pay_method VARCHAR(32),
    status INT NOT NULL DEFAULT 0,
    subject VARCHAR(256),
    body VARCHAR(512),
    third_party_no VARCHAR(128),
    third_party_response TEXT,
    pay_time TIMESTAMP,
    expire_time TIMESTAMP NOT NULL,
    client_ip VARCHAR(64),
    return_url VARCHAR(512),
    notify_url VARCHAR(512),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建支付记录索引
CREATE INDEX IF NOT EXISTS idx_payment_order_no ON payment_record(order_no);
CREATE INDEX IF NOT EXISTS idx_payment_user_id ON payment_record(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payment_record(status);
CREATE INDEX IF NOT EXISTS idx_payment_third_party_no ON payment_record(third_party_no);

-- 退款记录表
CREATE TABLE IF NOT EXISTS refund_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    refund_no VARCHAR(64) NOT NULL UNIQUE,
    payment_no VARCHAR(64) NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    refund_amount DECIMAL(18,4) NOT NULL,
    reason VARCHAR(512),
    status INT NOT NULL DEFAULT 0,
    third_party_no VARCHAR(128),
    third_party_response TEXT,
    refund_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建退款记录索引
CREATE INDEX IF NOT EXISTS idx_refund_payment_no ON refund_record(payment_no);
CREATE INDEX IF NOT EXISTS idx_refund_order_no ON refund_record(order_no);
CREATE INDEX IF NOT EXISTS idx_refund_status ON refund_record(status);

-- 支付渠道配置表
CREATE TABLE IF NOT EXISTS pay_channel_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_code VARCHAR(32) NOT NULL UNIQUE,
    channel_name VARCHAR(64) NOT NULL,
    channel_type VARCHAR(32) NOT NULL,
    app_id VARCHAR(128),
    merchant_id VARCHAR(128),
    private_key TEXT,
    public_key TEXT,
    gateway_url VARCHAR(256),
    notify_url VARCHAR(256),
    return_url VARCHAR(256),
    status INT NOT NULL DEFAULT 1,
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初始化支付渠道配置数据
INSERT INTO pay_channel_config (channel_code, channel_name, channel_type, status, sort_order) VALUES
('ALIPAY', '支付宝', 'ONLINE', 1, 1),
('WECHAT', '微信支付', 'ONLINE', 1, 2),
('UNION', '银联支付', 'ONLINE', 1, 3),
('HUABEI', '蚂蚁花呗', 'ONLINE', 1, 4);