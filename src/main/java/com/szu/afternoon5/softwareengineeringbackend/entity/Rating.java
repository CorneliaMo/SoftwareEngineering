package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 评分实体，记录用户对帖子的评分值。
 * <p>
 * 未来可拓展评分维度（如多指标评分）、防重复校验以及统计字段（平均分、总分）。
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    private Long postId;

    private Long userId;

    private Integer ratingValue;

    private Instant createdTime;

    private Instant updatedTime;

    /**
     * 创建评分记录的构造器，默认填充时间戳。
     */
    public Rating(Long postId, Long userId, Integer ratingValue) {
        this.ratingId = null;
        this.postId = postId;
        this.userId = userId;
        this.ratingValue = ratingValue;
        this.createdTime = Instant.now();
        this.updatedTime = Instant.now();
    }

    /**
     * 在插入前补齐创建时间，保证审计一致性。
     */
    @PrePersist
    public void onCreate() {
        if (this.createdTime == null) {
            this.createdTime = Instant.now();
        }
    }
}
