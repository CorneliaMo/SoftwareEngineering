package com.szu.afternoon5.softwareengineeringbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "builtin-super-admin")
public class BuiltinSuperAdminConfig {

    private String username;

    private String password;
}
