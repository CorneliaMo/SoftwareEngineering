package com.szu.afternoon5.softwareengineeringbackend.dto.admin;

import com.szu.afternoon5.softwareengineeringbackend.dto.PageMeta;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCover;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 管理端帖子列表响应体。
 * 对应接口文档中 GET /admin/posts 接口的数据部分。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdminPostListResponse extends PageMeta {

    private List<PostWithCover> posts;

    public AdminPostListResponse(Integer totalPage, Integer totalCount, Integer currentPage, Integer pageSize, List<PostWithCover> posts) {
        super(totalPage, totalCount, currentPage, pageSize);
        this.posts = posts;
    }
}