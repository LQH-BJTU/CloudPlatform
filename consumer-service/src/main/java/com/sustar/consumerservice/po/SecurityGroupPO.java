package com.sustar.consumerservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("openstack_security_group")
public class SecurityGroupPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sgId;

    private String sgName;

    private String description;

    private String tenantId;

    private String projectId;

    private String securityGroupRules;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer isExpired;

    private LocalDateTime collectedAt;
}