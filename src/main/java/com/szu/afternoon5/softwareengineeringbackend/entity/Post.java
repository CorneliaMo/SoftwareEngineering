package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Table(schema = "posts")
public class Post {

    @Id
    private Long postId;

    private Long userId;

    private String postTitle;

    private String postText;

    private Boolean isDeleted;

    private Instant deletedTime;

    private Instant createdTime;

    private Instant updatedTime;

    private Integer ratingCount;

    private Integer commentCount;

    public Post(Long userId, String postTitle, String postText) {
        this.postId = null;
        this.userId = userId;
        this.postTitle = postTitle;
        this.postText = postText;
        this.isDeleted = Boolean.FALSE;
        this.deletedTime = null;
        this.createdTime = Instant.now();
        this.updatedTime = Instant.now();
        this.ratingCount = 0;
        this.commentCount = 0;
    }

    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
