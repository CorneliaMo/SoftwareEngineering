package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 用户详细信息视图，用于认证响应体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UserDetail {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("created_time")
    private Instant createdTime;

    @JsonProperty("post_count")
    private Integer postCount;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public UserDetail(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.avatarUrl = user.getAvatarUrl();
        this.createdTime = user.getCreatedTime();
        this.postCount = user.getPostCount();
        this.ratingCount = user.getRatingCount();
        this.commentCount = user.getCommentCount();
    }

    public UserDetail(Long userId, String username, String email, String nickname,
                      String avatarUrl, Instant createdTime, Integer postCount,
                      Integer ratingCount, Integer commentCount) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.createdTime = createdTime;
        this.postCount = postCount;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }
}
