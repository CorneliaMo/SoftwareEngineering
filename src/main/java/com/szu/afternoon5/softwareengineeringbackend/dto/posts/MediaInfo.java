package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 媒体文件信息，描述资源标识、类型与访问地址。
 */
@Data
public class MediaInfo {

    @JsonProperty("media_id")
    private Integer mediaId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private String mediaType;
}
