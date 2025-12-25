package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.*;
import com.szu.afternoon5.softwareengineeringbackend.service.PostMediaService;
import com.szu.afternoon5.softwareengineeringbackend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@PreAuthorize("@perm.isUser(authentication.principal)")
public class PostController {

    private final PostMediaService postMediaService;
    private final PostService postService;

    public PostController(PostMediaService postMediaService, PostService postService) {
        this.postMediaService = postMediaService;
        this.postService = postService;
    }

    /**
     * 上传媒体文件。
     *
     * @param request 媒体上传请求
     * @return 上传结果，返回媒体记录信息
     */
    @PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadMediaResponse uploadMedia(@Valid @ModelAttribute UploadMediaRequest request, Authentication authentication) {
        return postMediaService.uploadMedia(request, authentication);
    }

    /**
     * 发布帖子。
     *
     * @param request 帖子发布请求
     * @return 发布结果，包含帖子标识
     */
    @PostMapping("/publish")
    public PublishPostResponse publishPost(@Valid @RequestBody PublishPostRequest request, Authentication authentication) {
        return postService.publish(request, authentication);
    }

    /**
     * 获取帖子列表。
     *
     * @param currentPage 当前页码
     * @param pageSize    页面容量
     * @param userId      用户过滤
     * @return 帖子分页列表
     */
    @GetMapping
    public PostListResponse getPostList(@RequestParam("current_page") Integer currentPage,
                                        @RequestParam("page_size") Integer pageSize,
                                        @RequestParam(value = "user_id") Long userId) {
        return postService.getPostList(currentPage, pageSize, userId);
    }

    /**
     * 获取帖子详情。
     *
     * @param postId 帖子标识
     * @return 帖子详情与媒体列表
     */
    @GetMapping("/detail/{post_id}")
    public PostDetailResponse getPostDetail(@PathVariable("post_id") Long postId) {
        return postService.getPostDetail(postId);
    }

    /**
     * 更新帖子内容及标签。
     *
     * @param postId  帖子标识
     * @param request 更新请求体
     * @return 操作结果
     */
    @PutMapping("/detail/{post_id}")
    @PreAuthorize("@perm.isUser(authentication.principal)")
    public BaseResponse updatePostDetail(@PathVariable("post_id") Long postId,
                                         @Valid @RequestBody PostUpdateRequest request,
                                         Authentication authentication) {
        postService.updatePostDetail(postId, request, authentication);
        return new BaseResponse();
    }

    /**
     * 删除帖子。
     *
     * @param postId 帖子标识
     * @return 操作结果
     */
    @DeleteMapping("/detail/{post_id}")
    @PreAuthorize("@perm.isUser(authentication.principal)")
    public BaseResponse deletePost(@PathVariable("post_id") Long postId, Authentication authentication) {
        postService.deletePost(postId, authentication);
        return new BaseResponse();
    }

    /**
     * 关注者内容流
     */
    @GetMapping("/timeline/following")
    public GetFollowingTimelineResponse getFollowingTimeline(@RequestParam("current_page") Integer currentPage,
                                                             @RequestParam("page_size") Integer pageSize,
                                                             Authentication authentication) {
        return postService.getFollowingTimeline(currentPage, pageSize, authentication);
    }
}
