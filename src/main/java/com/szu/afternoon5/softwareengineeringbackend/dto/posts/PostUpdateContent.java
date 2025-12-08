package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子更新内容，允许修改标题、正文与评论数量。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostUpdateContent extends BaseResponse {

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public PostUpdateContent(String postTitle, String postText, Integer commentCount) {
        super();
        this.postTitle = postTitle;
        this.postText = postText;
        this.commentCount = commentCount;
    }

    public PostUpdateContent(ErrorCode errorCode, String errMsg, String postTitle, String postText, Integer commentCount) {
        super(errorCode, errMsg);
        this.postTitle = postTitle;
        this.postText = postText;
        this.commentCount = commentCount;
    }
}
