package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 帖子更新内容，允许修改标题、正文与评论数量。
 */
@Data
public class PostUpdateContent {

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("comment_count")
    private Integer commentCount;
}
