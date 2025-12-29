package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.auth.*;
import com.szu.afternoon5.softwareengineeringbackend.dto.users.*;
import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import com.szu.afternoon5.softwareengineeringbackend.entity.UserDeletionRequest;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.*;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import com.szu.afternoon5.softwareengineeringbackend.utils.PasswordUtil;
import com.szu.afternoon5.softwareengineeringbackend.utils.WechatUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PageableUtils pageableUtils;
    private final SecurityService securityService;
    private final WechatUtils wechatUtils;

    // 注销申请处理的时间间隔
    private static final Duration GRACE_PERIOD = Duration.ofDays(7);
    private final UserDeletionRequestRepository userDeletionRequestRepository;
    private final UserDeletionService userDeletionService;

    public UserService(UserRepository userRepository, AdminRepository adminRepository, PageableUtils pageableUtils, SecurityService securityService, WechatUtils wechatUtils, UserDeletionRequestRepository userDeletionRequestRepository, UserDeletionService userDeletionService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.pageableUtils = pageableUtils;
        this.securityService = securityService;
        this.wechatUtils = wechatUtils;
        this.userDeletionRequestRepository = userDeletionRequestRepository;
        this.userDeletionService = userDeletionService;
    }

    // ========== 认证相关方法 ==========

    @Transactional
    public UserAuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "用户名已被占用");
        } else {
            User user = new User(
                    request.getUsername(),
                    null,
                    PasswordUtil.hash(request.getPassword()),
                    null,
                    Objects.requireNonNullElse(request.getNickname(), "新用户"),
                    null);
            User saved = userRepository.save(user);
            LoginPrincipal loginPrincipal = new LoginPrincipal(user.getUserId(), null, LoginPrincipal.LoginType.user);
            String refreshToken = securityService.issueRefreshToken(loginPrincipal);
            String accessToken = securityService.issueAccessToken(loginPrincipal);
            return new UserAuthResponse(saved, accessToken, refreshToken);
        }
    }

    public UserAuthResponse userLogin(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        } else {
            User user = userOptional.get();
            checkUserHasDeletionRequest(user.getUserId(), null);
            if (!user.getStatus()) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
            }
            if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID, "密码错误");
            } else {
                LoginPrincipal loginPrincipal = new LoginPrincipal(user.getUserId(), null, LoginPrincipal.LoginType.user);
                String refreshToken = securityService.issueRefreshToken(loginPrincipal);
                String accessToken = securityService.issueAccessToken(loginPrincipal);
                return new UserAuthResponse(user, accessToken, refreshToken);
            }
        }
    }

    @Transactional
    public ResetPasswordResponse resetPassword(@Valid ResetPasswordRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            if (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user) && loginPrincipal.getUserId() != null) {
                // 用户进行重置密码
                // 使先前所有刷新令牌失效
                Optional<User> userOptional = userRepository.findByUserId(loginPrincipal.getUserId());
                if (userOptional.isEmpty()) {
                    throw new BusinessException(ErrorCode.TOKEN_INVALID, "用户不存在");
                } else {
                    User user = userOptional.get();
                    if (user.getPassword() != null && !PasswordUtil.matches(request.getOldPassword(), user.getPassword())) {
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED, "旧密码不匹配");
                    } else {
                        user.setPassword(PasswordUtil.hash(request.getNewPassword()));
                        userRepository.save(user);
                        securityService.revokeRefreshTokens(loginPrincipal);
                        return new ResetPasswordResponse(
                                securityService.issueAccessToken(loginPrincipal),
                                securityService.issueRefreshToken(loginPrincipal)
                        );
                    }
                }
            } else if (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.admin) && loginPrincipal.getAdminId() != null) {
                // 管理员进行重置密码
                // 使先前所有刷新令牌失效
                Optional<Admin> adminOptional = adminRepository.findByAdminId(loginPrincipal.getAdminId());
                if (adminOptional.isEmpty()) {
                    throw new BusinessException(ErrorCode.TOKEN_INVALID, "用户不存在");
                } else {
                    Admin admin = adminOptional.get();
                    if (!PasswordUtil.matches(request.getOldPassword(), admin.getPassword())) {
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED, "旧密码不匹配");
                    } else {
                        admin.setPassword(PasswordUtil.hash(request.getNewPassword()));
                        adminRepository.save(admin);
                        securityService.revokeRefreshTokens(loginPrincipal);
                        return new ResetPasswordResponse(
                                securityService.issueAccessToken(loginPrincipal),
                                securityService.issueRefreshToken(loginPrincipal)
                        );
                    }
                }
            } else {
                throw new BusinessException(ErrorCode.FORBIDDEN, "当前角色不支持重置密码");
            }
        }
    }

    @Transactional
    public AdminAuthResponse adminLogin(@Valid AdminLoginRequest request) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(request.getUsername());
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        } else {
            Admin admin = adminOptional.get();
            if (!admin.getStatus()) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "管理员已被禁用");
            }
            if (!PasswordUtil.matches(request.getPassword(), admin.getPassword())) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID, "密码错误");
            } else {
                LoginPrincipal loginPrincipal = new LoginPrincipal(admin.getUserId(), admin.getAdminId(), LoginPrincipal.LoginType.admin);
                String refreshToken = securityService.issueRefreshToken(loginPrincipal);
                String accessToken = securityService.issueAccessToken(loginPrincipal);
                admin.setLastLogin(Instant.now());
                adminRepository.save(admin);
                return new AdminAuthResponse(admin, accessToken, refreshToken);
            }
        }
    }

    // ========== 用户信息相关方法 ==========

    /**
     * 获取用户详情信息
     * GET /users/{user_id}/info
     * 响应格式：{err_code, err_msg, user}
     */
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "用户不存在"));

        return new UserInfoResponse(new UserDetail(user));
    }

    /**
     * 获取用户统计信息
     * GET /users/{user_id}/stat
     * 响应格式：{err_code, err_msg, user_stat}
     */
    public UserStatResponse getUserStat(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "用户不存在"));

        return new UserStatResponse(new UserStatDTO(user));
    }

    /**
     * 搜索用户
     * GET /users/search-user
     * 参数：current_page, page_size, keyword(可选)
     * 响应格式：{total_page, total_count, current_page, page_size, err_code, err_msg, users}
     */
    public SearchUserResponse searchUsers(Integer currentPage, Integer pageSize, String keyword) {

        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("user_id", "nickname", "created_time", "updated_time", "comment_count", "post_count", "rating_count");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "user_id", "ASC");

        // 按昵称进行模糊搜索（不区分大小写）
        Page<User> userPage = userRepository.findByNicknameContainingIgnoreCase(keyword.trim(), pageable);

        List<UserDetail> results = userPage.getContent().stream()
                .map(UserDetail::new)
                .toList();

        return new SearchUserResponse(
                userPage.getTotalPages(),
                (int) userPage.getTotalElements(),
                userPage.getNumber() + 1,
                userPage.getSize(),
                results
        );
    }

    @Transactional
    public UserAuthResponse wechatLogin(@Valid WechatLoginRequest request) {
        String openid = wechatUtils.getOpenid(request.getJscode());
        if (openid != null) {
            checkUserHasDeletionRequest(null, openid);
            Optional<User> userOptional = userRepository.findByOpenid(openid);
            User user;
            if (userOptional.isEmpty()) {
                // 未注册用户，自动注册并分配默认用户名
                user = new User(
                        UUID.randomUUID().toString().toLowerCase().replace("-", "").substring(0, 8),
                        openid,
                        null,
                        null,
                        "新用户",
                        null
                );
                user = userRepository.save(user);
            } else {
                // 已注册用户，直接登录
                user = userOptional.get();
                if (!user.getStatus()) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
                }
            }
            LoginPrincipal loginPrincipal = new LoginPrincipal(user.getUserId(), null, LoginPrincipal.LoginType.user);
            String refreshToken = securityService.issueRefreshToken(loginPrincipal);
            String accessToken = securityService.issueAccessToken(loginPrincipal);
            return new UserAuthResponse(user, accessToken, refreshToken);
        } else {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "微信登录凭证无效");
        }
    }

    @Transactional
    public void bindWechat(@Valid BindWechatRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal != null) {
            Optional<User> userOptional = userRepository.findByUserId(loginPrincipal.getUserId());
            if (userOptional.isPresent()) {
                String openid = wechatUtils.getOpenid(request.getJscode());
                if (openid != null) {
                    User user = userOptional.get();
                    user.setOpenid(openid);
                    userRepository.save(user);
                } else {
                    throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "微信登录凭证无效");
                }
            } else {
                throw new  BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
            }
        } else {
            throw new  BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
    }

    /**
     * 提交注销申请（幂等）：
     * - 若不存在申请：创建 PENDING，execute_after=now+7d
     * - 若存在且已 CANCELLED/FAILED：重新激活为 PENDING
     * - 若已 DONE：直接拒绝/返回已完成
     */
    @Transactional
    public UserDeletionRequest requestDeletion(Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "用户不存在");
        } else {
            Long userId = loginPrincipal.getUserId();
            if (userId == null) throw new IllegalArgumentException("userId为空");

            if (!userRepository.existsById(userId)) {
                throw new IllegalArgumentException("用户不存在");
            }

            Instant now = Instant.now();
            Instant executeAfter = now.plus(GRACE_PERIOD);

            return userDeletionRequestRepository.findByUserId(userId)
                    .map(existing -> {
                        if (existing.getStatus() == UserDeletionRequest.DeletionRequestStatus.DONE) {
                            return existing;
                        }
                        // 重新发起/覆盖时间
                        existing.setRequestedTime(now);
                        existing.setExecuteAfter(executeAfter);
                        existing.setStatus(UserDeletionRequest.DeletionRequestStatus.PENDING);
                        existing.setProcessedTime(null);
                        existing.setFailReason(null);
                        return userDeletionRequestRepository.save(existing);
                    })
                    .orElseGet(() -> {
                        UserDeletionRequest r = new UserDeletionRequest(userId, executeAfter);
                        return userDeletionRequestRepository.save(r);
                    });
        }
    }

    /** 可选：用户在 7 天内反悔撤销 */
    @Transactional
    public void cancelDeletion(Long userId) {
        UserDeletionRequest r = userDeletionRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("未找到注销申请 userId=" + userId));
        if (r.getStatus() == UserDeletionRequest.DeletionRequestStatus.DONE) return;

        r.setStatus(UserDeletionRequest.DeletionRequestStatus.CANCELLED);
        r.setProcessedTime(Instant.now());
        userDeletionRequestRepository.save(r);
    }

    /**
     * 每天跑一次：拉取到期 PENDING，逐条“claim→删除→标记 DONE/FAILED”
     * - claim 用来防止多实例重复处理
     */
    @Scheduled(cron = "${deletion.cron:0 30 3 * * *}") // 默认每天 03:30
    public void processDueDeletions() {
        Instant now = Instant.now();
        List<UserDeletionRequest> due = userDeletionRequestRepository.findDueRequests(now);

        if (due.isEmpty()) return;
        log.info("[注销清理] due requests={}", due.size());

        for (UserDeletionRequest r : due) {
            try {
                // 抢占任务：只有一个线程/实例能把 PENDING 改成 PROCESSING
                int claimed = userDeletionRequestRepository.claim(r.getRequestId());
                if (claimed == 0) continue;

                userDeletionService.deleteUserDataTransactional(r.getUserId()); // 真正删除

                userDeletionRequestRepository.markResult(r.getRequestId(), UserDeletionRequest.DeletionRequestStatus.DONE, Instant.now(), null);
                log.info("[注销清理] DONE userId={} requestId={}", r.getUserId(), r.getRequestId());
            } catch (Exception ex) {
                log.error("[注销清理] FAILED userId={} requestId={}", r.getUserId(), r.getRequestId(), ex);
                userDeletionRequestRepository.markResult(r.getRequestId(), UserDeletionRequest.DeletionRequestStatus.FAILED, Instant.now(),
                        ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
    }

    // 检查用户是否处于注销处理期
    private void checkUserHasDeletionRequest(Long userId, String openid) {
        if (userDeletionRequestRepository.existsByUserIdOrOpenid(userId, openid)) {
            throw new BusinessException(ErrorCode.USER_DELETION_PENDING);
        }
    }
}