package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 帖子与标签的关联关系，支持多对多映射。
 * <p>
 * 后续可根据业务需要增加排序权重或创建时间，便于统计与推荐。
 */
@Data
@Entity
@NoArgsConstructor
@IdClass(PostTagId.class)
@Table(name = "post_tags")
public class PostTag {

    @Id
    private Long postId;

    @Id
    private Long tagId;

    /**
     * 构造关联记录，要求传入帖子与标签的主键。
     */
    public PostTag(Long postId, Long tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

}