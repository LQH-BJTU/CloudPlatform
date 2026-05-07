package com.sustar.couponservice.controller;

import com.sustar.couponservice.dto.CouponDTO;
import com.sustar.couponservice.query.CouponQuery;
import com.sustar.couponservice.service.CouponService;
import com.sustar.couponservice.vo.CouponVO;
import com.sustar.couponservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券控制器
 * 处理优惠券相关的HTTP请求
 */
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/{id}")
    public Result<CouponVO> getCoupon(@PathVariable Long id) {
        CouponDTO dto = couponService.getCouponById(id);
        return Result.success(convertToVO(dto));
    }

    @GetMapping("/list")
    public Result<List<CouponVO>> listCoupons(CouponQuery query) {
        List<CouponDTO> dtoList = couponService.listCoupons(query);
        List<CouponVO> voList = dtoList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping
    public Result<Long> createCoupon(@RequestBody CouponDTO dto) {
        Long id = couponService.createCoupon(dto);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateCoupon(@PathVariable Long id, @RequestBody CouponDTO dto) {
        couponService.updateCoupon(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return Result.success();
    }

    private CouponVO convertToVO(CouponDTO dto) {
        CouponVO vo = new CouponVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}
