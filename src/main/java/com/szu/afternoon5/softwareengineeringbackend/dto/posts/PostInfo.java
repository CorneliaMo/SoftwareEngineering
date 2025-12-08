package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子摘要信息，用于列表场景。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostInfo extends BaseResponse {

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("updated_time")
    private String updatedTime;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("comment_count")
    private Integer commentCount;

    public PostInfo(Integer postId, Integer userId, String postTitle, String updatedTime, Integer ratingCount,
                    Integer commentCount) {
        super();
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.updatedTime = updatedTime;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }

    public PostInfo(ErrorCode errorCode, String errMsg, Integer postId, Integer userId, String postTitle,
                    String updatedTime, Integer ratingCount, Integer commentCount) {
        super(errorCode, errMsg);
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.updatedTime = updatedTime;
        this.ratingCount = ratingCount;
        this.commentCount = commentCount;
    }
}
