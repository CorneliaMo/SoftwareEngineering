package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.MediaInfo;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostInfo;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCover;
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

    /**
     * 帖子摘要信息，包含帖子的基本元数据。
     */
    @JsonProperty("post")
    private PostInfo post;

    /**
     * 封面媒体信息，帖子的封面图片或视频。
     */
    @JsonProperty("cover")
    private MediaInfo cover;

    public MyPostsItem(PostInfo post, MediaInfo cover) {
        this.post = post;
        this.cover = cover;
    }

    public MyPostsItem(PostWithCover postWithCover) {
        this.post = new PostInfo(postWithCover.getPost());
        this.cover = new MediaInfo(postWithCover.getPostMedia());
    }
}
