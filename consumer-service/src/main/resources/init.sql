CREATE DATABASE IF NOT EXISTS cloud_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cloud_platform;

CREATE TABLE IF NOT EXISTS openstack_vm_metrics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    instance_id VARCHAR(100) NOT NULL COMMENT '云实例ID',
    instance_name VARCHAR(200) COMMENT '云实例名称',
    status VARCHAR(50) COMMENT '实例状态',
    vm_state VARCHAR(50) COMMENT '虚拟机状态',
    task_state VARCHAR(50) COMMENT '任务状态',
    flavor_id VARCHAR(100) COMMENT '规格ID',
    flavor_name VARCHAR(200) COMMENT '规格名称',
    image_id VARCHAR(100) COMMENT '镜像ID',
    image_name VARCHAR(200) COMMENT '镜像名称',
    host VARCHAR(200) COMMENT '主机名称',
    hypervisor_hostname VARCHAR(200) COMMENT 'hypervisor主机名',
    created_at DATETIME COMMENT '创建时间',
    launched_at DATETIME COMMENT '启动时间',
    updated_at DATETIME COMMENT '更新时间',
    tenant_id VARCHAR(100) COMMENT '租户ID',
    user_id VARCHAR(100) COMMENT '用户ID',
    addresses TEXT COMMENT '网络地址JSON',
    security_groups TEXT COMMENT '安全组JSON',
    vcpus INT COMMENT 'vCPU数量',
    memory_mb INT COMMENT '内存MB',
    local_gb INT COMMENT '本地磁盘GB',
    memory_resident_mb INT COMMENT '内存占用MB',
    vcpus_usage INT COMMENT 'vCPU使用数',
    is_expired TINYINT DEFAULT 0 COMMENT '是否删除',
    collected_at DATETIME NOT NULL COMMENT '采集时间',
    INDEX idx_instance_id (instance_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_status (status),
    INDEX idx_collected_at (collected_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OpenStack云实例监控指标表';

CREATE TABLE IF NOT EXISTS openstack_security_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    sg_id VARCHAR(100) NOT NULL COMMENT '安全组ID',
    sg_name VARCHAR(200) COMMENT '安全组名称',
    description VARCHAR(500) COMMENT '安全组描述',
    tenant_id VARCHAR(100) COMMENT '租户ID',
    project_id VARCHAR(100) COMMENT '项目ID',
    security_group_rules TEXT COMMENT '安全组规则JSON',
    created_at DATETIME COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间',
    is_expired TINYINT DEFAULT 0 COMMENT '是否删除',
    collected_at DATETIME NOT NULL COMMENT '采集时间',
    INDEX idx_sg_id (sg_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_collected_at (collected_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OpenStack安全组表';