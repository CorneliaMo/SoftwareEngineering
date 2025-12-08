package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * 帖子更新请求体，包含新的文本与标签列表。
 */
@Data
public class PostUpdateRequest {

    @JsonProperty("post")
    private PostUpdateContent post;

    @JsonProperty("tags")
    private List<String> tags;
}
