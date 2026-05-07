package com.sustar.paymentservice.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 支付查询参数对象
 * 用于接收前端查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String paymentNo;

    private String orderId;

    private String userId;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}
