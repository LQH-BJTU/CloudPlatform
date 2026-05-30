package com.sustar.collectorservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VmInfoDTO {
    private String instanceId;
    private String instanceName;
    private String healthStatus;
    private String paymentType;
    private String expireTime;
    private String instanceSpec;
    private Integer cpuCount;
    private BigDecimal memoryGb;
    private String publicIp;
    private String privateIp;
    private String osType;
    private BigDecimal publicBandwidth;
    private String bandwidthBillingType;
    private Integer autoRenewal;
    private String createdAt;
    private String systemDiskInfo;
    private String flavorId;
    private String tenantId;
    private String userId;
    private String status;
    private Integer isExpired;
    private String collectedAt;

    @Data
    public static class DiskInfo {
        private String diskType;
        private Integer sizeGb;
        private String diskName;
    }

    @Data
    public static class IpAddress {
        private String version;
        private String addr;
        private String type;
        private String macAddr;
    }
}