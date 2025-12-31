package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    /**
     * 按管理员ID查询管理员。
     */
    Optional<Admin> findByAdminId(Long adminId);

    /**
     * 按用户名查询管理员。
     */
    Optional<Admin> findByUsername(String username);

    /**
     * 判断用户名是否已存在。
     */
    boolean existsByUsername(String username);
}
