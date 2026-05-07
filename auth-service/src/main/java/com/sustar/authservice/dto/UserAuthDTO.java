package com.sustar.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户认证数据传输对象
 * 用于服务间传递用户认证信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String phone;

    private String email;
}
