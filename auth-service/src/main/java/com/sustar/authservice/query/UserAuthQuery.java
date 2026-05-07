package com.sustar.authservice.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户查询参数对象
 * 用于接收前端查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String phone;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}
