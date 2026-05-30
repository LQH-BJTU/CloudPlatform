package com.sustar.ecsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.ecsservice.po.ImagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImageMapper extends BaseMapper<ImagePO> {

    @Select("SELECT * FROM ecs_image WHERE is_expired = 0 AND os_category IN ('windows', 'linux') ORDER BY sort_order ASC")
    List<ImagePO> selectActiveImages();

    @Select("SELECT * FROM ecs_image WHERE is_expired = 0 AND os_category = #{osCategory} ORDER BY sort_order ASC")
    List<ImagePO> selectActiveImagesByCategory(@Param("osCategory") String osCategory);
}