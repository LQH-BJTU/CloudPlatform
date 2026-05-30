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
import com.sustar.collectorservice.dto.VmMetricsDTO;
import com.sustar.collectorservice.dto.VmInfoDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NovaClient {

    private final KeystoneClient keystoneClient;
    private final OpenStackConfig openStackConfig;

    private String getComputeUrl() {
        return openStackConfig.getAuthUrl().replace("/identity/v3", "/compute/v2.1");
    }

    public List<VmMetricsDTO> getServers() {
        List<VmMetricsDTO> servers = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String computeUrl = getComputeUrl();
            String url = computeUrl + "/servers/detail?all_tenants=true";

            HttpGet request = new HttpGet(url);
            request.setHeader("X-Auth-Token", keystoneClient.getToken());
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject result = JSON.parseObject(responseBody);
                JSONArray serversArray = result.getJSONArray("servers");

                if (serversArray != null) {
                    for (int i = 0; i < serversArray.size(); i++) {
                        JSONObject serverJson = serversArray.getJSONObject(i);
                        VmMetricsDTO vm = parseServer(serverJson);
                        servers.add(vm);
                    }
                }
                log.info("从Nova API获取到 {} 个云实例", servers.size());
            }
        } catch (Exception e) {
            log.error("从Nova API获取服务器列表失败: {}", e.getMessage());
        }
        return servers;
    }

    private VmMetricsDTO parseServer(JSONObject serverJson) {
        VmMetricsDTO vm = new VmMetricsDTO();
        vm.setInstanceId(serverJson.getString("id"));
        vm.setInstanceName(serverJson.getString("name"));
        vm.setStatus(serverJson.getString("status"));
        vm.setVmState(serverJson.getString("vm_state"));
        vm.setTaskState(serverJson.getString("task_state"));
        vm.setCreatedAt(serverJson.getString("created"));
        vm.setUpdatedAt(serverJson.getString("updated"));
        vm.setTenantId(serverJson.getString("tenant_id"));
        vm.setUserId(serverJson.getString("user_id"));
        vm.setHost(serverJson.getString("host"));
        vm.setHypervisorHostname(serverJson.getString("hypervisor_hostname"));

        JSONObject flavor = serverJson.getJSONObject("flavor");
        if (flavor != null) {
            vm.setFlavorId(flavor.getString("id"));
            vm.setFlavorName(flavor.getString("original_name"));
        }

        JSONObject image = serverJson.getJSONObject("image");
        if (image != null) {
            vm.setImageId(image.getString("id"));
        }

        JSONObject addresses = serverJson.getJSONObject("addresses");
        if (addresses != null) {
            vm.setAddresses(addresses.toJSONString());
        }

        JSONArray securityGroups = serverJson.getJSONArray("security_groups");
        if (securityGroups != null) {
            List<VmMetricsDTO.SecurityGroupInfo> sgList = new ArrayList<>();
            for (int i = 0; i < securityGroups.size(); i++) {
                JSONObject sg = securityGroups.getJSONObject(i);
                VmMetricsDTO.SecurityGroupInfo sgInfo = new VmMetricsDTO.SecurityGroupInfo();
                sgInfo.setId(sg.getString("id"));
                sgInfo.setName(sg.getString("name"));
                sgList.add(sgInfo);
            }
            vm.setSecurityGroups(sgList);
        }

        JSONObject resources = serverJson.getJSONObject("resources");
        if (resources != null) {
            VmMetricsDTO.VmResources res = new VmMetricsDTO.VmResources();
            res.setVcpus(resources.getInteger("vcpus"));
            res.setMemoryMb(resources.getInteger("memory_mb"));
            res.setLocalGb(resources.getInteger("local_gb"));
            res.setMemoryResidentMb(resources.getInteger("memory_resident_mb"));
            res.setVcpusUsage(resources.getInteger("vcpus_usage"));
            vm.setResources(res);
        }

        return vm;
    }

    public List<VmInfoDTO> getVmInfoList() {
        List<VmInfoDTO> vmInfoList = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String computeUrl = getComputeUrl();
            String url = computeUrl + "/servers/detail?all_tenants=true";

            HttpGet request = new HttpGet(url);
            request.setHeader("X-Auth-Token", keystoneClient.getToken());
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject result = JSON.parseObject(responseBody);
                JSONArray serversArray = result.getJSONArray("servers");

                if (serversArray != null) {
                    for (int i = 0; i < serversArray.size(); i++) {
                        JSONObject serverJson = serversArray.getJSONObject(i);
                        VmInfoDTO vmInfo = parseVmInfo(serverJson);
                        vmInfoList.add(vmInfo);
                    }
                }
                log.info("从Nova API获取到 {} 个实例基本信息", vmInfoList.size());
            }
        } catch (Exception e) {
            log.error("从Nova API获取实例基本信息失败: {}", e.getMessage());
        }
        return vmInfoList;
    }

    private VmInfoDTO parseVmInfo(JSONObject serverJson) {
        VmInfoDTO vmInfo = new VmInfoDTO();
        vmInfo.setInstanceId(serverJson.getString("id"));
        vmInfo.setInstanceName(serverJson.getString("name"));
        vmInfo.setStatus(serverJson.getString("status"));
        vmInfo.setCreatedAt(serverJson.getString("created"));
        vmInfo.setTenantId(serverJson.getString("tenant_id"));
        vmInfo.setUserId(serverJson.getString("user_id"));

        if ("ACTIVE".equals(serverJson.getString("status"))) {
            vmInfo.setHealthStatus("正常");
        } else if ("SHUTOFF".equals(serverJson.getString("status"))) {
            vmInfo.setHealthStatus("已关机");
        } else if ("ERROR".equals(serverJson.getString("status"))) {
            vmInfo.setHealthStatus("异常");
        } else {
            vmInfo.setHealthStatus("未知");
        }

        JSONObject flavor = serverJson.getJSONObject("flavor");
        if (flavor != null) {
            vmInfo.setFlavorId(flavor.getString("id"));
            String flavorName = flavor.getString("original_name");
            vmInfo.setInstanceSpec(flavorName);
            
            try (CloseableHttpClient flavorClient = HttpClients.createDefault()) {
                String flavorUrl = getComputeUrl() + "/flavors/" + flavor.getString("id");
                HttpGet flavorRequest = new HttpGet(flavorUrl);
                flavorRequest.setHeader("X-Auth-Token", keystoneClient.getToken());
                flavorRequest.setHeader("Content-Type", "application/json");
                
                try (CloseableHttpResponse flavorResponse = flavorClient.execute(flavorRequest)) {
                    String flavorBody = EntityUtils.toString(flavorResponse.getEntity());
                    JSONObject flavorDetail = JSON.parseObject(flavorBody).getJSONObject("flavor");
                    if (flavorDetail != null) {
                        vmInfo.setCpuCount(flavorDetail.getInteger("vcpus"));
                        vmInfo.setMemoryGb(BigDecimal.valueOf(flavorDetail.getInteger("ram")).divide(BigDecimal.valueOf(1024), 2, BigDecimal.ROUND_HALF_UP));
                    }
                }
            } catch (Exception e) {
                log.warn("获取flavor详细信息失败: {}", e.getMessage());
            }
        }

        JSONObject addresses = serverJson.getJSONObject("addresses");
        if (addresses != null) {
            extractIpAddresses(vmInfo, addresses);
        }

        JSONObject image = serverJson.getJSONObject("image");
        if (image != null && image.getString("id") != null) {
            try (CloseableHttpClient imageClient = HttpClients.createDefault()) {
                String imageUrl = openStackConfig.getAuthUrl().replace("/identity/v3", "/image/v2") 
                    + "/images/" + image.getString("id");
                HttpGet imageRequest = new HttpGet(imageUrl);
                imageRequest.setHeader("X-Auth-Token", keystoneClient.getToken());
                imageRequest.setHeader("Content-Type", "application/json");
                
                try (CloseableHttpResponse imageResponse = imageClient.execute(imageRequest)) {
                    String imageBody = EntityUtils.toString(imageResponse.getEntity());
                    JSONObject imageDetail = JSON.parseObject(imageBody);
                    if (imageDetail != null) {
                        vmInfo.setOsType(imageDetail.getString("os_distro"));
                    }
                }
            } catch (Exception e) {
                log.warn("获取镜像信息失败: {}", e.getMessage());
            }
        }

        vmInfo.setPaymentType("按需计费");
        vmInfo.setBandwidthBillingType("按带宽计费");
        vmInfo.setAutoRenewal(0);
        vmInfo.setIsExpired(0);

        return vmInfo;
    }

    private void extractIpAddresses(VmInfoDTO vmInfo, JSONObject addresses) {
        for (String networkName : addresses.keySet()) {
            JSONArray ipArray = addresses.getJSONArray(networkName);
            if (ipArray != null) {
                for (int i = 0; i < ipArray.size(); i++) {
                    JSONObject ipInfo = ipArray.getJSONObject(i);
                    String version = ipInfo.getString("version");
                    String addr = ipInfo.getString("addr");
                    String type = ipInfo.getString("OS-EXT-IPS:type");
                    
                    if ("4".equals(version)) {
                        if ("floating".equals(type)) {
                            vmInfo.setPublicIp(addr);
                        } else if ("fixed".equals(type)) {
                            if (vmInfo.getPrivateIp() == null) {
                                vmInfo.setPrivateIp(addr);
                            }
                        }
                    }
                }
            }
        }
    }
}