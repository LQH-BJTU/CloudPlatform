package com.sustar.couponservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券视图对象
 * 用于返回给前端展示优惠券信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String couponName;

    private String couponCode;

    private BigDecimal discountAmount;

    private BigDecimal minOrderAmount;

    private Integer totalCount;

    private Integer usedCount;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createTime;
}
