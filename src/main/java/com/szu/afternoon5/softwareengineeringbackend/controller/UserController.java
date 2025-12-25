package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.InteractionUserListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.users.BindWechatRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.users.SearchUserResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.users.UserInfoResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.users.UserStatResponse;
import com.szu.afternoon5.softwareengineeringbackend.service.InteractionService;
import com.szu.afternoon5.softwareengineeringbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器，负责用户资料的增删改查与状态管理。
 * 实现接口文档中的3个用户模块接口：
 * 1. GET /users/{user_id}/info - 获取其他用户信息
 * 2. GET /users/{user_id}/stat - 获取用户统计信息
 * 3. GET /users/search-user - 搜索用户
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final InteractionService interactionService;

    public UserController(UserService userService, InteractionService interactionService) {
        this.userService = userService;
        this.interactionService = interactionService;
    }

    /**
     * 获取其他用户信息
     * GET /users/{user_id}/info
     * 响应格式：{err_code, err_msg, user}
     */
    @GetMapping("/{user_id}/info")
    public UserInfoResponse getUserInfo(@PathVariable("user_id") Long userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 获取用户统计信息
     * GET /users/{user_id}/stat
     * 响应格式：{err_code, err_msg, user_stat}
     */
    @GetMapping("/{user_id}/stat")
    public UserStatResponse getUserStat(@PathVariable("user_id") Long userId) {
        return userService.getUserStat(userId);
    }

    /**
     * 搜索用户
     * GET /users/search-user
     * 参数：current_page, page_size, keyword
     * 响应格式：{total_page, total_count, current_page, page_size, err_code, err_msg, users}
     */
    @GetMapping("/search-user")
    public SearchUserResponse searchUsers(
            @RequestParam("current_page") Integer currentPage,
            @RequestParam("page_size") Integer pageSize,
            @RequestParam("keyword") String keyword) {

        return userService.searchUsers(currentPage, pageSize, keyword);
    }

    /**
     * 永久注销账号
     * POST /users/deactive
     */
    @PostMapping("/deactive")
    public BaseResponse deactiveUser(Authentication authentication) {
        userService.requestDeletion(authentication);
        return new BaseResponse();
    }

    /**
     * 绑定微信
     * PUT /users/bind-wechat
     */
    @PutMapping("/bind-wechat")
    public BaseResponse bindWechat(@Valid @RequestBody BindWechatRequest request,
                                   Authentication authentication) {
        userService.bindWechat(request, authentication);
        return new BaseResponse();
    }

    /**
     * 获取用户关注列表
     * GET /users/{user_id}/following
     */
    @GetMapping("/{user_id}/following")
    public InteractionUserListResponse getUserFollowing(@PathVariable("user_id") Long userId,
                                                   @RequestParam(value = "current_page") Integer currentPage,
                                                   @RequestParam(value = "page_size") Integer pageSize) {
        return interactionService.getFollowing(currentPage, pageSize, userId);
    }

    /**
     * 获取用户粉丝列表
     * GET /users/{user_id}/followers
     */
    @GetMapping("/{user_id}/followers")
    public InteractionUserListResponse getUserFollowers(@PathVariable("user_id") Long userId,
                                                        @RequestParam(value = "current_page") Integer currentPage,
                                                        @RequestParam(value = "page_size") Integer pageSize) {
        return interactionService.getFollowers(currentPage, pageSize, userId);
    }
}
