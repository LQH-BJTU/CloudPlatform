package com.sustar.ecsservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("ecs_image")
public class ImagePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String imageName;

    private String osCategory;

    private String osVersion;

    private String description;

    private Integer isExpired;

    private Integer sortOrder;

    private Integer isDefault;

    private Integer isFree;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}