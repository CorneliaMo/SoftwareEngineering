package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import org.springframework.stereotype.Component;

@Component("perm")
public class PermissionService {

    public boolean isUser(Object principal) {
        if (!(principal instanceof LoginPrincipal login)) {
            return false; // 不是登录用户，肯定不允许
        }

        return login.getLoginType().equals(LoginPrincipal.LoginType.user);
    }

    public boolean isAdmin(Object principal) {
        if (!(principal instanceof LoginPrincipal login)) {
            return false; // 不是登录用户，肯定不允许
        }

        return login.getLoginType().equals(LoginPrincipal.LoginType.admin);
    }
}
