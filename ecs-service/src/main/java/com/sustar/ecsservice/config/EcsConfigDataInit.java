package com.sustar.ecsservice.config;

import com.sustar.ecsservice.mapper.AppMapper;
import com.sustar.ecsservice.mapper.BandwidthModeMapper;
import com.sustar.ecsservice.mapper.BillingTypeMapper;
import com.sustar.ecsservice.mapper.ImageMapper;
import com.sustar.ecsservice.mapper.PackageMapper;
import com.sustar.ecsservice.po.AppPO;
import com.sustar.ecsservice.po.BandwidthModePO;
import com.sustar.ecsservice.po.BillingTypePO;
import com.sustar.ecsservice.po.ImagePO;
import com.sustar.ecsservice.po.PackagePO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EcsConfigDataInit {

    private final JdbcTemplate jdbcTemplate;
    private final ImageMapper imageMapper;
    private final AppMapper appMapper;
    private final PackageMapper packageMapper;
    private final BillingTypeMapper billingTypeMapper;
    private final BandwidthModeMapper bandwidthModeMapper;

    @PostConstruct
    public void init() {
        createImageTable();
        createAppTable();
        createPackageTable();
        createBillingTypeTable();
        createBandwidthModeTable();
        initImageData();
        initAppData();
        initPackageData();
        initBillingTypeData();
        initBandwidthModeData();
    }

    private void createImageTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ecs_image (
                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                image_name VARCHAR(100) NOT NULL COMMENT '镜像名',
                os_category VARCHAR(32) NOT NULL COMMENT '操作系统分类',
                os_version VARCHAR(50) COMMENT '操作系统版本',
                description VARCHAR(500) COMMENT '描述',
                is_expired TINYINT DEFAULT 0 COMMENT '是否过期',
                sort_order INT DEFAULT 0 COMMENT '排序号',
                is_default TINYINT DEFAULT 0 COMMENT '是否默认',
                is_free TINYINT DEFAULT 1 COMMENT '是否免费',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ECS镜像表'
            """;
        jdbcTemplate.execute(sql);
        log.info("ecs_image表创建完成");
    }

    private void createAppTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ecs_app (
                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                app_name VARCHAR(100) NOT NULL COMMENT '应用名称',
                app_code VARCHAR(50) NOT NULL COMMENT '应用编码',
                icon VARCHAR(200) COMMENT '图标标识',
                description VARCHAR(500) COMMENT '描述',
                install_time VARCHAR(50) COMMENT '预估安装时间',
                is_expired TINYINT DEFAULT 0 COMMENT '是否过期',
                sort_order INT DEFAULT 0 COMMENT '排序号',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预装应用表'
            """;
        jdbcTemplate.execute(sql);
        log.info("ecs_app表创建完成");
    }

    private void createPackageTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ecs_package (
                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                package_name VARCHAR(100) NOT NULL COMMENT '套餐名称',
                package_code VARCHAR(50) NOT NULL COMMENT '套餐编码',
                description VARCHAR(500) COMMENT '套餐描述',
                icon VARCHAR(200) COMMENT '图标标识',
                vcpus INT NOT NULL COMMENT 'vCPU核数',
                memory INT NOT NULL COMMENT '内存（GB）',
                system_disk VARCHAR(50) COMMENT '系统盘规格',
                bandwidth VARCHAR(50) COMMENT '带宽规格',
                price_month DECIMAL(10,2) COMMENT '月参考价格',
                is_recommended TINYINT DEFAULT 0 COMMENT '是否推荐',
                sort_order INT DEFAULT 0 COMMENT '排序号',
                is_expired TINYINT DEFAULT 0 COMMENT '是否过期',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ECS推荐套餐表'
            """;
        jdbcTemplate.execute(sql);
        log.info("ecs_package表创建完成");
    }

    private void createBillingTypeTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ecs_billing_type (
                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                billing_code VARCHAR(50) NOT NULL COMMENT '付费类型编码',
                billing_name VARCHAR(100) NOT NULL COMMENT '付费类型名称',
                description VARCHAR(500) COMMENT '描述',
                is_recommended TINYINT DEFAULT 0 COMMENT '是否推荐',
                is_expired TINYINT DEFAULT 0 COMMENT '是否过期',
                sort_order INT DEFAULT 0 COMMENT '排序号',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ECS付费类型表'
            """;
        jdbcTemplate.execute(sql);
        log.info("ecs_billing_type表创建完成");
    }

    private void createBandwidthModeTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ecs_bandwidth_mode (
                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                mode_code VARCHAR(50) NOT NULL COMMENT '带宽模式编码',
                mode_name VARCHAR(100) NOT NULL COMMENT '带宽模式名称',
                description VARCHAR(500) COMMENT '描述',
                is_default TINYINT DEFAULT 0 COMMENT '是否默认',
                is_expired TINYINT DEFAULT 0 COMMENT '是否过期',
                sort_order INT DEFAULT 0 COMMENT '排序号',
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ECS带宽模式表'
            """;
        jdbcTemplate.execute(sql);
        log.info("ecs_bandwidth_mode表创建完成");
    }

    private void initImageData() {
        List<ImagePO> images = imageMapper.selectList(null);
        if (images.isEmpty()) {
            ImagePO image1 = new ImagePO();
            image1.setImageName("Alibaba Cloud Linux");
            image1.setOsCategory("linux");
            image1.setOsVersion("3.2104 LTS 64位");
            image1.setDescription("阿里云自研Linux发行版，性能稳定，安全可靠");
            image1.setSortOrder(1);
            image1.setIsDefault(1);
            image1.setIsFree(1);
            imageMapper.insert(image1);

            ImagePO image2 = new ImagePO();
            image2.setImageName("Ubuntu");
            image2.setOsCategory("linux");
            image2.setOsVersion("22.04 64位");
            image2.setDescription("流行的Linux发行版，社区活跃");
            image2.setSortOrder(2);
            image2.setIsDefault(0);
            image2.setIsFree(1);
            imageMapper.insert(image2);

            ImagePO image3 = new ImagePO();
            image3.setImageName("CentOS");
            image3.setOsCategory("linux");
            image3.setOsVersion("7.9 64位");
            image3.setDescription("企业级Linux发行版，稳定性强");
            image3.setSortOrder(3);
            image3.setIsDefault(0);
            image3.setIsFree(1);
            imageMapper.insert(image3);

            ImagePO image4 = new ImagePO();
            image4.setImageName("Windows Server");
            image4.setOsCategory("windows");
            image4.setOsVersion("2022 数据中心版 64位中文版");
            image4.setDescription("适合运行Windows应用程序");
            image4.setSortOrder(4);
            image4.setIsDefault(0);
            image4.setIsFree(0);
            imageMapper.insert(image4);
            log.info("镜像数据初始化完成");
        } else {
            log.info("镜像表已有数据，跳过初始化");
        }
    }

    private void initAppData() {
        List<AppPO> apps = appMapper.selectList(null);
        if (apps.isEmpty()) {
            AppPO app1 = new AppPO();
            app1.setAppName("宝塔Linux面板");
            app1.setAppCode("bt-panel");
            app1.setIcon("bt");
            app1.setDescription("可视化服务器管理面板，一键部署网站");
            app1.setInstallTime("约5-15分钟");
            app1.setSortOrder(1);
            appMapper.insert(app1);

            AppPO app2 = new AppPO();
            app2.setAppName("WordPress");
            app2.setAppCode("wordpress");
            app2.setIcon("wp");
            app2.setDescription("开源博客系统，快速搭建个人网站");
            app2.setInstallTime("约5-15分钟");
            app2.setSortOrder(2);
            appMapper.insert(app2);

            AppPO app3 = new AppPO();
            app3.setAppName("Docker");
            app3.setAppCode("docker");
            app3.setIcon("docker");
            app3.setDescription("容器化平台，快速部署应用");
            app3.setInstallTime("约5-15分钟");
            app3.setSortOrder(3);
            appMapper.insert(app3);

            AppPO app4 = new AppPO();
            app4.setAppName("LNMP");
            app4.setAppCode("lnmp");
            app4.setIcon("lnmp");
            app4.setDescription("Linux + Nginx + MySQL + PHP环境");
            app4.setInstallTime("约5-15分钟");
            app4.setSortOrder(4);
            appMapper.insert(app4);
            log.info("预装应用数据初始化完成");
        } else {
            log.info("预装应用表已有数据，跳过初始化");
        }
    }

    private void initPackageData() {
        List<PackagePO> packages = packageMapper.selectList(null);
        if (packages.isEmpty()) {
            PackagePO pkg1 = new PackagePO();
            pkg1.setPackageName("基础版");
            pkg1.setPackageCode("basic");
            pkg1.setDescription("经济实惠，几十万开发者的共同选择，满足入门级应用需求");
            pkg1.setIcon("basic");
            pkg1.setVcpus(2);
            pkg1.setMemory(4);
            pkg1.setSystemDisk("ESSD Entry 40 GB");
            pkg1.setBandwidth("1 Mbps");
            pkg1.setPriceMonth(new BigDecimal("119.37"));
            pkg1.setIsRecommended(0);
            pkg1.setSortOrder(1);
            packageMapper.insert(pkg1);

            PackagePO pkg2 = new PackagePO();
            pkg2.setPackageName("标准版");
            pkg2.setPackageCode("standard");
            pkg2.setDescription("均衡性能，可轻松部署Web服务器、学习数据库、运行各类网站应用");
            pkg2.setIcon("standard");
            pkg2.setVcpus(4);
            pkg2.setMemory(8);
            pkg2.setSystemDisk("ESSD Entry 60 GB");
            pkg2.setBandwidth("1 Mbps");
            pkg2.setPriceMonth(new BigDecimal("218.84"));
            pkg2.setIsRecommended(1);
            pkg2.setSortOrder(2);
            packageMapper.insert(pkg2);

            PackagePO pkg3 = new PackagePO();
            pkg3.setPackageName("性能版");
            pkg3.setPackageCode("performance");
            pkg3.setDescription("性能卓越，满足软件编译、多任务处理或承载小型数据库等场景需求");
            pkg3.setIcon("performance");
            pkg3.setVcpus(4);
            pkg3.setMemory(16);
            pkg3.setSystemDisk("ESSD Entry 60 GB");
            pkg3.setBandwidth("1 Mbps");
            pkg3.setPriceMonth(new BigDecimal("239.74"));
            pkg3.setIsRecommended(0);
            pkg3.setSortOrder(3);
            packageMapper.insert(pkg3);
            log.info("推荐套餐数据初始化完成");
        } else {
            log.info("推荐套餐表已有数据，跳过初始化");
        }
    }

    private void initBillingTypeData() {
        List<BillingTypePO> billingTypes = billingTypeMapper.selectList(null);
        if (billingTypes.isEmpty()) {
            BillingTypePO prepaid = new BillingTypePO();
            prepaid.setBillingCode("prepaid");
            prepaid.setBillingName("包年包月");
            prepaid.setDescription("适合长期业务，预付费更划算");
            prepaid.setIsRecommended(1);
            prepaid.setSortOrder(1);
            billingTypeMapper.insert(prepaid);

            BillingTypePO postpaid = new BillingTypePO();
            postpaid.setBillingCode("postpaid");
            postpaid.setBillingName("按量付费");
            postpaid.setDescription("按实际使用付费，灵活调整");
            postpaid.setIsRecommended(0);
            postpaid.setSortOrder(2);
            billingTypeMapper.insert(postpaid);
            log.info("付费类型数据初始化完成");
        } else {
            log.info("付费类型表已有数据，跳过初始化");
        }
    }

    private void initBandwidthModeData() {
        List<BandwidthModePO> bandwidthModes = bandwidthModeMapper.selectList(null);
        if (bandwidthModes.isEmpty()) {
            BandwidthModePO fixed = new BandwidthModePO();
            fixed.setModeCode("fixed");
            fixed.setModeName("按固定带宽");
            fixed.setDescription("适用于流量较大、稳定的场景");
            fixed.setIsDefault(1);
            fixed.setSortOrder(1);
            bandwidthModeMapper.insert(fixed);

            BandwidthModePO traffic = new BandwidthModePO();
            traffic.setModeCode("traffic");
            traffic.setModeName("按使用流量");
            traffic.setDescription("适用于流量小、波动大的场景");
            traffic.setIsDefault(0);
            traffic.setSortOrder(2);
            bandwidthModeMapper.insert(traffic);
            log.info("带宽模式数据初始化完成");
        } else {
            log.info("带宽模式表已有数据，跳过初始化");
        }
    }
}