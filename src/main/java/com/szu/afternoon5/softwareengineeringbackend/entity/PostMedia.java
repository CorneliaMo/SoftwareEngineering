package com.szu.afternoon5.softwareengineeringbackend.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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

    public PostMedia(Long postId, Long uploadUserId, String mediaUrl, MediaType mediaType) {
        this.mediaId = null;
        this.postId = postId;
        this.uploadUserId = uploadUserId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
