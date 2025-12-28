package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员重置密码请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminResetPasswordRequest {

    /**
     * 新密码
     */
    @NotBlank
    @JsonProperty("new_password")
    private String newPassword;

    public AdminResetPasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }
}
