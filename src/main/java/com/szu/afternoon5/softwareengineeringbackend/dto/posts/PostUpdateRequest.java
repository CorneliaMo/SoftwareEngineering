package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子更新请求体，包含新的文本与标签列表。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostUpdateRequest extends BaseResponse {

    @JsonProperty("post")
    private PostUpdateContent post;

    @JsonProperty("tags")
    private List<String> tags;

    public PostUpdateRequest(PostUpdateContent post, List<String> tags) {
        super();
        this.post = post;
        this.tags = tags;
    }

    public PostUpdateRequest(ErrorCode errorCode, String errMsg, PostUpdateContent post, List<String> tags) {
        super(errorCode, errMsg);
        this.post = post;
        this.tags = tags;
    }
}
