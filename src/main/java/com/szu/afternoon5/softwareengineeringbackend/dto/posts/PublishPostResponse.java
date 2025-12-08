package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 发布帖子响应体，返回创建成功的帖子标识。
 */
@Data
public class PublishPostResponse {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("post")
    private PostPublishResult post;
}
