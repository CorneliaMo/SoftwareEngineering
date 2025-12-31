package com.szu.afternoon5.softwareengineeringbackend.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 帖子标签复合主键
 */
@NoArgsConstructor
@EqualsAndHashCode
public class PostTagId implements Serializable {

    private Long postId;
    private Long tagId;

    public PostTagId(Long postId, Long tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}
