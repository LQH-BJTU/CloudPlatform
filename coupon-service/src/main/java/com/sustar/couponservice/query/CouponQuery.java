package com.sustar.couponservice.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 优惠券查询参数对象
 * 用于接收前端查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String couponName;

    private String couponCode;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}
