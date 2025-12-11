package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserDetail;
import com.szu.afternoon5.softwareengineeringbackend.dto.user.UserSearchResult;
import com.szu.afternoon5.softwareengineeringbackend.dto.user.UserStatResponse;
import com.szu.afternoon5.softwareengineeringbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器，负责用户资料的增删改查与状态管理。
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取其他用户信息
     * GET /users/{user_id}/info
     */
    @GetMapping("/{user_id}/info")
    public UserDetail getUserInfo(@PathVariable("user_id") Long userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 获取用户统计信息
     * GET /users/{user_id}/stat
     */
    @GetMapping("/{user_id}/stat")
    public UserStatResponse getUserStat(@PathVariable("user_id") Long userId) {
        return userService.getUserStat(userId);
    }

    /**
     * 搜索用户
     * GET /users/search-user
     * 参数：keyword（搜索关键词），page（页码），size（每页数量）
     */
    @GetMapping("/search-user")
    public Page<UserSearchResult> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userService.searchUsers(keyword, pageable);
    }
}