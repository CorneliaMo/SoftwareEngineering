package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

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

    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
