package com.sustar.orderservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 * 用于返回给前端展示订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderNo;

    private String userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Long couponId;

    private BigDecimal discountAmount;

    private Integer status;

    private String statusDesc;

    private Integer payStatus;

    private String payStatusDesc;

    private String payType;

    private LocalDateTime payTime;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    private LocalDateTime createTime;

    /**
     * 订单明细列表
     */
    private List<OrderItemVO> items;
}