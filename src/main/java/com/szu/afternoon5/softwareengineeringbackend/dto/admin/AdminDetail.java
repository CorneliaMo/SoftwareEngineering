package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 管理员详情
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminDetail {

    @JsonProperty("admin_id")
    private Long adminId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("admin_name")
    private String adminName;

    @JsonProperty("role")
    private Admin.AdminRole role;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("last_login")
    private OffsetDateTime lastLogin;

    @JsonProperty("created_time")
    private OffsetDateTime createdTime;

    public AdminDetail(Admin admin) {
        this.adminId = admin.getAdminId();
        this.username = admin.getUsername();
        this.adminName = admin.getAdminName();
        this.role = admin.getRole();
        this.status = admin.getStatus();
        if (admin.getLastLogin() != null) {
            this.lastLogin = admin.getLastLogin().atZone(ZoneOffset.systemDefault()).toOffsetDateTime();
        }
        this.createdTime = admin.getCreatedTime().atZone(ZoneOffset.systemDefault()).toOffsetDateTime();
    }
}
