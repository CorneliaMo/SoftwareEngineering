package com.szu.afternoon5.softwareengineeringbackend.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

    /**
     * 搜索用户响应类
     */
    @Getter
    @Setter
    public class SearchUserResponse extends PageMeta {

        @JsonProperty("users")
        private List<UserDetail> users;

        public SearchUserResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize, List<UserDetail> users) {
            super(totalPage, totalCount, currentPage, pageSize);
            this.users = users;
        }
    }
