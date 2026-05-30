package com.sustar.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款数据传输对象
 * 用于服务间传递退款信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String refundNo;

    private String paymentNo;

    private String orderNo;

    private String userId;

    private BigDecimal refundAmount;

    private String reason;

    private Integer status;

    private String statusDesc;

    private String thirdPartyNo;

    private LocalDateTime refundTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
