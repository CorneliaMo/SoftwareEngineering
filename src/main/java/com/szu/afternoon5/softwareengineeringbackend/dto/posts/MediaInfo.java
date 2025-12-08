package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 媒体文件信息，描述资源标识、类型与访问地址。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MediaInfo extends BaseResponse {

    @JsonProperty("media_id")
    private Integer mediaId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private String mediaType;

    public MediaInfo(Integer mediaId, String mediaUrl, String mediaType) {
        super();
        this.mediaId = mediaId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }

    public MediaInfo(ErrorCode errorCode, String errMsg, Integer mediaId, String mediaUrl, String mediaType) {
        super(errorCode, errMsg);
        this.mediaId = mediaId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
