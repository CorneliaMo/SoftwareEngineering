package com.szu.afternoon5.softwareengineeringbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户详细信息视图，用于认证响应体。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDetail extends BaseResponse {

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

    public UserDetail(Integer userId, String username, String email, String nickname, String avatarUrl, String createdTime,
                      Integer postCount, Integer ratingCount, Integer commentCount) {
        super();
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

    public UserDetail(ErrorCode errorCode, String errMsg, Integer userId, String username, String email, String nickname,
                      String avatarUrl, String createdTime, Integer postCount, Integer ratingCount, Integer commentCount) {
        super(errorCode, errMsg);
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
