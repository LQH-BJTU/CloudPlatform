package com.sustar.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.query.OrderQuery;

import java.util.List;

/**
 * 订单业务接口
 * 定义订单相关的业务方法
 */
public interface OrderService extends IService<OrderPO> {

    OrderDTO getOrderById(Long id);

    List<OrderDTO> listOrders(OrderQuery query);

    Long createOrder(OrderDTO dto);

    void updateOrder(Long id, OrderDTO dto);

    void deleteOrder(Long id);
}
