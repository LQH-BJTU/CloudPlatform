package com.sustar.orderservice.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单查询参数对象
 * 用于接收前端查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String userId;

    private Integer status;

    private Integer payStatus;

    private Integer pageNum;

    private Integer pageSize;
}