package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发布帖子响应体，返回创建成功的帖子标识。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PublishPostResponse extends BaseResponse {

    @JsonProperty("post")
    private PostPublishResult post;

    public PublishPostResponse(PostPublishResult post) {
        super();
        this.post = post;
    }

    public PublishPostResponse(ErrorCode errorCode, String errMsg, PostPublishResult post) {
        super(errorCode, errMsg);
        this.post = post;
    }
}
