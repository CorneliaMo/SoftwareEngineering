package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 批量删除帖子请求体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminBatchDeletePostsRequest {

    @NotEmpty
    @JsonProperty("post_ids")
    private List<Long> postIds;

}
