package com.sustar.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建支付请求DTO
 * 用于接收前端发起支付的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道：ALIPAY/WECHAT/UNION/HUABEI
     */
    private String payChannel;

    /**
     * 支付方式：PC/H5/APP
     */
    private String payMethod;

    /**
     * 支付标题
     */
    private String subject;

    /**
     * 支付描述
     */
    private String body;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 支付成功回调URL
     */
    private String returnUrl;
}
