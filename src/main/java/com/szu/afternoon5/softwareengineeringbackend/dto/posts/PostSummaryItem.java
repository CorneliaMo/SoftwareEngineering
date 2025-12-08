package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 帖子列表条目，包含摘要与封面媒体。
 */
@Data
public class PostSummaryItem {

    @JsonProperty("post")
    private PostInfo post;

    @JsonProperty("cover")
    private MediaInfo cover;
}
