package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子列表条目，包含摘要与封面媒体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PostSummaryItem {

    @JsonProperty("post")
    private PostInfo post;

    @JsonProperty("cover")
    private MediaInfo cover;

    public PostSummaryItem(PostInfo post, MediaInfo cover) {
        this.post = post;
        this.cover = cover;
    }
}
