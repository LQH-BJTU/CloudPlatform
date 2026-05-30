package com.sustar.ecsservice.controller;

import com.sustar.ecsservice.dto.RegionAreaDTO;
import com.sustar.ecsservice.service.RegionCacheService;
import com.sustar.ecsservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地域控制器
 */
@RestController
@RequestMapping("/api/ecs/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionCacheService regionCacheService;

    /**
     * 获取地域信息列表（用于前端展示）
     * 一次查询返回完整的大区和地域结构
     */
    @GetMapping("/area-list")
    public Result<List<RegionAreaDTO>> getRegionAreaList() {
        List<RegionAreaDTO> regionAreaList = regionCacheService.getRegionAreaList();
        return Result.success(regionAreaList);
    }

    /**
     * 后台刷新缓存（管理员操作）
     */
    @PostMapping("/cache/refresh")
    public Result<Void> refreshCache() {
        regionCacheService.refreshCache();
        return Result.success("缓存刷新成功", null);
    }

    /**
     * 后台删除缓存（管理员操作）
     */
    @DeleteMapping("/cache")
    public Result<Void> deleteCache() {
        regionCacheService.deleteCache();
        return Result.success("缓存删除成功", null);
    }
}
