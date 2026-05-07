package com.sustar.authservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sustar.authservice.dto.UserAuthDTO;
import com.sustar.authservice.exceptions.BusinessException;
import com.sustar.authservice.mapper.UserAuthMapper;
import com.sustar.authservice.po.UserAuthPO;
import com.sustar.authservice.query.UserAuthQuery;
import com.sustar.authservice.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户认证业务实现类
 * 实现用户认证相关的业务逻辑
 */
@Slf4j
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuthPO> implements UserAuthService {

    @Override
    public UserAuthDTO getUserById(Long id) {
        UserAuthPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDTO(po);
    }

    @Override
    public List<UserAuthDTO> listUsers(UserAuthQuery query) {
        LambdaQueryWrapper<UserAuthPO> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(UserAuthPO::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getPhone())) {
            wrapper.eq(UserAuthPO::getPhone, query.getPhone());
        }
        if (query.getStatus() != null) {
            wrapper.eq(UserAuthPO::getStatus, query.getStatus());
        }
        
        wrapper.orderByDesc(UserAuthPO::getCreateTime);
        
        List<UserAuthPO> poList = this.list(wrapper);
        return poList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long createUser(UserAuthDTO dto) {
        LambdaQueryWrapper<UserAuthPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthPO::getUsername, dto.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        UserAuthPO po = new UserAuthPO();
        BeanUtils.copyProperties(dto, po);
        po.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        po.setStatus(1);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());
        
        this.save(po);
        log.info("创建用户成功，id={}", po.getId());
        return po.getId();
    }

    @Override
    public void updateUser(Long id, UserAuthDTO dto) {
        UserAuthPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        
        BeanUtils.copyProperties(dto, po, "password");
        if (StringUtils.hasText(dto.getPassword())) {
            po.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        }
        po.setUpdateTime(LocalDateTime.now());
        
        this.updateById(po);
        log.info("更新用户成功，id={}", id);
    }

    @Override
    public void deleteUser(Long id) {
        UserAuthPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("用户不存在");
        }
        
        this.removeById(id);
        log.info("删除用户成功，id={}", id);
    }

    private UserAuthDTO convertToDTO(UserAuthPO po) {
        UserAuthDTO dto = new UserAuthDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }
}
