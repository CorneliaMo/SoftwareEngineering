package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.IdCount;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.FindAverageRatingResult;
import com.szu.afternoon5.softwareengineeringbackend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * 根据帖子ID和用户ID查询评分记录。
     */
    Optional<Rating> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 查询帖子的平均评分和评分人数。
     *
     * @return Object[] 数组，第一个元素为平均评分，第二个元素为评分人数
     */
    @Query("SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.FindAverageRatingResult(AVG(r.ratingValue), COUNT(r)) FROM Rating r WHERE r.postId = :postId")
    FindAverageRatingResult findAverageRatingAndCountByPostId(Long postId);


    @Query(value = """
        SELECT r.post_id AS id, COUNT(*)::bigint AS cnt
        FROM ratings r
        WHERE r.post_id = ANY(:postIds)
        GROUP BY r.post_id
        """, nativeQuery = true)
    List<IdCount> countByPostIds(@Param("postIds") Long[] postIds);

    @Query(value = """
        SELECT r.user_id AS id, COUNT(*)::bigint AS cnt
        FROM ratings r
        WHERE r.user_id = ANY(:userIds)
        GROUP BY r.user_id
        """, nativeQuery = true)
    List<IdCount> countByUserIds(@Param("userIds") Long[] userIds);
}
