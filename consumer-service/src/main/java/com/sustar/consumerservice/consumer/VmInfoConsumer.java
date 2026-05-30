package com.sustar.consumerservice.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sustar.consumerservice.mapper.VmInfoMapper;
import com.sustar.consumerservice.po.VmInfoPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class VmInfoConsumer {

    private final VmInfoMapper vmInfoMapper;

    @KafkaListener(topics = "${spring.kafka.consumer.properties.topic.vm-info:openstack-vm-info}")
    public void consumeVmInfo(String message) {
        try {
            JSONObject json = JSON.parseObject(message);
            VmInfoPO po = convertToPO(json);
            vmInfoMapper.insert(po);
            log.info("实例基本信息已入库, instanceId: {}, instanceName: {}",
                    po.getInstanceId(), po.getInstanceName());
        } catch (Exception e) {
            log.error("处理实例基本信息消息失败: {}, message: {}", e.getMessage(), message);
        }
    }

    private VmInfoPO convertToPO(JSONObject json) {
        VmInfoPO po = new VmInfoPO();
        po.setInstanceId(json.getString("instanceId"));
        po.setInstanceName(json.getString("instanceName"));
        po.setHealthStatus(json.getString("healthStatus"));
        po.setPaymentType(json.getString("paymentType"));
        po.setInstanceSpec(json.getString("instanceSpec"));
        po.setCpuCount(json.getInteger("cpuCount"));
        
        if (json.getBigDecimal("memoryGb") != null) {
            po.setMemoryGb(json.getBigDecimal("memoryGb"));
        }
        
        po.setPublicIp(json.getString("publicIp"));
        po.setPrivateIp(json.getString("privateIp"));
        po.setOsType(json.getString("osType"));
        
        if (json.getBigDecimal("publicBandwidth") != null) {
            po.setPublicBandwidth(json.getBigDecimal("publicBandwidth"));
        }
        
        po.setBandwidthBillingType(json.getString("bandwidthBillingType"));
        po.setAutoRenewal(json.getInteger("autoRenewal"));
        po.setSystemDiskInfo(json.getString("systemDiskInfo"));
        po.setFlavorId(json.getString("flavorId"));
        po.setTenantId(json.getString("tenantId"));
        po.setUserId(json.getString("userId"));
        po.setStatus(json.getString("status"));
        po.setIsExpired(json.getInteger("isExpired"));

        po.setExpireTime(parseDateTime(json.getString("expireTime")));
        po.setCreatedAt(parseDateTime(json.getString("createdAt")));
        po.setCollectedAt(LocalDateTime.now());

        return po;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr.replace("Z", "").replace("T", " "),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateStr,
                        DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}