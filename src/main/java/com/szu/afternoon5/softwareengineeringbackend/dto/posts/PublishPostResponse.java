package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
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
    private PostInfo post;

    public PublishPostResponse(PostInfo post) {
        super();
        this.post = post;
    }
}
