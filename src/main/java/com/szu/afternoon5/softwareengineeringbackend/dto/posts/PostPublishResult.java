package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发布帖子后返回的简要结果，包含帖子信息标识。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostPublishResult extends BaseResponse {

    @JsonProperty("id")
    private PostInfo id;

    public PostPublishResult(PostInfo id) {
        super();
        this.id = id;
    }

    public PostPublishResult(ErrorCode errorCode, String errMsg, PostInfo id) {
        super(errorCode, errMsg);
        this.id = id;
    }
}
