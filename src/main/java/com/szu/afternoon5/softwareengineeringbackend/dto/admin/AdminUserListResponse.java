package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 管理端用户列表响应体。
 * 对应接口文档中 GET /admin/users 接口的数据部分。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminUserListResponse extends PageMeta {

    @JsonProperty("users")
    private List<User> users;

    public AdminUserListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize, List<User> users) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.users = users;
    }
}