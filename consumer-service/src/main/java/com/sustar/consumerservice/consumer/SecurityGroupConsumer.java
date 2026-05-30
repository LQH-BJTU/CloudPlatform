package com.sustar.consumerservice.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sustar.consumerservice.mapper.SecurityGroupMapper;
import com.sustar.consumerservice.po.SecurityGroupPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityGroupConsumer {

    private final SecurityGroupMapper securityGroupMapper;

    @KafkaListener(topics = "${spring.kafka.consumer.properties.topic.security-groups:openstack-security-groups}")
    public void consumeSecurityGroup(String message) {
        try {
            JSONObject json = JSON.parseObject(message);
            SecurityGroupPO po = convertToPO(json);
            securityGroupMapper.insert(po);
            log.info("安全组数据已入库, sgId: {}, sgName: {}",
                    po.getSgId(), po.getSgName());
        } catch (Exception e) {
            log.error("处理安全组消息失败: {}, message: {}", e.getMessage(), message);
        }
    }

    private SecurityGroupPO convertToPO(JSONObject json) {
        SecurityGroupPO po = new SecurityGroupPO();
        po.setSgId(json.getString("id"));
        po.setSgName(json.getString("name"));
        po.setDescription(json.getString("description"));
        po.setTenantId(json.getString("tenantId"));
        po.setProjectId(json.getString("projectId"));
        po.setSecurityGroupRules(json.getJSONArray("securityGroupRules") != null
                ? json.getJSONArray("securityGroupRules").toJSONString() : null);

        po.setCreatedAt(parseDateTime(json.getString("createdAt")));
        po.setUpdatedAt(parseDateTime(json.getString("updatedAt")));
        po.setCollectedAt(LocalDateTime.now());
        po.setIsExpired(0);

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