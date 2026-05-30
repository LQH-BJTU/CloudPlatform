package com.sustar.consumerservice.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sustar.consumerservice.mapper.VmMetricsMapper;
import com.sustar.consumerservice.po.VmMetricsPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class VmMetricsConsumer {

    private final VmMetricsMapper vmMetricsMapper;

    @KafkaListener(topics = "${spring.kafka.consumer.properties.topic.vm-metrics:openstack-vm-metrics}")
    public void consumeVmMetrics(String message) {
        try {
            JSONObject json = JSON.parseObject(message);
            VmMetricsPO po = convertToPO(json);
            vmMetricsMapper.insert(po);
            log.info("VM指标数据已入库, instanceId: {}, instanceName: {}",
                    po.getInstanceId(), po.getInstanceName());
        } catch (Exception e) {
            log.error("处理VM指标消息失败: {}, message: {}", e.getMessage(), message);
        }
    }

    private VmMetricsPO convertToPO(JSONObject json) {
        VmMetricsPO po = new VmMetricsPO();
        po.setInstanceId(json.getString("instanceId"));
        po.setInstanceName(json.getString("instanceName"));
        po.setStatus(json.getString("status"));
        po.setVmState(json.getString("vmState"));
        po.setTaskState(json.getString("taskState"));
        po.setFlavorId(json.getString("flavorId"));
        po.setFlavorName(json.getString("flavorName"));
        po.setImageId(json.getString("imageId"));
        po.setImageName(json.getString("imageName"));
        po.setHost(json.getString("host"));
        po.setHypervisorHostname(json.getString("hypervisorHostname"));
        po.setTenantId(json.getString("tenantId"));
        po.setUserId(json.getString("userId"));
        po.setAddresses(json.getString("addresses"));
        po.setSecurityGroups(json.getJSONArray("securityGroups") != null
                ? json.getJSONArray("securityGroups").toJSONString() : null);

        JSONObject resources = json.getJSONObject("resources");
        if (resources != null) {
            po.setVcpus(resources.getInteger("vcpus"));
            po.setMemoryMb(resources.getInteger("memoryMb"));
            po.setLocalGb(resources.getInteger("localGb"));
            po.setMemoryResidentMb(resources.getInteger("memoryResidentMb"));
            po.setVcpusUsage(resources.getInteger("vcpusUsage"));
        }

        po.setCreatedAt(parseDateTime(json.getString("createdAt")));
        po.setLaunchedAt(parseDateTime(json.getString("launchedAt")));
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