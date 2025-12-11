package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 帖子实体，包含文本内容及互动计数。
 * <p>
 * 后续可增加封面、状态机（草稿/发布/隐藏）及 SEO 信息，并考虑为软删除添加恢复逻辑。
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // 冗余字段，储存封面对应的postMediaId
    private Long coverMediaId;

    /**
     * 创建帖子时的便捷构造器，初始化计数与时间戳。
     */
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
        this.coverMediaId = null;
    }

    public Post(Long postId, Long userId, String postTitle, String postText, Boolean isDeleted, Instant deletedTime, Instant createdTime, Instant updatedTime, Integer ratingCount, Integer commentCount, Long coverMediaId) {
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.postText = postText;
        this.isDeleted = isDeleted;
        this.deletedTime = deletedTime;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
        this.coverMediaId = coverMediaId;
    }

    /**
     * 落库前确保创建时间存在，避免空值。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
