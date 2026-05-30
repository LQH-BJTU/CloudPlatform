package com.sustar.orderservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细视图对象
 * 用于返回给前端展示订单明细信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long orderId;

    /**
     * 商品类型：1-ECS套餐 2-镜像 3-其他
     */
    private Integer itemType;

    /**
     * 商品类型描述
     */
    private String itemTypeDesc;

    /**
     * ECS套餐ID
     */
    private Long packageId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 镜像ID
     */
    private Long imageId;

    /**
     * 镜像名称
     */
    private String imageName;

    /**
     * 地域编码
     */
    private String regionCode;

    /**
     * 地域名称
     */
    private String regionName;

    /**
     * 可用区编码
     */
    private String areaCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    private BigDecimal amount;

    /**
     * 计费方式：1-按月 2-按年 3-按需
     */
    private Integer billingType;

    /**
     * 计费方式描述
     */
    private String billingTypeDesc;

    /**
     * 购买时长
     */
    private Integer duration;

    /**
     * 配置信息JSON
     */
    private String configInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}