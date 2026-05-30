package com.sustar.orderservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单状态流水表实体类
 * 记录订单状态变更历史，用于追溯、审计、故障排查
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("order_status_flow")
public class OrderStatusFlowPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 变更前状态
     */
    private Integer beforeStatus;

    /**
     * 变更后状态
     */
    private Integer afterStatus;

    /**
     * 变更前支付状态
     */
    private Integer beforePayStatus;

    /**
     * 变更后支付状态
     */
    private Integer afterPayStatus;

    /**
     * 状态变更描述
     */
    private String changeDesc;

    /**
     * 操作类型：CREATE-创建 ORDER_CANCEL-取消 PAY-支付 SHIP-发货 RECEIPT-确认收货 
     * REFUND_APPLY-申请退款 REFUND_SUCCESS-退款成功 REFUND_FAILED-退款失败 AFTER_SALE-售后
     */
    private String operationType;

    /**
     * 操作人ID（系统操作为SYSTEM）
     */
    private String operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作备注（失败原因等）
     */
    private String remark;

    /**
     * 外部流水号（支付流水号、物流单号等）
     */
    private String externalNo;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 操作耗时（毫秒）
     */
    private Long durationMs;
}