package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.MediaInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * 管理端帖子列表响应体。
 * 对应接口文档中 GET /admin/posts 接口的完整响应结构。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AdminPostListResponse extends BaseResponse {

    @JsonProperty("total_page")
    private Integer totalPage;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("posts")
    private List<AdminPostItem> posts;

    @Getter
    @Setter
    @ToString
    public static class AdminPostItem {
        @JsonProperty("post")
        private PostSummary post;

        @JsonProperty("cover")
        private MediaInfo cover;

        @Getter
        @Setter
        @ToString
        public static class PostSummary {
            @JsonProperty("post_id")
            private Long postId;

            @JsonProperty("user_id")
            private Long userId;

            @JsonProperty("post_title")
            private String postTitle;

            @JsonProperty("updated_time")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            private Instant updatedTime;

            @JsonProperty("rating_count")
            private Integer ratingCount;

            @JsonProperty("comment_count")
            private Integer commentCount;
        }
    }
}