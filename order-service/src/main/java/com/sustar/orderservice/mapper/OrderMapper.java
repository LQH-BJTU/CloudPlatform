package com.sustar.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.orderservice.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {

}
