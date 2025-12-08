package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 管理员信息视图，用于后台登录响应。
 */
@Data
public class AdminInfo {

    @JsonProperty("admin_id")
    private Integer adminId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("admin_name")
    private String adminName;

    @JsonProperty("role")
    private String role;
}
