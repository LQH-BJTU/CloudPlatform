package com.sustar.orderservice.service.impl;

import com.sustar.orderservice.config.RabbitMQConfig;
import com.sustar.orderservice.service.MqMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * MQ消息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqMessageServiceImpl implements MqMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendOrderTimeoutDelayMessage(Long orderId, Integer delaySeconds) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("type", "ORDER_TIMEOUT_CANCEL");
            message.put("delaySeconds", delaySeconds);

            MessageProperties properties = new MessageProperties();
            // 设置延迟时间（毫秒）
            properties.setExpiration(String.valueOf(delaySeconds * 1000));

            Message msg = new Message(objectMapper.writeValueAsBytes(message), properties);

            rabbitTemplate.send(RabbitMQConfig.ORDER_TIMEOUT_EXCHANGE,
                    RabbitMQConfig.ORDER_TIMEOUT_ROUTING_KEY, msg);

            log.info("发送订单超时延迟消息成功，orderId={}, delaySeconds={}", orderId, delaySeconds);
        } catch (JsonProcessingException e) {
            log.error("发送订单超时延迟消息失败，orderId={}", orderId, e);
            throw new RuntimeException("发送延迟消息失败", e);
        }
    }

    @Override
    public void sendOrderCreateMessage(Long orderId, String orderNo, String userId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("orderNo", orderNo);
            message.put("userId", userId);
            message.put("type", "ORDER_CREATE");

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_CREATE_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATE_ROUTING_KEY, message);

            log.info("发送订单创建成功消息，orderId={}, orderNo={}", orderId, orderNo);
        } catch (Exception e) {
            log.error("发送订单创建消息失败，orderId={}", orderId, e);
        }
    }

    @Override
    public void sendOrderPaySuccessMessage(Long orderId, String orderNo, BigDecimal payAmount) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("orderNo", orderNo);
            message.put("payAmount", payAmount);
            message.put("type", "ORDER_PAY_SUCCESS");

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_PAY_EXCHANGE,
                    RabbitMQConfig.ORDER_PAY_ROUTING_KEY, message);

            log.info("发送订单支付成功消息，orderId={}, orderNo={}", orderId, orderNo);
        } catch (Exception e) {
            log.error("发送订单支付消息失败，orderId={}", orderId, e);
        }
    }

    @Override
    public void sendOrderCancelMessage(Long orderId, String orderNo) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("orderNo", orderNo);
            message.put("type", "ORDER_CANCEL");

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_CREATE_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATE_ROUTING_KEY, message);

            log.info("发送订单取消消息，orderId={}, orderNo={}", orderId, orderNo);
        } catch (Exception e) {
            log.error("发送订单取消消息失败，orderId={}", orderId, e);
        }
    }

    @Override
    public void sendOrderCompleteMessage(Long orderId, String orderNo) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("orderNo", orderNo);
            message.put("type", "ORDER_COMPLETE");

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_CREATE_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATE_ROUTING_KEY, message);

            log.info("发送订单完成消息，orderId={}, orderNo={}", orderId, orderNo);
        } catch (Exception e) {
            log.error("发送订单完成消息失败，orderId={}", orderId, e);
        }
    }
}