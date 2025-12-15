package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * 管理端用户列表响应体。
 * <p>
 * 对应接口文档中 GET /admin/users 接口的完整响应结构。
 * </p>
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AdminUserListResponse extends BaseResponse {

    @JsonProperty("total_page")
    private Integer totalPage;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("users")
    private List<AdminUserItem> users;

    @Getter
    @Setter
    @ToString
    public static class AdminUserItem {
        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;

        @JsonProperty("email")
        private String email;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("status")
        private Integer status;

        @JsonProperty("created_time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Instant createdTime;

        @JsonProperty("updated_time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Instant updatedTime;

        @JsonProperty("post_count")
        private Integer postCount;

        @JsonProperty("rating_count")
        private Integer ratingCount;

        @JsonProperty("comment_count")
        private Integer commentCount;
    }
}