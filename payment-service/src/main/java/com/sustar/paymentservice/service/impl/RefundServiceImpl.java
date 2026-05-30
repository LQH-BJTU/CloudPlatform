package com.sustar.paymentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.paymentservice.constants.PayStatus;
import com.sustar.paymentservice.dto.CreateRefundDTO;
import com.sustar.paymentservice.dto.RefundDTO;
import com.sustar.paymentservice.exceptions.BusinessException;
import com.sustar.paymentservice.mapper.RefundMapper;
import com.sustar.paymentservice.po.RefundPO;
import com.sustar.paymentservice.service.RefundService;
import com.sustar.paymentservice.util.PaymentNoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 退款业务实现类
 * 实现退款相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl extends ServiceImpl<RefundMapper, RefundPO> implements RefundService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundDTO createRefund(CreateRefundDTO dto) {
        // 1. 参数校验
        validateCreateRefundDTO(dto);

        // 2. 创建退款记录
        RefundPO refund = new RefundPO();
        refund.setRefundNo(PaymentNoUtil.generateRefundNo());
        refund.setPaymentNo(dto.getPaymentNo());
        refund.setOrderNo(dto.getOrderNo());
        refund.setUserId(dto.getUserId());
        refund.setRefundAmount(dto.getRefundAmount());
        refund.setReason(dto.getReason());
        refund.setStatus(PayStatus.REFUND_STATUS_PROCESSING);
        refund.setCreateTime(LocalDateTime.now());
        refund.setUpdateTime(LocalDateTime.now());

        this.save(refund);
        log.info("创建退款记录成功，refundNo={}, paymentNo={}, amount={}",
                refund.getRefundNo(), refund.getPaymentNo(), refund.getRefundAmount());

        // TODO: 调用第三方支付退款接口

        return convertToDTO(refund);
    }

    @Override
    public RefundDTO getRefundById(Long id) {
        RefundPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("退款记录不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public RefundDTO getRefundByRefundNo(String refundNo) {
        if (!StringUtils.hasText(refundNo)) {
            throw new BusinessException("退款流水号不能为空");
        }
        RefundPO po = baseMapper.selectByRefundNo(refundNo);
        if (po == null) {
            throw new BusinessException("退款记录不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<RefundDTO> listRefundsByPaymentNo(String paymentNo) {
        if (!StringUtils.hasText(paymentNo)) {
            throw new BusinessException("支付流水号不能为空");
        }
        List<RefundPO> poList = baseMapper.selectByPaymentNo(paymentNo);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RefundDTO> listRefundsByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }
        List<RefundPO> poList = baseMapper.selectByOrderNo(orderNo);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRefundNotify(String channel, Map<String, String> params) {
        // TODO: 实现退款回调处理
        log.info("收到退款回调，channel={}, params={}", channel, params);
        return true;
    }

    @Override
    public Integer queryRefundStatus(String refundNo) {
        RefundPO refund = baseMapper.selectByRefundNo(refundNo);
        if (refund == null) {
            throw new BusinessException("退款记录不存在");
        }
        return refund.getStatus();
    }

    /**
     * 校验创建退款请求参数
     */
    private void validateCreateRefundDTO(CreateRefundDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (!StringUtils.hasText(dto.getPaymentNo())) {
            throw new BusinessException("支付流水号不能为空");
        }
        if (!StringUtils.hasText(dto.getOrderNo())) {
            throw new BusinessException("订单编号不能为空");
        }
        if (!StringUtils.hasText(dto.getUserId())) {
            throw new BusinessException("用户ID不能为空");
        }
        if (dto.getRefundAmount() == null || dto.getRefundAmount().doubleValue() <= 0) {
            throw new BusinessException("退款金额必须大于0");
        }
    }

    /**
     * 转换为DTO
     */
    private RefundDTO convertToDTO(RefundPO po) {
        RefundDTO dto = new RefundDTO();
        BeanUtils.copyProperties(po, dto);
        dto.setStatusDesc(PayStatus.getRefundStatusDesc(po.getStatus()));
        return dto;
    }
}
