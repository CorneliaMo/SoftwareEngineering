package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminPostListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.admin.AdminUserListResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCover;
import com.szu.afternoon5.softwareengineeringbackend.entity.*;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.*;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final PostRepository postRepository;
    private final PageableUtils pageableUtils;
    private final UserRepository userRepository;
    private final OperationLogRepository operationLogRepository;

    public AdminService(PostRepository postRepository, PageableUtils pageableUtils, UserRepository userRepository, OperationLogRepository operationLogRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pageableUtils = pageableUtils;
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 管理员获取用户列表
     * GET /admin/users
     * 响应格式：{err_code, err_msg, total_page, total_count, current_page, page_size, users}
     */
    public AdminUserListResponse adminGetUserList(Integer currentPage, Integer pageSize) {

        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("user_id", "nickname", "created_time", "updated_time", "comment_count", "post_count", "rating_count");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "user_id", "ASC");

        Page<User> userPage = userRepository.findAll(pageable);

        return new AdminUserListResponse(
                userPage.getTotalPages(),
                (int) userPage.getTotalElements(),
                userPage.getNumber() + 1,
                userPage.getSize(),
                userPage.getContent()
        );
    }

    /**
     * 删除用户（管理员操作）
     * DELETE /admin/users/{user_id}
     * 响应格式：{err_code, err_msg}
     */
    @Transactional
    public void adminDeleteUser(Long userId, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "用户不存在"));

        // 标记用户状态为禁用（软删除）
        user.setStatus(false);
        user.setUpdatedTime(java.time.Instant.now());
        userRepository.save(user);
        insertOperationLog(
                loginPrincipal.getAdminId(),
                "delete",
                userId,
                "user",
                "",
                "");
    }

    /**
     * 管理员获取帖子列表
     * GET /admin/posts
     * 参数：current_page, page_size, user_id(可选)
     * 响应格式：{err_code, err_msg, total_page, total_count, current_page, page_size, posts}
     */
    public AdminPostListResponse getAdminPostList(Integer currentPage, Integer pageSize, Long userId) {
        Page<PostWithCover> postPage;

        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("post_id", "user_id", "is_deleted", "deleted_time", "created_time", "updated_time", "rating_count", "comment_count");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "post_id", "ASC");

        if (userId != null) {
            // 按用户ID筛选帖子
            postPage = postRepository.findByUserIdWithCover(userId, pageable);
        } else {
            // 查询所有帖子
            postPage = postRepository.findAllWithCover(pageable);
        }

        return new AdminPostListResponse(
                postPage.getTotalPages(),
                (int)postPage.getTotalElements(),
                postPage.getNumber() + 1,
                postPage.getSize(),
                postPage.getContent()
        );
    }

    /**
     * 管理员删除帖子
     * DELETE /admin/posts/{post_id}
     * 响应格式：{err_code, err_msg}
     */
    @Transactional
    public void deletePostByAdmin(Long postId, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在"));

        // 软删除帖子
        post.setIsDeleted(true);
        postRepository.save(post);
        insertOperationLog(
                loginPrincipal.getAdminId(),
                "delete",
                postId,
                "post",
                "",
                "");
    }

    @Transactional
    protected void insertOperationLog(Long adminId, String operationType, Long targetId, String targetType, String operationDetail, String ipAddress) {
        OperationLog operationLog = new OperationLog(adminId, operationType, targetId, targetType, operationDetail, ipAddress);
        operationLogRepository.save(operationLog);
    }
}