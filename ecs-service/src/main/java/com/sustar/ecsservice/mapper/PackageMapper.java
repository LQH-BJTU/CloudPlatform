package com.sustar.ecsservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.ecsservice.po.PackagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PackageMapper extends BaseMapper<PackagePO> {

    @Select("SELECT * FROM ecs_package WHERE is_expired = 0 ORDER BY sort_order ASC")
    List<PackagePO> selectActivePackages();

    @Select("SELECT * FROM ecs_package WHERE is_expired = 0 AND package_code = #{packageCode} ORDER BY sort_order ASC")
    List<PackagePO> selectActivePackagesByCode(@Param("packageCode") String packageCode);
}