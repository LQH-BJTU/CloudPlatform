package com.sustar.paymentservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.exceptions.BusinessException;
import com.sustar.paymentservice.mapper.PaymentMapper;
import com.sustar.paymentservice.po.PaymentPO;
import com.sustar.paymentservice.query.PaymentQuery;
import com.sustar.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 支付业务实现类
 * 实现支付相关的业务逻辑
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentPO> implements PaymentService {

    @Override
    public PaymentDTO getPaymentById(Long id) {
        PaymentPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("支付记录不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<PaymentDTO> listPayments(PaymentQuery query) {
        LambdaQueryWrapper<PaymentPO> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getPaymentNo())) {
            wrapper.eq(PaymentPO::getPaymentNo, query.getPaymentNo());
        }
        if (StringUtils.hasText(query.getOrderId())) {
            wrapper.eq(PaymentPO::getOrderId, query.getOrderId());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(PaymentPO::getUserId, query.getUserId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(PaymentPO::getStatus, query.getStatus());
        }
        
        wrapper.orderByDesc(PaymentPO::getCreateTime);
        
        List<PaymentPO> poList = this.list(wrapper);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createPayment(PaymentDTO dto) {
        PaymentPO po = new PaymentPO();
        BeanUtils.copyProperties(dto, po);
        po.setPaymentNo(generatePaymentNo());
        po.setStatus(0);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        
        this.save(po);
        log.info("创建支付记录成功，id={}, paymentNo={}", po.getId(), po.getPaymentNo());
        return po.getId();
    }

    @Override
    public void updatePayment(Long id, PaymentDTO dto) {
        PaymentPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("支付记录不存在");
        }
        
        BeanUtils.copyProperties(dto, po, "paymentNo");
        po.setUpdateTime(LocalDateTime.now());
        
        this.updateById(po);
        log.info("更新支付记录成功，id={}", id);
    }

    @Override
    public void deletePayment(Long id) {
        PaymentPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("支付记录不存在");
        }
        
        this.removeById(id);
        log.info("删除支付记录成功，id={}", id);
    }

    private PaymentDTO convertToDTO(PaymentPO po) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
