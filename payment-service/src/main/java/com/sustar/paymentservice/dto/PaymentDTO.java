package com.sustar.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付数据传输对象
 * 用于服务间传递支付信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String paymentNo;

    private String orderId;

    private String userId;

    private BigDecimal amount;

    private Integer paymentType;
}
