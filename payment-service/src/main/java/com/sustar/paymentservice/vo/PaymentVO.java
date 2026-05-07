package com.sustar.paymentservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付视图对象
 * 用于返回给前端展示支付信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String paymentNo;

    private String orderId;

    private String userId;

    private BigDecimal amount;

    private Integer paymentType;

    private Integer status;

    private String transactionId;

    private LocalDateTime paymentTime;

    private LocalDateTime createTime;
}
