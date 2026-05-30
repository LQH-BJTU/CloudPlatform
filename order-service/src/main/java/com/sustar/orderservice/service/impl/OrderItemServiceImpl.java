package com.sustar.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.orderservice.dto.OrderItemDTO;
import com.sustar.orderservice.mapper.OrderItemMapper;
import com.sustar.orderservice.po.OrderItemPO;
import com.sustar.orderservice.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单明细业务实现类
 * 实现订单明细相关的业务逻辑
 */
@Slf4j
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItemPO> implements OrderItemService {

    @Override
    public List<OrderItemDTO> listByOrderId(Long orderId) {
        List<OrderItemPO> poList = baseMapper.selectByOrderId(orderId);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long orderId, String orderNo, List<OrderItemDTO> itemDTOList) {
        List<OrderItemPO> poList = itemDTOList.stream()
                .map(dto -> {
                    OrderItemPO po = new OrderItemPO();
                    BeanUtils.copyProperties(dto, po);
                    po.setOrderId(orderId);
                    po.setOrderNo(orderNo);
                    po.setCreateTime(LocalDateTime.now());
                    return po;
                })
                .collect(Collectors.toList());

        this.saveBatch(poList);
        log.info("批量保存订单明细成功，orderId={}, count={}", orderId, poList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByOrderId(Long orderId) {
        baseMapper.deleteByOrderId(orderId);
        log.info("删除订单明细成功，orderId={}", orderId);
    }

    private OrderItemDTO convertToDTO(OrderItemPO po) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }
}