package com.sustar.ecsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.ecsservice.po.BillingTypePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BillingTypeMapper extends BaseMapper<BillingTypePO> {

    @Select("SELECT * FROM ecs_billing_type WHERE is_expired = 0 ORDER BY sort_order ASC")
    List<BillingTypePO> selectActiveBillingTypes();
}