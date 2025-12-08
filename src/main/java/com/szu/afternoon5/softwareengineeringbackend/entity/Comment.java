package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * 评论实体，记录用户对帖子或其他评论的回复。
 * <p>
 * 后续可添加楼层、点赞数、审核状态等字段，并结合软删除标记实现回收站能力。
 */
@Data
@Table(schema = "comments")
public class Comment {

    @Id
    private Long commentId;

    private Long postId;

    private Long userId;

    private Long parentId;

    private String commentText;

    private Boolean isDeleted;

    private Instant createdTime;

    private Instant updatedTime;

    /**
     * 创建评论时的便捷构造器，默认未删除并记录创建、更新时间。
     */
    public Comment(Long postId, Long userId, Long parentId, String commentText) {
        this.commentId = null;
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.commentText = commentText;
        this.isDeleted = Boolean.FALSE;
        this.createdTime = Instant.now();
        this.updatedTime = Instant.now();
    }

    /**
     * 在入库前补全创建时间，避免空值。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
