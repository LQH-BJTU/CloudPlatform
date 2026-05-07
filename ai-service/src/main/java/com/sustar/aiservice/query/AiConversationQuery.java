package com.sustar.aiservice.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI对话查询参数对象
 * 用于接收前端查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiConversationQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}
