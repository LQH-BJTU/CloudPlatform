package com.sustar.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.orderservice.constants.OrderStatus;
import com.sustar.orderservice.mapper.OrderStatusFlowMapper;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.po.OrderStatusFlowPO;
import com.sustar.orderservice.service.OrderStatusFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单状态流水业务实现类
 */
@Slf4j
@Service
public class OrderStatusFlowServiceImpl extends ServiceImpl<OrderStatusFlowMapper, OrderStatusFlowPO> implements OrderStatusFlowService {

    @Override
    public void recordStatusChange(OrderPO order, Integer beforeStatus, Integer afterStatus,
                                   Integer beforePayStatus, Integer afterPayStatus,
                                   String operationType, String operatorId, String operatorName,
                                   String remark, String externalNo) {
        OrderStatusFlowPO flow = OrderStatusFlowPO.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .beforePayStatus(beforePayStatus)
                .afterPayStatus(afterPayStatus)
                .changeDesc(buildChangeDesc(beforeStatus, afterStatus))
                .operationType(operationType)
                .operatorId(operatorId)
                .operatorName(operatorName)
                .remark(remark)
                .externalNo(externalNo)
                .createTime(LocalDateTime.now())
                .build();

        this.save(flow);
        log.info("记录订单状态流水，orderNo={}, beforeStatus={}, afterStatus={}", 
                order.getOrderNo(), beforeStatus, afterStatus);
    }

    @Override
    public List<OrderStatusFlowPO> listByOrderId(Long orderId) {
        return baseMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderStatusFlowPO> listByOrderNo(String orderNo) {
        return baseMapper.selectByOrderNo(orderNo);
    }

    @Override
    public OrderStatusFlowPO getLatestByOrderId(Long orderId) {
        return baseMapper.selectLatestByOrderId(orderId);
    }

    /**
     * 构建状态变更描述
     */
    private String buildChangeDesc(Integer beforeStatus, Integer afterStatus) {
        String beforeDesc = OrderStatus.getOrderStatusDesc(beforeStatus);
        String afterDesc = OrderStatus.getOrderStatusDesc(afterStatus);
        
        if (beforeStatus == null) {
            return "创建订单，状态变为[" + afterDesc + "]";
        }
        return "状态变更：[" + beforeDesc + "] → [" + afterDesc + "]";
    }
}