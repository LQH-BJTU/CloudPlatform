package com.sustar.collectorservice.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Component;
import com.sustar.collectorservice.config.OpenStackConfig;
import com.sustar.collectorservice.dto.SecurityGroupDTO;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NeutronClient {

    private final KeystoneClient keystoneClient;
    private final OpenStackConfig openStackConfig;

    public List<SecurityGroupDTO> getSecurityGroups() {
        List<SecurityGroupDTO> securityGroups = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
        String url = "http://10.126.41.229/networking/v2.0/security-groups";

            HttpGet request = new HttpGet(url);
            request.setHeader("X-Auth-Token", keystoneClient.getToken());
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject result = JSON.parseObject(responseBody);
                JSONArray sgArray = result.getJSONArray("security_groups");

                if (sgArray != null) {
                    for (int i = 0; i < sgArray.size(); i++) {
                        JSONObject sgJson = sgArray.getJSONObject(i);
                        SecurityGroupDTO sg = parseSecurityGroup(sgJson);
                        securityGroups.add(sg);
                    }
                }
                log.info("从Neutron API获取到 {} 个安全组", securityGroups.size());
            }
        } catch (Exception e) {
            log.error("从Neutron API获取安全组列表失败: {}", e.getMessage());
        }
        return securityGroups;
    }

    private SecurityGroupDTO parseSecurityGroup(JSONObject sgJson) {
        SecurityGroupDTO sg = new SecurityGroupDTO();
        sg.setId(sgJson.getString("id"));
        sg.setName(sgJson.getString("name"));
        sg.setDescription(sgJson.getString("description"));
        sg.setTenantId(sgJson.getString("tenant_id"));
        sg.setProjectId(sgJson.getString("project_id"));
        sg.setCreatedAt(sgJson.getString("created_at"));
        sg.setUpdatedAt(sgJson.getString("updated_at"));

        JSONArray rulesArray = sgJson.getJSONArray("security_group_rules");
        if (rulesArray != null) {
            List<SecurityGroupDTO.SecurityRuleDTO> rules = new ArrayList<>();
            for (int i = 0; i < rulesArray.size(); i++) {
                JSONObject ruleJson = rulesArray.getJSONObject(i);
                SecurityGroupDTO.SecurityRuleDTO rule = new SecurityGroupDTO.SecurityRuleDTO();
                rule.setId(ruleJson.getString("id"));
                rule.setDirection(ruleJson.getString("direction"));
                rule.setProtocol(ruleJson.getString("protocol"));
                rule.setPortRangeMin(ruleJson.getString("port_range_min"));
                rule.setPortRangeMax(ruleJson.getString("port_range_max"));
                rule.setRemoteIpPrefix(ruleJson.getString("remote_ip_prefix"));
                rule.setRemoteGroupId(ruleJson.getString("remote_group_id"));
                rule.setEthertype(ruleJson.getString("ethertype"));
                rules.add(rule);
            }
            sg.setSecurityGroupRules(rules);
        }

        return sg;
    }
}