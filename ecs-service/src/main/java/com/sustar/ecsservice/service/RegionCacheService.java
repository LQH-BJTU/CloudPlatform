package com.sustar.ecsservice.service;

import com.sustar.ecsservice.dto.RegionAreaDTO;

import java.util.List;

/**
 * 地域缓存服务接口
 */
public interface RegionCacheService {

    /**
     * 获取地域信息列表（用于前端展示）
     * 一次查询返回完整的大区和地域结构
     */
    List<RegionAreaDTO> getRegionAreaList();

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 删除缓存
     */
    void deleteCache();
}
