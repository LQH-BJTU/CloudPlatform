package com.sustar.ecsservice.vo;

import lombok.Data;

@Data
public class ImageVO {
    private Long id;
    private String imageName;
    private String osCategory;
    private String osVersion;
    private String description;
    private Boolean isDefault;
    private Boolean isFree;
}