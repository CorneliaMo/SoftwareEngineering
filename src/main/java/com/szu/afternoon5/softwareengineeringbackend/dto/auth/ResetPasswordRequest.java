package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户密码重置请求体。
 */
@Data
public class ResetPasswordRequest {

    /**
     * 旧密码。
     */
    @JsonProperty("old_password")
    private String oldPassword;

    /**
     * 新密码。
     */
    @JsonProperty("new_password")
    private String newPassword;
}
