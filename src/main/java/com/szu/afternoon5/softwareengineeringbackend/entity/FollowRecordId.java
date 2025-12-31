package com.szu.afternoon5.softwareengineeringbackend.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 关注关系复合主键
 */
@NoArgsConstructor
@EqualsAndHashCode
public class FollowRecordId implements Serializable {

    private Long followerId;
    private Long followeeId;

    public FollowRecordId(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }
}
