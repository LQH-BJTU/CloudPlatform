package com.sustar.aiservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话视图对象
 * 用于返回给前端展示对话信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiConversationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userId;

    private String question;

    private String answer;

    private Integer status;

    private LocalDateTime createTime;
}
