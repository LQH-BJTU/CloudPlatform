package com.sustar.collectorservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "collector")
public class CollectorConfig {
    private long pollingInterval = 60000;
    private boolean enabled = true;
}