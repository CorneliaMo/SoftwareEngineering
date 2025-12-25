package com.szu.afternoon5.softwareengineeringbackend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDeletionService {

    @PersistenceContext
    private EntityManager em;

    /**
     * 物理删除用户数据（一次事务内）。
     * 注意：这是“彻底删除”策略，不是匿名化。
     */
    @Transactional
    public void deleteUserDataTransactional(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");

        // 0) 锁定用户行（避免并发修改/重复删除）
        int exists = em.createNativeQuery("""
            SELECT 1 FROM users WHERE user_id = :uid FOR UPDATE
        """)
        .setParameter("uid", userId)
        .getResultList()
        .size();

        if (exists == 0) {
            log.info("[注销删除] 用户不存在，跳过 userId={}", userId);
            return;
        }

        // 1) 修正关注冗余计数（在删 follows 之前做）
        // 我关注的人：他们的 follower_count -1
        em.createNativeQuery("""
            UPDATE users u
            SET follower_count = GREATEST(u.follower_count - x.cnt, 0),
                updated_time = CURRENT_TIMESTAMP
            FROM (
                SELECT followee_id AS uid, COUNT(*)::int AS cnt
                FROM follows
                WHERE follower_id = :uid
                GROUP BY followee_id
            ) x
            WHERE u.user_id = x.uid
        """).setParameter("uid", userId).executeUpdate();

        // 关注我的人：他们的 following_count -1
        em.createNativeQuery("""
            UPDATE users u
            SET following_count = GREATEST(u.following_count - x.cnt, 0),
                updated_time = CURRENT_TIMESTAMP
            FROM (
                SELECT follower_id AS uid, COUNT(*)::int AS cnt
                FROM follows
                WHERE followee_id = :uid
                GROUP BY follower_id
            ) x
            WHERE u.user_id = x.uid
        """).setParameter("uid", userId).executeUpdate();

        // 删关注关系（其实删 users 也会 CASCADE，但这里显式删，便于计数逻辑更可控）
        em.createNativeQuery("""
            DELETE FROM follows
            WHERE follower_id = :uid OR followee_id = :uid
        """).setParameter("uid", userId).executeUpdate();

        // 2) 删该用户发出的消息（messages.sender_id ON DELETE CASCADE，但先删好做 conversations 修复）
        em.createNativeQuery("""
            DELETE FROM messages
            WHERE sender_id = :uid
        """).setParameter("uid", userId).executeUpdate();

        // 3) 修复 conversations 的 last_message 指针（因为 last_message_id 没 FK）
        // 简化策略：如果 last_message_id 已不存在，就置空
        em.createNativeQuery("""
            UPDATE conversations c
            SET last_message_id = NULL,
                last_message_time = NULL,
                updated_time = CURRENT_TIMESTAMP
            WHERE c.last_message_id IS NOT NULL
              AND NOT EXISTS (
                  SELECT 1 FROM messages m WHERE m.message_id = c.last_message_id
              )
        """).executeUpdate();

        // 4) 删 participants / o2o map（删 users 也会 CASCADE，但显式删更干净）
        em.createNativeQuery("""
            DELETE FROM participants
            WHERE user_id = :uid
        """).setParameter("uid", userId).executeUpdate();

        em.createNativeQuery("""
            DELETE FROM conversation_one_to_one_map
            WHERE user_low_id = :uid OR user_high_id = :uid
        """).setParameter("uid", userId).executeUpdate();

        // 5) 删除“用户自己的评论”，并重算受影响帖子的 comment_count（只重算别人的帖子）
        em.createNativeQuery("""
            WITH del AS (
                DELETE FROM comments
                WHERE user_id = :uid
                RETURNING post_id
            )
            UPDATE posts p
            SET comment_count = (
                SELECT COUNT(*)::int FROM comments c
                WHERE c.post_id = p.post_id AND c.is_deleted = false
            ),
            updated_time = CURRENT_TIMESTAMP
            WHERE p.post_id IN (SELECT DISTINCT post_id FROM del WHERE post_id IS NOT NULL)
        """).setParameter("uid", userId).executeUpdate();

        // 6) 删除“用户自己的评分”，并重算受影响帖子的 rating_count
        em.createNativeQuery("""
            WITH del AS (
                DELETE FROM ratings
                WHERE user_id = :uid
                RETURNING post_id
            )
            UPDATE posts p
            SET rating_count = (
                SELECT COUNT(*)::int FROM ratings r
                WHERE r.post_id = p.post_id
            ),
            updated_time = CURRENT_TIMESTAMP
            WHERE p.post_id IN (SELECT DISTINCT post_id FROM del WHERE post_id IS NOT NULL)
        """).setParameter("uid", userId).executeUpdate();

        // 7) 删除用户发布的帖子下的所有评论/评分（别人对他帖子的互动也清理）
        // 先删评论（避免 parent_id 自引用残留无所谓，FK 是 SET NULL，不会阻塞）
        em.createNativeQuery("""
            DELETE FROM comments
            WHERE post_id IN (SELECT post_id FROM posts WHERE user_id = :uid)
        """).setParameter("uid", userId).executeUpdate();

        em.createNativeQuery("""
            DELETE FROM ratings
            WHERE post_id IN (SELECT post_id FROM posts WHERE user_id = :uid)
        """).setParameter("uid", userId).executeUpdate();

        // 8) 删媒体：
        // - 用户上传的媒体（upload_user_id = uid）
        // - 用户帖子内容媒体（post_id in user's posts）
        // 注意：posts.cover_media_id FK -> post_media ON DELETE SET NULL，不会阻塞 media 删除
        em.createNativeQuery("""
            DELETE FROM post_media
            WHERE upload_user_id = :uid
               OR post_id IN (SELECT post_id FROM posts WHERE user_id = :uid)
        """).setParameter("uid", userId).executeUpdate();

        // 9) 删用户帖子（post_tags 会 CASCADE）
        em.createNativeQuery("""
            DELETE FROM posts
            WHERE user_id = :uid
        """).setParameter("uid", userId).executeUpdate();

        // 10) 最后删用户（user_deletion_requests 会 ON DELETE CASCADE 一起删掉）
        em.createNativeQuery("""
            DELETE FROM users
            WHERE user_id = :uid
        """).setParameter("uid", userId).executeUpdate();

        log.info("[注销删除] 完成 userId={}", userId);
    }
}
