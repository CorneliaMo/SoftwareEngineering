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

    /**
     * 评论ID，唯一标识一条评论。
     */
    @JsonProperty("comment_id")
    private Long commentId;

    /**
     * 帖子ID，标识评论所属的帖子（可为空）。
     */
    @JsonProperty("post_id")
    private Long postId;

    /**
     * 用户ID，发出评论的用户标识（可为空）。
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 父评论ID，用于回复功能，标识被回复的评论（可为空）。
     */
    @JsonProperty("parent_id")
    private Long parentId;

    /**
     * 评论内容，用户输入的评论文本。
     */
    @JsonProperty("comment_text")
    private String commentText;

    /**
     * 创建时间，评论首次发布的时间戳。
     */
    @JsonProperty("created_time")
    private Instant createdTime;

    /**
     * 更新时间，评论最后编辑的时间戳。
     */
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