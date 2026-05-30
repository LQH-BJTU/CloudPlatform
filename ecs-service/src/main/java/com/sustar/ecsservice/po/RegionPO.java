package com.sustar.ecsservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地域实体类，与地域表一一对应
 */
@Data
@TableName("sys_common_region")
public class RegionPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 大区名（亚太-中国）
     */
    private String regionName;

    /**
     * 大区编码（apac-cn）
     */
    private String regionCode;

    /**
     * 地域编码（cn-beijing）
     */
    private String areaCode;

    /**
     * 展示名（华北2（北京））
     */
    private String areaName;

    /**
     * 状态：1正常 2停用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortNum;

    /**
     * 是否删除：0未删除 1已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
