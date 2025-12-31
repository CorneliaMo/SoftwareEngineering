package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.auth.RefreshTokenRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.auth.RefreshTokenResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.JwtUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 令牌安全服务，负责刷新令牌校验与签发
 */
@Service
public class SecurityService {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private static final String REFRESH_TOKENS_BY_USER_KEY_PREFIX = "refresh_tokens_by_user:";
    private static final String REFRESH_TOKENS_BY_ADMIN_KEY_PREFIX = "refresh_tokens_by_admin:";
    private static final long ACCESS_TOKEN_TTL_MILLIS = 15 * 60 * 1000L; // 访问令牌有效期：15分钟
    private static final long REFRESH_TOKEN_TTL_MILLIS = 31 * 24 * 60 * 60 * 1000L; // 刷新令牌有效期：31天
    private static final long REFRESH_TOKEN_ROTATE_THRESHOLD_MILLIS = 24 * 60 * 60 * 1000L; // 刷新令牌更新阈值：1天

    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构建安全服务并注入依赖。
     */
    public SecurityService(JwtUtils jwtUtils, StringRedisTemplate stringRedisTemplate) {
        this.jwtUtils = jwtUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 校验刷新令牌并签发新的访问令牌
     */
    public RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request) {
        String trimmedToken = request.getRefreshToken().trim();
        String key = buildRefreshKey(trimmedToken);
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "刷新令牌已过期");
        }

        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl == null || ttl == -2 || ttl <= 0) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "刷新令牌已过期");
        }

        LoginPrincipal loginPrincipal = parseRefreshValue(value);
        String accessToken = issueAccessToken(loginPrincipal);

        String refreshTokenToReturn = trimmedToken;
        if (ttl <= REFRESH_TOKEN_ROTATE_THRESHOLD_MILLIS) {
            String newRefreshToken = issueRefreshToken(loginPrincipal);
            String indexKey = buildRefreshIndexKey(loginPrincipal);
            stringRedisTemplate.opsForSet().remove(indexKey, trimmedToken);
            stringRedisTemplate.delete(key);
            refreshTokenToReturn = newRefreshToken;
        }

        return new RefreshTokenResponse(accessToken, refreshTokenToReturn);
    }

    /*
     * 通过登录上下文信息签发访问令牌，其格式与单Token时相同
     * @param loginPrincipal 登录上下文
     * @return String 访问令牌
     */
    /**
     * 生成访问令牌
     */
    public String issueAccessToken(LoginPrincipal loginPrincipal) {
        Date expiration = new Date(System.currentTimeMillis() + ACCESS_TOKEN_TTL_MILLIS);
        return jwtUtils.generateToken(loginPrincipal.getUserId(), loginPrincipal.getAdminId(), loginPrincipal.getLoginType(), expiration);
    }

    /*
     * 通过登录上下文签发刷新令牌
     * @param loginPrincipal 登录上下文
     * @return String 刷新令牌
     */
    /**
     * 生成刷新令牌并写入索引
     */
    public String issueRefreshToken(LoginPrincipal loginPrincipal) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String key = buildRefreshKey(refreshToken);
        String value = buildRefreshValue(loginPrincipal);
        stringRedisTemplate.opsForValue().set(key, value, REFRESH_TOKEN_TTL_MILLIS, TimeUnit.MILLISECONDS);
        indexRefreshToken(loginPrincipal, refreshToken);
        return refreshToken;
    }

    // 构造刷新令牌Key
    /**
     * 构造刷新令牌存储键
     */
    private String buildRefreshKey(String refreshToken) {
        return REFRESH_TOKEN_KEY_PREFIX + refreshToken;
    }

    /**
     * 构造刷新令牌索引键
     */
    private String buildRefreshIndexKey(LoginPrincipal loginPrincipal) {
        if (loginPrincipal == null || loginPrincipal.getLoginType() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无效的登录上下文");
        }
        if (loginPrincipal.getLoginType() == LoginPrincipal.LoginType.user && loginPrincipal.getUserId() != null) {
            return REFRESH_TOKENS_BY_USER_KEY_PREFIX + loginPrincipal.getUserId();
        }
        if (loginPrincipal.getLoginType() == LoginPrincipal.LoginType.admin && loginPrincipal.getAdminId() != null) {
            return REFRESH_TOKENS_BY_ADMIN_KEY_PREFIX + loginPrincipal.getAdminId();
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "无效的登录上下文");
    }

    // 为用户的刷新令牌建立索引，便于一次性注销
    /**
     * 建立刷新令牌索引，便于注销
     */
    private void indexRefreshToken(LoginPrincipal loginPrincipal, String refreshToken) {
        String indexKey = buildRefreshIndexKey(loginPrincipal);
        stringRedisTemplate.opsForSet().add(indexKey, refreshToken);
        stringRedisTemplate.expire(indexKey, REFRESH_TOKEN_TTL_MILLIS, TimeUnit.MILLISECONDS);
    }

    /*
     * 提供登录上下文，注销对应的所有刷新令牌
     * @param loginPrincipal 登录上下文
     */
    /**
     * 注销登录上下文对应的所有刷新令牌
     */
    public void revokeRefreshTokens(LoginPrincipal loginPrincipal) {
        String indexKey = buildRefreshIndexKey(loginPrincipal);
        Set<String> refreshTokens = stringRedisTemplate.opsForSet().members(indexKey);
        if (refreshTokens != null) {
            for (String token : refreshTokens) {
                stringRedisTemplate.delete(buildRefreshKey(token));
            }
        }
        stringRedisTemplate.delete(indexKey);
    }

    // 通过登录上下文构造Redis中的储存值
    /**
     * 构造刷新令牌存储值
     */
    private String buildRefreshValue(LoginPrincipal loginPrincipal) {
        String userId = loginPrincipal.getUserId() == null ? "" : loginPrincipal.getUserId().toString();
        String adminId = loginPrincipal.getAdminId() == null ? "" : loginPrincipal.getAdminId().toString();
        return loginPrincipal.getLoginType().name() + "|" + userId + "|" + adminId;
    }

    // 从Redis储存值中解析出登录上下文
    /**
     * 解析刷新令牌存储值
     */
    private LoginPrincipal parseRefreshValue(String value) {
        String[] parts = value.split("\\|", -1);
        if (parts.length != 3) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token载荷无效");
        }

        LoginPrincipal.LoginType loginType;
        try {
            loginType = LoginPrincipal.LoginType.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token载荷无效");
        }

        Long userId = parseNullableLong(parts[1]);
        Long adminId = parseNullableLong(parts[2]);

        if (loginType == LoginPrincipal.LoginType.user && userId == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token载荷无效");
        }
        if (loginType == LoginPrincipal.LoginType.admin && adminId == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token载荷无效");
        }

        return new LoginPrincipal(userId, adminId, loginType);
    }

    // 解析Long的工具方法，防止NullPointerException
    /**
     * 解析可空的长整型字段
     */
    private Long parseNullableLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token载荷无效");
        }
    }
}
