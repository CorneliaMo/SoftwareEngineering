package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserDetail;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 获取用户信息的响应体。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfoResponse extends BaseResponse {

    @JsonProperty("user")
    private UserDetail user;

    public UserInfoResponse(UserDetail user) {
        super();
        this.user = user;
    }
}
