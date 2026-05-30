package com.sustar.paymentservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水实体类
 * 对应数据库表payment_record
 * 记录每笔支付的完整生命周期
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("payment_record")
public class PaymentPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道：ALIPAY-支付宝 WECHAT-微信 UNION-银联 HUABEI-花呗
     */
    private String payChannel;

    /**
     * 支付方式：PC-电脑端 H5-手机端 APP-应用端
     */
    private String payMethod;

    /**
     * 支付状态：0-待支付 1-支付中 2-支付成功 3-支付失败 4-已关闭
     */
    private Integer status;

    /**
     * 支付标题
     */
    private String subject;

    /**
     * 支付描述
     */
    private String body;

    /**
     * 第三方支付流水号
     */
    private String thirdPartyNo;

    /**
     * 第三方支付响应
     */
    private String thirdPartyResponse;

    /**
     * 实际支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 支付成功回调URL
     */
    private String returnUrl;

    /**
     * 异步通知URL
     */
    private String notifyUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
