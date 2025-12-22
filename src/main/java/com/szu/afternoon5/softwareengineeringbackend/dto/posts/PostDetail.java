package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 帖子详细信息，包含正文内容与时间字段。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PostDetail {

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("created_time")
    private OffsetDateTime createdTime;

    @JsonProperty("updated_time")
    private OffsetDateTime updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    @JsonProperty("tags")
    private List<TagInfo> tags;

    public PostDetail(Post post, List<TagInfo> tags) {
        this.postId = post.getPostId();
        this.userId = post.getUserId();
        this.postTitle = post.getPostTitle();
        this.postText = post.getPostText();
        this.createdTime = post.getCreatedTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        this.updatedTime = post.getUpdatedTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        this.ratingCount = post.getRatingCount();
        this.commentCount = post.getCommentCount();
        this.tags = tags;
    }
}
