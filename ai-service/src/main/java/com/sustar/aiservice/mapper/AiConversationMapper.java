package com.sustar.aiservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.aiservice.po.AiConversationPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationPO> {

}
