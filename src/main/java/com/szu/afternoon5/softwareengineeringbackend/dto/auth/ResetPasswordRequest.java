package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户密码重置请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ResetPasswordRequest {

    /**
     * 旧密码。
     */
    @NotNull
    @JsonProperty("old_password")
    private String oldPassword;

    /**
     * 新密码。
     */
    @NotNull
    @JsonProperty("new_password")
    private String newPassword;

    public ResetPasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
