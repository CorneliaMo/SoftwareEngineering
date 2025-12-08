package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户账密登录请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends BaseResponse {

    /**
     * 登录用户名。
     */
    @JsonProperty("username")
    private String username;

    /**
     * 登录密码。
     */
    @JsonProperty("password")
    private String password;

    public LoginRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public LoginRequest(ErrorCode errorCode, String errMsg, String username, String password) {
        super(errorCode, errMsg);
        this.username = username;
        this.password = password;
    }
}
