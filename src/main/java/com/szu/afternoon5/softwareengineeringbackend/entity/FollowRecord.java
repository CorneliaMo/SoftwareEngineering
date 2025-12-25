package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@IdClass(FollowRecordId.class)
@Table(name = "follows")
public class FollowRecord {

    @Id
    private Long followerId;

    @Id
    private Long followeeId;

    private Instant createdTime;

    public FollowRecord(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.createdTime = Instant.now();
    }
}
