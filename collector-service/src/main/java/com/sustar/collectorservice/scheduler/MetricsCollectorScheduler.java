package com.sustar.collectorservice.scheduler;

import com.alibaba.fastjson2.JSON;
import com.sustar.collectorservice.client.NovaClient;
import com.sustar.collectorservice.client.NeutronClient;
import com.sustar.collectorservice.config.CollectorConfig;
import com.sustar.collectorservice.dto.SecurityGroupDTO;
import com.sustar.collectorservice.dto.VmMetricsDTO;
import com.sustar.collectorservice.dto.VmInfoDTO;
import com.sustar.collectorservice.producer.MetricsProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsCollectorScheduler {

    private final NovaClient novaClient;
    private final NeutronClient neutronClient;
    private final MetricsProducer metricsProducer;
    private final CollectorConfig collectorConfig;

    @Scheduled(fixedDelayString = "${collector.polling-interval:60000}")
    public void collectMetrics() {
        if (!collectorConfig.isEnabled()) {
            log.debug("指标采集已禁用");
            return;
        }

        log.info("开始采集OpenStack指标...");
        long startTime = System.currentTimeMillis();

        try {
            collectVmMetrics();
            collectVmInfo();
            collectSecurityGroups();
        } catch (Exception e) {
            log.error("采集指标时发生错误: {}", e.getMessage(), e);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("指标采集完成，耗时: {} ms", duration);
    }

    private void collectVmMetrics() {
        try {
            List<VmMetricsDTO> servers = novaClient.getServers();
            for (VmMetricsDTO server : servers) {
                try {
                    String json = JSON.toJSONString(server);
                    metricsProducer.sendVmMetrics(json);
                } catch (Exception e) {
                    log.error("发送VM指标失败, instanceId: {}, error: {}",
                            server.getInstanceId(), e.getMessage());
                }
            }
            log.info("VM指标采集完成，共采集 {} 个实例", servers.size());
        } catch (Exception e) {
            log.error("VM指标采集失败: {}", e.getMessage());
        }
    }

    private void collectSecurityGroups() {
        try {
            List<SecurityGroupDTO> securityGroups = neutronClient.getSecurityGroups();
            for (SecurityGroupDTO sg : securityGroups) {
                try {
                    String json = JSON.toJSONString(sg);
                    metricsProducer.sendSecurityGroups(json);
                } catch (Exception e) {
                    log.error("发送安全组数据失败, sgId: {}, error: {}",
                            sg.getId(), e.getMessage());
                }
            }
            log.info("安全组数据采集完成，共采集 {} 个安全组", securityGroups.size());
        } catch (Exception e) {
            log.error("安全组数据采集失败: {}", e.getMessage());
        }
    }

    private void collectVmInfo() {
        try {
            List<VmInfoDTO> vmInfoList = novaClient.getVmInfoList();
            for (VmInfoDTO vmInfo : vmInfoList) {
                try {
                    String json = JSON.toJSONString(vmInfo);
                    metricsProducer.sendVmInfo(json);
                } catch (Exception e) {
                    log.error("发送实例基本信息失败, instanceId: {}, error: {}",
                            vmInfo.getInstanceId(), e.getMessage());
                }
            }
            log.info("实例基本信息采集完成，共采集 {} 个实例", vmInfoList.size());
        } catch (Exception e) {
            log.error("实例基本信息采集失败: {}", e.getMessage());
        }
    }
}