package com.sustar.orderservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细表实体类
 * 对应数据库表order_item
 * 存储订单包含的商品明细信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("order_item")
public class OrderItemPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

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
     * 配置信息JSON（如CPU、内存等详情）
     */
    private String configInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}