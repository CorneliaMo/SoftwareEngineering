package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户详细信息视图，用于认证响应体。
 */
@Data
public class UserDetail {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("created_time")
    private String createdTime;

    @JsonProperty("post_count")
    private Integer postCount;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;
}
