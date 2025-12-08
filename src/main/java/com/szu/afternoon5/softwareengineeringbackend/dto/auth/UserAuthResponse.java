package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户注册或登录后的响应体，返回用户信息与 JWT。
 */
@Data
public class UserAuthResponse {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("user")
    private UserDetail user;

    @JsonProperty("jwt_token")
    private String jwtToken;
}
