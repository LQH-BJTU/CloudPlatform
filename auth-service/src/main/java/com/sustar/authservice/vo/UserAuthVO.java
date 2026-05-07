package com.sustar.authservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户认证视图对象
 * 用于返回给前端展示用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String phone;

    private String email;

    private Integer status;

    private LocalDateTime createTime;
}
