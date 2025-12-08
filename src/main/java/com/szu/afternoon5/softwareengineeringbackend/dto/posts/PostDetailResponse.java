package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * 单个帖子详情响应，包含帖子主体与媒体列表。
 */
@Data
public class PostDetailResponse {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("post")
    private PostDetail post;

    @JsonProperty("medias")
    private List<MediaInfo> medias;
}
