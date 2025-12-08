package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 发布帖子请求体，包含帖子类型、标题、正文、标签与媒体引用。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PublishPostRequest extends BaseResponse {

    @JsonProperty("post_type")
    private Integer postType;

    @JsonProperty("post_title")
    private String postTitle;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("media_ids")
    private List<Integer> mediaIds;

    public PublishPostRequest(Integer postType, String postTitle, String postText, List<String> tags,
                              List<Integer> mediaIds) {
        super();
        this.postType = postType;
        this.postTitle = postTitle;
        this.postText = postText;
        this.tags = tags;
        this.mediaIds = mediaIds;
    }

    public PublishPostRequest(ErrorCode errorCode, String errMsg, Integer postType, String postTitle, String postText,
                              List<String> tags, List<Integer> mediaIds) {
        super(errorCode, errMsg);
        this.postType = postType;
        this.postTitle = postTitle;
        this.postText = postText;
        this.tags = tags;
        this.mediaIds = mediaIds;
    }
}
