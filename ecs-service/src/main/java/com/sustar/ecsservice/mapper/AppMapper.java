package com.sustar.ecsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.ecsservice.po.AppPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppMapper extends BaseMapper<AppPO> {

    @Select("SELECT * FROM ecs_app WHERE is_expired = 0 ORDER BY sort_order ASC")
    List<AppPO> selectActiveApps();
}