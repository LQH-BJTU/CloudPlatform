package com.sustar.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI对话数据传输对象
 * 用于服务间传递对话信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiConversationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userId;

    private String question;

    private String answer;

    private Integer status;
}
