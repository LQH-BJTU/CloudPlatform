package com.sustar.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.orderservice.po.OrderStatusFlowPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单状态流水数据访问接口
 */
@Mapper
public interface OrderStatusFlowMapper extends BaseMapper<OrderStatusFlowPO> {

    /**
     * 根据订单ID查询状态流水列表（按时间倒序）
     *
     * @param orderId 订单ID
     * @return 状态流水列表
     */
    List<OrderStatusFlowPO> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单编号查询状态流水列表
     *
     * @param orderNo 订单编号
     * @return 状态流水列表
     */
    List<OrderStatusFlowPO> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询订单最新状态变更记录
     *
     * @param orderId 订单ID
     * @return 最新状态变更记录
     */
    OrderStatusFlowPO selectLatestByOrderId(@Param("orderId") Long orderId);
}