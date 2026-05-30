package com.sustar.paymentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.paymentservice.constants.PayStatus;
import com.sustar.paymentservice.dto.CreatePaymentDTO;
import com.sustar.paymentservice.dto.PayResponseDTO;
import com.sustar.paymentservice.dto.PaymentDTO;
import com.sustar.paymentservice.exceptions.BusinessException;
import com.sustar.paymentservice.mapper.PaymentMapper;
import com.sustar.paymentservice.po.PaymentPO;
import com.sustar.paymentservice.service.PaymentService;
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
 * 支付业务实现类
 * 实现支付相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentPO> implements PaymentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResponseDTO createPayment(CreatePaymentDTO dto) {
        // 1. 参数校验
        validateCreatePaymentDTO(dto);

        // 2. 检查是否已存在待支付的记录
        List<PaymentPO> existingPayments = baseMapper.selectByOrderNo(dto.getOrderNo());
        for (PaymentPO existing : existingPayments) {
            if (existing.getStatus() == PayStatus.PAY_STATUS_PENDING) {
                // 返回已有的支付记录
                log.info("订单已存在待支付记录，orderNo={}, paymentNo={}", dto.getOrderNo(), existing.getPaymentNo());
                return buildPayResponse(existing);
            }
        }

        // 3. 创建支付记录
        PaymentPO payment = new PaymentPO();
        payment.setPaymentNo(PaymentNoUtil.generatePaymentNo());
        payment.setOrderNo(dto.getOrderNo());
        payment.setOrderId(0L); // 实际应从订单服务获取
        payment.setUserId(dto.getUserId());
        payment.setAmount(dto.getAmount());
        payment.setPayChannel(dto.getPayChannel());
        payment.setPayMethod(dto.getPayMethod());
        payment.setStatus(PayStatus.PAY_STATUS_PENDING);
        payment.setSubject(dto.getSubject());
        payment.setBody(dto.getBody());
        payment.setClientIp(dto.getClientIp());
        payment.setReturnUrl(dto.getReturnUrl());
        payment.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 默认30分钟过期
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        this.save(payment);
        log.info("创建支付记录成功，paymentNo={}, orderNo={}, amount={}",
                payment.getPaymentNo(), payment.getOrderNo(), payment.getAmount());

        // 4. 构建支付响应
        return buildPayResponse(payment);
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        PaymentPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("支付记录不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public PaymentDTO getPaymentByPaymentNo(String paymentNo) {
        if (!StringUtils.hasText(paymentNo)) {
            throw new BusinessException("支付流水号不能为空");
        }
        PaymentPO po = baseMapper.selectByPaymentNo(paymentNo);
        if (po == null) {
            throw new BusinessException("支付记录不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<PaymentDTO> listPaymentsByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }
        List<PaymentPO> poList = baseMapper.selectByOrderNo(orderNo);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePayNotify(String channel, Map<String, String> params) {
        // TODO: 实现支付回调处理
        // 1. 验签
        // 2. 幂等校验
        // 3. 更新支付状态
        // 4. 通知订单服务
        log.info("收到支付回调，channel={}, params={}", channel, params);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePayReturn(String channel, Map<String, String> params) {
        // TODO: 实现支付同步回调处理
        log.info("收到支付同步回调，channel={}, params={}", channel, params);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closePayment(String paymentNo) {
        PaymentPO payment = baseMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 只能关闭待支付或支付中的订单
        if (payment.getStatus() != PayStatus.PAY_STATUS_PENDING
                && payment.getStatus() != PayStatus.PAY_STATUS_PROCESSING) {
            throw new BusinessException("当前支付状态不允许关闭");
        }

        payment.setStatus(PayStatus.PAY_STATUS_CLOSED);
        payment.setUpdateTime(LocalDateTime.now());
        this.updateById(payment);

        log.info("关闭支付订单成功，paymentNo={}", paymentNo);
        return true;
    }

    @Override
    public Integer queryPayStatus(String paymentNo) {
        PaymentPO payment = baseMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }
        return payment.getStatus();
    }

    /**
     * 校验创建支付请求参数
     */
    private void validateCreatePaymentDTO(CreatePaymentDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (!StringUtils.hasText(dto.getOrderNo())) {
            throw new BusinessException("订单编号不能为空");
        }
        if (!StringUtils.hasText(dto.getUserId())) {
            throw new BusinessException("用户ID不能为空");
        }
        if (dto.getAmount() == null || dto.getAmount().doubleValue() <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }
        if (!StringUtils.hasText(dto.getPayChannel())) {
            throw new BusinessException("支付渠道不能为空");
        }
    }

    /**
     * 构建支付响应
     */
    private PayResponseDTO buildPayResponse(PaymentPO payment) {
        // TODO: 实际应调用第三方支付SDK生成支付表单或参数
        // 这里模拟返回支付表单
        String payForm = String.format(
                "<form action=\"/mock/pay\" method=\"post\">" +
                        "<input type=\"hidden\" name=\"paymentNo\" value=\"%s\"/>" +
                        "<input type=\"hidden\" name=\"amount\" value=\"%s\"/>" +
                        "<input type=\"hidden\" name=\"channel\" value=\"%s\"/>" +
                        "</form>",
                payment.getPaymentNo(), payment.getAmount(), payment.getPayChannel());

        return PayResponseDTO.builder()
                .paymentNo(payment.getPaymentNo())
                .payChannel(payment.getPayChannel())
                .payForm(payForm)
                .expireSeconds(1800) // 30分钟
                .build();
    }

    /**
     * 转换为DTO
     */
    private PaymentDTO convertToDTO(PaymentPO po) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(po, dto);
        dto.setStatusDesc(PayStatus.getPayStatusDesc(po.getStatus()));
        return dto;
    }
}
