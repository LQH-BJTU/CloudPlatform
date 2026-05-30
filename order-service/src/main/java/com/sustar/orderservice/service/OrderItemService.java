package com.sustar.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.orderservice.dto.OrderItemDTO;
import com.sustar.orderservice.po.OrderItemPO;

import java.util.List;

/**
 * 订单明细业务接口
 * 定义订单明细相关的业务方法
 */
public interface OrderItemService extends IService<OrderItemPO> {

    /**
     * 根据订单ID查询订单明细列表
     *
     * @param orderId 订单ID
     * @return 订单明细DTO列表
     */
    List<OrderItemDTO> listByOrderId(Long orderId);

    /**
     * 批量保存订单明细
     *
     * @param orderId    订单ID
     * @param orderNo    订单编号
     * @param itemDTOList 订单明细DTO列表
     */
    void batchSave(Long orderId, String orderNo, List<OrderItemDTO> itemDTOList);

    /**
     * 根据订单ID删除订单明细
     *
     * @param orderId 订单ID
     */
    void deleteByOrderId(Long orderId);
}