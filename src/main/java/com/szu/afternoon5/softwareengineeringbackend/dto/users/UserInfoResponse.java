package com.szu.afternoon5.softwareengineeringbackend.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserDetail;
import lombok.Getter;
import lombok.Setter;

    /**
     * 用户详情响应类
     */
    @Getter
    @Setter
    public class UserInfoResponse extends BaseResponse {
        @JsonProperty("user")
        private UserDetail user;

        public UserInfoResponse(UserDetail user) {
            super();
            this.user = user;
        }
    }