package com.szu.afternoon5.softwareengineeringbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项，集中管理密钥等安全参数。
 * <p>
 * 后续可继续扩展过期时间、刷新令牌开关等字段，并结合 {@code application.yml} 中的配置完成动态调节。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT 签名使用的密钥，部署时应通过安全配置中心注入。
     */
    private String secretKey;

}
