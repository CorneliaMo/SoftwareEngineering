package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.admin.*;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCover;
import com.szu.afternoon5.softwareengineeringbackend.entity.*;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.*;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import com.szu.afternoon5.softwareengineeringbackend.utils.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 管理端业务服务
 */
@Service
public class AdminService {

    private final PostRepository postRepository;
    private final PageableUtils pageableUtils;
    private final UserRepository userRepository;
    private final OperationLogRepository operationLogRepository;
    private final AdminRepository adminRepository;

    /**
     * 构建管理端服务并注入依赖。
     */
    public AdminService(PostRepository postRepository, PageableUtils pageableUtils, UserRepository userRepository, OperationLogRepository operationLogRepository, AdminRepository adminRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pageableUtils = pageableUtils;
        this.operationLogRepository = operationLogRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * 管理员获取用户列表
     * GET /admin/users
     * 响应格式：{err_code, err_msg, total_page, total_count, current_page, page_size, users}
     */
    public AdminUserListResponse adminGetUserList(Integer currentPage, Integer pageSize, String nickname, String username, Long userId) {

        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("user_id", "nickname", "created_time", "updated_time", "comment_count", "post_count", "rating_count");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "user_id", "ASC");

        Page<User> userPage = userRepository.findAllByOptionalNicknameUsernameUserId(
                pageable,
                (nickname == null || nickname.isBlank())?null:"%"+nickname+"%",
                (username == null || username.isBlank())?null:"%"+username+"%",
                userId);

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
    public AdminPostListResponse getAdminPostList(Integer currentPage, Integer pageSize, Long userId, String username, String nickname, String postType) {
        Page<PostWithCover> postPage;

        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("post_id", "user_id", "is_deleted", "deleted_time", "created_time", "updated_time", "rating_count", "comment_count");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "post_id", "ASC");
        // 查询所有帖子
        postPage = postRepository.findAllWithCoverByOptionalUserIdUsernameNicknamePostType(pageable,
                userId,
                (username == null || username.isBlank())?null:"%"+username+"%",
                (nickname == null || nickname.isBlank())?null:"%"+nickname+"%",
                (postType == null || postType.isBlank()||postType.equals("video"))?null:postType.equals("image"),
                (postType == null || postType.isBlank()||postType.equals("image"))?null:postType.equals("video"));

        return new AdminPostListResponse(
                postPage.getTotalPages(),
                (int)postPage.getTotalElements(),
                postPage.getNumber() + 1,
                postPage.getSize(),
                postPage.getContent()
        );
    }

    /**
     * 写入单条操作日志
     */
    @Transactional
    protected void insertOperationLog(Long adminId, String operationType, Long targetId, String targetType, String operationDetail, String ipAddress) {
        OperationLog operationLog = new OperationLog(adminId, operationType, targetId, targetType, operationDetail, ipAddress);
        operationLogRepository.save(operationLog);
    }

    /**
     * 批量写入操作日志
     */
    @Transactional
    protected void insertOperationLogs(List<OperationLog> operationLogs) {
        operationLogRepository.saveAll(operationLogs);
    }

    /**
     * 创建管理员（仅超级管理员可用）
     */
    @Transactional
    public void createAdmin(@Valid AdminCreateRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Optional<Admin> adminOptional = adminRepository.findByAdminId(loginPrincipal.getAdminId());
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Admin admin = adminOptional.get();
            if (!admin.getRole().equals(Admin.AdminRole.superadmin)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "只有超级管理员可以添加管理员");
            } else {
                if (adminRepository.existsByUsername(request.getUsername())) {
                    throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "管理员已存在");
                }
                Admin newAdmin = new Admin(
                        null,
                        request.getUsername(),
                        PasswordUtil.hash(request.getPassword()),
                        "新管理员",
                        Admin.AdminRole.admin
                );
                adminRepository.save(newAdmin);
            }
        }
    }

    /**
     * 获取管理员列表（分页）
     */
    public AdminListResponse getAdmins(Integer currentPage, Integer pageSize) {
        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("admin_id", "user_id", "role", "status", "last_login", "created_time");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "admin_id", "ASC");
        Page<Admin> adminPage = adminRepository.findAll(pageable);
        return new AdminListResponse(
                adminPage.getTotalPages(),
                (int) adminPage.getTotalElements(),
                adminPage.getNumber() + 1,
                adminPage.getSize(),
                adminPage.get().map(AdminDetail::new).toList()
        );
    }

    /**
     * 更新管理员信息（仅超级管理员可用）
     */
    @Transactional
    public void updateAdmin(Long adminId, AdminUpdateRequest request, Authentication authentication) {
        checkIsSuperadmin(authentication);
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "管理员不存在");
        } else {
            Admin admin = adminOptional.get();
            admin.setUsername(Objects.requireNonNullElse(request.getUsername(), admin.getUsername()));
            admin.setAdminName(Objects.requireNonNullElse(request.getAdminName(), admin.getAdminName()));
            admin.setRole(Objects.requireNonNullElse(request.getRole(), admin.getRole()));
            admin.setStatus(Objects.requireNonNullElse(request.getStatus(), admin.getStatus()));
            adminRepository.save(admin);
        }
    }

    /**
     * 重置管理员密码（仅超级管理员可用）
     */
    @Transactional
    public void resetAdminPassword(Long adminId, @Valid AdminResetPasswordRequest request, Authentication authentication) {
        checkIsSuperadmin(authentication);
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "管理员不存在");
        } else {
            Admin admin = adminOptional.get();
            admin.setPassword(PasswordUtil.hash(request.getNewPassword()));
            adminRepository.save(admin);
        }
    }

    /**
     * 校验当前登录用户是否为超级管理员
     */
    @Transactional
    protected void checkIsSuperadmin(Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Optional<Admin> adminOptional = adminRepository.findByAdminId(loginPrincipal.getAdminId());
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Admin admin = adminOptional.get();
            if (!admin.getRole().equals(Admin.AdminRole.superadmin)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "只有超级管理员可以操作");
            }
        }
    }

    /**
     * 删除管理员（仅超级管理员可用）
     */
    @Transactional
    public void deleteAdmin(Long adminId, Authentication authentication) {
        checkIsSuperadmin(authentication);
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "管理员不存在");
        } else {
            adminRepository.delete(adminOptional.get());
        }
    }

    /**
     * 批量删除帖子并记录操作日志
     */
    @Transactional
    public void deletePosts(@Valid AdminBatchDeletePostsRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        List<Post> posts = postRepository.findAllByPostIdIn(request.getPostIds());
        List<OperationLog> operationLogs = new ArrayList<>();
        posts.forEach(post -> {
            OperationLog operationLog = new OperationLog(loginPrincipal.getAdminId(), "delete", post.getPostId(), "post", "", "");
            operationLogs.add(operationLog);
            post.setIsDeleted(true);
        });
        postRepository.saveAll(posts);
        insertOperationLogs(operationLogs);
    }
}
