package com.sustar.authservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.authservice.po.UserAuthPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户认证数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthPO> {

}
