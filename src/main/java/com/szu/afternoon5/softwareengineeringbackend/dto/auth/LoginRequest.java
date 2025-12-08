package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户账密登录请求体。
 */
@Data
public class LoginRequest {

    /**
     * 登录用户名。
     */
    @JsonProperty("username")
    private String username;

    /**
     * 登录密码。
     */
    @JsonProperty("password")
    private String password;
}
