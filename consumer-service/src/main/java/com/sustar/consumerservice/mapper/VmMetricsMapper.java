package com.sustar.consumerservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.consumerservice.po.VmMetricsPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VmMetricsMapper extends BaseMapper<VmMetricsPO> {
}