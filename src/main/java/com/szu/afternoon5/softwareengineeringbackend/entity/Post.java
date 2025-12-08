package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * 帖子实体，包含文本内容及互动计数。
 * <p>
 * 后续可增加封面、状态机（草稿/发布/隐藏）及 SEO 信息，并考虑为软删除添加恢复逻辑。
 */
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
