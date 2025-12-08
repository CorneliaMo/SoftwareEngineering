package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 帖子媒体资源信息，覆盖媒体标识、归属与类型。
 */
@Data
public class PostMediaItem {

    @JsonProperty("media_id")
    private Integer mediaId;

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("upload_user_id")
    private Integer uploadUserId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private String mediaType;
}
