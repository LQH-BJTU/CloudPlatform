package com.sustar.paymentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.paymentservice.exceptions.BusinessException;
import com.sustar.paymentservice.mapper.PayChannelConfigMapper;
import com.sustar.paymentservice.po.PayChannelConfigPO;
import com.sustar.paymentservice.service.PayChannelConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付渠道配置业务实现类
 * 实现支付渠道配置相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayChannelConfigServiceImpl extends ServiceImpl<PayChannelConfigMapper, PayChannelConfigPO> implements PayChannelConfigService {

    @Override
    public PayChannelConfigPO getByChannelCode(String channelCode) {
        if (!StringUtils.hasText(channelCode)) {
            throw new BusinessException("渠道编码不能为空");
        }
        return baseMapper.selectByChannelCode(channelCode);
    }

    @Override
    public List<PayChannelConfigPO> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableChannel(String channelCode) {
        if (!StringUtils.hasText(channelCode)) {
            throw new BusinessException("渠道编码不能为空");
        }
        PayChannelConfigPO config = baseMapper.selectByChannelCode(channelCode);
        if (config == null) {
            throw new BusinessException("渠道配置不存在");
        }
        config.setStatus(1);
        config.setUpdateTime(LocalDateTime.now());
        this.updateById(config);
        log.info("启用支付渠道成功，channelCode={}", channelCode);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableChannel(String channelCode) {
        if (!StringUtils.hasText(channelCode)) {
            throw new BusinessException("渠道编码不能为空");
        }
        PayChannelConfigPO config = baseMapper.selectByChannelCode(channelCode);
        if (config == null) {
            throw new BusinessException("渠道配置不存在");
        }
        config.setStatus(0);
        config.setUpdateTime(LocalDateTime.now());
        this.updateById(config);
        log.info("禁用支付渠道成功，channelCode={}", channelCode);
        return true;
    }
}
