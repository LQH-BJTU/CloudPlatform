package com.sustar.orderservice.service;

/**
 * MQ消息服务接口
 * 封装订单相关的MQ消息发送逻辑
 */
public interface MqMessageService {

    /**
     * 发送订单超时延迟消息
     *
     * @param orderId 订单ID
     * @param delaySeconds 延迟秒数
     */
    void sendOrderTimeoutDelayMessage(Long orderId, Integer delaySeconds);

    /**
     * 发送订单创建成功消息
     *
     * @param orderId 订单ID
     * @param orderNo 订单编号
     * @param userId  用户ID
     */
    void sendOrderCreateMessage(Long orderId, String orderNo, String userId);

    /**
     * 发送订单支付成功消息
     *
     * @param orderId 订单ID
     * @param orderNo 订单编号
     * @param payAmount 支付金额
     */
    void sendOrderPaySuccessMessage(Long orderId, String orderNo, java.math.BigDecimal payAmount);

    /**
     * 发送订单取消消息
     *
     * @param orderId 订单ID
     * @param orderNo 订单编号
     */
    void sendOrderCancelMessage(Long orderId, String orderNo);

    /**
     * 发送订单完成消息
     *
     * @param orderId 订单ID
     * @param orderNo 订单编号
     */
    void sendOrderCompleteMessage(Long orderId, String orderNo);
}