package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 媒体上传响应体，返回媒体文件的记录信息。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UploadMediaResponse extends BaseResponse {

    @JsonProperty("post_media")
    private PostMediaItem postMedia;

    public UploadMediaResponse(PostMediaItem postMedia) {
        super();
        this.postMedia = postMedia;
    }
}
