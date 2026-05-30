package com.sustar.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 支付响应DTO
 * 用于返回支付表单或支付参数给前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 支付表单HTML（支付宝PC/H5支付）
     */
    private String payForm;

    /**
     * 支付参数（微信JSAPI/APP支付）
     */
    private Object payParams;

    /**
     * 支付二维码URL（扫码支付）
     */
    private String qrCodeUrl;

    /**
     * 支付跳转URL
     */
    private String payUrl;

    /**
     * 过期时间（秒）
     */
    private Integer expireSeconds;
}
