package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 删除评论请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DeleteCommentRequest {

    /**
     * 要删除的评论标识。
     */
    @NotNull
    @JsonProperty("comment_id")
    private Long commentId;

    public DeleteCommentRequest(Long commentId) {
        this.commentId = commentId;
    }
}