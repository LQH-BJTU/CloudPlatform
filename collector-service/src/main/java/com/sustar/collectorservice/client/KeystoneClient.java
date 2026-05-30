package com.sustar.collectorservice.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sustar.collectorservice.config.OpenStackConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeystoneClient {

    private final OpenStackConfig openStackConfig;
    private String cachedToken;
    private long tokenExpireTime;

    public String getToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime - 300000) {
            return cachedToken;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String authUrl = openStackConfig.getAuthUrl() + "/auth/tokens";

            JSONObject authRequest = new JSONObject();
            authRequest.put("auth", new JSONObject() {{
                put("identity", new JSONObject() {{
                    put("methods", new String[]{"password"});
                    put("password", new JSONObject() {{
                        put("user", new JSONObject() {{
                            put("name", openStackConfig.getUsername());
                            put("password", openStackConfig.getPassword());
                            put("domain", new JSONObject() {{
                                put("id", openStackConfig.getUserDomainId());
                            }});
                        }});
                    }});
                }});
                put("scope", new JSONObject() {{
                    put("project", new JSONObject() {{
                        put("name", openStackConfig.getProjectName());
                        put("domain", new JSONObject() {{
                            put("id", openStackConfig.getProjectDomainId());
                        }});
                    }});
                }});
            }});

            HttpPost request = new HttpPost(authUrl);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(authRequest.toJSONString()));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                String token = response.getFirstHeader("X-Subject-Token").getValue();

                JSONObject result = JSON.parseObject(responseBody);
                JSONObject tokenInfo = result.getJSONObject("token");
                String expiresAt = tokenInfo.getString("expires_at");

                cachedToken = token;
                tokenExpireTime = parseExpiresAt(expiresAt);
                log.info("OpenStack认证成功，Token有效期至: {}", expiresAt);
                return token;
            }
        } catch (Exception e) {
            log.error("OpenStack认证失败: {}", e.getMessage());
            throw new RuntimeException("OpenStack认证失败", e);
        }
    }

    private long parseExpiresAt(String expiresAt) {
        try {
            return java.time.LocalDateTime.parse(expiresAt.replace("Z", "").replace("T", " "))
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
        } catch (Exception e) {
            return System.currentTimeMillis() + 3600000;
        }
    }

    public void clearToken() {
        this.cachedToken = null;
        this.tokenExpireTime = 0;
    }
}