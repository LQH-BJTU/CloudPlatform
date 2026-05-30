package com.sustar.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.po.OrderStatusFlowPO;

import java.util.List;

/**
 * 订单状态流水业务接口
 */
public interface OrderStatusFlowService extends IService<OrderStatusFlowPO> {

    /**
     * 记录订单状态变更
     *
     * @param order          订单实体
     * @param beforeStatus   变更前状态
     * @param afterStatus    变更后状态
     * @param beforePayStatus 变更前支付状态
     * @param afterPayStatus 变更后支付状态
     * @param operationType  操作类型
     * @param operatorId     操作人ID
     * @param operatorName   操作人名称
     * @param remark         备注
     * @param externalNo     外部流水号
     */
    void recordStatusChange(OrderPO order, Integer beforeStatus, Integer afterStatus,
                           Integer beforePayStatus, Integer afterPayStatus,
                           String operationType, String operatorId, String operatorName,
                           String remark, String externalNo);

    /**
     * 根据订单ID查询状态流水列表
     *
     * @param orderId 订单ID
     * @return 状态流水列表
     */
    List<OrderStatusFlowPO> listByOrderId(Long orderId);

    /**
     * 根据订单编号查询状态流水列表
     *
     * @param orderNo 订单编号
     * @return 状态流水列表
     */
    List<OrderStatusFlowPO> listByOrderNo(String orderNo);

    /**
     * 获取订单最新状态变更记录
     *
     * @param orderId 订单ID
     * @return 最新状态变更记录
     */
    OrderStatusFlowPO getLatestByOrderId(Long orderId);
}