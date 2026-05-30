package com.sustar.ecsservice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sustar.ecsservice.dto.RegionAreaDTO;
import com.sustar.ecsservice.mapper.RegionMapper;
import com.sustar.ecsservice.po.RegionPO;
import com.sustar.ecsservice.service.RegionCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 地域缓存服务实现类
 * 
 * 采用两级缓存策略：
 * 1. 一级缓存：Guava本地缓存（内存）- 高性能，低延迟
 * 2. 二级缓存：Redis分布式缓存 - 多实例共享，数据一致性
 * 
 * 核心设计：一次查询返回完整的大区和地域结构
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegionCacheServiceImpl implements RegionCacheService {

    private final StringRedisTemplate redisTemplate;
    private final RegionMapper regionMapper;

    /**
     * Guava本地缓存 - 一级缓存
     */
    private Cache<String, Object> localCache;

    /**
     * Redis缓存Key前缀
     */
    private static final String REDIS_KEY_PREFIX = "cloudplatform:ecs:region:";
    
    /**
     * 地域信息列表缓存Key（用于前端展示）
     */
    private static final String REGION_AREA_LIST_KEY = REDIS_KEY_PREFIX + "area_list";
    
    /**
     * 本地缓存过期时间：5分钟
     */
    private static final long LOCAL_CACHE_TTL_MINUTES = 5L;

    @PostConstruct
    public void init() {
        localCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(LOCAL_CACHE_TTL_MINUTES, TimeUnit.MINUTES)
                .recordStats()
                .build();
        log.info("Guava本地缓存初始化完成，过期时间: {}分钟", LOCAL_CACHE_TTL_MINUTES);
    }

    @Override
    public List<RegionAreaDTO> getRegionAreaList() {
        String cacheKey = "region_area_list";
        
        Object localResult = localCache.getIfPresent(cacheKey);
        if (localResult != null) {
            log.debug("地域信息列表本地缓存命中");
            return (List<RegionAreaDTO>) localResult;
        }

        try {
            String cached = redisTemplate.opsForValue().get(REGION_AREA_LIST_KEY);
            if (cached != null) {
                log.debug("地域信息列表Redis缓存命中");
                List<RegionAreaDTO> result = JSON.parseArray(cached, RegionAreaDTO.class);
                localCache.put(cacheKey, result);
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis连接失败，降级使用数据库查询: {}", e.getMessage());
        }

        log.info("地域信息列表缓存未命中，从数据库加载");
        List<RegionPO> regions = regionMapper.selectAllActive();
        List<RegionAreaDTO> regionAreaList = buildRegionAreaList(regions);

        try {
            String listJson = JSON.toJSONString(regionAreaList);
            redisTemplate.opsForValue().set(REGION_AREA_LIST_KEY, listJson);
            log.info("地域信息列表Redis缓存写入成功");
            log.info(redisTemplate.opsForValue().get(REGION_AREA_LIST_KEY));
        } catch (Exception e) {
            log.warn("Redis写入失败，仅使用本地缓存: {}", e.getMessage());
        }
        localCache.put(cacheKey, regionAreaList);

        return regionAreaList;
    }

    @Override
    public void refreshCache() {
        log.info("主动刷新地域缓存");
        deleteCache();
        getRegionAreaList();
    }

    @Override
    public void deleteCache() {
        localCache.invalidateAll();
        log.debug("地域本地缓存已清除");
        
        try {
            redisTemplate.delete(REGION_AREA_LIST_KEY);
            log.info("地域Redis缓存已删除: {}", REGION_AREA_LIST_KEY);
        } catch (Exception e) {
            log.warn("Redis删除失败: {}", e.getMessage());
        }
    }

    private List<RegionAreaDTO> buildRegionAreaList(List<RegionPO> regions) {
        Map<String, List<RegionPO>> grouped = regions.stream()
                .collect(Collectors.groupingBy(RegionPO::getRegionCode));

        List<RegionAreaDTO> result = new ArrayList<>();

        grouped.forEach((regionCode, areas) -> {
            RegionAreaDTO regionArea = new RegionAreaDTO();
            if (!areas.isEmpty()) {
                RegionPO firstArea = areas.get(0);
                regionArea.setRegionGroupName(firstArea.getRegionName());
                regionArea.setRegionGroupCode(firstArea.getRegionCode());
            }

            List<RegionAreaDTO.AreaItemDTO> areaItemList = areas.stream()
                    .map(area -> convertToAreaItemDTO(area))
                    .sorted(Comparator.comparing(RegionAreaDTO.AreaItemDTO::getSortOrder))
                    .collect(Collectors.toList());
            regionArea.setAreas(areaItemList);

            result.add(regionArea);
        });

        return result;
    }

    private RegionAreaDTO.AreaItemDTO convertToAreaItemDTO(RegionPO regionPO) {
        return RegionAreaDTO.AreaItemDTO.builder()
                .areaCode(regionPO.getAreaCode())
                .displayName(regionPO.getAreaName())
                .selected(false)
                .sortOrder(regionPO.getSortNum())
                .build();
    }
}
