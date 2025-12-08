package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 管理员登录响应体，返回管理员信息与 JWT。
 */
@Data
public class AdminAuthResponse {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("admin")
    private AdminInfo admin;

    @JsonProperty("jwt_token")
    private String jwtToken;
}
