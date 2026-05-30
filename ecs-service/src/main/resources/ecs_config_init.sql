CREATE TABLE IF NOT EXISTS ecs_image (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    image_name      VARCHAR(100) NOT NULL COMMENT '镜像名',
    os_category     VARCHAR(32) NOT NULL COMMENT '操作系统分类（windows/linux/other）',
    os_version      VARCHAR(50) COMMENT '操作系统版本',
    description     VARCHAR(500) COMMENT '描述',
    is_expired      TINYINT DEFAULT 0 COMMENT '是否过期：0-未过期 1-已过期',
    sort_order      INT DEFAULT 0 COMMENT '排序号',
    is_default      TINYINT DEFAULT 0 COMMENT '是否默认：0-否 1-是',
    is_free         TINYINT DEFAULT 1 COMMENT '是否免费：0-收费 1-免费',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ECS镜像表';

INSERT INTO ecs_image (image_name, os_category, os_version, description, sort_order, is_default, is_free) VALUES
('Alibaba Cloud Linux', 'linux', '3.2104 LTS 64位', '阿里云自研Linux发行版，性能稳定，安全可靠', 1, 1, 1),
('Ubuntu', 'linux', '22.04 64位', '流行的Linux发行版，社区活跃', 2, 0, 1),
('CentOS', 'linux', '7.9 64位', '企业级Linux发行版，稳定性强', 3, 0, 1),
('Windows Server', 'windows', '2022 数据中心版 64位中文版', '适合运行Windows应用程序', 4, 0, 0);

CREATE TABLE IF NOT EXISTS ecs_app (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    app_name        VARCHAR(100) NOT NULL COMMENT '应用名称',
    app_code        VARCHAR(50) NOT NULL COMMENT '应用编码',
    icon            VARCHAR(200) COMMENT '图标标识',
    description     VARCHAR(500) COMMENT '描述',
    install_time    VARCHAR(50) COMMENT '预估安装时间',
    is_expired      TINYINT DEFAULT 0 COMMENT '是否过期',
    sort_order      INT DEFAULT 0 COMMENT '排序号',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预装应用表';

INSERT INTO ecs_app (app_name, app_code, icon, description, install_time, sort_order) VALUES
('宝塔Linux面板', 'bt-panel', 'bt', '可视化服务器管理面板，一键部署网站', '约5-15分钟', 1),
('WordPress', 'wordpress', 'wp', '开源博客系统，快速搭建个人网站', '约5-15分钟', 2),
('Docker', 'docker', 'docker', '容器化平台，快速部署应用', '约5-15分钟', 3),
('LNMP', 'lnmp', 'lnmp', 'Linux + Nginx + MySQL + PHP环境', '约5-15分钟', 4);

CREATE TABLE IF NOT EXISTS ecs_package (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    package_name    VARCHAR(100) NOT NULL COMMENT '套餐名称',
    package_code    VARCHAR(50) NOT NULL COMMENT '套餐编码',
    description     VARCHAR(500) COMMENT '套餐描述',
    icon            VARCHAR(200) COMMENT '图标标识',
    vcpus           INT NOT NULL COMMENT 'vCPU核数',
    memory          INT NOT NULL COMMENT '内存（GB）',
    system_disk     VARCHAR(50) COMMENT '系统盘规格',
    bandwidth       VARCHAR(50) COMMENT '带宽规格',
    price_month     DECIMAL(10,2) COMMENT '月参考价格（元）',
    is_recommended  TINYINT DEFAULT 0 COMMENT '是否推荐',
    sort_order      INT DEFAULT 0 COMMENT '排序号',
    is_expired      TINYINT DEFAULT 0 COMMENT '是否过期',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ECS推荐套餐表';

INSERT INTO ecs_package (package_name, package_code, description, icon, vcpus, memory, system_disk, bandwidth, price_month, is_recommended, sort_order) VALUES
('基础版', 'basic', '经济实惠，几十万开发者的共同选择，满足入门级应用需求', 'basic', 2, 4, 'ESSD Entry 40 GB', '1 Mbps', 119.37, 0, 1),
('标准版', 'standard', '均衡性能，可轻松部署Web服务器、学习数据库、运行各类网站应用', 'standard', 4, 8, 'ESSD Entry 60 GB', '1 Mbps', 218.84, 1, 2),
('性能版', 'performance', '性能卓越，满足软件编译、多任务处理或承载小型数据库等场景需求', 'performance', 4, 16, 'ESSD Entry 60 GB', '1 Mbps', 239.74, 0, 3);