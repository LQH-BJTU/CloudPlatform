package com.sustar.authservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sustar.authservice.dto.UserAuthDTO;
import com.sustar.authservice.po.UserAuthPO;
import com.sustar.authservice.query.UserAuthQuery;

import java.util.List;

/**
 * 用户认证业务接口
 * 定义用户认证相关的业务方法
 */
public interface UserAuthService extends IService<UserAuthPO> {

    UserAuthDTO getUserById(Long id);

    List<UserAuthDTO> listUsers(UserAuthQuery query);

    Long createUser(UserAuthDTO dto);

    void updateUser(Long id, UserAuthDTO dto);

    void deleteUser(Long id);
}
