package com.sustar.consumerservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("openstack_vm_metrics")
public class VmMetricsPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String instanceId;

    private String instanceName;

    private String status;

    private String vmState;

    private String taskState;

    private String flavorId;

    private String flavorName;

    private String imageId;

    private String imageName;

    private String host;

    private String hypervisorHostname;

    private LocalDateTime createdAt;

    private LocalDateTime launchedAt;

    private LocalDateTime updatedAt;

    private String tenantId;

    private String userId;

    private String addresses;

    private String securityGroups;

    private Integer vcpus;

    private Integer memoryMb;

    private Integer localGb;

    private Integer memoryResidentMb;

    private Integer vcpusUsage;

    private Integer isExpired;

    private LocalDateTime collectedAt;
}