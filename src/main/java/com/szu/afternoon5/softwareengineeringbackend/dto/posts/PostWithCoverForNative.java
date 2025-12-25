package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@AllArgsConstructor
public class PostWithCoverForNative {

    private Integer postId;

    private Integer userId;

    private String postTitle;

    private String postText;

    private Boolean isDeleted;

    private LocalDateTime deletedTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Integer ratingCount;

    private Integer commentCount;

    private Integer coverMediaId;

    private Integer uploadUserId;

    private String mediaUrl;

    private String mediaType;

    private Short sortOrder;

    private Boolean hasImage;

    private Boolean hasVideo;

    public Post getPost() {
        return new Post(
                postId == null? null : Long.valueOf(postId),
                userId == null? null : Long.valueOf(userId),
                postTitle,
                postText,
                isDeleted,
                deletedTime == null? null : deletedTime.toInstant(ZoneOffset.UTC),
                createdTime == null? null : createdTime.toInstant(ZoneOffset.UTC),
                updatedTime == null? null : updatedTime.toInstant(ZoneOffset.UTC),
                ratingCount,
                commentCount,
                coverMediaId == null? null : Long.valueOf(coverMediaId),
                hasImage,
                hasVideo);
    }

    public PostMedia getPostMedia() {
        return new PostMedia(
                coverMediaId == null ? null : Long.valueOf(coverMediaId),
                uploadUserId == null? null : Long.valueOf(uploadUserId),
                mediaUrl,
                mediaType == null? null : PostMedia.MediaType.valueOf(mediaType),
                sortOrder == null? null : Integer.valueOf(sortOrder));
    }
}
