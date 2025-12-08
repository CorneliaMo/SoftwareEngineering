package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子分页列表响应，包含分页信息与帖子条目集合。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostListResponse extends PageMeta {

    @JsonProperty("posts")
    private List<PostSummaryItem> posts;

    public PostListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize,
                            List<PostSummaryItem> posts) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.posts = posts;
    }

    public PostListResponse(ErrorCode errorCode, String errMsg, Integer totalPage, Integer totalCount,
                            Integer currentPage, Integer pageSize, List<PostSummaryItem> posts) {
        super(errorCode, errMsg, totalPage, totalCount, currentPage, pageSize);
        this.posts = posts;
    }
}
