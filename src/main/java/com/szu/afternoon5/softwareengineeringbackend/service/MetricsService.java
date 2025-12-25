package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.IdCount;
import com.szu.afternoon5.softwareengineeringbackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private static final String DIRTY_POST_KEY = "dirty:post";
    private static final String DIRTY_USER_KEY = "dirty:user";

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 用于“校准”时重算真实 count
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final StringRedisTemplate redis;

    /**
     * 每次校准最多处理多少个 postId/userId（避免对 DB 产生尖峰压力）
     */
    @Value("${metrics.reconcile.batchSize:1000}")
    private int reconcileBatchSize;

    // ------------------------
    // 事件消费入口（给 Consumer 调用）
    // ------------------------

    @Transactional
    public void onCommentDelta(Long commentId, Long postId, Long userId, int delta) {
        int postRows = postRepository.addCommentCount(postId, delta);
        int userRows = userRepository.addCommentCount(userId, delta);

        markDirtyPost(postId);
        markDirtyUser(userId);

        log.debug("[指标消费] comment{}1 已处理. commentId={}, postId={}, userId={}, delta={}, postRows={}, userRows={}",
                delta > 0 ? "+" : "-", commentId, postId, userId, delta, postRows, userRows);
    }

    @Transactional
    public void onPostDelta(Long postId, Long userId, int delta) {
        int userRows = userRepository.addPostCount(userId, delta);

        markDirtyUser(userId);

        log.debug("[指标消费] post{}1 已处理. postId={}, userId={}, delta={}, userRows={}",
                delta > 0 ? "+" : "-", postId, userId, delta, userRows);
    }

    @Transactional
    public void onRatingDelta(Long ratingId, Long postId, Long userId, int delta) {
        int postRows = postRepository.addRatingCount(postId, delta);
        int userRows = userRepository.addRatingCount(userId, delta);

        markDirtyPost(postId);
        markDirtyUser(userId);

        log.debug("[指标消费] rating{}1 已处理. ratingId={}, postId={}, userId={}, delta={}, postRows={}, userRows={}",
                delta > 0 ? "+" : "-", ratingId, postId, userId, delta, postRows, userRows);
    }

    @Transactional
    public void onFollowDelta(Long followerId, Long followeeId, int delta) {
        int userRows1 = userRepository.addFollowerCount(followeeId, delta);
        int userRows2 = userRepository.addFollowingCount(followerId, delta);

        // TODO：定时聚合

        log.debug("[指标消费] follow{}1 已处理. followerId={}, followeeId={}, delta={}, userRows1={}, userRows2={}",
                delta > 0 ? "+" : "-", followerId, followeeId, delta, userRows1, userRows2);
    }

    // ------------------------
    // Dirty set
    // ------------------------

    private void markDirtyPost(Long postId) {
        if (postId == null) return;
        redis.opsForSet().add(DIRTY_POST_KEY, String.valueOf(postId));
    }

    private void markDirtyUser(Long userId) {
        if (userId == null) return;
        redis.opsForSet().add(DIRTY_USER_KEY, String.valueOf(userId));
    }

    // ------------------------
    // 定时聚合（校准冗余计数）
    // ------------------------
    /**
     * 每隔一段时间校准一次：只校准“dirty 的 user / post”
     * <p>
     * 注意：
     * - 这个任务是“兜底”，不追求 100% 实时
     * - 为避免影响主业务：批量处理 + 控制 batchSize
     */
    @Scheduled(fixedDelayString = "${metrics.reconcile.fixedDelayMs:300000}") // 默认 5 分钟
    @Transactional
    public void reconcileDirty() {
        List<Long> postIds = popDirtyIds(DIRTY_POST_KEY, reconcileBatchSize);
        List<Long> userIds = popDirtyIds(DIRTY_USER_KEY, reconcileBatchSize);

        if (postIds.isEmpty() && userIds.isEmpty()) {
            return;
        }

        if (!postIds.isEmpty()) {
            reconcilePosts(postIds);
        }
        if (!userIds.isEmpty()) {
            reconcileUsers(userIds);
        }

        log.debug("[指标校准] reconcile 完成. postIds={}, userIds={}", postIds.size(), userIds.size());
    }

    private List<Long> popDirtyIds(String key, int count) {
        // Spring Data Redis 支持一次 pop 多个（底层用 SPOP count）
        Set<String> popped = new HashSet<>(redis.opsForSet().pop(key, count));
        if (popped.isEmpty()) return List.of();

        List<Long> ids = new ArrayList<>(popped.size());
        for (String s : popped) {
            try {
                ids.add(Long.parseLong(s));
            } catch (NumberFormatException ignored) {}
        }
        return ids;
    }

    // ------------------------
    // 校准：Post（commentCount / ratingCount）
    // ------------------------
    private void reconcilePosts(List<Long> postIds) {
        // 1) 重算 comment_count（只算这些 post）
        Map<Long, Long> commentCounts = toMap(commentRepository.countByPostIds(postIds.toArray(new Long[0])));
        // 2) 重算 rating_count
        Map<Long, Long> ratingCounts = toMap(ratingRepository.countByPostIds(postIds.toArray(new Long[0])));

        // 3) 回写到 Post 冗余字段（不存在的默认 0）
        for (Long postId : postIds) {
            long cc = commentCounts.getOrDefault(postId, 0L);
            long rc = ratingCounts.getOrDefault(postId, 0L);

            postRepository.setCommentCount(postId, cc);
            postRepository.setRatingCount(postId, rc);
        }

        log.debug("[指标校准] post 校准完成. ids={}, commentMap={}, ratingMap={}",
                postIds.size(), commentCounts.size(), ratingCounts.size());
    }

    // ------------------------
    // 校准：User（commentCount / postCount / ratingCount）
    // ------------------------
    private void reconcileUsers(List<Long> userIds) {
        Map<Long, Long> commentCounts = toMap(commentRepository.countByUserIds(userIds.toArray(new Long[0])));
        Map<Long, Long> postCounts = toMap(postRepository.countByUserIds(userIds.toArray(new Long[0])));
        Map<Long, Long> ratingCounts = toMap(ratingRepository.countByUserIds(userIds.toArray(new Long[0])));

        for (Long userId : userIds) {
            long cc = commentCounts.getOrDefault(userId, 0L);
            long pc = postCounts.getOrDefault(userId, 0L);
            long rc = ratingCounts.getOrDefault(userId, 0L);

            userRepository.setCommentCount(userId, cc);
            userRepository.setPostCount(userId, pc);
            userRepository.setRatingCount(userId, rc);
        }

        log.debug("[指标校准] user 校准完成. ids={}, commentMap={}, postMap={}, ratingMap={}",
                userIds.size(), commentCounts.size(), postCounts.size(), ratingCounts.size());
    }

    private static Map<Long, Long> toMap(List<IdCount> rows) {
        return rows.stream().collect(Collectors.toMap(IdCount::getId, IdCount::getCnt));
    }
}
