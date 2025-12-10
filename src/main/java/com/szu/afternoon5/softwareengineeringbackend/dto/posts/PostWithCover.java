package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.Data;

import java.time.Instant;

@Data
public class PostWithCover {

    private Post post;

    private PostMedia postMedia;

    // 提供给Spring Data JPA的映射构造器
    public PostWithCover(Long postId, Long userId, String postTitle, String postText, Boolean isDeleted, Instant deletedTime, Instant createdTime, Instant updatedTime, Integer ratingCount, Integer commentCount, Long coverMediaId, Long uploadUserId, String mediaUrl, PostMedia.MediaType mediaType, Integer sortOrder) {
        this.post = new Post(postId, userId, postTitle, postText, isDeleted, deletedTime, createdTime, updatedTime, ratingCount, commentCount, coverMediaId);
        this.postMedia = new  PostMedia(coverMediaId, uploadUserId, mediaUrl, mediaType, sortOrder);
    }

}
