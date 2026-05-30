package com.sustar.ecsservice.vo;

import lombok.Data;

@Data
public class BandwidthModeVO {
    private Long id;
    private String modeCode;
    private String modeName;
    private String description;
    private Boolean isDefault;
}