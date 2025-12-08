package com.szu.afternoon5.softwareengineeringbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WechatConfig {

    private String appId;

    private String appSecret;

    private String testJscode;

}

