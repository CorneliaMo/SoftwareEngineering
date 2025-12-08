package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * 帖子分页列表响应，包含分页信息与帖子条目集合。
 */
@Data
public class PostListResponse extends PageMeta {

    @JsonProperty("err_code")
    private Integer errCode;

    @JsonProperty("err_msg")
    private String errMsg;

    @JsonProperty("posts")
    private List<PostSummaryItem> posts;
}
