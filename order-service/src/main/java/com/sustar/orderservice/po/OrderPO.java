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
 * 订单主表实体类
 * 对应数据库表order_info
 * 存储订单的基本信息和状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("order_info")
public class OrderPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Long couponId;

    private BigDecimal discountAmount;

    private Integer status;

    private Integer payStatus;

    private String payType;

    private LocalDateTime payTime;

    private String logisticsNo;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}