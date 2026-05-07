package com.sustar.aiservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.aiservice.dto.AiConversationDTO;
import com.sustar.aiservice.po.AiConversationPO;
import com.sustar.aiservice.query.AiConversationQuery;

import java.util.List;

/**
 * AI对话业务接口
 * 定义对话相关的业务方法
 */
public interface AiConversationService extends IService<AiConversationPO> {

    AiConversationDTO getConversationById(Long id);

    List<AiConversationDTO> listConversations(AiConversationQuery query);

    Long createConversation(AiConversationDTO dto);

    void updateConversation(Long id, AiConversationDTO dto);

    void deleteConversation(Long id);
}
