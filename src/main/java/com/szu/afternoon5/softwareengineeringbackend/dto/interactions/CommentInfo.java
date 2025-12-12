package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
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

    /**
     * 用户ID，评论发布者的唯一标识。
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 用户昵称，评论发布者的显示名称。
     */
    @JsonProperty("nickname")
    private String nickname;

    /**
     * 用户头像URL，评论发布者的头像地址（可为空）。
     */
    @JsonProperty("avatar_url")
    private String avatarUrl;

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

    public CommentInfo(Comment comment) {
        this.userId = comment.getUserId();

    }
}