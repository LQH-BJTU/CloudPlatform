package com.sustar.ecsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.ecsservice.po.BandwidthModePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BandwidthModeMapper extends BaseMapper<BandwidthModePO> {

    @Select("SELECT * FROM ecs_bandwidth_mode WHERE is_expired = 0 ORDER BY sort_order ASC")
    List<BandwidthModePO> selectActiveBandwidthModes();
}