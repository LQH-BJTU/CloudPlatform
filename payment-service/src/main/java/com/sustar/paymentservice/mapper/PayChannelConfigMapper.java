package com.sustar.paymentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.paymentservice.po.PayChannelConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付渠道配置数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface PayChannelConfigMapper extends BaseMapper<PayChannelConfigPO> {

    /**
     * 根据渠道编码查询
     *
     * @param channelCode 渠道编码
     * @return 渠道配置
     */
    PayChannelConfigPO selectByChannelCode(@Param("channelCode") String channelCode);

    /**
     * 查询所有启用的渠道配置
     *
     * @return 渠道配置列表
     */
    List<PayChannelConfigPO> selectAllEnabled();
}
