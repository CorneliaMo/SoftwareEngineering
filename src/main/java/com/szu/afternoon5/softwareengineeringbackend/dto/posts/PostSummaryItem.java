package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子列表条目，包含摘要与封面媒体。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostSummaryItem extends BaseResponse {

    @JsonProperty("post")
    private PostInfo post;

    @JsonProperty("cover")
    private MediaInfo cover;

    public PostSummaryItem(PostInfo post, MediaInfo cover) {
        super();
        this.post = post;
        this.cover = cover;
    }

    public PostSummaryItem(ErrorCode errorCode, String errMsg, PostInfo post, MediaInfo cover) {
        super(errorCode, errMsg);
        this.post = post;
        this.cover = cover;
    }
}
