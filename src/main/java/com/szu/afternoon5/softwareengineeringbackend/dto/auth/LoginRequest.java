package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户账密登录请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class LoginRequest {

    /**
     * 登录用户名。
     */
    @NotNull
    @JsonProperty("username")
    private String username;

    /**
     * 登录密码。
     */
    @NotNull
    @JsonProperty("password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
