package com.szu.afternoon5.softwareengineeringbackend.utils;

import com.szu.afternoon5.softwareengineeringbackend.config.JwtConfig;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * JWT 工具类，负责生成与解析令牌。
 * <p>
 * 后续可增加刷新令牌、黑名单校验或多端设备标识，以提升安全性与可追踪性。
 */
@Component
public class JwtUtils {

    private final String SECRET_KEY;
    private final static long EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 一周

    public JwtUtils(JwtConfig jwtConfig) {
        this.SECRET_KEY = jwtConfig.getSecretKey();
    }

    /**
     * 生成包含登录类型的 JWT，默认有效期一周。
     * 可通过传入自定义过期时间或在 claims 中补充更多业务字段。
     */
    public String generateToken(Long userId, Long adminId, LoginPrincipal.LoginType loginType, Date expiration) {
        Claims claims = Jwts.claims();

        if (loginType.equals(LoginPrincipal.LoginType.admin) && adminId != null) {
            if (userId != null) claims.put("user_id", userId);
            claims.put("admin_id", adminId);
            claims.put("login_type", loginType);
        } else if (loginType.equals(LoginPrincipal.LoginType.user) && userId != null) {
            claims.put("user_id", userId);
            if (adminId != null) claims.put("admin_id", adminId);
            claims.put("login_type", loginType);
        } else {
            throw new JwtException("Invalid userLogin information.");
        }

        Date exp = Objects.requireNonNullElseGet(expiration, () -> new Date(System.currentTimeMillis() + EXPIRATION));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT 并返回 claims，失败时抛出 {@link JwtException}。
     * 可在外层结合缓存或数据库校验实现令牌吊销。
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
