// 需要在 UserRepository.java 中添加的方法：

package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
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

    /**
     * 根据昵称搜索用户（模糊搜索），支持分页
     * 新增方法：实现用户搜索功能
     */
    Page<User> findByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

    Optional<User> findByOpenid(String openid);

    @Query("""
    SELECT u FROM User u
        WHERE (:userId IS NULL OR u.userId = :userId)
        AND (:nickname IS NULL OR u.nickname LIKE :nickname)
        AND (:username IS NULL OR u.username LIKE :username)
""")
    Page<User> findAllByOptionalNicknameUsernameUserId(Pageable pageable, String nickname, String username, Long userId);
}