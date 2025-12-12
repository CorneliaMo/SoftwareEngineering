package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 帖子摘要信息，用于列表展示。
 */
@Getter
@Setter
@ToString
public class PostInfo {

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("updated_time")
    private Instant updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public PostInfo(Post post) {
        this.postId = post.getPostId();
        this.userId = post.getUserId();
        this.postTitle = post.getPostTitle();
        this.updatedTime = post.getUpdatedTime();
        this.ratingCount = post.getRatingCount();
        this.commentCount = post.getCommentCount();
    }

    public PostInfo(Long postId, Long userId, String postTitle, Instant updatedTime,
                    Integer ratingCount, Integer commentCount) {
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.updatedTime = updatedTime;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }
}
