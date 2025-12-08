package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 帖子摘要信息，用于列表场景。
 */
@Data
public class PostInfo {

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("updated_time")
    private String updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;
}
