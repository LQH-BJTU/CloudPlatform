package com.sustar.collectorservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openstack")
public class OpenStackConfig {
    private String authUrl;
    private String username;
    private String password;
    private String projectName;
    private String userDomainId;
    private String projectDomainId;
    private String region;
}