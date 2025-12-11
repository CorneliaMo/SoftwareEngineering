package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.user.AdminUserListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.post.AdminPostListResponse;
import com.szu.afternoon5.softwareengineeringbackend.service.UserService;
import com.szu.afternoon5.softwareengineeringbackend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 管理端接口控制器，面向后台运营。
 * <p>
 * 未来可以在此补充管理员登录、内容审核、用户封禁等接口，并结合权限注解限制访问。
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
}
package com.szu.afternoon5.softwareengineeringbackend.controller;


@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    // ==================== 用户管理接口 ====================

    /**
     * 获取用户列表（管理端）
     * GET /admin/users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AdminUserListResponse> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userService.getAdminUserList(pageable, keyword, status);
    }

    /**
     * 删除用户
     * DELETE /admin/users/{user_id}
     */
    @DeleteMapping("/users/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return new BaseResponse();
    }

    // ==================== 内容管理接口 ====================

    /**
     * 内容管理列表
     * GET /admin/posts
     */
    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AdminPostListResponse> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return postService.getAdminPostList(pageable, status);
    }

    /**
     * 删除内容（管理）
     * DELETE /admin/posts/{post_id}
     */
    @DeleteMapping("/posts/{post_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse deletePost(@PathVariable("post_id") Long postId) {
        postService.deletePostByAdmin(postId);
        return new BaseResponse();
    }
}