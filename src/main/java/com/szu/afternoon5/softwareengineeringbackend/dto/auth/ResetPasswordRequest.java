package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户密码重置请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResetPasswordRequest extends BaseResponse {

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

    public ResetPasswordRequest(String oldPassword, String newPassword) {
        super();
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ResetPasswordRequest(ErrorCode errorCode, String errMsg, String oldPassword, String newPassword) {
        super(errorCode, errMsg);
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
