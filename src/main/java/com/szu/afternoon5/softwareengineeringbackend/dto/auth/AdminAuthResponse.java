package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员登录响应体，返回管理员信息与 JWT。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AdminAuthResponse extends BaseResponse {

    @JsonProperty("admin")
    private AdminInfo admin;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    public AdminAuthResponse(Admin admin, String accessToken, String refreshToken) {
        super();
        this.admin = new AdminInfo(admin);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
