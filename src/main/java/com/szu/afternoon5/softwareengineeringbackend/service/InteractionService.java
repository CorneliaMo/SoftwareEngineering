package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.*;
import com.szu.afternoon5.softwareengineeringbackend.entity.*;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.event.*;
import com.szu.afternoon5.softwareengineeringbackend.repository.*;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PageableUtils pageableUtils;
    private final ContentFilterService contentFilterService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserRepository userRepository;
    private final FollowRecordRepository followRecordRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationOneToOneMapRepository conversationOneToOneMapRepository;
    private final ParticipantRepository participantRepository;
    private final MessageRepository messageRepository;

    /**
     * 构造互动服务，注入所需仓储与工具。
     */
    public InteractionService(RatingRepository ratingRepository, CommentRepository commentRepository,
                              PostRepository postRepository, PageableUtils pageableUtils, ContentFilterService contentFilterService, ApplicationEventPublisher applicationEventPublisher, UserRepository userRepository, FollowRecordRepository followRecordRepository, ConversationRepository conversationRepository, ConversationOneToOneMapRepository conversationOneToOneMapRepository, ParticipantRepository participantRepository, MessageRepository messageRepository) {
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.pageableUtils = pageableUtils;
        this.contentFilterService = contentFilterService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userRepository = userRepository;
        this.followRecordRepository = followRecordRepository;
        this.conversationRepository = conversationRepository;
        this.conversationOneToOneMapRepository = conversationOneToOneMapRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * 获取帖子评分信息，包括平均分、总评分人数和当前用户评分。
     *
     * @param postId         帖子ID
     * @param authentication 认证信息，用于获取当前用户
     * @return 评分统计信息
     */
    public RatingInfo getRating(Long postId, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        checkPrincipalAndPostExist(loginPrincipal, postId);

        // 使用Repository的查询方法获取平均评分和数量
        // TODO：未来使用冗余字段与Redis缓存
        FindAverageRatingResult ratingStats = ratingRepository.findAverageRatingAndCountByPostId(postId);

        // 获取当前用户评分
        Optional<Rating> userRating = ratingRepository.findByPostIdAndUserId(postId, loginPrincipal.getUserId());
        Integer myRating = userRating.map(Rating::getRatingValue).orElse(0);

        return new RatingInfo(
                ratingStats.getAverageRating(),
                Math.toIntExact(ratingStats.getRatingCount()),
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
        checkPrincipalAndPostExist(loginPrincipal, postId);

        // 检查用户是否已评分
        Optional<Rating> existingRating = ratingRepository.findByPostIdAndUserId(
                postId, loginPrincipal.getUserId());

        Rating rating;
        if (existingRating.isPresent()) {
            // 更新现有评分
            rating = existingRating.get();
            rating.setRatingValue(request.getRating());
            rating.setUpdatedTime(Instant.now());
            ratingRepository.save(rating);
        } else {
            // 创建新评分
            rating = new Rating(
                    postId,
                    loginPrincipal.getUserId(),
                    request.getRating()
            );
            Rating newRating = ratingRepository.save(rating);
            applicationEventPublisher.publishEvent(new RatingCreatedEvent(newRating.getRatingId(), postId, loginPrincipal.getUserId()));
        }

        // 重新计算统计信息
        FindAverageRatingResult ratingStats = ratingRepository.findAverageRatingAndCountByPostId(postId);

        return new SubmitRatingResponse(
                ratingStats.getAverageRating(),
                Math.toIntExact(ratingStats.getRatingCount())
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
        checkPrincipalAndPostExist(loginPrincipal, postId);

        // 删除用户的评分
        Optional<Rating> userRating = ratingRepository.findByPostIdAndUserId(postId, loginPrincipal.getUserId());
        if (userRating.isPresent()) {
            ratingRepository.delete(userRating.get());
            applicationEventPublisher.publishEvent(new RatingDeletedEvent(userRating.get().getRatingId(), postId, loginPrincipal.getUserId()));
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
        checkPrincipalAndPostExist(loginPrincipal, postId);

        // 进行内容过滤
        ContentFilterService.FilterResult textFilterResult;
        textFilterResult = contentFilterService.filter(request.getCommentText());
        if (textFilterResult.isMatched()) {
            throw new BusinessException(ErrorCode.CONTENT_BLOCKED);
        }

        // 创建新评论
        // TODO：设计接口支持回复功能
        Comment comment = new Comment(
                postId,
                loginPrincipal.getUserId(),
                null, // 父评论ID，暂时不支持回复功能
                request.getCommentText()
        );

        Comment savedComment = commentRepository.save(comment);
        applicationEventPublisher.publishEvent(new CommentCreatedEvent(savedComment.getCommentId(), postId, loginPrincipal.getUserId()));
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

        // TODO：支持前端传入排序参数
        List<String> sortColumns = List.of("created_time", "updated_time");
        Pageable pageable = pageableUtils.buildPageable(
                sortColumns, currentPage - 1, pageSize, "created_time", "DESC");

        Page<CommentInfo> commentPage = commentRepository.findCommentInfoByPostIdAndIsDeletedFalse(postId, pageable);

        return new CommentListResponse(
                commentPage.getTotalPages(),
                (int) commentPage.getTotalElements(),
                commentPage.getNumber() + 1,
                commentPage.getSize(),
                commentPage.getContent()
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
        if (comment.getPostId() != null && !comment.getPostId().equals(postId)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "评论不属于该帖子");
        }

        // 检查权限：评论作者可以删除
        boolean hasPermission = false;
        if (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            hasPermission = loginPrincipal.getUserId().equals(comment.getUserId());
        }

        if (hasPermission) {
            comment.setIsDeleted(true);
            comment.setUpdatedTime(Instant.now());
            commentRepository.save(comment);
            applicationEventPublisher.publishEvent(new CommentDeletedEvent(comment.getCommentId(), postId, loginPrincipal.getUserId()));
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有评论作者可以删除评论");
        }
    }

    private void checkPrincipalAndPostExist(LoginPrincipal loginPrincipal, Long postId) {
        checkPrincipal(loginPrincipal);
        // 检查帖子是否存在
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
    }

    private LoginPrincipal checkPrincipal(LoginPrincipal loginPrincipal) {
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginPrincipal;
    }

    @Transactional
    public void followUser(Long userId, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        if (!userRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "关注的用户不存在");
        } else if (followRecordRepository.existsByFollowerIdAndFolloweeId(loginPrincipal.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.SUCCESS);
        } else {
            FollowRecord followRecord = new FollowRecord(loginPrincipal.getUserId(), userId);
            followRecordRepository.save(followRecord);
            applicationEventPublisher.publishEvent(new FollowCreatedEvent(followRecord.getFollowerId(), followRecord.getFolloweeId()));
        }
    }

    @Transactional
    public void unfollowUser(Long userId, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        if (!userRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "关注的用户不存在");
        } else if (!followRecordRepository.existsByFollowerIdAndFolloweeId(loginPrincipal.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未关注此用户");
        } else {
            followRecordRepository.deleteByFollowerIdAndFolloweeId(loginPrincipal.getUserId(), userId);
            applicationEventPublisher.publishEvent(new FollowDeletedEvent(loginPrincipal.getUserId(), userId));
        }
    }

    public FollowStatusResponse getFollowStatus(Long userId, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        if (!userRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        } else {
            return followRecordRepository.getFollowStatus(loginPrincipal.getUserId(), userId);
        }
    }

    public InteractionUserListResponse getFollowing(Integer currentPage, Integer pageSize, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        return getFollowing(currentPage, pageSize, loginPrincipal.getUserId());
    }

    public InteractionUserListResponse getFollowing(Integer currentPage, Integer pageSize, Long userId) {
        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("follower_id", "followee_id", "created_time");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "followee_id", "ASC");

        Page<UserInfo> userInfoPage = followRecordRepository.getFollowingUserInfo(userId, pageable);
        return new InteractionUserListResponse(
                userInfoPage.getTotalPages(),
                (int) userInfoPage.getTotalElements(),
                userInfoPage.getNumber() + 1,
                userInfoPage.getSize(),
                userInfoPage.getContent()
        );
    }

    public InteractionUserListResponse getFollowers(Integer currentPage, Integer pageSize, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        return getFollowers(currentPage, pageSize, loginPrincipal.getUserId());
    }

    public InteractionUserListResponse getFollowers(Integer currentPage, Integer pageSize, Long userId) {
        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("follower_id", "followee_id", "created_time");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "follower_id", "ASC");

        Page<UserInfo> userInfoPage = followRecordRepository.getFollowerUserInfo(userId, pageable);
        return new InteractionUserListResponse(
                userInfoPage.getTotalPages(),
                (int) userInfoPage.getTotalElements(),
                userInfoPage.getNumber() + 1,
                userInfoPage.getSize(),
                userInfoPage.getContent()
        );
    }

    public FriendStatusResponse getFriendStatus(Long userId, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        return followRecordRepository.getFriendStatus(userId, loginPrincipal.getUserId());
    }

    public InteractionUserListResponse getFriends(Integer currentPage, Integer pageSize, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        // TODO：预留前端排序的空间
        List<String> sortColumns = List.of("user_id", "nickname", "created_time", "updated_time", "comment_count", "post_count", "rating_count");
        Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "user_id", "ASC");

        Page<UserInfo> userInfoPage = followRecordRepository.getFriends(loginPrincipal.getUserId(), pageable);
        return new InteractionUserListResponse(
                userInfoPage.getTotalPages(),
                (int) userInfoPage.getTotalElements(),
                userInfoPage.getNumber() + 1,
                userInfoPage.getSize(),
                userInfoPage.getContent()
        );
    }

    public ConversationListResponse getConversations(Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        List<ConversationDetail> conversationDetails = conversationRepository.getConversations(loginPrincipal.getUserId());
        return new ConversationListResponse(conversationDetails);
    }

    @Transactional
    public ConversationCreateResponse createConversation(@Valid ConversationCreateRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        if (!userRepository.existsByUserId(loginPrincipal.getUserId()) || !userRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        } else {
            Long userLowId = Math.min(request.getUserId(), loginPrincipal.getUserId());
            Long userHighId = Math.max(request.getUserId(), loginPrincipal.getUserId());
            if (conversationOneToOneMapRepository.existsByUserLowIdAndUserHighId(userLowId, userHighId)) {
                // 对话已存在，返回现有信息
                Optional<ConversationDetail> conversationDetailOptional = conversationRepository.getConversation(loginPrincipal.getUserId(), request.getUserId());
                if (conversationDetailOptional.isPresent()) {
                    return new ConversationCreateResponse(conversationDetailOptional.get());
                } else {
                    throw new BusinessException(ErrorCode.NOT_FOUND, "对话不存在");
                }
            } else {
                // 创建新对话
                Conversation conversation = new Conversation();
                Conversation savedConversation = conversationRepository.save(conversation);
                ConversationOneToOneMap conversationOneToOneMap = new ConversationOneToOneMap(userLowId, userHighId, savedConversation.getConversationId());
                Participant participantMe = new Participant(savedConversation.getConversationId(), loginPrincipal.getUserId());
                Participant participantOther = new Participant(savedConversation.getConversationId(), request.getUserId());
                conversationOneToOneMapRepository.save(conversationOneToOneMap);
                participantRepository.save(participantMe);
                participantRepository.save(participantOther);

                return new ConversationCreateResponse(new ConversationDetail(savedConversation, participantMe, conversationOneToOneMap));
            }
        }
    }

    public ConversationMessageListResponse getConversationMessages(Long conversationId, Long beforeId, Long afterId, Integer limit, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        if (conversationRepository.existsByConversationIdAndParticipantId(conversationId, loginPrincipal.getUserId())) {
            List<MessageInfo> messageInfos;
            if (limit != null) {
                // 构建分页
                PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdTime"));
                messageInfos = messageRepository.getConversationMessages(conversationId, beforeId, afterId, pageRequest);
            } else {
                // 无需分页
                Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
                messageInfos = messageRepository.getConversationMessages(conversationId, beforeId, afterId, sort);
            }
            return new ConversationMessageListResponse(messageInfos);
        } else {
            throw new BusinessException(ErrorCode.NOT_FOUND, "对话不存在");
        }
    }

    public MessageSendResponse sendConversationMessage(Long conversationId, @Valid MessageSendRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = checkPrincipal((LoginPrincipal) authentication.getPrincipal());
        if (conversationRepository.existsByConversationIdAndParticipantId(conversationId, loginPrincipal.getUserId())) {
            Message message = new Message(conversationId, loginPrincipal.getUserId(), Message.MessageType.text, request.getText());
            Message savedMessage = messageRepository.save(message);
            return new MessageSendResponse(new MessageInfo(savedMessage));
        } else {
            throw new BusinessException(ErrorCode.NOT_FOUND, "对话不存在");
        }
    }
}
