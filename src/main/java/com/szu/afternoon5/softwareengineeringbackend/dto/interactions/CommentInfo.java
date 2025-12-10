package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * 评论信息视图，用于展示评论详情。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CommentInfo {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("comment_text")
    private String commentText;

    @JsonProperty("created_time")
    private Instant createdTime;

    @JsonProperty("updated_time")
    private Instant updatedTime;

    public CommentInfo(Long userId, String nickname, String avatarUrl, Long commentId, Long postId, Long parentId,
                       String commentText, Instant createdTime, Instant updatedTime) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.commentId = commentId;
        this.postId = postId;
        this.parentId = parentId;
        this.commentText = commentText;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}