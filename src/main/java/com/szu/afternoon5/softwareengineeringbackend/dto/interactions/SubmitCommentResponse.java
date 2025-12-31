package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发表评论响应体，返回新创建评论的标识。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SubmitCommentResponse extends BaseResponse {

    /**
     * 新创建的评论ID，用于标识刚发布的评论。
     */
    @JsonProperty("comment_id")
    private Long commentId;

    public SubmitCommentResponse(Long commentId) {
        super();
        this.commentId = commentId;
    }
}
