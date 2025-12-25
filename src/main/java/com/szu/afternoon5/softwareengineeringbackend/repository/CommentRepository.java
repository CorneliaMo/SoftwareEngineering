package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.IdCount;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.CommentInfo;
import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据帖子ID查询未删除的评论列表，支持分页。
     * 联接Rating表获取评论用户对该帖子的评分。
     */
    @Query(value = """
    SELECT new com.szu.afternoon5.softwareengineeringbackend.dto.interactions.CommentInfo(c.userId, u.nickname, u.avatarUrl, c.commentId, c.postId, c.parentId, c.commentText, c.createdTime, c.updatedTime, r.ratingValue) FROM Comment c
    JOIN User u ON c.userId = u.userId
    LEFT JOIN Rating r ON c.postId = r.postId AND c.userId = r.userId
    WHERE c.postId = :postId AND c.isDeleted = FALSE
""")
    Page<CommentInfo> findCommentInfoByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    /**
     * 根据用户ID查询未删除的评论列表，支持分页。
     */
    Page<Comment> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    @Query(value = """
        SELECT c.post_id AS id, COUNT(*)::bigint AS cnt
        FROM comments c
        WHERE c.is_deleted = false
          AND c.post_id = ANY(:postIds)
        GROUP BY c.post_id
        """, nativeQuery = true)
    List<IdCount> countByPostIds(@Param("postIds") Long[] postIds);

    @Query(value = """
        SELECT c.user_id AS id, COUNT(*)::bigint AS cnt
        FROM comments c
        WHERE c.is_deleted = false
          AND c.user_id = ANY(:userIds)
        GROUP BY c.user_id
        """, nativeQuery = true)
    List<IdCount> countByUserIds(@Param("userIds") Long[] userIds);
}