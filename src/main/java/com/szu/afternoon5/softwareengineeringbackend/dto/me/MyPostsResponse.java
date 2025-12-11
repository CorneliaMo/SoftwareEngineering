package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 我的帖子分页列表响应，包含分页信息与帖子条目集合。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MyPostsResponse extends PageMeta {

    /**
     * 帖子列表，包含当前页用户发布的所有帖子及其封面信息。
     */
    @JsonProperty("posts")
    private List<MyPostsItem> posts;

    public MyPostsResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize,
                           List<MyPostsItem> posts) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.posts = posts;
    }
}