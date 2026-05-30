package com.sustar.orderservice.service;

import com.sustar.orderservice.OrderServiceApplication;
import com.sustar.orderservice.constants.OrderStatus;
import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.dto.OrderItemDTO;
import com.sustar.orderservice.exceptions.BusinessException;
import com.sustar.orderservice.query.OrderQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单服务单元测试
 */
@SpringBootTest(classes = OrderServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    private OrderDTO createTestOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.setUserId("testUser001");
        dto.setReceiverName("张三");
        dto.setReceiverPhone("13800138000");
        dto.setReceiverAddress("北京市朝阳区");

        List<OrderItemDTO> items = new ArrayList<>();
        OrderItemDTO item = new OrderItemDTO();
        item.setItemId("pkg001");
        item.setItemName("云服务器ECS");
        item.setItemType(1);
        item.setBillingType(2);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("467.04"));
        items.add(item);
        dto.setItems(items);

        return dto;
    }

    @Test
    @DisplayName("创建订单 - 成功")
    void createOrder_Success() {
        OrderDTO dto = createTestOrderDTO();

        Long orderId = orderService.createOrder(dto);

        assertNotNull(orderId);
        assertTrue(orderId > 0);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        assertNotNull(createdOrder);
        assertEquals(dto.getUserId(), createdOrder.getUserId());
        assertEquals(OrderStatus.ORDER_STATUS_PENDING, createdOrder.getStatus());
        assertEquals(OrderStatus.PAY_STATUS_UNPAID, createdOrder.getPayStatus());
    }

    @Test
    @DisplayName("创建订单 - 用户ID为空")
    void createOrder_UserIdEmpty() {
        OrderDTO dto = createTestOrderDTO();
        dto.setUserId(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(dto);
        });
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("创建订单 - 商品数量为0")
    void createOrder_QuantityZero() {
        OrderDTO dto = createTestOrderDTO();
        dto.getItems().get(0).setQuantity(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(dto);
        });
        assertEquals("商品数量必须大于0", exception.getMessage());
    }

    @Test
    @DisplayName("查询订单 - 按ID查询")
    void getOrderById_Success() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO foundOrder = orderService.getOrderById(orderId);

        assertNotNull(foundOrder);
        assertEquals(orderId, foundOrder.getId());
        assertEquals(dto.getUserId(), foundOrder.getUserId());
    }

    @Test
    @DisplayName("查询订单 - 按ID查询不存在")
    void getOrderById_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.getOrderById(99999L);
        });
        assertEquals("订单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("查询订单 - 按订单号查询")
    void getOrderByOrderNo_Success() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        String orderNo = createdOrder.getOrderNo();

        OrderDTO foundOrder = orderService.getOrderByOrderNo(orderNo);

        assertNotNull(foundOrder);
        assertEquals(orderNo, foundOrder.getOrderNo());
    }

    @Test
    @DisplayName("查询订单列表 - 按用户ID查询")
    void listOrders_ByUserId() {
        // 创建两个订单
        OrderDTO dto1 = createTestOrderDTO();
        dto1.setUserId("user001");
        orderService.createOrder(dto1);

        OrderDTO dto2 = createTestOrderDTO();
        dto2.setUserId("user001");
        orderService.createOrder(dto2);

        OrderQuery query = new OrderQuery();
        query.setUserId("user001");

        List<OrderDTO> orders = orderService.listOrders(query);

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("更新订单 - 成功")
    void updateOrder_Success() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        // 获取订单编号
        OrderDTO createdOrder = orderService.getOrderById(orderId);
        String orderNo = createdOrder.getOrderNo();

        OrderDTO updateDto = new OrderDTO();
        updateDto.setReceiverName("李四");
        updateDto.setReceiverPhone("13900139000");
        updateDto.setReceiverAddress("上海市浦东新区");

        orderService.updateOrder(orderId, updateDto);

        // 使用订单编号查询来验证更新
        OrderDTO updatedOrder = orderService.getOrderByOrderNo(orderNo);
        assertEquals("李四", updatedOrder.getReceiverName());
        assertEquals("13900139000", updatedOrder.getReceiverPhone());
        assertEquals("上海市浦东新区", updatedOrder.getReceiverAddress());
    }

    @Test
    @DisplayName("更新订单 - 已支付订单不允许修改")
    void updateOrder_PaidOrder() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        String orderNo = createdOrder.getOrderNo();

        // 确认订单进入待支付状态
        orderService.confirmOrder(orderNo);
        // 支付处理中
        orderService.handlePaying(orderNo);
        // 支付成功
        orderService.handlePaySuccess(orderNo, "ALIPAY");

        OrderDTO updateDto = new OrderDTO();
        updateDto.setReceiverName("李四");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.updateOrder(orderId, updateDto);
        });
        assertEquals("已支付订单不允许修改", exception.getMessage());
    }

    @Test
    @DisplayName("删除订单 - 成功")
    void deleteOrder_Success() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        orderService.deleteOrder(orderId);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.getOrderById(orderId);
        });
        assertEquals("订单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("删除订单 - 已支付订单不允许删除")
    void deleteOrder_PaidOrder() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        String orderNo = createdOrder.getOrderNo();

        // 确认订单进入待支付状态
        orderService.confirmOrder(orderNo);
        // 支付处理中
        orderService.handlePaying(orderNo);
        // 支付成功
        orderService.handlePaySuccess(orderNo, "ALIPAY");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.deleteOrder(orderId);
        });
        assertEquals("已支付订单不允许删除", exception.getMessage());
    }

    @Test
    @DisplayName("取消订单 - 待确认状态")
    void cancelOrder_Pending() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_PENDING, createdOrder.getStatus());

        orderService.cancelOrder(orderId);

        OrderDTO cancelledOrder = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_CANCELLED, cancelledOrder.getStatus());
    }

    @Test
    @DisplayName("取消订单 - 待支付状态")
    void cancelOrder_Unpaid() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        String orderNo = createdOrder.getOrderNo();

        // 确认订单进入待支付状态
        orderService.confirmOrder(orderNo);

        OrderDTO unpaidOrder = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_UNPAID, unpaidOrder.getStatus());

        orderService.cancelOrder(orderId);

        OrderDTO cancelledOrder = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_CANCELLED, cancelledOrder.getStatus());
    }

    @Test
    @DisplayName("取消订单 - 已支付订单不允许取消")
    void cancelOrder_PaidOrder() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        OrderDTO createdOrder = orderService.getOrderById(orderId);
        String orderNo = createdOrder.getOrderNo();

        // 确认订单进入待支付状态
        orderService.confirmOrder(orderNo);
        // 支付处理中
        orderService.handlePaying(orderNo);
        // 支付成功
        orderService.handlePaySuccess(orderNo, "ALIPAY");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(orderId);
        });
        assertEquals("只有待确认或待支付订单才能取消", exception.getMessage());
    }

    @Test
    @DisplayName("订单状态流转 - 完整流程：创建->确认->支付->发货->确认收货")
    void orderFlow_FullProcess() {
        // 1. 创建订单
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);
        OrderDTO order = orderService.getOrderById(orderId);
        String orderNo = order.getOrderNo();
        assertEquals(OrderStatus.ORDER_STATUS_PENDING, order.getStatus());

        // 2. 确认订单（进入待支付状态）
        orderService.confirmOrder(orderNo);
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_UNPAID, order.getStatus());

        // 3. 支付处理中
        orderService.handlePaying(orderNo);
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_PAYING, order.getStatus());

        // 4. 支付成功
        orderService.handlePaySuccess(orderNo, "ALIPAY");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_PAID, order.getStatus());
        assertEquals(OrderStatus.PAY_STATUS_SUCCESS, order.getPayStatus());

        // 5. 发货
        orderService.shipOrder(orderNo, "SF1234567890");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_DELIVERING, order.getStatus());

        // 6. 确认收货
        orderService.confirmReceipt(orderNo);
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("订单状态流转 - 退款流程：已支付->申请退款->退款成功")
    void orderFlow_RefundSuccess() {
        // 创建订单并支付成功
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);
        OrderDTO order = orderService.getOrderById(orderId);
        String orderNo = order.getOrderNo();

        orderService.confirmOrder(orderNo);
        orderService.handlePaying(orderNo);
        orderService.handlePaySuccess(orderNo, "ALIPAY");

        // 申请退款
        orderService.applyRefund(orderNo, "用户不想买了");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_REFUNDING, order.getStatus());

        // 退款成功
        orderService.handleRefundSuccess(orderNo);
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_REFUNDED, order.getStatus());
        assertEquals(OrderStatus.PAY_STATUS_REFUNDED, order.getPayStatus());
    }

    @Test
    @DisplayName("订单状态流转 - 退款流程：已支付->申请退款->退款失败")
    void orderFlow_RefundFailed() {
        // 创建订单并支付成功
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);
        OrderDTO order = orderService.getOrderById(orderId);
        String orderNo = order.getOrderNo();

        orderService.confirmOrder(orderNo);
        orderService.handlePaying(orderNo);
        orderService.handlePaySuccess(orderNo, "ALIPAY");

        // 申请退款
        orderService.applyRefund(orderNo, "用户不想买了");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_REFUNDING, order.getStatus());

        // 退款失败
        orderService.handleRefundFailed(orderNo, "账户异常");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_REFUND_FAILED, order.getStatus());
        assertEquals(OrderStatus.PAY_STATUS_REFUND_FAILED, order.getPayStatus());
    }

    @Test
    @DisplayName("订单状态流转 - 售后流程：已完成->申请售后->完成售后")
    void orderFlow_AfterSale() {
        // 创建订单并完成
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);
        OrderDTO order = orderService.getOrderById(orderId);
        String orderNo = order.getOrderNo();

        orderService.confirmOrder(orderNo);
        orderService.handlePaying(orderNo);
        orderService.handlePaySuccess(orderNo, "ALIPAY");
        orderService.shipOrder(orderNo, "SF1234567890");
        orderService.confirmReceipt(orderNo);

        // 申请售后
        orderService.applyAfterSale(orderNo, "质量问题");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_AFTER_SALE, order.getStatus());

        // 完成售后
        orderService.completeAfterSale(orderNo);
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("支付失败处理")
    void handlePayFailed_Success() {
        // 创建订单
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);
        OrderDTO order = orderService.getOrderById(orderId);
        String orderNo = order.getOrderNo();

        // 确认订单并进入支付处理中
        orderService.confirmOrder(orderNo);
        orderService.handlePaying(orderNo);

        // 支付失败
        orderService.handlePayFailed(orderNo, "余额不足");
        order = orderService.getOrderById(orderId);
        assertEquals(OrderStatus.ORDER_STATUS_PAY_FAILED, order.getStatus());
        assertEquals(OrderStatus.PAY_STATUS_FAILED, order.getPayStatus());
    }

    @Test
    @DisplayName("获取订单明细")
    void listOrderItems_Success() {
        OrderDTO dto = createTestOrderDTO();
        Long orderId = orderService.createOrder(dto);

        List<OrderItemDTO> items = orderService.listOrderItems(orderId);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("pkg001", items.get(0).getItemId());
        assertEquals("云服务器ECS", items.get(0).getItemName());
    }
}