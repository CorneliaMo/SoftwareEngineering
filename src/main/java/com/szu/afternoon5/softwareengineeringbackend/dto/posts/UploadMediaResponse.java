package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 媒体上传响应体，返回媒体文件的记录信息。
 */
@Data
public class UploadMediaResponse {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("post_media")
    private PostMediaItem postMedia;
}
