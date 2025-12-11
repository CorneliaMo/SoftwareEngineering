package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * 帖子摘要信息，用于列表场景。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PostInfo {

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("updated_time")
    private OffsetDateTime updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public PostInfo(Post post) {
        this.postId = post.getPostId();
        this.userId = post.getUserId();
        this.postTitle = post.getPostTitle();
        this.updatedTime = post.getUpdatedTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        this.ratingCount = post.getRatingCount();
        this.commentCount = post.getCommentCount();
    }
}
