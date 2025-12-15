package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminPostListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminUserListResponse;
import com.szu.afternoon5.softwareengineeringbackend.service.AdminService;
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

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 获取用户列表（管理端）
     * GET /admin/users
     * 响应格式：{err_code, err_msg, total_page, total_count, current_page, page_size, users}
     */
    @GetMapping("/users")
    public AdminUserListResponse getUserList(
            @RequestParam(value = "current_page", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {

        return adminService.adminGetUserList(currentPage, pageSize);
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
            @RequestParam(value = "user_id", required = false) Long userId) {

        return adminService.getAdminPostList(currentPage, pageSize, userId);
    }

    /**
     * 删除内容（管理）
     * DELETE /admin/posts/{post_id}
     * 响应格式：{err_code, err_msg}
     */
    @DeleteMapping("/posts/{post_id}")
    public BaseResponse deletePost(@PathVariable("post_id") Long postId, Authentication authentication) {
        adminService.deletePostByAdmin(postId, authentication);
        return new BaseResponse();
    }
}