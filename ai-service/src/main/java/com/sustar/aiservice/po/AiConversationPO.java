package com.sustar.aiservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话记录实体类
 * 对应数据库表ai_conversation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("ai_conversation")
public class AiConversationPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String question;

    private String answer;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
