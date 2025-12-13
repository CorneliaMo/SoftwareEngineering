package com.szu.afternoon5.softwareengineeringbackend.repository;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCover;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostWithCoverForNative;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface PostRepository extends JpaRepository<Post,Long> {

    /**
     * 查询用户的帖子并携带封面媒体信息，支持分页。
     */
    @Query(value = """
    SELECT p.postId, p.userId, p.postTitle, p.postText, p.isDeleted, p.deletedTime, p.createdTime, p.updatedTime, p.ratingCount, p.commentCount, p.coverMediaId, pm.uploadUserId, pm.mediaUrl, pm.mediaType, pm.sortOrder FROM Post p
    LEFT JOIN PostMedia pm ON p.coverMediaId = pm.mediaId
    WHERE p.userId = :userId AND p.isDeleted = :isDeleted
""")
    Page<PostWithCover> findByUserIdAndIsDeletedWithCover(Long userId, boolean isDeleted, Pageable pageable);

    /**
     * 插入Post实体的所有数据，通过标题与正文分词构建全文搜索索引，自增生成postId并返回
     * @return post_id 新增的post记录的自增postId
     */
    @Transactional
    @Query(value = """
        INSERT INTO posts (
            user_id,
            post_title,
            post_text,
            is_deleted,
            deleted_time,
            created_time,
            updated_time,
            rating_count,
            comment_count,
            cover_media_id,
            text_query
        )
        VALUES (
            :userId,
            :postTitle,
            :postText,
            :isDeleted,
            :deletedTime,
            :createdTime,
            :updatedTime,
            :ratingCount,
            :commentCount,
            :coverMediaId,

            -- 这里构建全文检索向量
            setweight(to_tsvector('simple', :titleSeg), 'A') ||
            setweight(to_tsvector('simple', :bodySeg),  'B')
        )
        RETURNING post_id
        """,
            nativeQuery = true)
    Long insertPostWithIndex(
            @Param("userId") Long userId,
            @Param("postTitle") String postTitle,
            @Param("postText") String postText,
            @Param("isDeleted") Boolean isDeleted,
            @Param("deletedTime") Instant deletedTime,
            @Param("createdTime") Instant createdTime,
            @Param("updatedTime") Instant updatedTime,
            @Param("ratingCount") Integer ratingCount,
            @Param("commentCount") Integer commentCount,
            @Param("coverMediaId") Long coverMediaId,
            @Param("titleSeg") String titleSeg,
            @Param("bodySeg") String bodySeg
    );

    /**
     * 更新指定postId的行的所有数据，通过标题与正文分词构建全文搜索索引，并返回受影响的行数
     * @return rows
     */
    @Transactional
    @Query(value = """
    UPDATE posts
    SET
        user_id = :userId,
        post_title = :postTitle,
        post_text = :postText,
        is_deleted = :isDeleted,
        deleted_time = :deletedTime,
        created_time = :createdTime,
        updated_time = :updatedTime,
        rating_count = :ratingCount,
        comment_count = :commentCount,
        cover_media_id = :coverMediaId,

        -- 重新构建全文检索向量
        text_query =
              setweight(to_tsvector('simple', :titleSeg), 'A')
          ||  setweight(to_tsvector('simple', :bodySeg),  'B')

    WHERE post_id = :postId
    """,
            nativeQuery = true)
    int updatePostWithIndex(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            @Param("postTitle") String postTitle,
            @Param("postText") String postText,
            @Param("isDeleted") Boolean isDeleted,
            @Param("deletedTime") Instant deletedTime,
            @Param("createdTime") Instant createdTime,
            @Param("updatedTime") Instant updatedTime,
            @Param("ratingCount") Integer ratingCount,
            @Param("commentCount") Integer commentCount,
            @Param("coverMediaId") Long coverMediaId,
            @Param("titleSeg") String titleSeg,
            @Param("bodySeg") String bodySeg
    );

    @Query("""
        SELECT p.postId, p.userId, p.postTitle, p.postText, p.isDeleted, p.deletedTime, p.createdTime, p.updatedTime, p.ratingCount, p.commentCount, p.coverMediaId, pm.uploadUserId, pm.mediaUrl, pm.mediaType, pm.sortOrder FROM Post p
        LEFT JOIN PostMedia pm ON p.coverMediaId = pm.mediaId
        WHERE p.isDeleted = false
          AND p.createdTime >= :start
          AND p.createdTime < :end
          AND (:userId IS NULL OR p.userId = :userId)
        """)
    Page<PostWithCover> searchByDateRange(Instant start,
                                 Instant end,
                                 Long userId,
                                 Pageable pageable);

    @Query("""
        SELECT p.postId, p.userId, p.postTitle, p.postText, p.isDeleted, p.deletedTime, p.createdTime, p.updatedTime, p.ratingCount, p.commentCount, p.coverMediaId, pm.uploadUserId, pm.mediaUrl, pm.mediaType, pm.sortOrder FROM Post p
        JOIN PostTag pt ON p.postId = pt.postId
        JOIN Tag t ON pt.tagId = t.tagId
        LEFT JOIN PostMedia pm ON p.coverMediaId = pm.mediaId
        WHERE p.isDeleted = false
          AND t.name = :tagName
          AND (:userId IS NULL OR p.userId = :userId)
        """)
    Page<PostWithCover> searchByTagName(String tagName,
                               Long userId,
                               Pageable pageable);

    @Query(value = """
            SELECT p.post_id, p.user_id, p.post_title, p.post_text, p.is_deleted, p.deleted_time, p.created_time, p.updated_time, p.rating_count, p.comment_count, p.cover_media_id, pm.upload_user_id, pm.media_url, pm.media_type, pm.sort_order FROM posts p
            LEFT JOIN post_media pm ON p.cover_media_id = pm.media_id
            WHERE p.is_deleted = FALSE
              AND p.text_query @@ plainto_tsquery('simple', :queryText)
            ORDER BY
              ts_rank_cd(p.text_query, plainto_tsquery('simple', :queryText)),
              p.created_time DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM posts p
            WHERE p.is_deleted = FALSE
              AND (
                    :queryText IS NULL
                    OR p.text_query @@ plainto_tsquery('simple', :queryText)
                  )
            """,
            nativeQuery = true
    )
    Page<PostWithCoverForNative> searchFullText(@Param("queryText") String queryText,
                                                Pageable pageable);

    @Modifying
    @Query(value = """
    UPDATE Post p
    SET p.coverMediaId = :coverMediaId
    WHERE p.postId = :postId
    """)
    void updatePostCover(Long postId, Long coverMediaId);

    @Query("""
    SELECT p.postId, p.userId, p.postTitle, p.postText, p.isDeleted, p.deletedTime, p.createdTime, p.updatedTime, p.ratingCount, p.commentCount, p.coverMediaId, pm.uploadUserId, pm.mediaUrl, pm.mediaType, pm.sortOrder FROM Post p
    LEFT JOIN PostMedia pm ON p.coverMediaId = pm.mediaId
    WHERE p.userId = :userId AND p.isDeleted = FALSE
""")
    Page<PostWithCover> findByUserIdAndIsDeleted(Long userId, boolean isDeleted, Pageable pageable);
}
