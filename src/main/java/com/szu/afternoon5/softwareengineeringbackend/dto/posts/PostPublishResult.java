package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 发布帖子后返回的简要结果，包含帖子信息标识。
 */
@Data
public class PostPublishResult {

    @JsonProperty("id")
    private PostInfo id;
}
