CREATE TABLE IF NOT EXISTS ai_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    question TEXT NOT NULL,
    answer TEXT,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO ai_conversation (user_id, question, answer, status) VALUES
('user001', 'Hello, explain cloud computing benefits', 'Cloud computing benefits include: 1) Elastic scaling; 2) Cost optimization; 3) High availability; 4) Flexible choices.', 1),
('user001', 'What is microservice architecture', 'Microservice architecture is a software design approach that divides applications into small, autonomous services.', 1),
('user002', 'How to choose cloud server configuration', 'Choose based on: 1) Business scenario; 2) User scale; 3) Storage needs; 4) Budget constraints.', 1);
