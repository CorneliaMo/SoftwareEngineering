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

@Component
public class JwtUtils {

    private final String SECRET_KEY;
    private final static long EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 一周

    public JwtUtils(JwtConfig jwtConfig) {
        this.SECRET_KEY = jwtConfig.getSecretKey();
    }

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
            throw new JwtException("Invalid login information.");
        }

        Date exp = Objects.requireNonNullElseGet(expiration, () -> new Date(System.currentTimeMillis() + EXPIRATION));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
