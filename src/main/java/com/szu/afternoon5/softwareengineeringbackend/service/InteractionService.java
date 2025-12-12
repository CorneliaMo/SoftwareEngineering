package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.*;
import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
import com.szu.afternoon5.softwareengineeringbackend.entity.Rating;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.CommentRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.RatingRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.UserRepository;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 用户互动服务，负责处理评分和评论相关业务逻辑，
 * 包括评分提交、查询、删除以及评论的发布、查询、删除等操作。
 */
@Service
public class InteractionService {

    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PageableUtils pageableUtils;

    /**
     * 构造互动服务，注入所需仓储与工具。
     */
    public InteractionService(RatingRepository ratingRepository, CommentRepository commentRepository,
                              PostRepository postRepository, UserRepository userRepository,
                              PageableUtils pageableUtils) {
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pageableUtils = pageableUtils;
    }

    /**
     * 获取帖子评分信息，包括平均分、总评分人数和当前用户评分。
     *
     * @param postId         帖子ID
     * @param authentication 认证信息，用于获取当前用户
     * @return 评分统计信息
     */
    public RatingInfo getRating(Long postId, Authentication authentication) {
        // 检查帖子是否存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 使用Repository的查询方法获取平均评分和数量
        Object[] ratingStats = ratingRepository.findAverageRatingAndCountByPostId(postId);
        Double averageRating = ratingStats != null && ratingStats[0] != null ?
                ((Number) ratingStats[0]).doubleValue() : 0.0;
        Long ratingCount = ratingStats != null && ratingStats[1] != null ?
                ((Number) ratingStats[1]).longValue() : 0L;

        // 获取当前用户评分
        Optional<Rating> userRating = ratingRepository.findByPostIdAndUserId(postId, loginPrincipal.getUserId());
        Integer myRating = userRating.map(Rating::getRatingValue).orElse(0);

        return new RatingInfo(
                averageRating,
                ratingCount.intValue(),
                myRating
        );
    }

    /**
     * 提交评分，用户可为帖子打分（1-5分），如已评分则更新。
     *
     * @param postId         帖子ID
     * @param request        评分请求
     * @param authentication 认证信息
     * @return 提交后的评分统计信息
     */
    @Transactional
    public SubmitRatingResponse submitRating(Long postId, @Valid SubmitRatingRequest request,
                                             Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 检查帖子是否存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        // 检查用户是否已评分
        Optional<Rating> existingRating = ratingRepository.findByPostIdAndUserId(
                postId, loginPrincipal.getUserId());

        Rating rating;
        if (existingRating.isPresent()) {
            // 更新现有评分
            rating = existingRating.get();
            rating.setRatingValue(request.getRating());
            rating.setUpdatedTime(Instant.now());
        } else {
            // 创建新评分
            rating = new Rating(
                    postId,
                    loginPrincipal.getUserId(),
                    request.getRating()
            );
        }

        ratingRepository.save(rating);

        // 重新计算统计信息
        Object[] ratingStats = ratingRepository.findAverageRatingAndCountByPostId(postId);
        Double averageRating = ratingStats != null && ratingStats[0] != null ?
                ((Number) ratingStats[0]).doubleValue() : 0.0;
        Long ratingCount = ratingStats != null && ratingStats[1] != null ?
                ((Number) ratingStats[1]).longValue() : 0L;

        return new SubmitRatingResponse(
                averageRating,
                ratingCount.intValue()
        );
    }

    /**
     * 删除用户对帖子的评分。
     *
     * @param postId         帖子ID
     * @param authentication 认证信息
     */
    @Transactional
    public void deleteRating(Long postId, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 检查帖子是否存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        // 删除用户的评分
        Optional<Rating> userRating = ratingRepository.findByPostIdAndUserId(postId, loginPrincipal.getUserId());
        if (userRating.isPresent()) {
            ratingRepository.delete(userRating.get());
        }
        // 如果用户没有评分，也不抛出异常，静默成功
    }

    /**
     * 发表评论，用户可在帖子下发表评论。
     *
     * @param postId         帖子ID
     * @param request        评论请求
     * @param authentication 认证信息
     * @return 新创建的评论ID
     */
    @Transactional
    public SubmitCommentResponse submitComment(Long postId, @Valid SubmitCommentRequest request,
                                               Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 检查帖子是否存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        // 创建新评论
        Comment comment = new Comment(
                postId,
                loginPrincipal.getUserId(),
                null, // 父评论ID，暂时不支持回复功能
                request.getCommentText()
        );

        Comment savedComment = commentRepository.save(comment);
        return new SubmitCommentResponse(savedComment.getCommentId());
    }

    /**
     * 获取帖子的评论列表，支持分页。
     *
     * @param postId      帖子ID
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 评论分页列表
     */
    public CommentListResponse getComments(Long postId, Integer currentPage, Integer pageSize) {
        // 检查帖子是否存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        Pageable pageable = pageableUtils.buildPageable(
                List.of("createdTime"), currentPage - 1, pageSize, "createdTime", "DESC");

        Page<Comment> commentPage = commentRepository.findByPostIdAndIsDeletedFalse(postId, pageable);

        // 转换Comment实体为CommentInfo DTO
        List<CommentInfo> commentInfos = commentPage.getContent().stream()
                .map(comment -> {
                    // 获取用户信息
                    var user = userRepository.findByUserId(comment.getUserId()).orElse(null);
                    return new CommentInfo(
                            comment.getUserId(),
                            user != null ? user.getNickname() : "未知用户",
                            user != null ? user.getAvatarUrl() : null,
                            comment.getCommentId(),
                            comment.getPostId(),
                            comment.getParentId(),
                            comment.getCommentText(),
                            comment.getCreatedTime(),
                            comment.getUpdatedTime()
                    );
                })
                .toList();

        return new CommentListResponse(
                commentPage.getTotalPages(),
                (int) commentPage.getTotalElements(),
                commentPage.getNumber() + 1,
                commentPage.getSize(),
                commentInfos
        );
    }

    /**
     * 删除评论，要求操作者为评论作者或管理员。
     *
     * @param postId         帖子ID
     * @param request        删除评论请求
     * @param authentication 认证信息
     */
    @Transactional
    public void deleteComment(Long postId, @Valid DeleteCommentRequest request,
                              Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Optional<Comment> commentOptional = commentRepository.findById(request.getCommentId());
        if (commentOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }

        Comment comment = commentOptional.get();

        // 检查评论是否属于该帖子
        if (!comment.getPostId().equals(postId)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "评论不属于该帖子");
        }

        // 检查权限：评论作者或管理员可以删除
        boolean hasPermission = false;
        if (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            hasPermission = loginPrincipal.getUserId().equals(comment.getUserId());
        } else if (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.admin)) {
            hasPermission = true;
        }

        if (hasPermission) {
            comment.setIsDeleted(true);
            comment.setUpdatedTime(Instant.now());
            commentRepository.save(comment);
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有评论作者或管理员可以删除评论");
        }
    }
}
