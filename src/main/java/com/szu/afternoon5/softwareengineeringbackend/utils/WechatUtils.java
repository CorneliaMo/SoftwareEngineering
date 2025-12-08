package com.szu.afternoon5.softwareengineeringbackend.utils;

import com.szu.afternoon5.softwareengineeringbackend.config.WechatConfig;
import com.szu.afternoon5.softwareengineeringbackend.dto.WechatSessionResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 微信工具类，封装与微信开放接口的交互逻辑。
 * <p>
 * 可在此增加缓存、错误重试、接口调用监控等功能，统一管理外部依赖调用。
 */
@Component
public class WechatUtils {

    private final WechatConfig wechatConfig;
    private final WebClient webClient;

    public WechatUtils(WechatConfig wechatConfig, @Qualifier("wechatWebClient") WebClient wxWebClient) {
        this.wechatConfig = wechatConfig;
        this.webClient = wxWebClient;
    }

    /**
     * 根据前端传递的 jsCode 获取 openid，测试场景下返回固定值。
     * 后续可在此校验错误码、封装异常或返回更多会话信息。
     */
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
