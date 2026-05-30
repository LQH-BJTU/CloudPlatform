package com.sustar.ecsservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ecs_package")
public class PackagePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String packageName;

    private String packageCode;

    private String description;

    private String icon;

    private Integer vcpus;

    private Integer memory;

    private String systemDisk;

    private String bandwidth;

    private BigDecimal priceMonth;

    private Integer isRecommended;

    private Integer sortOrder;

    private Integer isExpired;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}