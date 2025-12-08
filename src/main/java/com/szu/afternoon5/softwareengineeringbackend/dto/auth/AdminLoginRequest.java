package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 管理员登录请求体。
 */
@Data
public class AdminLoginRequest {

    /**
     * 管理员账号。
     */
    @JsonProperty("username")
    private String username;

    /**
     * 管理员密码。
     */
    @JsonProperty("password")
    private String password;
}
