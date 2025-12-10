package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 我的评论列表条目。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MyCommentsItem {

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("comment_text")
    private String commentText;

    @JsonProperty("created_time")
    private Instant createdTime;

    @JsonProperty("updated_time")
    private Instant updatedTime;

    public MyCommentsItem(Long commentId, Long postId, Long userId, Long parentId, String commentText,
                          Instant createdTime, Instant updatedTime) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.commentText = commentText;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}