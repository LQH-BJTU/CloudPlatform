package com.sustar.couponservice.po;

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
 * 优惠券实体类
 * 对应数据库表coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("coupon")
public class CouponPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String couponName;

    private String couponCode;

    private BigDecimal discountAmount;

    private BigDecimal minOrderAmount;

    private Integer totalCount;

    private Integer usedCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
