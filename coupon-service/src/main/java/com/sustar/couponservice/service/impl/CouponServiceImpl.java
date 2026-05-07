package com.sustar.couponservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.couponservice.dto.CouponDTO;
import com.sustar.couponservice.exceptions.BusinessException;
import com.sustar.couponservice.mapper.CouponMapper;
import com.sustar.couponservice.po.CouponPO;
import com.sustar.couponservice.query.CouponQuery;
import com.sustar.couponservice.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券业务实现类
 * 实现优惠券相关的业务逻辑
 */
@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponPO> implements CouponService {

    @Override
    public CouponDTO getCouponById(Long id) {
        CouponPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("优惠券不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<CouponDTO> listCoupons(CouponQuery query) {
        LambdaQueryWrapper<CouponPO> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getCouponName())) {
            wrapper.like(CouponPO::getCouponName, query.getCouponName());
        }
        if (StringUtils.hasText(query.getCouponCode())) {
            wrapper.eq(CouponPO::getCouponCode, query.getCouponCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CouponPO::getStatus, query.getStatus());
        }
        
        wrapper.orderByDesc(CouponPO::getCreateTime);
        
        List<CouponPO> poList = this.list(wrapper);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createCoupon(CouponDTO dto) {
        LambdaQueryWrapper<CouponPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CouponPO::getCouponCode, dto.getCouponCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("优惠券代码已存在");
        }
        
        CouponPO po = new CouponPO();
        BeanUtils.copyProperties(dto, po);
        po.setUsedCount(0);
        po.setStatus(1);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        
        this.save(po);
        log.info("创建优惠券成功，id={}", po.getId());
        return po.getId();
    }

    @Override
    public void updateCoupon(Long id, CouponDTO dto) {
        CouponPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        BeanUtils.copyProperties(dto, po);
        po.setUpdateTime(LocalDateTime.now());
        
        this.updateById(po);
        log.info("更新优惠券成功，id={}", id);
    }

    @Override
    public void deleteCoupon(Long id) {
        CouponPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        this.removeById(id);
        log.info("删除优惠券成功，id={}", id);
    }

    private CouponDTO convertToDTO(CouponPO po) {
        CouponDTO dto = new CouponDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }
}
