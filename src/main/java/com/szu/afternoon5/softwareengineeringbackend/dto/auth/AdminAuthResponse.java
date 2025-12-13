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

    @JsonProperty("jwt_token")
    private String jwtToken;

    public AdminAuthResponse(Admin admin, String jwtToken) {
        super();
        this.admin = new AdminInfo(admin);
        this.jwtToken = jwtToken;
    }
}
