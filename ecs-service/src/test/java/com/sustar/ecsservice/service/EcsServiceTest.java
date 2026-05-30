package com.sustar.ecsservice.service;

import com.sustar.ecsservice.EcsServiceApplication;
import com.sustar.ecsservice.dto.RegionAreaDTO;
import com.sustar.ecsservice.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ECS服务单元测试
 */
@SpringBootTest(classes = EcsServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EcsServiceTest {

    @Autowired
    private RegionCacheService regionCacheService;

    @Autowired
    private EcsConfigService ecsConfigService;

    @Test
    @DisplayName("地域服务 - 获取地域列表")
    void getRegionAreaList_Success() {
        List<RegionAreaDTO> regionAreaList = regionCacheService.getRegionAreaList();

        assertNotNull(regionAreaList);
        assertTrue(regionAreaList.size() > 0);

        // 验证数据结构
        for (RegionAreaDTO region : regionAreaList) {
            assertNotNull(region.getRegionGroupName());
            assertNotNull(region.getRegionGroupCode());
            assertNotNull(region.getAreas());
            assertTrue(region.getAreas().size() > 0);

            for (RegionAreaDTO.AreaItemDTO area : region.getAreas()) {
                assertNotNull(area.getAreaCode());
                assertNotNull(area.getDisplayName());
            }
        }
    }

    @Test
    @DisplayName("地域服务 - 获取地域列表包含亚太中国大区")
    void getRegionAreaList_ContainsChinaRegion() {
        List<RegionAreaDTO> regionAreaList = regionCacheService.getRegionAreaList();

        boolean foundChinaRegion = regionAreaList.stream()
                .anyMatch(r -> "亚太-中国".equals(r.getRegionGroupName()));

        assertTrue(foundChinaRegion, "应该包含亚太-中国大区");
    }

    @Test
    @DisplayName("地域服务 - 刷新缓存")
    void refreshCache_Success() {
        // 首次获取
        List<RegionAreaDTO> list1 = regionCacheService.getRegionAreaList();

        // 刷新缓存
        regionCacheService.refreshCache();

        // 再次获取
        List<RegionAreaDTO> list2 = regionCacheService.getRegionAreaList();

        assertNotNull(list2);
        assertEquals(list1.size(), list2.size());
    }

    @Test
    @DisplayName("地域服务 - 删除缓存")
    void deleteCache_Success() {
        // 确保缓存有数据
        regionCacheService.getRegionAreaList();

        // 删除缓存
        regionCacheService.deleteCache();

        // 再次获取应该重新加载
        List<RegionAreaDTO> list = regionCacheService.getRegionAreaList();

        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    @DisplayName("ECS配置服务 - 获取镜像列表")
    void getImages_Success() {
        List<ImageVO> images = ecsConfigService.getImages(null);

        assertNotNull(images);
        assertTrue(images.size() > 0);

        for (ImageVO image : images) {
            assertNotNull(image.getImageName());
            assertNotNull(image.getOsCategory());
        }
    }

    @Test
    @DisplayName("ECS配置服务 - 获取Linux镜像列表")
    void getImages_LinuxOnly() {
        List<ImageVO> images = ecsConfigService.getImages("linux");

        assertNotNull(images);
        assertTrue(images.size() > 0);

        for (ImageVO image : images) {
            assertEquals("linux", image.getOsCategory());
        }
    }

    @Test
    @DisplayName("ECS配置服务 - 获取Windows镜像列表")
    void getImages_WindowsOnly() {
        List<ImageVO> images = ecsConfigService.getImages("windows");

        assertNotNull(images);

        for (ImageVO image : images) {
            assertEquals("windows", image.getOsCategory());
        }
    }

    @Test
    @DisplayName("ECS配置服务 - 获取预装应用列表")
    void getApps_Success() {
        List<AppVO> apps = ecsConfigService.getApps();

        assertNotNull(apps);
        assertTrue(apps.size() > 0);

        for (AppVO app : apps) {
            assertNotNull(app.getAppName());
            assertNotNull(app.getAppCode());
        }
    }

    @Test
    @DisplayName("ECS配置服务 - 获取推荐套餐列表")
    void getPackages_Success() {
        List<PackageVO> packages = ecsConfigService.getPackages(null);

        assertNotNull(packages);
        assertTrue(packages.size() > 0);

        for (PackageVO pkg : packages) {
            assertNotNull(pkg.getPackageName());
            assertNotNull(pkg.getPackageCode());
            assertNotNull(pkg.getPriceMonth());
        }
    }

    @Test
    @DisplayName("ECS配置服务 - 获取指定套餐")
    void getPackages_ByCode() {
        List<PackageVO> allPackages = ecsConfigService.getPackages(null);
        assertTrue(allPackages.size() > 0);

        String firstCode = allPackages.get(0).getPackageCode();
        List<PackageVO> packages = ecsConfigService.getPackages(firstCode);

        assertNotNull(packages);
        assertEquals(1, packages.size());
        assertEquals(firstCode, packages.get(0).getPackageCode());
    }

    @Test
    @DisplayName("ECS配置服务 - 获取付费类型列表")
    void getBillingTypes_Success() {
        List<BillingTypeVO> types = ecsConfigService.getBillingTypes();

        assertNotNull(types);
        assertEquals(2, types.size());

        // 验证包含包年包月和按量付费
        boolean hasPrepaid = types.stream().anyMatch(t -> "prepaid".equals(t.getBillingCode()));
        boolean hasPostpaid = types.stream().anyMatch(t -> "postpaid".equals(t.getBillingCode()));

        assertTrue(hasPrepaid, "应该包含包年包月");
        assertTrue(hasPostpaid, "应该包含按量付费");
    }

    @Test
    @DisplayName("ECS配置服务 - 获取带宽模式列表")
    void getBandwidthModes_Success() {
        List<BandwidthModeVO> modes = ecsConfigService.getBandwidthModes();

        assertNotNull(modes);
        assertEquals(2, modes.size());

        // 验证包含按固定带宽和按使用流量
        boolean hasFixed = modes.stream().anyMatch(m -> "fixed".equals(m.getModeCode()));
        boolean hasTraffic = modes.stream().anyMatch(m -> "traffic".equals(m.getModeCode()));

        assertTrue(hasFixed, "应该包含按固定带宽");
        assertTrue(hasTraffic, "应该包含按使用流量");
    }

    @Test
    @DisplayName("ECS配置服务 - 刷新所有缓存")
    void refreshAllCache_Success() {
        // 首次获取各项配置
        ecsConfigService.getImages(null);
        ecsConfigService.getApps();
        ecsConfigService.getPackages(null);
        ecsConfigService.getBillingTypes();
        ecsConfigService.getBandwidthModes();

        // 刷新缓存
        ecsConfigService.refreshAllCache();

        // 验证缓存刷新后仍能正常获取
        assertNotNull(ecsConfigService.getImages(null));
        assertNotNull(ecsConfigService.getApps());
        assertNotNull(ecsConfigService.getPackages(null));
        assertNotNull(ecsConfigService.getBillingTypes());
        assertNotNull(ecsConfigService.getBandwidthModes());
    }

    @Test
    @DisplayName("ECS配置服务 - 删除所有缓存")
    void deleteAllCache_Success() {
        // 确保缓存有数据
        ecsConfigService.getImages(null);
        ecsConfigService.getApps();

        // 删除缓存
        ecsConfigService.deleteAllCache();

        // 再次获取应该重新加载
        assertNotNull(ecsConfigService.getImages(null));
        assertNotNull(ecsConfigService.getApps());
    }
}