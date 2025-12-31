package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.me.*;
import com.szu.afternoon5.softwareengineeringbackend.service.MeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人中心控制器，用于获取和更新当前登录用户的信息。
 * <p>
 * 提供获取/更新用户信息、我的帖子列表、我的评论列表等功能。
 */
@RestController
@RequestMapping("/me")
@PreAuthorize("@perm.isUser(authentication.principal)")
public class MeController {

    private final MeService meService;

    /**
     * 构建个人中心控制器并注入依赖。
     */
    public MeController(MeService meService) {
        this.meService = meService;
    }

    /**
     * 获取本用户信息。
     *
     * @param authentication 认证信息
     * @return 当前用户的详细信息
     */
    @GetMapping("/info")
    public UserInfoResponse getUserInfo(Authentication authentication) {
        return meService.getUserInfo(authentication);
    }

    /**
     * 更新本用户信息。
     *
     * @param request        用户信息更新请求
     * @param authentication 认证信息
     * @return 更新后的用户信息
     */
    @PutMapping("/info")
    public UserInfoResponse updateUserInfo(@Valid @RequestBody UpdateUserInfoRequest request,
                                           Authentication authentication) {
        return meService.updateUserInfo(request, authentication);
    }

    /**
     * 获取我的帖子列表。
     *
     * @param currentPage    当前页码
     * @param pageSize       每页数量
     * @param authentication 认证信息
     * @return 当前用户发布的分页帖子列表
     */
    @GetMapping("/posts")
    public MyPostsResponse getMyPosts(@RequestParam(value = "current_page", defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize,
                                      Authentication authentication) {
        return meService.getMyPosts(currentPage, pageSize, authentication);
    }

    /**
     * 获取我的评论列表。
     *
     * @param currentPage    当前页码（可选）
     * @param pageSize       每页数量（可选）
     * @param authentication 认证信息
     * @return 当前用户发布的分页评论列表
     */
    @GetMapping("/comments")
    public MyCommentsResponse getMyComments(@RequestParam(value = "current_page", required = false) Integer currentPage,
                                            @RequestParam(value = "page_size", required = false) Integer pageSize,
                                            Authentication authentication) {
        return meService.getMyComments(currentPage, pageSize, authentication);
    }

    /**
     * 上传并更新用户头像。
     *
     * @param request        头像上传请求体
     * @param authentication 认证信息
     * @return 操作结果
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadAvatar(@ModelAttribute UploadAvatarRequest request, Authentication authentication) {
        meService.uploadAvatar(request, authentication);
        return new BaseResponse();
    }
}
