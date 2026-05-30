package com.sustar.ecsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.ecsservice.po.RegionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地域数据访问层
 */
@Mapper
public interface RegionMapper extends BaseMapper<RegionPO> {

    /**
     * 查询所有启用的地域
     */
    @Select("SELECT * FROM sys_common_region WHERE is_deleted = 0 AND status = 1 ORDER BY sort_num")
    List<RegionPO> selectAllActive();

    /**
     * 查询所有大区名
     */
    @Select("SELECT DISTINCT region_name FROM sys_common_region WHERE is_deleted = 0 AND status = 1 ORDER BY region_name")
    List<String> selectAllRegionNames();
}
