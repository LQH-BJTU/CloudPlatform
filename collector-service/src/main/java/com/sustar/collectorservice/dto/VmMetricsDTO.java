package com.sustar.collectorservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class VmMetricsDTO {
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
    private String createdAt;
    private String launchedAt;
    private String updatedAt;
    private String tenantId;
    private String userId;
    private String addresses;
    private List<SecurityGroupInfo> securityGroups;
    private VmResources resources;

    @Data
    public static class SecurityGroupInfo {
        private String id;
        private String name;
    }

    @Data
    public static class VmResources {
        private Integer vcpus;
        private Integer memoryMb;
        private Integer localGb;
        private Integer memoryResidentMb;
        private Integer vcpusUsage;
    }
}