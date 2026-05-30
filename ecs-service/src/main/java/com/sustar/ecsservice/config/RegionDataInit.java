package com.sustar.ecsservice.config;

import com.sustar.ecsservice.mapper.RegionMapper;
import com.sustar.ecsservice.po.RegionPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 地域数据初始化组件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegionDataInit implements CommandLineRunner {

    private final RegionMapper regionMapper;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有数据
        long count = regionMapper.selectCount(null);
        if (count > 0) {
            log.info("地域表已有数据，跳过初始化");
            return;
        }

        log.info("开始初始化地域数据...");

        List<RegionPO> regions = new ArrayList<>();

        // 亚太-中国 (apac-cn)
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-qingdao", "华北1（青岛）", 1));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-beijing", "华北2（北京）", 2));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-zhangjiakou", "华北3（张家口）", 3));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-huhehaote", "华北5（呼和浩特）", 4));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-wulanchabu", "华北6（乌兰察布）", 5));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-hangzhou", "华东1（杭州）", 6));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-shanghai", "华东2（上海）", 7));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-nanjing", "华东5（南京-本地地域）", 8));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-fuzhou", "华东6（福州-本地地域）", 9));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-shenzhen", "华南1（深圳）", 10));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-heyuan", "华南2（河源）", 11));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-guangzhou", "华南3（广州）", 12));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-chengdu", "西南1（成都）", 13));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-hongkong", "中国香港", 14));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-wuhan", "华中1（武汉-本地地域）", 15));
        regions.add(createRegion("亚太-中国", "apac-cn", "cn-zhongwei", "西北2（中卫）", 16));

        // 亚太-其他 (apac-other)
        regions.add(createRegion("亚太-其他", "apac-other", "jp-tokyo", "日本（东京）", 1));
        regions.add(createRegion("亚太-其他", "apac-other", "kr-seoul", "韩国（首尔）", 2));
        regions.add(createRegion("亚太-其他", "apac-other", "sg-singapore", "新加坡", 3));
        regions.add(createRegion("亚太-其他", "apac-other", "my-johor", "马来西亚（柔佛州）", 4));
        regions.add(createRegion("亚太-其他", "apac-other", "my-kualalumpur", "马来西亚（吉隆坡）", 5));
        regions.add(createRegion("亚太-其他", "apac-other", "ph-manila", "菲律宾（马尼拉）", 6));
        regions.add(createRegion("亚太-其他", "apac-other", "id-jakarta", "印度尼西亚（雅加达）", 7));
        regions.add(createRegion("亚太-其他", "apac-other", "th-bangkok", "泰国（曼谷）", 8));

        // 欧洲与美洲 (emea-us)
        regions.add(createRegion("欧洲与美洲", "emea-us", "us-virginia", "美国（弗吉尼亚）", 1));
        regions.add(createRegion("欧洲与美洲", "emea-us", "us-siliconvalley", "美国（硅谷）", 2));
        regions.add(createRegion("欧洲与美洲", "emea-us", "mx-mexico", "墨西哥", 3));
        regions.add(createRegion("欧洲与美洲", "emea-us", "uk-london", "英国（伦敦）", 4));
        regions.add(createRegion("欧洲与美洲", "emea-us", "fr-paris", "法国（巴黎）", 5));
        regions.add(createRegion("欧洲与美洲", "emea-us", "de-frankfurt", "德国（法兰克福）", 6));

        // 中东 (mea)
        regions.add(createRegion("中东", "mea", "ae-dubai", "阿联酋（迪拜）", 1));

        // 批量插入
        regions.forEach(regionMapper::insert);

        log.info("地域数据初始化完成，共插入 {} 条记录", regions.size());
    }

    private RegionPO createRegion(String regionName, String regionCode, String areaCode, String areaName, int sortNum) {
        RegionPO region = new RegionPO();
        region.setRegionName(regionName);
        region.setRegionCode(regionCode);
        region.setAreaCode(areaCode);
        region.setAreaName(areaName);
        region.setStatus(1);
        region.setSortNum(sortNum);
        region.setIsDeleted(0);
        return region;
    }
}
