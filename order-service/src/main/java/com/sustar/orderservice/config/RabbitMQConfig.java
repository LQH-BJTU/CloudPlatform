package com.sustar.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 延迟队列配置
 * 使用死信队列实现延迟消息
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 超时关单相关 ====================

    /**
     * 超时关单交换机
     */
    public static final String ORDER_TIMEOUT_EXCHANGE = "order_timeout_exchange";

    /**
     * 超时关单队列（死信队列）
     */
    public static final String ORDER_TIMEOUT_QUEUE = "order_timeout_queue";

    /**
     * 超时关单路由键
     */
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout.cancel";

    /**
     * 死信交换机
     */
    public static final String ORDER_DEAD_LETTER_EXCHANGE = "order_dead_letter_exchange";

    /**
     * 死信队列
     */
    public static final String ORDER_DEAD_LETTER_QUEUE = "order_dead_letter_queue";

    /**
     * 死信路由键
     */
    public static final String ORDER_DEAD_LETTER_ROUTING_KEY = "order.dead.letter";

    // ==================== 订单支付成功通知 ====================

    /**
     * 订单支付交换机
     */
    public static final String ORDER_PAY_EXCHANGE = "order_pay_exchange";

    /**
     * 订单支付队列
     */
    public static final String ORDER_PAY_QUEUE = "order_pay_queue";

    /**
     * 订单支付路由键
     */
    public static final String ORDER_PAY_ROUTING_KEY = "order.pay.success";

    // ==================== 订单创建成功通知 ====================

    /**
     * 订单创建交换机
     */
    public static final String ORDER_CREATE_EXCHANGE = "order_create_exchange";

    /**
     * 订单创建队列
     */
    public static final String ORDER_CREATE_QUEUE = "order_create_queue";

    /**
     * 订单创建路由键
     */
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create.success";

    // ==================== 死信队列配置 ====================

    /**
     * 死信交换机
     */
    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(ORDER_DEAD_LETTER_EXCHANGE, true, false);
    }

    /**
     * 死信队列
     */
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(ORDER_DEAD_LETTER_QUEUE).build();
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue queue,
                                     @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_DEAD_LETTER_ROUTING_KEY);
    }

    // ==================== 超时关单队列配置（带死信转发）====================

    /**
     * 超时关单交换机
     */
    @Bean("orderTimeoutExchange")
    public DirectExchange orderTimeoutExchange() {
        return new DirectExchange(ORDER_TIMEOUT_EXCHANGE, true, false);
    }

    /**
     * 超时关单队列（配置死信转发）
     */
    @Bean("orderTimeoutQueue")
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE)
                // 死信交换机
                .withArgument("x-dead-letter-exchange", ORDER_DEAD_LETTER_EXCHANGE)
                // 死信路由键
                .withArgument("x-dead-letter-routing-key", ORDER_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    /**
     * 超时关单队列绑定
     */
    @Bean
    public Binding orderTimeoutBinding(@Qualifier("orderTimeoutQueue") Queue queue,
                                       @Qualifier("orderTimeoutExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_TIMEOUT_ROUTING_KEY);
    }

    // ==================== 订单支付队列配置 ====================

    @Bean("orderPayExchange")
    public DirectExchange orderPayExchange() {
        return new DirectExchange(ORDER_PAY_EXCHANGE, true, false);
    }

    @Bean("orderPayQueue")
    public Queue orderPayQueue() {
        return QueueBuilder.durable(ORDER_PAY_QUEUE).build();
    }

    @Bean
    public Binding orderPayBinding(@Qualifier("orderPayQueue") Queue queue,
                                   @Qualifier("orderPayExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_PAY_ROUTING_KEY);
    }

    // ==================== 订单创建队列配置 ====================

    @Bean("orderCreateExchange")
    public DirectExchange orderCreateExchange() {
        return new DirectExchange(ORDER_CREATE_EXCHANGE, true, false);
    }

    @Bean("orderCreateQueue")
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE).build();
    }

    @Bean
    public Binding orderCreateBinding(@Qualifier("orderCreateQueue") Queue queue,
                                      @Qualifier("orderCreateExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_CREATE_ROUTING_KEY);
    }
}