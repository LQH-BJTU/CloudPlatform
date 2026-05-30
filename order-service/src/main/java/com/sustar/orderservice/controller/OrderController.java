package com.sustar.orderservice.controller;

import com.sustar.orderservice.constants.OrderStatus;
import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.dto.OrderItemDTO;
import com.sustar.orderservice.query.OrderQuery;
import com.sustar.orderservice.service.OrderService;
import com.sustar.orderservice.vo.OrderItemVO;
import com.sustar.orderservice.vo.OrderVO;
import com.sustar.orderservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单控制器
 * 处理订单相关的HTTP请求，包含完整的状态流转接口
 * 支持千万级高并发订单系统
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public Result<OrderVO> getOrder(@PathVariable Long id) {
        OrderDTO dto = orderService.getOrderById(id);
        return Result.success(convertToVO(dto));
    }

    @GetMapping("/orderNo/{orderNo}")
    public Result<OrderVO> getOrderByOrderNo(@PathVariable String orderNo) {
        OrderDTO dto = orderService.getOrderByOrderNo(orderNo);
        return Result.success(convertToVO(dto));
    }

    @GetMapping("/list")
    public Result<List<OrderVO>> listOrders(OrderQuery query) {
        List<OrderDTO> dtoList = orderService.listOrders(query);
        List<OrderVO> voList = dtoList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping
    public Result<OrderVO> createOrder(@RequestBody OrderDTO dto) {
        Long id = orderService.createOrder(dto);
        OrderDTO createdOrder = orderService.getOrderById(id);
        return Result.success("创建成功", convertToVO(createdOrder));
    }

    @PutMapping("/{id}")
    public Result<Void> updateOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        orderService.updateOrder(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/{orderNo}/paying")
    public Result<Void> handlePaying(@PathVariable String orderNo) {
        orderService.handlePaying(orderNo);
        return Result.success();
    }

    @PostMapping("/pay/callback")
    public Result<Void> payCallback(@RequestBody PayCallbackRequest request) {
        orderService.handlePaySuccess(request.getOrderNo(), request.getPayType());
        return Result.success();
    }

    @PostMapping("/{orderNo}/payFailed")
    public Result<Void> handlePayFailed(@PathVariable String orderNo, 
                                        @RequestParam(required = false) String reason) {
        orderService.handlePayFailed(orderNo, reason);
        return Result.success();
    }

    @PostMapping("/{orderNo}/confirm")
    public Result<Void> confirmOrder(@PathVariable String orderNo) {
        orderService.confirmOrder(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/ship")
    public Result<Void> shipOrder(@PathVariable String orderNo, 
                                  @RequestParam(required = false) String logisticsNo) {
        orderService.shipOrder(orderNo, logisticsNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/confirmReceipt")
    public Result<Void> confirmReceipt(@PathVariable String orderNo) {
        orderService.confirmReceipt(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/applyRefund")
    public Result<Void> applyRefund(@PathVariable String orderNo, 
                                    @RequestParam(required = false) String reason) {
        orderService.applyRefund(orderNo, reason);
        return Result.success();
    }

    @PostMapping("/{orderNo}/refundSuccess")
    public Result<Void> handleRefundSuccess(@PathVariable String orderNo) {
        orderService.handleRefundSuccess(orderNo);
        return Result.success();
    }

    @PostMapping("/{orderNo}/refundFailed")
    public Result<Void> handleRefundFailed(@PathVariable String orderNo, 
                                           @RequestParam(required = false) String reason) {
        orderService.handleRefundFailed(orderNo, reason);
        return Result.success();
    }

    @PostMapping("/{orderNo}/applyAfterSale")
    public Result<Void> applyAfterSale(@PathVariable String orderNo, 
                                       @RequestParam(required = false) String reason) {
        orderService.applyAfterSale(orderNo, reason);
        return Result.success();
    }

    @PostMapping("/{orderNo}/completeAfterSale")
    public Result<Void> completeAfterSale(@PathVariable String orderNo) {
        orderService.completeAfterSale(orderNo);
        return Result.success();
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItemVO>> listOrderItems(@PathVariable Long id) {
        List<OrderItemDTO> dtoList = orderService.listOrderItems(id);
        List<OrderItemVO> voList = dtoList.stream()
                .map(this::convertToItemVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private OrderVO convertToVO(OrderDTO dto) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(dto, vo);

        vo.setStatusDesc(OrderStatus.getOrderStatusDesc(dto.getStatus()));
        vo.setPayStatusDesc(OrderStatus.getPayStatusDesc(dto.getPayStatus()));

        if (dto.getItems() != null) {
            List<OrderItemVO> itemVOList = dto.getItems().stream()
                    .map(this::convertToItemVO)
                    .collect(Collectors.toList());
            vo.setItems(itemVOList);
        }

        return vo;
    }

    private OrderItemVO convertToItemVO(OrderItemDTO dto) {
        OrderItemVO vo = new OrderItemVO();
        BeanUtils.copyProperties(dto, vo);

        vo.setItemTypeDesc(OrderStatus.getItemTypeDesc(dto.getItemType()));
        vo.setBillingTypeDesc(OrderStatus.getBillingTypeDesc(dto.getBillingType()));

        return vo;
    }

    public static class PayCallbackRequest {
        private String orderNo;
        private String payType;
        private String payNo;
        private String sign;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getPayNo() {
            return payNo;
        }

        public void setPayNo(String payNo) {
            this.payNo = payNo;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}