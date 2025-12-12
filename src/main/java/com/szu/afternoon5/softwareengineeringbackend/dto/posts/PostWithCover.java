package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PostWithCover {

    private Long postId;

    private Long userId;

    private String postTitle;

    private String postText;

    private Boolean isDeleted;

    private Instant deletedTime;

    private Instant createdTime;

    private Instant updatedTime;

    private Integer ratingCount;

    private Integer commentCount;

    private Long coverMediaId;

    private Long uploadUserId;

    private String mediaUrl;

    private PostMedia.MediaType mediaType;

    private Integer sortOrder;

    public Post getPost() {
        return new Post(postId, userId, postTitle, postText, isDeleted, deletedTime, createdTime, updatedTime, ratingCount, commentCount, coverMediaId);
    }

    public PostMedia getPostMedia() {
        return new PostMedia(coverMediaId, uploadUserId, mediaUrl, mediaType, sortOrder);
    }
}
