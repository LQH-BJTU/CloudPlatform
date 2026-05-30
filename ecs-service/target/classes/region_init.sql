-- 创建公共地域表
CREATE TABLE IF NOT EXISTS sys_common_region (
    id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    region_name VARCHAR(50) NOT NULL COMMENT '大区名（亚太-中国）',
    area_code VARCHAR(32) NOT NULL COMMENT '地域编码（cn-beijing）',
    area_name VARCHAR(100) NOT NULL COMMENT '展示名（华北2（北京））',
    status TINYINT DEFAULT 1 COMMENT '1正常 2停用',
    sort_num INT DEFAULT 0 COMMENT '排序',
    is_deleted TINYINT DEFAULT 0 COMMENT '0未删除 1已删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_query (is_deleted, status, sort_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公共地域表，所有用户共用';

-- 插入完整地域数据
INSERT INTO sys_common_region (region_name, area_code, area_name, sort_num) VALUES
-- 亚太-中国
('亚太-中国', 'cn-qingdao', '华北1（青岛）', 1),
('亚太-中国', 'cn-beijing', '华北2（北京）', 2),
('亚太-中国', 'cn-zhangjiakou', '华北3（张家口）', 3),
('亚太-中国', 'cn-huhehaote', '华北5（呼和浩特）', 4),
('亚太-中国', 'cn-wulanchabu', '华北6（乌兰察布）', 5),
('亚太-中国', 'cn-hangzhou', '华东1（杭州）', 6),
('亚太-中国', 'cn-shanghai', '华东2（上海）', 7),
('亚太-中国', 'cn-nanjing', '华东5（南京-本地地域）', 8),
('亚太-中国', 'cn-fuzhou', '华东6（福州-本地地域）', 9),
('亚太-中国', 'cn-shenzhen', '华南1（深圳）', 10),
('亚太-中国', 'cn-heyuan', '华南2（河源）', 11),
('亚太-中国', 'cn-guangzhou', '华南3（广州）', 12),
('亚太-中国', 'cn-chengdu', '西南1（成都）', 13),
('亚太-中国', 'cn-hongkong', '中国香港', 14),
('亚太-中国', 'cn-wuhan', '华中1（武汉-本地地域）', 15),
('亚太-中国', 'cn-zhongwei', '西北2（中卫）', 16),

-- 亚太-其他
('亚太-其他', 'jp-tokyo', '日本（东京）', 1),
('亚太-其他', 'kr-seoul', '韩国（首尔）', 2),
('亚太-其他', 'sg-singapore', '新加坡', 3),
('亚太-其他', 'my-johor', '马来西亚（柔佛州）', 4),
('亚太-其他', 'my-kualalumpur', '马来西亚（吉隆坡）', 5),
('亚太-其他', 'ph-manila', '菲律宾（马尼拉）', 6),
('亚太-其他', 'id-jakarta', '印度尼西亚（雅加达）', 7),
('亚太-其他', 'th-bangkok', '泰国（曼谷）', 8),

-- 欧洲与美洲
('欧洲与美洲', 'us-virginia', '美国（弗吉尼亚）', 1),
('欧洲与美洲', 'us-siliconvalley', '美国（硅谷）', 2),
('欧洲与美洲', 'mx-mexico', '墨西哥', 3),
('欧洲与美洲', 'uk-london', '英国（伦敦）', 4),
('欧洲与美洲', 'fr-paris', '法国（巴黎）', 5),
('欧洲与美洲', 'de-frankfurt', '德国（法兰克福）', 6),

-- 中东
('中东', 'ae-dubai', '阿联酋（迪拜）', 1);
