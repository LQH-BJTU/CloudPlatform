package com.sustar.collectorservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class SecurityGroupDTO {
    private String id;
    private String name;
    private String description;
    private String tenantId;
    private String projectId;
    private List<SecurityRuleDTO> securityGroupRules;
    private String createdAt;
    private String updatedAt;

    @Data
    public static class SecurityRuleDTO {
        private String id;
        private String direction;
        private String protocol;
        private String portRangeMin;
        private String portRangeMax;
        private String remoteIpPrefix;
        private String remoteGroupId;
        private String ethertype;
    }
}