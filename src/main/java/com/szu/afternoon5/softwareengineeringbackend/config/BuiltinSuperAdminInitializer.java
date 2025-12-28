package com.szu.afternoon5.softwareengineeringbackend.config;

import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import com.szu.afternoon5.softwareengineeringbackend.repository.AdminRepository;
import com.szu.afternoon5.softwareengineeringbackend.utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 启动时检查并初始化内置超级管理员账号
 */
@Component
public class BuiltinSuperAdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BuiltinSuperAdminInitializer.class);
    private static final String DEFAULT_ADMIN_NAME = "系统超级管理员";

    private final BuiltinSuperAdminConfig builtinSuperAdminConfig;
    private final AdminRepository adminRepository;

    public BuiltinSuperAdminInitializer(BuiltinSuperAdminConfig builtinSuperAdminConfig,
                                        AdminRepository adminRepository) {
        this.builtinSuperAdminConfig = builtinSuperAdminConfig;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        String username = builtinSuperAdminConfig.getUsername();
        String password = builtinSuperAdminConfig.getPassword();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            logger.warn("未配置内置超级管理员账号，跳过初始化");
            return;
        }

        boolean exists = adminRepository.findByUsername(username).isPresent();
        if (exists) {
            logger.info("内置超级管理员账号已存在，跳过初始化");
            return;
        }

        Admin admin = new Admin(null, username, PasswordUtil.hash(password), DEFAULT_ADMIN_NAME, Admin.AdminRole.superadmin);
        adminRepository.save(admin);
        logger.info("已初始化内置超级管理员账号: {}", username);
    }
}
