package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子详细信息，包含正文内容与时间字段。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostDetail extends BaseResponse {

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("created_time")
    private String createdTime;

    @JsonProperty("updated_time")
    private String updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public PostDetail(Integer postId, Integer userId, String postTitle, String postText, String createdTime,
                      String updatedTime, Integer ratingCount, Integer commentCount) {
        super();
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.postText = postText;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }

    public PostDetail(ErrorCode errorCode, String errMsg, Integer postId, Integer userId, String postTitle,
                      String postText, String createdTime, String updatedTime, Integer ratingCount,
                      Integer commentCount) {
        super(errorCode, errMsg);
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.postText = postText;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }
}
