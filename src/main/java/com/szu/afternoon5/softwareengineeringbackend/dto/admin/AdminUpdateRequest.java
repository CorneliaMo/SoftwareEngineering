package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 更新管理员信息请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminUpdateRequest {

    /**
     * 管理员用户名
     */
    @JsonProperty("username")
    private String username;

    /**
     * 管理员姓名
     */
    @JsonProperty("admin_name")
    private String adminName;

    /**
     * 权限角色
     */
    @JsonProperty("role")
    private Admin.AdminRole role;

    /**
     * 状态
     */
    @JsonProperty("status")
    private Boolean status;

    public AdminUpdateRequest(String username, String adminName, Admin.AdminRole role, Boolean status) {
        this.username = username;
        this.adminName = adminName;
        this.role = role;
        this.status = status;
    }
}
