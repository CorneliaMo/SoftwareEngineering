package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 帖子媒体实体，记录图片或视频等资源信息。
 * <p>
 * 后续可加入排序字段、封面标记或存储桶信息，并考虑为不同媒体类型设置校验规则。
 */
@Data
@Table(schema = "post_media")
public class PostMedia {

    @Id
    private Long mediaId;

    private Long postId;

    private Long uploadUserId;

    private String mediaUrl;

    private MediaType mediaType;

    public enum MediaType {
        image, video
    }

    /**
     * 创建媒体记录的构造器，存储关联帖子与上传者信息。
     */
    public PostMedia(Long postId, Long uploadUserId, String mediaUrl, MediaType mediaType) {
        this.mediaId = null;
        this.postId = postId;
        this.uploadUserId = uploadUserId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
