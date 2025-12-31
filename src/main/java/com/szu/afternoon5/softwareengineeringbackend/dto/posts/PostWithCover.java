package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 帖子与封面媒体的聚合视图
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private Boolean hasImage;

    private Boolean hasVideo;

    /**
     * 还原帖子实体
     */
    public Post getPost() {
        return new Post(postId, userId, postTitle, postText, isDeleted, deletedTime, createdTime, updatedTime, ratingCount, commentCount, coverMediaId, hasImage, hasVideo);
    }

    /**
     * 还原封面媒体实体
     */
    public PostMedia getPostMedia() {
        return new PostMedia(coverMediaId, uploadUserId, mediaUrl, mediaType, sortOrder);
    }
}
