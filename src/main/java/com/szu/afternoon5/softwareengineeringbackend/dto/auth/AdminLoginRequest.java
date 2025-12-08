package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 管理员登录请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AdminLoginRequest extends BaseResponse {

    /**
     * 管理员账号。
     */
    @JsonProperty("username")
    private String username;

    /**
     * 管理员密码。
     */
    @JsonProperty("password")
    private String password;

    public AdminLoginRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public AdminLoginRequest(ErrorCode errorCode, String errMsg, String username, String password) {
        super(errorCode, errMsg);
        this.username = username;
        this.password = password;
    }
}
