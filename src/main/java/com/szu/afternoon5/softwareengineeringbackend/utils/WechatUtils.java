package com.szu.afternoon5.softwareengineeringbackend.utils;

import com.szu.afternoon5.softwareengineeringbackend.config.WechatConfig;
import com.szu.afternoon5.softwareengineeringbackend.dto.WechatSessionResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WechatUtils {

    private final WechatConfig wechatConfig;
    private final WebClient webClient;

    public WechatUtils(WechatConfig wechatConfig, @Qualifier("wechatWebClient") WebClient wxWebClient) {
        this.wechatConfig = wechatConfig;
        this.webClient = wxWebClient;
    }

    public String getOpenid(String jscode) {
        if (jscode.equals(wechatConfig.getTestJscode())){
            return "TEST_ONLY_OPENID";
        }
        WechatSessionResponse wechatSessionResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sns/jscode2session")
                        .queryParam("appid", wechatConfig.getAppId())
                        .queryParam("secret", wechatConfig.getAppSecret())
                        .queryParam("js_code", jscode)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .retrieve()
                .bodyToMono(WechatSessionResponse.class)
                .block();

        if (wechatSessionResponse == null) {
            return null;
        } else {
            return wechatSessionResponse.getOpenid();
        }
    }
}
