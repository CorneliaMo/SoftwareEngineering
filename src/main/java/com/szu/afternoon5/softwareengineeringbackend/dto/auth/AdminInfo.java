package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员信息视图，用于后台登录响应。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminInfo {

    @JsonProperty("admin_id")
    private Long adminId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("admin_name")
    private String adminName;

    @JsonProperty("role")
    private Admin.AdminRole role;

    public AdminInfo(Admin admin) {
        this.adminId = admin.getAdminId();
        this.userId = admin.getUserId();
        this.username = admin.getUsername();
        this.adminName = admin.getAdminName();
        this.role = admin.getRole();
    }
}
