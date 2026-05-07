package com.sustar.orderservice.controller;

import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.query.OrderQuery;
import com.sustar.orderservice.service.OrderService;
import com.sustar.orderservice.vo.OrderVO;
import com.sustar.orderservice.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单控制器
 * 处理订单相关的HTTP请求
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

    @GetMapping("/list")
    public Result<List<OrderVO>> listOrders(OrderQuery query) {
        List<OrderDTO> dtoList = orderService.listOrders(query);
        List<OrderVO> voList = dtoList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping
    public Result<Long> createOrder(@RequestBody OrderDTO dto) {
        Long id = orderService.createOrder(dto);
        return Result.success("创建成功", id);
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

    private OrderVO convertToVO(OrderDTO dto) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}
