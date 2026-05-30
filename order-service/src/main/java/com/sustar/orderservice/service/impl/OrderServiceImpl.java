package com.sustar.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.orderservice.constants.OrderStatus;
import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.dto.OrderItemDTO;
import com.sustar.orderservice.exceptions.BusinessException;
import com.sustar.orderservice.mapper.OrderMapper;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.query.OrderQuery;
import com.sustar.orderservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单业务实现类
 * 实现订单相关的业务逻辑，包含完整的状态流转，支持千万级高并发订单系统
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderPO> implements OrderService {

    private final OrderItemService orderItemService;
    private final OrderStatusFlowService orderStatusFlowService;
    private final MqMessageService mqMessageService;

    @Override
    public OrderDTO getOrderById(Long id) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToDTOWithItems(po);
    }

    @Override
    public OrderDTO getOrderByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }

        LambdaQueryWrapper<OrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderPO::getOrderNo, orderNo);

        OrderPO po = this.getOne(wrapper);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToDTOWithItems(po);
    }

    @Override
    public List<OrderDTO> listOrders(OrderQuery query) {
        LambdaQueryWrapper<OrderPO> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getOrderNo())) {
            wrapper.eq(OrderPO::getOrderNo, query.getOrderNo());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(OrderPO::getUserId, query.getUserId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(OrderPO::getStatus, query.getStatus());
        }
        if (query.getPayStatus() != null) {
            wrapper.eq(OrderPO::getPayStatus, query.getPayStatus());
        }

        wrapper.orderByDesc(OrderPO::getCreateTime);

        List<OrderPO> poList = this.list(wrapper);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderDTO dto) {
        // 1. 校验订单数据
        validateOrderDTO(dto);

        // 2. 计算订单金额
        calculateOrderAmount(dto);

        // 3. 创建订单主记录（待确认状态）
        OrderPO po = new OrderPO();
        BeanUtils.copyProperties(dto, po);
        po.setOrderNo(generateOrderNo());
        po.setStatus(OrderStatus.ORDER_STATUS_PENDING);
        po.setPayStatus(OrderStatus.PAY_STATUS_UNPAID);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());

        this.save(po);
        log.info("创建订单成功，id={}, orderNo={}", po.getId(), po.getOrderNo());

        // 4. 保存订单明细
        if (!CollectionUtils.isEmpty(dto.getItems())) {
            orderItemService.batchSave(po.getId(), po.getOrderNo(), dto.getItems());
        }

        // 5. 记录状态流水
        orderStatusFlowService.recordStatusChange(po, null, OrderStatus.ORDER_STATUS_PENDING,
                null, OrderStatus.PAY_STATUS_UNPAID, "CREATE", "SYSTEM", "系统", null, null);

        // 6. 发送订单创建成功消息（异步）
        mqMessageService.sendOrderCreateMessage(po.getId(), po.getOrderNo(), po.getUserId());

        return po.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(Long id, OrderDTO dto) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }

        // 已支付订单不允许修改
        if (po.getStatus() >= OrderStatus.ORDER_STATUS_PAID) {
            throw new BusinessException("已支付订单不允许修改");
        }

        // 手动设置属性，避免BeanUtils.copyProperties的问题
        if (dto.getReceiverName() != null) {
            po.setReceiverName(dto.getReceiverName());
        }
        if (dto.getReceiverPhone() != null) {
            po.setReceiverPhone(dto.getReceiverPhone());
        }
        if (dto.getReceiverAddress() != null) {
            po.setReceiverAddress(dto.getReceiverAddress());
        }
        if (dto.getRemark() != null) {
            po.setRemark(dto.getRemark());
        }
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);
        log.info("更新订单成功，id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }

        // 已支付订单不允许删除
        if (po.getStatus() >= OrderStatus.ORDER_STATUS_PAID) {
            throw new BusinessException("已支付订单不允许删除");
        }

        // 删除订单明细
        orderItemService.deleteByOrderId(id);

        // 删除订单主记录
        this.removeById(id);
        log.info("删除订单成功，id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }

        // 只能取消待确认或待支付订单
        if (po.getStatus() != OrderStatus.ORDER_STATUS_PENDING &&
                po.getStatus() != OrderStatus.ORDER_STATUS_UNPAID) {
            throw new BusinessException("只有待确认或待支付订单才能取消");
        }

        Integer beforeStatus = po.getStatus();
        Integer beforePayStatus = po.getPayStatus();

        po.setStatus(OrderStatus.ORDER_STATUS_CANCELLED);
        po.setPayStatus(OrderStatus.PAY_STATUS_FAILED);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_CANCELLED,
                beforePayStatus, OrderStatus.PAY_STATUS_FAILED, "ORDER_CANCEL", "SYSTEM", "系统", null, null);

        // 发送订单取消消息
        mqMessageService.sendOrderCancelMessage(po.getId(), po.getOrderNo());

        // 释放库存
        releaseStock(po.getId());

        log.info("取消订单成功，id={}, orderNo={}", id, po.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayStatus(String orderNo, Integer payStatus, String payType) {
        LambdaQueryWrapper<OrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderPO::getOrderNo, orderNo);

        OrderPO po = this.getOne(wrapper);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }

        Integer beforePayStatus = po.getPayStatus();

        po.setPayStatus(payStatus);
        po.setPayType(payType);
        po.setUpdateTime(LocalDateTime.now());

        // 如果支付成功，更新订单状态为已支付
        if (OrderStatus.PAY_STATUS_SUCCESS.equals(payStatus)) {
            Integer beforeStatus = po.getStatus();
            po.setStatus(OrderStatus.ORDER_STATUS_PAID);
            po.setPayTime(LocalDateTime.now());

            this.updateById(po);

            // 记录状态流水
            orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_PAID,
                    beforePayStatus, payStatus, "PAY", "SYSTEM", "系统", null, null);

            // 发送支付成功消息
            mqMessageService.sendOrderPaySuccessMessage(po.getId(), po.getOrderNo(), po.getPayAmount());
        } else {
            this.updateById(po);
        }

        log.info("更新订单支付状态成功，orderNo={}, payStatus={}", orderNo, payStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaying(String orderNo) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_PAYING)) {
            throw new BusinessException("订单状态不允许进入支付处理中");
        }

        po.setStatus(OrderStatus.ORDER_STATUS_PAYING);
        po.setPayStatus(OrderStatus.PAY_STATUS_PAYING);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_PAYING,
                OrderStatus.PAY_STATUS_UNPAID, OrderStatus.PAY_STATUS_PAYING, "PAY", "SYSTEM", "系统", null, null);

        log.info("订单进入支付处理中，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaySuccess(String orderNo, String payType) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();
        Integer beforePayStatus = po.getPayStatus();

        // 校验状态流转：待支付或支付处理中都可以进入已支付
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_PAID)) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 更新支付状态和订单状态
        po.setPayStatus(OrderStatus.PAY_STATUS_SUCCESS);
        po.setPayType(payType);
        po.setPayTime(LocalDateTime.now());
        po.setStatus(OrderStatus.ORDER_STATUS_PAID);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_PAID,
                beforePayStatus, OrderStatus.PAY_STATUS_SUCCESS, "PAY", "SYSTEM", "系统", null, null);

        // 发送支付成功消息
        mqMessageService.sendOrderPaySuccessMessage(po.getId(), po.getOrderNo(), po.getPayAmount());

        log.info("支付成功处理完成，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePayFailed(String orderNo, String reason) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_PAY_FAILED)) {
            throw new BusinessException("订单状态不允许支付失败");
        }

        po.setStatus(OrderStatus.ORDER_STATUS_PAY_FAILED);
        po.setPayStatus(OrderStatus.PAY_STATUS_FAILED);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_PAY_FAILED,
                OrderStatus.PAY_STATUS_PAYING, OrderStatus.PAY_STATUS_FAILED, "PAY", "SYSTEM", "系统", reason, null);

        // 释放库存
        releaseStock(po.getId());

        log.info("支付失败处理完成，orderNo={}, reason={}", orderNo, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(String orderNo) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转：待提交可以进入待支付
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_UNPAID)) {
            throw new BusinessException("订单状态不允许确认");
        }

        // 调用库存系统扣减库存（模拟调用）
        deductStock(orderNo);

        // 更新订单状态为待支付
        po.setStatus(OrderStatus.ORDER_STATUS_UNPAID);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_UNPAID,
                po.getPayStatus(), po.getPayStatus(), "CONFIRM", "SYSTEM", "系统", null, null);

        log.info("订单确认完成，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(String orderNo, String logisticsNo) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转：已支付可以进入发货中
        if (po.getStatus() != OrderStatus.ORDER_STATUS_PAID &&
                po.getStatus() != OrderStatus.ORDER_STATUS_DELIVERING) {
            throw new BusinessException("订单状态不允许发货");
        }

        // 更新订单状态为发货中
        if (po.getStatus() == OrderStatus.ORDER_STATUS_PAID) {
            // 如果还没扣减库存，先扣减
            deductStock(orderNo);
        }

        po.setStatus(OrderStatus.ORDER_STATUS_DELIVERING);
        po.setLogisticsNo(logisticsNo);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_DELIVERING,
                po.getPayStatus(), po.getPayStatus(), "SHIP", "SYSTEM", "系统", null, logisticsNo);

        log.info("订单发货完成，orderNo={}, logisticsNo={}", orderNo, logisticsNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(String orderNo) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_COMPLETED)) {
            throw new BusinessException("订单状态不允许确认收货");
        }

        // 更新订单状态为已完成
        po.setStatus(OrderStatus.ORDER_STATUS_COMPLETED);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_COMPLETED,
                po.getPayStatus(), po.getPayStatus(), "RECEIPT", "SYSTEM", "系统", null, null);

        // 发送订单完成消息
        mqMessageService.sendOrderCompleteMessage(po.getId(), po.getOrderNo());

        log.info("订单确认收货完成，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(String orderNo, String reason) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_REFUNDING)) {
            throw new BusinessException("订单状态不允许申请退款");
        }

        // 更新订单状态为退款中
        po.setStatus(OrderStatus.ORDER_STATUS_REFUNDING);
        po.setPayStatus(OrderStatus.PAY_STATUS_REFUNDING);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_REFUNDING,
                po.getPayStatus(), OrderStatus.PAY_STATUS_REFUNDING, "REFUND_APPLY", "SYSTEM", "系统", reason, null);

        log.info("订单申请退款，orderNo={}, reason={}", orderNo, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundSuccess(String orderNo) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_REFUNDED)) {
            throw new BusinessException("订单状态不允许退款完成");
        }

        // 更新订单状态为已退款
        po.setStatus(OrderStatus.ORDER_STATUS_REFUNDED);
        po.setPayStatus(OrderStatus.PAY_STATUS_REFUNDED);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_REFUNDED,
                OrderStatus.PAY_STATUS_REFUNDING, OrderStatus.PAY_STATUS_REFUNDED, "REFUND_SUCCESS", "SYSTEM", "系统", null, null);

        // 恢复库存
        releaseStock(po.getId());

        log.info("订单退款成功，orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundFailed(String orderNo, String reason) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_REFUND_FAILED)) {
            throw new BusinessException("订单状态不允许退款失败");
        }

        // 更新订单状态为退款失败
        po.setStatus(OrderStatus.ORDER_STATUS_REFUND_FAILED);
        po.setPayStatus(OrderStatus.PAY_STATUS_REFUND_FAILED);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_REFUND_FAILED,
                OrderStatus.PAY_STATUS_REFUNDING, OrderStatus.PAY_STATUS_REFUND_FAILED, "REFUND_FAILED", "SYSTEM", "系统", reason, null);

        log.info("订单退款失败，orderNo={}, reason={}", orderNo, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyAfterSale(String orderNo, String reason) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_AFTER_SALE)) {
            throw new BusinessException("订单状态不允许申请售后");
        }

        // 更新订单状态为售后中
        po.setStatus(OrderStatus.ORDER_STATUS_AFTER_SALE);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_AFTER_SALE,
                po.getPayStatus(), po.getPayStatus(), "AFTER_SALE", "SYSTEM", "系统", reason, null);

        log.info("订单申请售后，orderNo={}, reason={}", orderNo, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAfterSale(String orderNo) {
        OrderPO po = getOrderPOByOrderNo(orderNo);

        Integer beforeStatus = po.getStatus();

        // 校验状态流转
        if (!OrderStatus.isValidTransition(beforeStatus, OrderStatus.ORDER_STATUS_COMPLETED)) {
            throw new BusinessException("订单状态不允许完成售后");
        }

        // 更新订单状态为已完成
        po.setStatus(OrderStatus.ORDER_STATUS_COMPLETED);
        po.setUpdateTime(LocalDateTime.now());

        this.updateById(po);

        // 记录状态流水
        orderStatusFlowService.recordStatusChange(po, beforeStatus, OrderStatus.ORDER_STATUS_COMPLETED,
                po.getPayStatus(), po.getPayStatus(), "AFTER_SALE_COMPLETE", "SYSTEM", "系统", null, null);

        log.info("订单售后完成，orderNo={}", orderNo);
    }

    @Override
    public boolean updateStatusWithCheck(Long orderId, Integer expectedStatus, Integer targetStatus) {
        OrderPO po = this.getById(orderId);
        if (po == null) {
            return false;
        }

        // 校验当前状态是否符合预期
        if (!expectedStatus.equals(po.getStatus())) {
            return false;
        }

        // 校验状态流转是否合法
        if (!OrderStatus.isValidTransition(expectedStatus, targetStatus)) {
            return false;
        }

        // 原子更新状态
        return baseMapper.updateStatusWithCheck(orderId, expectedStatus, targetStatus) > 0;
    }

    @Override
    public void releaseStock(Long orderId) {
        log.info("释放订单锁定的库存，orderId={}", orderId);
        // TODO: 实际应调用库存微服务释放库存
        // stockService.releaseStock(orderId);
    }

    @Override
    public OrderPO getOrderPOByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }

        LambdaQueryWrapper<OrderPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderPO::getOrderNo, orderNo);

        OrderPO po = this.getOne(wrapper);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }
        return po;
    }

    @Override
    public List<OrderItemDTO> listOrderItems(Long orderId) {
        return orderItemService.listByOrderId(orderId);
    }

    /**
     * 校验订单DTO数据
     */
    private void validateOrderDTO(OrderDTO dto) {
        if (!StringUtils.hasText(dto.getUserId())) {
            throw new BusinessException("用户ID不能为空");
        }

        if (CollectionUtils.isEmpty(dto.getItems())) {
            throw new BusinessException("订单商品明细不能为空");
        }

        for (OrderItemDTO item : dto.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("商品数量必须大于0");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("商品单价必须大于0");
            }
        }
    }

    /**
     * 计算订单金额
     */
    private void calculateOrderAmount(OrderDTO dto) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO item : dto.getItems()) {
            // 计算单品小计
            BigDecimal itemAmount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setAmount(itemAmount);
            totalAmount = totalAmount.add(itemAmount);
        }

        dto.setTotalAmount(totalAmount);

        // 计算优惠后金额（如果有优惠券）
        BigDecimal discountAmount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount.subtract(discountAmount);

        // 支付金额不能小于0
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
            discountAmount = totalAmount;
        }

        dto.setDiscountAmount(discountAmount);
        dto.setPayAmount(payAmount);
    }

    /**
     * 模拟扣减库存（实际应调用库存微服务）
     */
    private void deductStock(String orderNo) {
        log.info("调用库存系统扣减库存，orderNo={}", orderNo);
        // TODO: 实际实现应调用库存微服务
        // stockService.deductStock(orderNo);
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 转换PO到DTO（不含明细）
     */
    private OrderDTO convertToDTO(OrderPO po) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    /**
     * 转换PO到DTO（包含明细）
     */
    private OrderDTO convertToDTOWithItems(OrderPO po) {
        OrderDTO dto = convertToDTO(po);
        List<OrderItemDTO> items = orderItemService.listByOrderId(po.getId());
        dto.setItems(items);
        return dto;
    }
}