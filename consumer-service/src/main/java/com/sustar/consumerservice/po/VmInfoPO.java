package com.sustar.consumerservice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("openstack_vm_info")
public class VmInfoPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String instanceId;

    private String instanceName;

    private String healthStatus;

    private String paymentType;

    private LocalDateTime expireTime;

    private String instanceSpec;

    private Integer cpuCount;

    private BigDecimal memoryGb;

    private String publicIp;

    private String privateIp;

    private String osType;

    private BigDecimal publicBandwidth;

    private String bandwidthBillingType;

    private Integer autoRenewal;

    private LocalDateTime createdAt;

    private String systemDiskInfo;

    private String flavorId;

    private String tenantId;

    private String userId;

    private String status;

    private Integer isExpired;

    private LocalDateTime collectedAt;
}