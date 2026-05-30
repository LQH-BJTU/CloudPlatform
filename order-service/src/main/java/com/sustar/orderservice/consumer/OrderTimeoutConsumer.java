package com.sustar.orderservice.consumer;

import com.sustar.orderservice.config.RabbitMQConfig;
import com.sustar.orderservice.constants.OrderStatus;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 订单超时消息消费者
 * 处理超时未支付订单的自动关闭
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    /**
     * 消费死信队列消息（订单超时）
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_DEAD_LETTER_QUEUE)
    public void consumeDeadLetter(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, 
                    new TypeReference<Map<String, Object>>() {});
            
            Long orderId = ((Number) data.get("orderId")).longValue();
            String type = (String) data.get("type");

            log.info("收到订单超时消息，orderId={}, type={}", orderId, type);

            // 查询订单
            OrderPO order = orderService.getById(orderId);
            if (order == null) {
                log.warn("订单不存在，orderId={}", orderId);
                return;
            }

            // 如果不是待支付状态，直接忽略
            if (!OrderStatus.ORDER_STATUS_UNPAID.equals(order.getStatus())) {
                log.info("订单状态不是待支付，忽略处理，orderId={}, status={}", 
                        orderId, order.getStatus());
                return;
            }

            // 原子更新状态：待支付 → 已取消
            boolean success = orderService.updateStatusWithCheck(orderId, 
                    OrderStatus.ORDER_STATUS_UNPAID, 
                    OrderStatus.ORDER_STATUS_CANCELLED);

            if (success) {
                log.info("超时关单成功，orderId={}, orderNo={}", orderId, order.getOrderNo());
                // 释放库存
                orderService.releaseStock(orderId);
            } else {
                log.warn("超时关单失败，可能已被其他线程处理，orderId={}", orderId);
            }

        } catch (JsonProcessingException e) {
            log.error("解析订单超时消息失败，message={}", message, e);
        } catch (Exception e) {
            log.error("处理订单超时消息异常，message={}", message, e);
        }
    }

    /**
     * 消费订单支付成功消息
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_PAY_QUEUE)
    public void consumeOrderPay(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message,
                    new TypeReference<Map<String, Object>>() {});

            Long orderId = ((Number) data.get("orderId")).longValue();
            String orderNo = (String) data.get("orderNo");

            log.info("收到订单支付成功消息，orderId={}, orderNo={}", orderId, orderNo);

            // 执行订单确认逻辑（扣减库存）
            orderService.confirmOrder(orderNo);

        } catch (JsonProcessingException e) {
            log.error("解析订单支付消息失败，message={}", message, e);
        } catch (Exception e) {
            log.error("处理订单支付消息异常，message={}", message, e);
        }
    }

    /**
     * 消费订单创建成功消息
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATE_QUEUE)
    public void consumeOrderCreate(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message,
                    new TypeReference<Map<String, Object>>() {});

            Long orderId = ((Number) data.get("orderId")).longValue();
            String orderNo = (String) data.get("orderNo");

            log.info("收到订单创建成功消息，orderId={}, orderNo={}", orderId, orderNo);

            // 后续可以添加：消息推送、积分预扣、活动记录等

        } catch (JsonProcessingException e) {
            log.error("解析订单创建消息失败，message={}", message, e);
        } catch (Exception e) {
            log.error("处理订单创建消息异常，message={}", message, e);
        }
    }
}