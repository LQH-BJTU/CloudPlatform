package com.sustar.consumerservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.consumerservice.po.SecurityGroupPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SecurityGroupMapper extends BaseMapper<SecurityGroupPO> {
}