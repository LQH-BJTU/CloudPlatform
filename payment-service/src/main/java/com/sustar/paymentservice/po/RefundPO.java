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
 * 退款记录实体类
 * 对应数据库表refund_record
 * 记录每笔退款的完整生命周期
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("refund_record")
public class RefundPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 退款流水号
     */
    private String refundNo;

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

    /**
     * 退款状态：0-退款中 1-退款成功 2-退款失败
     */
    private Integer status;

    /**
     * 第三方退款流水号
     */
    private String thirdPartyNo;

    /**
     * 第三方退款响应
     */
    private String thirdPartyResponse;

    /**
     * 实际退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
