package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 帖子详细信息，包含正文内容与时间字段。
 */
@Data
public class PostDetail {

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("created_time")
    private String createdTime;

    @JsonProperty("updated_time")
    private String updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;
}
