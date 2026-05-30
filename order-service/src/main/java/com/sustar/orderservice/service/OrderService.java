package com.sustar.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.dto.OrderItemDTO;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.query.OrderQuery;

import java.util.List;

/**
 * 订单业务接口
 * 定义订单相关的业务方法，支持千万级高并发订单系统
 */
public interface OrderService extends IService<OrderPO> {

    /**
     * 根据订单ID查询订单详情（包含明细）
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    OrderDTO getOrderById(Long id);

    /**
     * 根据订单编号查询订单详情
     *
     * @param orderNo 订单编号
     * @return 订单DTO
     */
    OrderDTO getOrderByOrderNo(String orderNo);

    /**
     * 根据查询条件查询订单列表
     *
     * @param query 查询条件
     * @return 订单DTO列表
     */
    List<OrderDTO> listOrders(OrderQuery query);

    /**
     * 创建订单（包含明细）
     *
     * @param dto 订单DTO
     * @return 订单ID
     */
    Long createOrder(OrderDTO dto);

    /**
     * 更新订单信息
     *
     * @param id  订单ID
     * @param dto 订单DTO
     */
    void updateOrder(Long id, OrderDTO dto);

    /**
     * 删除订单（包含明细）
     *
     * @param id 订单ID
     */
    void deleteOrder(Long id);

    /**
     * 取消订单（待支付状态）
     *
     * @param id 订单ID
     */
    void cancelOrder(Long id);

    /**
     * 更新订单支付状态
     *
     * @param orderNo   订单编号
     * @param payStatus 支付状态
     * @param payType   支付方式
     */
    void updatePayStatus(String orderNo, Integer payStatus, String payType);

    /**
     * 支付成功处理（进入已支付状态，发送延迟消息）
     *
     * @param orderNo 订单编号
     * @param payType 支付方式
     */
    void handlePaySuccess(String orderNo, String payType);

    /**
     * 支付处理中（中间态，防重复回调）
     *
     * @param orderNo 订单编号
     */
    void handlePaying(String orderNo);

    /**
     * 支付失败处理
     *
     * @param orderNo 订单编号
     * @param reason  失败原因
     */
    void handlePayFailed(String orderNo, String reason);

    /**
     * 系统确认订单（扣减库存后进入待发货）
     *
     * @param orderNo 订单编号
     */
    void confirmOrder(String orderNo);

    /**
     * 商家发货（进入发货中状态）
     *
     * @param orderNo      订单编号
     * @param logisticsNo  物流单号
     */
    void shipOrder(String orderNo, String logisticsNo);

    /**
     * 用户确认收货
     *
     * @param orderNo 订单编号
     */
    void confirmReceipt(String orderNo);

    /**
     * 用户申请退款
     *
     * @param orderNo 订单编号
     * @param reason  退款原因
     */
    void applyRefund(String orderNo, String reason);

    /**
     * 退款成功处理
     *
     * @param orderNo 订单编号
     */
    void handleRefundSuccess(String orderNo);

    /**
     * 退款失败处理
     *
     * @param orderNo 订单编号
     * @param reason  失败原因
     */
    void handleRefundFailed(String orderNo, String reason);

    /**
     * 用户申请售后（已完成状态）
     *
     * @param orderNo 订单编号
     * @param reason  售后原因
     */
    void applyAfterSale(String orderNo, String reason);

    /**
     * 售后完成
     *
     * @param orderNo 订单编号
     */
    void completeAfterSale(String orderNo);

    /**
     * 查询订单明细列表
     *
     * @param orderId 订单ID
     * @return 订单明细DTO列表
     */
    List<OrderItemDTO> listOrderItems(Long orderId);

    /**
     * 原子更新订单状态（高并发安全）
     *
     * @param orderId       订单ID
     * @param expectedStatus 期望状态
     * @param targetStatus   目标状态
     * @return 是否更新成功
     */
    boolean updateStatusWithCheck(Long orderId, Integer expectedStatus, Integer targetStatus);

    /**
     * 释放订单锁定的库存
     *
     * @param orderId 订单ID
     */
    void releaseStock(Long orderId);

    /**
     * 根据订单编号获取订单PO
     *
     * @param orderNo 订单编号
     * @return 订单PO
     */
    OrderPO getOrderPOByOrderNo(String orderNo);
}