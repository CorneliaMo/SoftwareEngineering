package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.MediaInfo;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 我的帖子列表条目，包含帖子摘要与封面媒体。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MyPostsItem {

    @JsonProperty("post")
    private PostInfo post;

    @JsonProperty("cover")
    private MediaInfo cover;

    public MyPostsItem(PostInfo post, MediaInfo cover) {
        this.post = post;
        this.cover = cover;
    }
}