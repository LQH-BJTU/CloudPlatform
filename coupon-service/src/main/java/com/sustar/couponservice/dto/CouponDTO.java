package com.sustar.couponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券数据传输对象
 * 用于服务间传递优惠券信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String couponName;

    private String couponCode;

    private BigDecimal discountAmount;

    private BigDecimal minOrderAmount;

    private Integer totalCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
