package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 我的评论分页列表响应，包含分页信息与评论条目集合。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MyCommentsResponse extends PageMeta {

    @JsonProperty("comments")
    private List<MyCommentsItem> comments;

    public MyCommentsResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize,
                              List<MyCommentsItem> comments) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.comments = comments;
    }
}