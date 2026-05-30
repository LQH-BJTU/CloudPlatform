package com.sustar.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建退款请求DTO
 * 用于接收前端发起退款的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRefundDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String reason;
}
