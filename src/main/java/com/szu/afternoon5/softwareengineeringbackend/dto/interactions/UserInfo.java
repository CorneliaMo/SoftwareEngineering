package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户摘要信息
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UserInfo {

    /**
     * 用户id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 用户昵称
     */
    @JsonProperty("nickname")
    private String nickname;

    /**
     * 用户头像url
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;

    /**
     * 关注数量
     */
    @JsonProperty("following_count")
    private Integer followingCount;

    /**
     * 粉丝数量
     */
    @JsonProperty("follower_count")
    private Integer followerCount;

    public UserInfo(Long userId, String nickname, String avatarUrl, Integer followingCount, Integer followerCount) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.followingCount = followingCount;
        this.followerCount = followerCount;
    }
}
