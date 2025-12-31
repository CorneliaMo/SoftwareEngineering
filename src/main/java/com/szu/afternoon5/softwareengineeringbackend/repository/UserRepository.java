// 需要在 UserRepository.java 中添加的方法：

package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 根据微信openid查询用户。
     */
    Optional<User> findByOpenid(String openid);

    /**
     * 按可选条件分页查询用户列表。
     */
    @Query("""
    SELECT u FROM User u
        WHERE (:userId IS NULL OR u.userId = :userId)
        AND (:nickname IS NULL OR u.nickname LIKE :nickname)
        AND (:username IS NULL OR u.username LIKE :username)
""")
    Page<User> findAllByOptionalNicknameUsernameUserId(Pageable pageable, String nickname, String username, Long userId);

    /**
     * 增量更新用户评论计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE "users"
        SET comment_count = comment_count + :delta
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addCommentCount(@Param("userId") Long userId, @Param("delta") long delta);

    /**
     * 增量更新用户帖子计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE "users"
        SET post_count = post_count + :delta
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addPostCount(@Param("userId") Long userId, @Param("delta") long delta);

    /**
     * 增量更新用户评分计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE "users"
        SET rating_count = rating_count + :delta
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addRatingCount(@Param("userId") Long userId, @Param("delta") long delta);

    /**
     * 校准并设置用户评论计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE users
        SET comment_count = :value
        WHERE user_id = :userId
        """, nativeQuery = true)
    int setCommentCount(@Param("userId") Long userId, @Param("value") long value);

    /**
     * 校准并设置用户帖子计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE users
        SET post_count = :value
        WHERE user_id = :userId
        """, nativeQuery = true)
    int setPostCount(@Param("userId") Long userId, @Param("value") long value);

    /**
     * 校准并设置用户评分计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE users
        SET rating_count = :value
        WHERE user_id = :userId
        """, nativeQuery = true)
    int setRatingCount(@Param("userId") Long userId, @Param("value") long value);

    /**
     * 增量更新用户粉丝计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE "users"
        SET follower_count = follower_count + :delta
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addFollowerCount(@Param("userId") Long userId, int delta);

    /**
     * 增量更新用户关注计数。
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE "users"
        SET following_count = following_count + :delta
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addFollowingCount(@Param("userId") Long userId, int delta);
}
