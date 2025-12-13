package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员登录请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminLoginRequest {

    /**
     * 管理员账号。
     */
    @NotNull
    @JsonProperty("username")
    private String username;

    /**
     * 管理员密码。
     */
    @NotNull
    @JsonProperty("password")
    private String password;

    public AdminLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
