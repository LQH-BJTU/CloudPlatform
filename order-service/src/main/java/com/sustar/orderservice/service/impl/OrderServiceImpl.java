package com.sustar.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.orderservice.dto.OrderDTO;
import com.sustar.orderservice.exceptions.BusinessException;
import com.sustar.orderservice.mapper.OrderMapper;
import com.sustar.orderservice.po.OrderPO;
import com.sustar.orderservice.query.OrderQuery;
import com.sustar.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单业务实现类
 * 实现订单相关的业务逻辑
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderPO> implements OrderService {

    @Override
    public OrderDTO getOrderById(Long id) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<OrderDTO> listOrders(OrderQuery query) {
        LambdaQueryWrapper<OrderPO> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getOrderNo())) {
            wrapper.eq(OrderPO::getOrderNo, query.getOrderNo());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(OrderPO::getUserId, query.getUserId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(OrderPO::getStatus, query.getStatus());
        }
        
        wrapper.orderByDesc(OrderPO::getCreateTime);
        
        List<OrderPO> poList = this.list(wrapper);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createOrder(OrderDTO dto) {
        OrderPO po = new OrderPO();
        BeanUtils.copyProperties(dto, po);
        po.setOrderNo(generateOrderNo());
        po.setStatus(0);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        
        this.save(po);
        log.info("创建订单成功，id={}, orderNo={}", po.getId(), po.getOrderNo());
        return po.getId();
    }

    @Override
    public void updateOrder(Long id, OrderDTO dto) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }
        
        BeanUtils.copyProperties(dto, po, "orderNo");
        po.setUpdateTime(LocalDateTime.now());
        
        this.updateById(po);
        log.info("更新订单成功，id={}", id);
    }

    @Override
    public void deleteOrder(Long id) {
        OrderPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("订单不存在");
        }
        
        this.removeById(id);
        log.info("删除订单成功，id={}", id);
    }

    private OrderDTO convertToDTO(OrderPO po) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
