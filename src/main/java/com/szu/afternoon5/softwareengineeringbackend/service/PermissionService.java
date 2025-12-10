package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import org.springframework.stereotype.Component;

/**
 * 权限辅助服务：用于在安全表达式中判定当前登录主体是否具有用户或管理员身份。
 */
@Component("perm")
public class PermissionService {

    /**
     * 判断主体是否为普通用户登录。
     *
     * @param principal Spring Security 传入的主体对象
     * @return 当且仅当为普通用户时返回 true
     */
    public boolean isUser(Object principal) {
        if (!(principal instanceof LoginPrincipal login)) {
            return false; // 不是登录用户，肯定不允许
        }

        return login.getLoginType().equals(LoginPrincipal.LoginType.user);
    }

    /**
     * 判断主体是否为管理员登录。
     *
     * @param principal Spring Security 传入的主体对象
     * @return 当且仅当为管理员时返回 true
     */
    public boolean isAdmin(Object principal) {
        if (!(principal instanceof LoginPrincipal login)) {
            return false; // 不是登录用户，肯定不允许
        }

        return login.getLoginType().equals(LoginPrincipal.LoginType.admin);
    }
}
