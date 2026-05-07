package com.sustar.couponservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.couponservice.dto.CouponDTO;
import com.sustar.couponservice.po.CouponPO;
import com.sustar.couponservice.query.CouponQuery;

import java.util.List;

/**
 * 优惠券业务接口
 * 定义优惠券相关的业务方法
 */
public interface CouponService extends IService<CouponPO> {

    CouponDTO getCouponById(Long id);

    List<CouponDTO> listCoupons(CouponQuery query);

    Long createCoupon(CouponDTO dto);

    void updateCoupon(Long id, CouponDTO dto);

    void deleteCoupon(Long id);
}
