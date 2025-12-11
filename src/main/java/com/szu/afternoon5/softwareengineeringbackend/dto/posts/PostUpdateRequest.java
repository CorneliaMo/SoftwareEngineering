package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子更新请求体，包含新的文本与标签列表。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PostUpdateRequest {

    @NotNull
    @JsonProperty("post")
    private PostUpdateContent post;

    @JsonProperty("tags")
    private List<String> tags;

    public PostUpdateRequest(PostUpdateContent post, List<String> tags) {
        this.post = post;
        this.tags = tags;
    }
}
