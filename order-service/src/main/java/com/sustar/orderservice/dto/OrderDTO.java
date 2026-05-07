package com.sustar.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单数据传输对象
 * 用于服务间传递订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderNo;

    private String userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;
}
