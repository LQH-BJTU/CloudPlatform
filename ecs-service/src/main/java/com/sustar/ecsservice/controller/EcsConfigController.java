package com.sustar.ecsservice.controller;

import com.sustar.ecsservice.service.EcsConfigService;
import com.sustar.ecsservice.vo.*;
import com.sustar.ecsservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecs")
@RequiredArgsConstructor
public class EcsConfigController {

    private final EcsConfigService ecsConfigService;

    @GetMapping("/images")
    public Result<List<ImageVO>> getImages(@RequestParam(required = false) String osCategory) {
        List<ImageVO> images = ecsConfigService.getImages(osCategory);
        return Result.success(images);
    }

    @GetMapping("/apps")
    public Result<List<AppVO>> getApps() {
        List<AppVO> apps = ecsConfigService.getApps();
        return Result.success(apps);
    }

    @GetMapping("/packages")
    public Result<List<PackageVO>> getPackages(@RequestParam(required = false) String packageCode) {
        List<PackageVO> packages = ecsConfigService.getPackages(packageCode);
        return Result.success(packages);
    }

    @GetMapping("/billing-types")
    public Result<List<BillingTypeVO>> getBillingTypes() {
        List<BillingTypeVO> types = ecsConfigService.getBillingTypes();
        return Result.success(types);
    }

    @GetMapping("/bandwidth-modes")
    public Result<List<BandwidthModeVO>> getBandwidthModes() {
        List<BandwidthModeVO> modes = ecsConfigService.getBandwidthModes();
        return Result.success(modes);
    }

    /**
     * 刷新所有ECS配置缓存
     */
    @PostMapping("/cache/refresh")
    public Result<Void> refreshCache() {
        ecsConfigService.refreshAllCache();
        return Result.success("ECS配置缓存刷新成功", null);
    }

    /**
     * 删除所有ECS配置缓存
     */
    @DeleteMapping("/cache")
    public Result<Void> deleteCache() {
        ecsConfigService.deleteAllCache();
        return Result.success("ECS配置缓存删除成功", null);
    }
}
