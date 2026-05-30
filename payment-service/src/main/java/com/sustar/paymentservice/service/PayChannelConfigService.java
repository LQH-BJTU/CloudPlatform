package com.sustar.paymentservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.paymentservice.po.PayChannelConfigPO;

import java.util.List;

/**
 * 支付渠道配置业务接口
 * 定义支付渠道配置相关的业务方法
 */
public interface PayChannelConfigService extends IService<PayChannelConfigPO> {

    /**
     * 根据渠道编码查询配置
     *
     * @param channelCode 渠道编码
     * @return 渠道配置
     */
    PayChannelConfigPO getByChannelCode(String channelCode);

    /**
     * 查询所有启用的渠道配置
     *
     * @return 渠道配置列表
     */
    List<PayChannelConfigPO> listAllEnabled();

    /**
     * 启用渠道
     *
     * @param channelCode 渠道编码
     * @return 是否成功
     */
    boolean enableChannel(String channelCode);

    /**
     * 禁用渠道
     *
     * @param channelCode 渠道编码
     * @return 是否成功
     */
    boolean disableChannel(String channelCode);
}
