package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 帖子媒体资源信息，覆盖媒体标识、归属与类型。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostMediaItem extends BaseResponse {

    @JsonProperty("media_id")
    private Integer mediaId;

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("upload_user_id")
    private Integer uploadUserId;

    @JsonProperty("media_url")
    private String mediaUrl;

    @JsonProperty("media_type")
    private String mediaType;

    public PostMediaItem(Integer mediaId, Integer postId, Integer uploadUserId, String mediaUrl, String mediaType) {
        super();
        this.mediaId = mediaId;
        this.postId = postId;
        this.uploadUserId = uploadUserId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }

    public PostMediaItem(ErrorCode errorCode, String errMsg, Integer mediaId, Integer postId, Integer uploadUserId,
                         String mediaUrl, String mediaType) {
        super(errorCode, errMsg);
        this.mediaId = mediaId;
        this.postId = postId;
        this.uploadUserId = uploadUserId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
