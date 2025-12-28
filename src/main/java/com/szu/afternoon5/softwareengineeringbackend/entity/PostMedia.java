package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

/**
 * 帖子媒体实体，记录图片或视频等资源信息。
 * <p>
 * 后续可加入排序字段、封面标记或存储桶信息，并考虑为不同媒体类型设置校验规则。
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "post_media")
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    @Nullable
    private Long postId;

    @Nullable
    private Long uploadUserId;

    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    // 用于指示媒体在帖子中的顺序
    @Nullable
    private Integer sortOrder;

    public enum MediaType {
        image, video
    }

    /**
     * 创建媒体记录的构造器，存储关联帖子与上传者信息。
     */
    public PostMedia(Long postId, Long uploadUserId, String mediaUrl, MediaType mediaType, Integer sortOrder) {
        this.mediaId = null;
        this.postId = postId;
        this.uploadUserId = uploadUserId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.sortOrder = sortOrder;
    }
}
