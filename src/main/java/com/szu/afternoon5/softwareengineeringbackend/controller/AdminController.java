package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminBatchDeletePostsRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminCreateRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminPostListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminResetPasswordRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminUpdateRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminUserListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.ContentFilterCreateRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.ContentFilterListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.ContentFilterUpdateRequest;
import com.szu.afternoon5.softwareengineeringbackend.service.AdminService;
import com.szu.afternoon5.softwareengineeringbackend.service.ContentFilterService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端接口控制器，面向后台运营。
 * 实现了接口文档中的4个管理模块接口：
 * 1. GET /admin/users - 获取用户列表
 * 2. DELETE /admin/users/{user_id} - 删除用户
 * 3. GET /admin/posts - 内容管理列表
 * 4. DELETE /admin/posts/{post_id} - 删除内容（管理）
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("@perm.isAdmin(authentication.principal)")
public class AdminController {

    private final AdminService adminService;
    private final ContentFilterService contentFilterService;

    public AdminController(AdminService adminService, ContentFilterService contentFilterService) {
        this.adminService = adminService;
        this.contentFilterService = contentFilterService;
    }

    /**
     * 获取用户列表（管理端）
     * GET /admin/users
     * 响应格式：{err_code, err_msg, total_page, total_count, current_page, page_size, users}
     */
    @GetMapping("/users")
    public AdminUserListResponse getUserList(
            @RequestParam(value = "current_page", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "user_id", required = false) Long userId) {

        return adminService.adminGetUserList(currentPage, pageSize, nickname, username, userId);
    }

    /**
     * 删除用户
     * DELETE /admin/users/{user_id}
     * 响应格式：{err_code, err_msg}
     */
    @DeleteMapping("/users/{user_id}")
    public BaseResponse deleteUser(@PathVariable("user_id") Long userId, Authentication authentication) {
        adminService.adminDeleteUser(userId, authentication);
        return new BaseResponse();
    }

    /**
     * 内容管理列表
     * GET /admin/posts
     * 参数：current_page, page_size, user_id(可选)
     * 响应格式：{err_code, err_msg, total_page, total_count, current_page, page_size, posts}
     */
    @GetMapping("/posts")
    public AdminPostListResponse getPostList(
            @RequestParam("current_page") Integer currentPage,
            @RequestParam("page_size") Integer pageSize,
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "post_type", required = false) String postType) {

        return adminService.getAdminPostList(currentPage, pageSize, userId, username, nickname, postType);
    }

    /**
     * 新增管理员
     * POST /admin/admins
     */
    @PostMapping("/admins")
    public BaseResponse createAdmin(@Valid @RequestBody AdminCreateRequest request, Authentication authentication) {
        adminService.createAdmin(request, authentication);
        return new BaseResponse();
    }

    /**
     * 获取管理员列表
     * GET /admin/admins
     */
    @GetMapping("/admins")
    public AdminListResponse getAdmins(
            @RequestParam(value = "current_page") Integer currentPage,
            @RequestParam(value = "page_size") Integer pageSize) {
        return adminService.getAdmins(currentPage, pageSize);
    }

    /**
     * 修改管理员信息
     * PUT /admin/admins/{admin_id}
     */
    @PutMapping("/admins/{admin_id}")
    public BaseResponse updateAdmin(@PathVariable("admin_id") Long adminId,
                                    @RequestBody AdminUpdateRequest request,
                                    Authentication authentication) {
        adminService.updateAdmin(adminId, request, authentication);
        return new BaseResponse();
    }

    /**
     * 重置管理员密码
     * PUT /admin/admins/{admin_id}/password
     */
    @PutMapping("/admins/{admin_id}/password")
    public BaseResponse resetAdminPassword(@PathVariable("admin_id") Long adminId,
                                           @Valid @RequestBody AdminResetPasswordRequest request,
                                           Authentication authentication) {
        adminService.resetAdminPassword(adminId, request, authentication);
        return new BaseResponse();
    }

    /**
     * 删除管理员
     * DELETE /admin/admins/{admin_id}
     */
    @DeleteMapping("/admins/{admin_id}")
    public BaseResponse deleteAdmin(@PathVariable("admin_id") Long adminId, Authentication authentication) {
        adminService.deleteAdmin(adminId, authentication);
        return new BaseResponse();
    }

    /**
     * 获取内容过滤器列表
     * GET /admin/content-filters
     */
    @GetMapping("/content-filters")
    public ContentFilterListResponse getContentFilters(
            @RequestParam(value = "current_page") Integer currentPage,
            @RequestParam(value = "page_size") Integer pageSize) {
        return contentFilterService.getContentFilters(currentPage, pageSize);
    }

    /**
     * 新增内容过滤器
     * POST /admin/content-filters
     */
    @PostMapping("/content-filters")
    public BaseResponse createContentFilter(@Valid @RequestBody ContentFilterCreateRequest request) {
        contentFilterService.createContentFilter(request);
        return new BaseResponse();
    }

    /**
     * 更新内容过滤器
     * PUT /admin/content-filters/{filter_id}
     */
    @PutMapping("/content-filters/{filter_id}")
    public BaseResponse updateContentFilter(@PathVariable("filter_id") Long filterId,
                                            @Valid @RequestBody ContentFilterUpdateRequest request) {
        contentFilterService.updateContentFilter(filterId, request);
        return new BaseResponse();
    }

    /**
     * 删除内容过滤器
     * DELETE /admin/content-filters/{filter_id}
     */
    @DeleteMapping("/content-filters/{filter_id}")
    public BaseResponse deleteContentFilter(@PathVariable("filter_id") Long filterId) {
        contentFilterService.deleteContentFilter(filterId);
        return new BaseResponse();
    }

    /**
     * 批量删除帖子
     * DELETE /admin/posts
     */
    @DeleteMapping("/posts")
    public BaseResponse deletePosts(@Valid @RequestBody AdminBatchDeletePostsRequest request, Authentication authentication) {
        adminService.deletePosts(request, authentication);
        return new BaseResponse();
    }


}
