package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(schema = "post_tags")
public class PostTag {

    @Id
    private Long postId;

    @Id
    private Long tagId;

    public PostTag(Long postId, Long tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}