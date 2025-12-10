package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 评论分页列表响应，包含分页信息与评论条目集合。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommentListResponse extends PageMeta {

    @JsonProperty("comments")
    private List<CommentInfo> comments;

    public CommentListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize,
                               List<CommentInfo> comments) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.comments = comments;
    }
}