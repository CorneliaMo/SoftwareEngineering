package com.szu.afternoon5.softwareengineeringbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置，包含 App 信息与调试所需的测试凭证。
 * <p>
 * 部署到生产环境时应替换为真实凭据，并可在此扩展支付、订阅消息等配置项。
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WechatConfig {

    /**
     * 微信小程序的 AppId。
     */
    private String appId;

    /**
     * 微信小程序的 AppSecret，用于换取 session 信息。
     */
    private String appSecret;

    /**
     * 便于联调的测试登录凭证，上线时应移除或替换为动态入参。
     */
    private String testJscode;

}

