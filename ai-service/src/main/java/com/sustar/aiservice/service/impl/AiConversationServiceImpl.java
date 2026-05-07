package com.sustar.aiservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.aiservice.dto.AiConversationDTO;
import com.sustar.aiservice.exceptions.BusinessException;
import com.sustar.aiservice.mapper.AiConversationMapper;
import com.sustar.aiservice.po.AiConversationPO;
import com.sustar.aiservice.query.AiConversationQuery;
import com.sustar.aiservice.service.AiConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI对话业务实现类
 * 实现对话相关的业务逻辑
 */
@Slf4j
@Service
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversationPO> implements AiConversationService {

    @Override
    public AiConversationDTO getConversationById(Long id) {
        AiConversationPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("对话记录不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<AiConversationDTO> listConversations(AiConversationQuery query) {
        LambdaQueryWrapper<AiConversationPO> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(AiConversationPO::getUserId, query.getUserId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(AiConversationPO::getStatus, query.getStatus());
        }
        
        wrapper.orderByDesc(AiConversationPO::getCreateTime);
        
        List<AiConversationPO> poList = this.list(wrapper);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createConversation(AiConversationDTO dto) {
        AiConversationPO po = new AiConversationPO();
        BeanUtils.copyProperties(dto, po);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        
        this.save(po);
        log.info("创建AI对话记录成功，id={}", po.getId());
        return po.getId();
    }

    @Override
    public void updateConversation(Long id, AiConversationDTO dto) {
        AiConversationPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("对话记录不存在");
        }
        
        BeanUtils.copyProperties(dto, po);
        po.setUpdateTime(LocalDateTime.now());
        
        this.updateById(po);
        log.info("更新AI对话记录成功，id={}", id);
    }

    @Override
    public void deleteConversation(Long id) {
        AiConversationPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("对话记录不存在");
        }
        
        this.removeById(id);
        log.info("删除AI对话记录成功，id={}", id);
    }

    private AiConversationDTO convertToDTO(AiConversationPO po) {
        AiConversationDTO dto = new AiConversationDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }
}
