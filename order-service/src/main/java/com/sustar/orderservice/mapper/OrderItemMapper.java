package com.sustar.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.orderservice.po.OrderItemPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemPO> {

    /**
     * 根据订单ID查询订单明细列表
     *
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    List<OrderItemPO> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单ID删除订单明细
     *
     * @param orderId 订单ID
     * @return 删除数量
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
}