package com.szu.afternoon5.softwareengineeringbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户搜索结果项。
 * 对应接口文档中 GET /users/search-user 接口的 "users" 数组中的单个元素。
 */
@Getter
@Setter
@ToString
public class UserSearchResult {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    public static UserSearchResult fromEntity(com.szu.afternoon5.softwareengineeringbackend.entity.User user) {
        UserSearchResult result = new UserSearchResult();
        result.setUserId(user.getUserId());
        result.setNickname(user.getNickname());
        result.setAvatarUrl(user.getAvatarUrl());
        return result;
    }
}