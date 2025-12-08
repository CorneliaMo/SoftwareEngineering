package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员信息视图，用于后台登录响应。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AdminInfo extends BaseResponse {

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

    public AdminInfo(Integer adminId, Integer userId, String username, String adminName, String role) {
        super();
        this.adminId = adminId;
        this.userId = userId;
        this.username = username;
        this.adminName = adminName;
        this.role = role;
    }

    public AdminInfo(ErrorCode errorCode, String errMsg, Integer adminId, Integer userId, String username, String adminName,
                     String role) {
        super(errorCode, errMsg);
        this.adminId = adminId;
        this.userId = userId;
        this.username = username;
        this.adminName = adminName;
        this.role = role;
    }
}
