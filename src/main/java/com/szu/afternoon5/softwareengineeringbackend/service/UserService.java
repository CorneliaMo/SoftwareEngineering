package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.auth.*;
import com.szu.afternoon5.softwareengineeringbackend.dto.users.*;
import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.AdminRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.UserRepository;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import com.szu.afternoon5.softwareengineeringbackend.utils.PasswordUtil;
import com.szu.afternoon5.softwareengineeringbackend.utils.WechatUtils;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PageableUtils pageableUtils;
    private final SecurityService securityService;
    private final WechatUtils wechatUtils;

    public UserService(UserRepository userRepository, AdminRepository adminRepository, PageableUtils pageableUtils, SecurityService securityService, WechatUtils wechatUtils) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.pageableUtils = pageableUtils;
        this.securityService = securityService;
        this.wechatUtils = wechatUtils;
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

    public AdminAuthResponse adminLogin(@Valid AdminLoginRequest request) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(request.getUsername());
        if (adminOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        } else {
            Admin admin = adminOptional.get();
            if (!PasswordUtil.matches(request.getPassword(), admin.getPassword())) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID, "密码错误");
            } else {
                LoginPrincipal loginPrincipal = new LoginPrincipal(admin.getUserId(), admin.getAdminId(), LoginPrincipal.LoginType.admin);
                String refreshToken = securityService.issueRefreshToken(loginPrincipal);
                String accessToken = securityService.issueAccessToken(loginPrincipal);
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
        }
        LoginPrincipal loginPrincipal = new LoginPrincipal(user.getUserId(), null, LoginPrincipal.LoginType.user);
        String refreshToken = securityService.issueRefreshToken(loginPrincipal);
        String accessToken = securityService.issueAccessToken(loginPrincipal);
        return new UserAuthResponse(user, accessToken, refreshToken);
    }
}