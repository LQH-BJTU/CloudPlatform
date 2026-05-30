package com.sustar.ecsservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import com.sustar.ecsservice.service.EcsConfigService;
import com.sustar.ecsservice.vo.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ECS配置服务实现类
 *
 * 采用两级缓存策略：
 * 1. 一级缓存：Guava本地缓存（内存）- 高性能，低延迟，5分钟过期
 * 2. 二级缓存：Redis分布式缓存 - 多实例共享，永不过期
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcsConfigServiceImpl implements EcsConfigService {

    private final ImageMapper imageMapper;
    private final AppMapper appMapper;
    private final PackageMapper packageMapper;
    private final BillingTypeMapper billingTypeMapper;
    private final BandwidthModeMapper bandwidthModeMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * Guava本地缓存 - 一级缓存
     */
    private Cache<String, Object> localCache;

    /**
     * Redis缓存Key前缀
     */
    private static final String REDIS_KEY_PREFIX = "cloudplatform:ecs:config:";

    /**
     * 镜像列表缓存Key
     */
    private static final String IMAGES_KEY = REDIS_KEY_PREFIX + "images";
    private static final String IMAGES_KEY_PREFIX = REDIS_KEY_PREFIX + "images:";

    /**
     * 预装应用列表缓存Key
     */
    private static final String APPS_KEY = REDIS_KEY_PREFIX + "apps";

    /**
     * 推荐套餐列表缓存Key
     */
    private static final String PACKAGES_KEY = REDIS_KEY_PREFIX + "packages";

    /**
     * 付费类型列表缓存Key
     */
    private static final String BILLING_TYPES_KEY = REDIS_KEY_PREFIX + "billing_types";

    /**
     * 带宽模式列表缓存Key
     */
    private static final String BANDWIDTH_MODES_KEY = REDIS_KEY_PREFIX + "bandwidth_modes";

    /**
     * 本地缓存过期时间：5分钟
     */
    private static final long LOCAL_CACHE_TTL_MINUTES = 5L;

    @PostConstruct
    public void init() {
        localCache = CacheBuilder.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(LOCAL_CACHE_TTL_MINUTES, TimeUnit.MINUTES)
                .recordStats()
                .build();
        log.info("ECS配置Guava本地缓存初始化完成，过期时间: {}分钟", LOCAL_CACHE_TTL_MINUTES);
    }

    @Override
    public List<ImageVO> getImages(String osCategory) {
        String cacheKey = "images_" + (osCategory == null ? "all" : osCategory);

        // 1. 查询本地缓存
        Object localResult = localCache.getIfPresent(cacheKey);
        if (localResult != null) {
            log.debug("镜像列表本地缓存命中, osCategory: {}", osCategory);
            return (List<ImageVO>) localResult;
        }

        // 2. 查询Redis缓存
        String redisKey = osCategory == null ? IMAGES_KEY : IMAGES_KEY_PREFIX + osCategory;
        try {
            String cached = redisTemplate.opsForValue().get(redisKey);
            if (cached != null) {
                log.debug("镜像列表Redis缓存命中, osCategory: {}", osCategory);
                List<ImageVO> result = JSON.parseArray(cached, ImageVO.class);
                localCache.put(cacheKey, result);
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，降级使用数据库查询: {}", e.getMessage());
        }

        // 3. 从数据库加载
        log.info("镜像列表缓存未命中，从数据库加载, osCategory: {}", osCategory);
        List<ImagePO> images;
        if (osCategory != null && !osCategory.equals("all")) {
            images = imageMapper.selectActiveImagesByCategory(osCategory);
        } else {
            images = imageMapper.selectActiveImages();
        }
        List<ImageVO> result = images.stream().map(this::convertToImageVO).collect(Collectors.toList());

        // 4. 写入缓存
        try {
            String json = JSON.toJSONString(result);
            redisTemplate.opsForValue().set(redisKey, json);
        } catch (Exception e) {
            log.warn("Redis写入失败，仅使用本地缓存: {}", e.getMessage());
        }
        localCache.put(cacheKey, result);

        return result;
    }

    @Override
    public List<AppVO> getApps() {
        String cacheKey = "apps";

        // 1. 查询本地缓存
        Object localResult = localCache.getIfPresent(cacheKey);
        if (localResult != null) {
            log.debug("预装应用列表本地缓存命中");
            return (List<AppVO>) localResult;
        }

        // 2. 查询Redis缓存
        try {
            String cached = redisTemplate.opsForValue().get(APPS_KEY);
            if (cached != null) {
                log.debug("预装应用列表Redis缓存命中");
                List<AppVO> result = JSON.parseArray(cached, AppVO.class);
                localCache.put(cacheKey, result);
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，降级使用数据库查询: {}", e.getMessage());
        }

        // 3. 从数据库加载
        log.info("预装应用列表缓存未命中，从数据库加载");
        List<AppPO> apps = appMapper.selectActiveApps();
        List<AppVO> result = apps.stream().map(this::convertToAppVO).collect(Collectors.toList());

        // 4. 写入缓存
        try {
            String json = JSON.toJSONString(result);
            redisTemplate.opsForValue().set(APPS_KEY, json);
        } catch (Exception e) {
            log.warn("Redis写入失败，仅使用本地缓存: {}", e.getMessage());
        }
        localCache.put(cacheKey, result);

        return result;
    }

    @Override
    public List<PackageVO> getPackages(String packageCode) {
        String cacheKey = "packages_" + (packageCode == null ? "all" : packageCode);

        // 1. 查询本地缓存
        Object localResult = localCache.getIfPresent(cacheKey);
        if (localResult != null) {
            log.debug("推荐套餐列表本地缓存命中, packageCode: {}", packageCode);
            return (List<PackageVO>) localResult;
        }

        // 2. 查询Redis缓存
        try {
            String cached = redisTemplate.opsForValue().get(PACKAGES_KEY);
            if (cached != null) {
                log.debug("推荐套餐列表Redis缓存命中, packageCode: {}", packageCode);
                List<PackageVO> allPackages = JSON.parseArray(cached, PackageVO.class);
                List<PackageVO> result = allPackages;
                if (packageCode != null && !packageCode.equals("all")) {
                    result = allPackages.stream()
                            .filter(p -> packageCode.equals(p.getPackageCode()))
                            .collect(Collectors.toList());
                }
                localCache.put(cacheKey, result);
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，降级使用数据库查询: {}", e.getMessage());
        }

        // 3. 从数据库加载
        log.info("推荐套餐列表缓存未命中，从数据库加载, packageCode: {}", packageCode);
        List<PackagePO> packages;
        if (packageCode != null && !packageCode.equals("all")) {
            packages = packageMapper.selectActivePackagesByCode(packageCode);
        } else {
            packages = packageMapper.selectActivePackages();
        }
        List<PackageVO> result = packages.stream().map(this::convertToPackageVO).collect(Collectors.toList());

        // 4. 写入缓存（缓存全部套餐）
        try {
            List<PackagePO> allPackages = packageMapper.selectActivePackages();
            List<PackageVO> allPackageVOs = allPackages.stream().map(this::convertToPackageVO).collect(Collectors.toList());
            String json = JSON.toJSONString(allPackageVOs);
            redisTemplate.opsForValue().set(PACKAGES_KEY, json);
        } catch (Exception e) {
            log.warn("Redis写入失败，仅使用本地缓存: {}", e.getMessage());
        }
        localCache.put(cacheKey, result);

        return result;
    }

    @Override
    public List<BillingTypeVO> getBillingTypes() {
        String cacheKey = "billing_types";

        // 1. 查询本地缓存
        Object localResult = localCache.getIfPresent(cacheKey);
        if (localResult != null) {
            log.debug("付费类型列表本地缓存命中");
            return (List<BillingTypeVO>) localResult;
        }

        // 2. 查询Redis缓存
        try {
            String cached = redisTemplate.opsForValue().get(BILLING_TYPES_KEY);
            if (cached != null) {
                log.debug("付费类型列表Redis缓存命中");
                List<BillingTypeVO> result = JSON.parseArray(cached, BillingTypeVO.class);
                localCache.put(cacheKey, result);
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，降级使用内存数据: {}", e.getMessage());
        }

        // 3. 从数据库加载
        log.info("付费类型列表缓存未命中，从数据库加载");
        List<BillingTypePO> billingTypes = billingTypeMapper.selectActiveBillingTypes();
        List<BillingTypeVO> result = billingTypes.stream().map(this::convertToBillingTypeVO).collect(Collectors.toList());

        // 4. 写入缓存
        try {
            String json = JSON.toJSONString(result);
            redisTemplate.opsForValue().set(BILLING_TYPES_KEY, json);
        } catch (Exception e) {
            log.warn("Redis写入失败，仅使用本地缓存: {}", e.getMessage());
        }
        localCache.put(cacheKey, result);

        return result;
    }

    @Override
    public List<BandwidthModeVO> getBandwidthModes() {
        String cacheKey = "bandwidth_modes";

        // 1. 查询本地缓存
        Object localResult = localCache.getIfPresent(cacheKey);
        if (localResult != null) {
            log.debug("带宽模式列表本地缓存命中");
            return (List<BandwidthModeVO>) localResult;
        }

        // 2. 查询Redis缓存
        try {
            String cached = redisTemplate.opsForValue().get(BANDWIDTH_MODES_KEY);
            if (cached != null) {
                log.debug("带宽模式列表Redis缓存命中");
                List<BandwidthModeVO> result = JSON.parseArray(cached, BandwidthModeVO.class);
                localCache.put(cacheKey, result);
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，降级使用内存数据: {}", e.getMessage());
        }

        // 3. 从数据库加载
        log.info("带宽模式列表缓存未命中，从数据库加载");
        List<BandwidthModePO> bandwidthModes = bandwidthModeMapper.selectActiveBandwidthModes();
        List<BandwidthModeVO> result = bandwidthModes.stream().map(this::convertToBandwidthModeVO).collect(Collectors.toList());

        // 4. 写入缓存
        try {
            String json = JSON.toJSONString(result);
            redisTemplate.opsForValue().set(BANDWIDTH_MODES_KEY, json);
        } catch (Exception e) {
            log.warn("Redis写入失败，仅使用本地缓存: {}", e.getMessage());
        }
        localCache.put(cacheKey, result);

        return result;
    }

    @Override
    public void refreshAllCache() {
        log.info("主动刷新所有ECS配置缓存");
        deleteAllCache();
        // 预热缓存
        getImages(null);
        getApps();
        getPackages(null);
        getBillingTypes();
        getBandwidthModes();
    }

    @Override
    public void deleteAllCache() {
        localCache.invalidateAll();
        log.debug("ECS配置本地缓存已清除");

        try {
            redisTemplate.delete(IMAGES_KEY);
            redisTemplate.delete(APPS_KEY);
            redisTemplate.delete(PACKAGES_KEY);
            redisTemplate.delete(BILLING_TYPES_KEY);
            redisTemplate.delete(BANDWIDTH_MODES_KEY);
            redisTemplate.delete(IMAGES_KEY + ":windows");
            redisTemplate.delete(IMAGES_KEY + ":linux");
            log.info("ECS配置Redis缓存已删除");
        } catch (Exception e) {
            log.warn("Redis删除失败: {}", e.getMessage());
        }
    }

    private BillingTypeVO convertToBillingTypeVO(BillingTypePO po) {
        BillingTypeVO vo = new BillingTypeVO();
        vo.setId(po.getId());
        vo.setBillingCode(po.getBillingCode());
        vo.setBillingName(po.getBillingName());
        vo.setDescription(po.getDescription());
        vo.setIsRecommended(po.getIsRecommended() != null && po.getIsRecommended() == 1);
        return vo;
    }

    private BandwidthModeVO convertToBandwidthModeVO(BandwidthModePO po) {
        BandwidthModeVO vo = new BandwidthModeVO();
        vo.setId(po.getId());
        vo.setModeCode(po.getModeCode());
        vo.setModeName(po.getModeName());
        vo.setDescription(po.getDescription());
        vo.setIsDefault(po.getIsDefault() != null && po.getIsDefault() == 1);
        return vo;
    }

    private ImageVO convertToImageVO(ImagePO po) {
        ImageVO vo = new ImageVO();
        vo.setId(po.getId());
        vo.setImageName(po.getImageName());
        vo.setOsCategory(po.getOsCategory());
        vo.setOsVersion(po.getOsVersion());
        vo.setDescription(po.getDescription());
        vo.setIsDefault(po.getIsDefault() != null && po.getIsDefault() == 1);
        vo.setIsFree(po.getIsFree() != null && po.getIsFree() == 1);
        return vo;
    }

    private AppVO convertToAppVO(AppPO po) {
        AppVO vo = new AppVO();
        vo.setId(po.getId());
        vo.setAppName(po.getAppName());
        vo.setAppCode(po.getAppCode());
        vo.setIcon(po.getIcon());
        vo.setDescription(po.getDescription());
        vo.setInstallTime(po.getInstallTime());
        return vo;
    }

    private PackageVO convertToPackageVO(PackagePO po) {
        PackageVO vo = new PackageVO();
        vo.setId(po.getId());
        vo.setPackageName(po.getPackageName());
        vo.setPackageCode(po.getPackageCode());
        vo.setDescription(po.getDescription());
        vo.setIcon(po.getIcon());
        vo.setVcpus(po.getVcpus());
        vo.setMemory(po.getMemory());
        vo.setSystemDisk(po.getSystemDisk());
        vo.setBandwidth(po.getBandwidth());
        vo.setPriceMonth(po.getPriceMonth());
        vo.setIsRecommended(po.getIsRecommended() != null && po.getIsRecommended() == 1);
        return vo;
    }
}
