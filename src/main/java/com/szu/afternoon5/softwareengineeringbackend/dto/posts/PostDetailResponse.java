package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个帖子详情响应，包含帖子主体与媒体列表。
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostDetailResponse extends BaseResponse {

    @JsonProperty("post")
    private PostDetail post;

    @JsonProperty("medias")
    private List<MediaInfo> medias;

    public PostDetailResponse(PostDetail post, List<MediaInfo> medias) {
        super();
        this.post = post;
        this.medias = medias;
    }
}
