package com.szu.afternoon5.softwareengineeringbackend.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
