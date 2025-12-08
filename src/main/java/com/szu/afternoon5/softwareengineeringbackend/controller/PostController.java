package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.SimpleResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostDetailResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostUpdateRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PublishPostRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PublishPostResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.UploadMediaRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.UploadMediaResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子内容控制器，负责帖子创建、查询与更新等操作。
 * <p>
 * 未来可添加分页查询、媒体上传、草稿保存等接口，并与 {@code Post}、{@code PostMedia} 等实体联动。
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    /**
     * 上传媒体文件。
     *
     * @param request 媒体上传请求
     * @return 上传结果，返回媒体记录信息
     */
    @PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadMediaResponse uploadMedia(@ModelAttribute UploadMediaRequest request) {
        return null;
    }

    /**
     * 发布帖子。
     *
     * @param request 帖子发布请求
     * @return 发布结果，包含帖子标识
     */
    @PostMapping("/publish")
    public PublishPostResponse publish(@RequestBody PublishPostRequest request) {
        return null;
    }

    /**
     * 获取帖子列表。
     *
     * @param currentPage 当前页码
     * @param pageSize    页面容量
     * @param keyword     搜索关键词
     * @param userId      可选的用户过滤
     * @return 帖子分页列表
     */
    @GetMapping
    public PostListResponse list(@RequestParam("current_page") Integer currentPage,
                                 @RequestParam("page_size") Integer pageSize,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "user_id", required = false) String userId) {
        return null;
    }

    /**
     * 获取帖子详情。
     *
     * @param postId 帖子标识
     * @return 帖子详情与媒体列表
     */
    @GetMapping("/detail/{post_id}")
    public PostDetailResponse detail(@PathVariable("post_id") Integer postId) {
        return null;
    }

    /**
     * 更新帖子内容及标签。
     *
     * @param postId  帖子标识
     * @param request 更新请求体
     * @return 操作结果
     */
    @PutMapping("/detail/{post_id}")
    public SimpleResponse update(@PathVariable("post_id") Integer postId,
                                 @RequestBody PostUpdateRequest request) {
        return null;
    }

    /**
     * 删除帖子。
     *
     * @param postId 帖子标识
     * @return 操作结果
     */
    @DeleteMapping("/detail/{post_id}")
    public SimpleResponse delete(@PathVariable("post_id") Integer postId) {
        return null;
    }
}
