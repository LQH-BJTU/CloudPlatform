package com.sustar.ecsservice.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackageVO {
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
    private Boolean isRecommended;
}