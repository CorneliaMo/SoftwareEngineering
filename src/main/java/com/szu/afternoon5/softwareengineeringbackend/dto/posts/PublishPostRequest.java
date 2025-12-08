package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * 发布帖子请求体，包含帖子类型、标题、正文、标签与媒体引用。
 */
@Data
public class PublishPostRequest {

    @JsonProperty("post_type")
    private Integer postType;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("media_ids")
    private List<Integer> mediaIds;
}
