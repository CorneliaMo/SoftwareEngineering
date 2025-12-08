package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 媒体文件信息，描述资源标识、类型与访问地址。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MediaInfo {

    @JsonProperty("media_id")
    private Long mediaId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private PostMedia.MediaType mediaType;

    public MediaInfo(PostMedia postMedia) {
        this.mediaId = postMedia.getMediaId();
        this.mediaUrl = postMedia.getMediaUrl();
        this.mediaType = postMedia.getMediaType();
    }
}
