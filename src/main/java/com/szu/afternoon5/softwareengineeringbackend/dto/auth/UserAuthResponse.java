package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户注册或登录后的响应体，返回用户信息与 JWT。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserAuthResponse extends BaseResponse {

    @JsonProperty("user")
    private UserDetail user;

    @JsonProperty("jwt_token")
    private String jwtToken;

    public UserAuthResponse(UserDetail user, String jwtToken) {
        super();
        this.user = user;
        this.jwtToken = jwtToken;
    }

    public UserAuthResponse(ErrorCode errorCode, String errMsg, UserDetail user, String jwtToken) {
        super(errorCode, errMsg);
        this.user = user;
        this.jwtToken = jwtToken;
    }
}
