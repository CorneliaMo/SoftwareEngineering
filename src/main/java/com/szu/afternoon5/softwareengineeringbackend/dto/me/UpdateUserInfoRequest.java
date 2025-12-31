package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 更新用户信息请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UpdateUserInfoRequest {

    /**
     * 新昵称，长度 1-50 字符。
     */
    @Size(min = 1, max = 50)
    @JsonProperty("nickname")
    private String nickname;

    /**
     * 新头像 URL。
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;

    /**
     * 新用户名，长度 3-20 字符。
     */
    @Size(min = 3, max = 20)
    @JsonProperty("username")
    private String username;

    public UpdateUserInfoRequest(String nickname, String avatarUrl, String username) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.username = username;
    }
}
