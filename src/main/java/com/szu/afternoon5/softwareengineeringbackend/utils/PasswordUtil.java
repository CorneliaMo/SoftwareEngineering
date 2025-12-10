package com.szu.afternoon5.softwareengineeringbackend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码哈希与校验工具类。
 * 使用 BCrypt 进行安全加密，自动带盐，并支持可配置的成本因子（strength）。
 */
public final class PasswordUtil {

    // cost = 10 是默认值，也可以调大，比如 12 更安全但更耗时
    private static final int STRENGTH = 10;

    // 单例的 PasswordEncoder，线程安全
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(STRENGTH);

    private PasswordUtil() {
        // 禁止实例化
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 对密码进行 BCrypt 哈希。
     *
     * @param rawPassword 明文密码
     * @return 哈希后的字符串，自动包含 salt
     */
    public static String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验原密码与哈希是否匹配。
     *
     * @param rawPassword 明文密码
     * @param hashedPassword 数据库中存储的哈希
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String hashedPassword) {
        return ENCODER.matches(rawPassword, hashedPassword);
    }
}

