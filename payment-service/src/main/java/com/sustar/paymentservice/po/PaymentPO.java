package com.sustar.paymentservice.po;

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
 * 支付记录实体类
 * 对应数据库表payment_record
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("payment_record")
public class PaymentPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paymentNo;

    private String orderId;

    private String userId;

    private BigDecimal amount;

    private Integer paymentType;

    private Integer status;

    private String transactionId;

    private LocalDateTime paymentTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
