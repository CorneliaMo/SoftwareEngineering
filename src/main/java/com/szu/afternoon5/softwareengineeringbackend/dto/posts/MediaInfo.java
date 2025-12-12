package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 媒体文件信息，用于帖子封面等场景。
 */
@Getter
@Setter
@ToString
public class MediaInfo {

    @JsonProperty("media_id")
    private Long mediaId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private String mediaType;

    public MediaInfo(PostMedia postMedia) {
        this.mediaId = postMedia.getMediaId();
        this.mediaUrl = postMedia.getMediaUrl();
        this.mediaType = postMedia.getMediaType().toString();
    }

    public MediaInfo(Long mediaId, String mediaUrl, String mediaType) {
        this.mediaId = mediaId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
