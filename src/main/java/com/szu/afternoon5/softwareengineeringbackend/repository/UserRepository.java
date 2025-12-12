package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 检查用户名是否存在，用于注册或唯一性校验。
     */
    boolean existsByUsername(String username);

    /**
     * 根据用户名查询用户。
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户 ID 查询用户。
     */
    Optional<User> findByUserId(Long userId);

    /**
     * 判断指定用户 ID 是否存在。
     */
    boolean existsByUserId(Long userId);
}
