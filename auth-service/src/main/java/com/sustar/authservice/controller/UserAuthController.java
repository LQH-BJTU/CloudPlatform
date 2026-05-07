package com.sustar.authservice.controller;

import com.sustar.authservice.dto.UserAuthDTO;
import com.sustar.authservice.query.UserAuthQuery;
import com.sustar.authservice.service.UserAuthService;
import com.sustar.authservice.vo.Result;
import com.sustar.authservice.vo.UserAuthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户认证控制器
 * 处理用户认证相关的HTTP请求
 */
@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @GetMapping("/{id}")
    public Result<UserAuthVO> getUser(@PathVariable Long id) {
        UserAuthDTO dto = userAuthService.getUserById(id);
        return Result.success(convertToVO(dto));
    }

    @GetMapping("/list")
    public Result<List<UserAuthVO>> listUsers(UserAuthQuery query) {
        List<UserAuthDTO> dtoList = userAuthService.listUsers(query);
        List<UserAuthVO> voList = dtoList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping
    public Result<Long> createUser(@RequestBody UserAuthDTO dto) {
        Long id = userAuthService.createUser(dto);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserAuthDTO dto) {
        userAuthService.updateUser(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userAuthService.deleteUser(id);
        return Result.success();
    }

    private UserAuthVO convertToVO(UserAuthDTO dto) {
        UserAuthVO vo = new UserAuthVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}
