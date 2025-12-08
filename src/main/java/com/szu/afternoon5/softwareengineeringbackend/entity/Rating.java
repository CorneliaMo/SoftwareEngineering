package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Table(schema = "ratings")
public class Rating {

    @Id
    private Long ratingId;

    private Long postId;

    private Long userId;

    private Integer ratingValue;

    private Instant createdTime;

    private Instant updatedTime;

    public Rating(Long postId, Long userId, Integer ratingValue) {
        this.ratingId = null;
        this.postId = postId;
        this.userId = userId;
        this.ratingValue = ratingValue;
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
