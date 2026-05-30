package com.sustar.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细数据传输对象
 * 用于服务间传递订单明细信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long orderId;

    /**
     * 商品类型：1-ECS套餐 2-镜像 3-其他
     */
    private Integer itemType;

    /**
     * 商品/套餐ID
     */
    private String itemId;

    /**
     * 商品/套餐名称
     */
    private String itemName;

    /**
     * ECS套餐ID
     */
    private String packageId;

    /**
     * 镜像ID
     */
    private String imageId;

    /**
     * 地域编码
     */
    private String regionCode;

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
     * 购买时长
     */
    private Integer duration;

    /**
     * 配置信息JSON
     */
    private String configInfo;

    /**
     * 备注
     */
    private String remark;
}