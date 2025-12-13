package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子媒体资源信息，覆盖媒体标识、归属与类型。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PostMediaItem {

    @JsonProperty("media_id")
    private Long mediaId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("upload_user_id")
    private Long uploadUserId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private PostMedia.MediaType mediaType;

    public PostMediaItem(PostMedia postMedia) {
        this.mediaId = postMedia.getMediaId();
        this.postId = postMedia.getPostId();
        this.uploadUserId = postMedia.getUploadUserId();
        this.mediaUrl = postMedia.getMediaUrl();
        this.mediaType = postMedia.getMediaType();
    }
}
