package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户注册请求体，包含用户名、密码以及可选昵称。
 */
@Data
public class RegisterRequest {

    /**
     * 用户名，需满足英文、数字或下划线的 3-20 字符约束。
     */
    @JsonProperty("username")
    private String username;

    /**
     * 登录密码，长度 6-20 字符。
     */
    @JsonProperty("password")
    private String password;

    /**
     * 可选昵称，支持中英文与表情，1-50 字符。
     */
    @JsonProperty("nickname")
    private String nickname;
}
