package com.szu.afternoon5.softwareengineeringbackend.security;

import com.szu.afternoon5.softwareengineeringbackend.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 鉴权过滤器，从 Authorization 头解析令牌并写入 SecurityContext。
 * <p>
 * 如需增加黑名单校验、刷新令牌逻辑或审计日志，可在解析成功后补充相应的校验与记录。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 解析 Bearer Token，构建 {@link LoginPrincipal} 并注入上下文。
     * 当前仅支持用户与管理员两种登录类型，若后续扩展角色体系，可在此处调整 claims 解析逻辑。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                UsernamePasswordAuthenticationToken authentication;
                Claims claims = jwtUtils.parseToken(token);

                LoginPrincipal loginPrincipal;
                LoginPrincipal.LoginType loginType = null;
                Long userId = null;
                Long adminId = null;
                if (claims.containsKey("login_type")) loginType = LoginPrincipal.LoginType.valueOf(claims.get("login_type", String.class));
                if (claims.containsKey("user_id")) userId = claims.get("user_id", Long.class);
                if (claims.containsKey("admin_id")) adminId = claims.get("admin_id", Long.class);

                if (loginType != null && loginType.equals(LoginPrincipal.LoginType.user) && userId != null) {
                    // 用户登录类型必须包含userId，必须不包含adminId
                    loginPrincipal = new LoginPrincipal(userId, null, LoginPrincipal.LoginType.user);
                } else if (loginType != null && loginType.equals(LoginPrincipal.LoginType.admin) && adminId != null) {
                    // 管理员登录类型必须包含adminId，可能包含userId
                    loginPrincipal = new LoginPrincipal(userId, adminId, LoginPrincipal.LoginType.admin);
                } else {
                    // 不符合任何有效登录类型，抛出异常
                    throw new JwtException("Invalid login information.");
                }
                authentication = new UsernamePasswordAuthenticationToken(loginPrincipal, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Throwable e) {
                // token 无效
                throw new JwtException("Invalid login information.");
            }
        }

        filterChain.doFilter(request, response);
    }
}

