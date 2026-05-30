package com.sustar.ecsservice.vo;

import lombok.Data;

@Data
public class AppVO {
    private Long id;
    private String appName;
    private String appCode;
    private String icon;
    private String description;
    private String installTime;
}