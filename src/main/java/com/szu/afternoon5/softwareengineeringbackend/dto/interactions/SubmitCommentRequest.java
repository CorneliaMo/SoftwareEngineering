package com.szu.afternoon5.softwareengineeringbackend.dto.interactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发表评论请求体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SubmitCommentRequest {

    /**
     * 评论内容。
     */
    @NotBlank
    @Size(max = 500)
    @JsonProperty("comment_text")
    private String commentText;

    public SubmitCommentRequest(String commentText) {
        this.commentText = commentText;
    }
}
