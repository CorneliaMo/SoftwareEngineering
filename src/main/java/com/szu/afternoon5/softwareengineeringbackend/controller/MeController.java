package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserDetail;
import com.szu.afternoon5.softwareengineeringbackend.dto.me.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心控制器，用于获取和更新当前登录用户的信息。
 * <p>
 * 后续可以加入头像上传、账号绑定、个人配置等接口，并结合安全上下文自动识别用户身份。
 */
@RestController
@RequestMapping("/me")
public class MeController {

    /**
     * 获取本用户信息。
     *
     * @return 当前用户的详细信息
     */
    @GetMapping("/info")
    public BaseResponse<UserDetail> getUserInfo() {
        return null;
    }

    /**
     * 更新本用户信息。
     *
     * @param request 用户信息更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/info")
    public BaseResponse<UserDetail> updateUserInfo(@Valid @RequestBody UpdateUserInfoRequest request) {
        return null;
    }

    /**
     * 获取我的帖子列表。
     *
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 当前用户发布的分页帖子列表
     */
    @GetMapping("/posts")
    public MyPostsResponse getMyPosts(@RequestParam("current_page") Integer currentPage,
                                      @RequestParam("page_size") Integer pageSize) {
        return null;
    }

    /**
     * 获取我的评论列表。
     *
     * @param currentPage 当前页码（可选）
     * @param pageSize    每页数量（可选）
     * @return 当前用户发布的分页评论列表
     */
    @GetMapping("/comments")
    public MyCommentsResponse getMyComments(@RequestParam(value = "current_page", required = false) Integer currentPage,
                                            @RequestParam(value = "page_size", required = false) Integer pageSize) {
        return null;
    }
}