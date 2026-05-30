package com.sustar.ecsservice.service;

import com.sustar.ecsservice.vo.*;

import java.util.List;

public interface EcsConfigService {

    List<ImageVO> getImages(String osCategory);

    List<AppVO> getApps();

    List<PackageVO> getPackages(String packageCode);

    List<BillingTypeVO> getBillingTypes();

    List<BandwidthModeVO> getBandwidthModes();

    /**
     * 刷新所有缓存
     */
    void refreshAllCache();

    /**
     * 删除所有缓存
     */
    void deleteAllCache();
}
