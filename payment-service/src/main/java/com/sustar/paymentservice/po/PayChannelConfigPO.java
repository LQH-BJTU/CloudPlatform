package com.sustar.paymentservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付渠道配置实体类
 * 对应数据库表pay_channel_config
 * 存储各支付渠道的配置信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("pay_channel_config")
public class PayChannelConfigPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道编码：ALIPAY/WECHAT/UNION/HUABEI
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道类型：ONLINE-线上 OFFLINE-线下
     */
    private String channelType;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 网关地址
     */
    private String gatewayUrl;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 同步回调地址
     */
    private String returnUrl;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
