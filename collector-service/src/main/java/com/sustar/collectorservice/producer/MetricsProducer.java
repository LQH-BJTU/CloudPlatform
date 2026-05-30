package com.sustar.collectorservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.properties.topic.vm-metrics:openstack-vm-metrics}")
    private String vmMetricsTopic;

    @Value("${spring.kafka.properties.topic.security-groups:openstack-security-groups}")
    private String securityGroupsTopic;

    @Value("${spring.kafka.properties.topic.vm-info:openstack-vm-info}")
    private String vmInfoTopic;

    public void sendVmMetrics(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(vmMetricsTopic, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("发送VM指标到Kafka失败: {}", ex.getMessage());
            } else {
                log.debug("VM指标已发送到Kafka, topic: {}, partition: {}, offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void sendSecurityGroups(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(securityGroupsTopic, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("发送安全组数据到Kafka失败: {}", ex.getMessage());
            } else {
                log.debug("安全组数据已发送到Kafka, topic: {}, partition: {}, offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void sendVmInfo(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(vmInfoTopic, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("发送实例基本信息到Kafka失败: {}", ex.getMessage());
            } else {
                log.debug("实例基本信息已发送到Kafka, topic: {}, partition: {}, offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}