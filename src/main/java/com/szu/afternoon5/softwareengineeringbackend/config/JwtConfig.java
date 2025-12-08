package com.szu.afternoon5.softwareengineeringbackend.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secretKey;

}
