package com.sustar.ecsservice.vo;

import lombok.Data;

@Data
public class BillingTypeVO {
    private Long id;
    private String billingCode;
    private String billingName;
    private String description;
    private Boolean isRecommended;
}