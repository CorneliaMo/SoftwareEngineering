package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.*;
import com.szu.afternoon5.softwareengineeringbackend.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户互动相关接口控制器，涵盖评分、评论等行为。
 * <p>
 * 提供评分的增删查、评论的增删查功能，并结合安全上下文进行权限校验。
 */
@RestController
@RequestMapping("/interactions")
@PreAuthorize("@perm.isUser(authentication.principal)")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    /**
     * 获取评分信息。
     *
     * @param postId         帖子标识
     * @param authentication 认证信息
     * @return 评分统计信息，包含平均分、总人数及当前用户评分
     */
    @GetMapping("/rating/{post_id}")
    public RatingInfo getRating(@PathVariable("post_id") Long postId, Authentication authentication) {
        return interactionService.getRating(postId, authentication);
    }

    /**
     * 提交评分。
     *
     * @param postId         帖子标识
     * @param request        评分提交请求
     * @param authentication 认证信息
     * @return 操作结果，包含更新后的评分统计
     */
    @PostMapping("/rating/{post_id}")
    public SubmitRatingResponse submitRating(@PathVariable("post_id") Long postId,
                                             @Valid @RequestBody SubmitRatingRequest request,
                                             Authentication authentication) {
        return interactionService.submitRating(postId, request, authentication);
    }

    /**
     * 删除评分。
     *
     * @param postId         帖子标识
     * @param authentication 认证信息
     * @return 操作结果
     */
    @DeleteMapping("/rating/{post_id}")
    public BaseResponse deleteRating(@PathVariable("post_id") Long postId, Authentication authentication) {
        interactionService.deleteRating(postId, authentication);
        return new BaseResponse();
    }

    /**
     * 发表评论。
     *
     * @param postId         帖子标识
     * @param request        评论提交请求
     * @param authentication 认证信息
     * @return 操作结果，包含新评论的标识
     */
    @PostMapping("/comments/{post_id}")
    public SubmitCommentResponse submitComment(@PathVariable("post_id") Long postId,
                                               @Valid @RequestBody SubmitCommentRequest request,
                                               Authentication authentication) {
        return interactionService.submitComment(postId, request, authentication);
    }

    /**
     * 获取评论列表。
     *
     * @param postId      帖子标识
     * @param currentPage 当前页码
     * @param pageSize    每页数量
     * @return 分页评论列表
     */
    @GetMapping("/comments/{post_id}")
    public CommentListResponse getComments(@PathVariable("post_id") Long postId,
                                           @RequestParam(value = "current_page", defaultValue = "1") Integer currentPage,
                                           @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return interactionService.getComments(postId, currentPage, pageSize);
    }

    /**
     * 删除评论。
     *
     * @param postId         帖子标识
     * @param request        评论删除请求
     * @param authentication 认证信息
     * @return 操作结果
     */
    @DeleteMapping("/comments/{post_id}")
    @PreAuthorize("@perm.isUser(authentication.principal) || @perm.isAdmin(authentication.principal)")
    public BaseResponse deleteComment(@PathVariable("post_id") Long postId,
                                      @Valid @RequestBody DeleteCommentRequest request,
                                      Authentication authentication) {
        interactionService.deleteComment(postId, request, authentication);
        return new BaseResponse();
    }

    /**
     * 关注用户
     * POST /interactions/follows/{user_id}
     */
    @PostMapping("/follows/{user_id}")
    public BaseResponse followUser(@PathVariable("user_id") Long userId, Authentication authentication) {
        interactionService.followUser(userId, authentication);
        return new BaseResponse();
    }

    /**
     * 取消关注用户
     * DELETE /interactions/follows/{user_id}
     */
    @DeleteMapping("/follows/{user_id}")
    public BaseResponse unfollowUser(@PathVariable("user_id") Long userId, Authentication authentication) {
        interactionService.unfollowUser(userId, authentication);
        return new BaseResponse();
    }

    /**
     * 查询是否关注某用户
     * GET /interactions/follows/{user_id}
     */
    @GetMapping("/follows/{user_id}")
    public FollowStatusResponse getFollowStatus(@PathVariable("user_id") Long userId, Authentication authentication) {
        return interactionService.getFollowStatus(userId, authentication);
    }

    /**
     * 我的关注列表
     * GET /interactions/following
     */
    @GetMapping("/following")
    public InteractionUserListResponse getFollowing(
            @RequestParam(value = "current_page") Integer currentPage,
            @RequestParam(value = "page_size") Integer pageSize,
            Authentication authentication) {
        return interactionService.getFollowing(currentPage, pageSize, authentication);
    }

    /**
     * 我的粉丝列表
     * GET /interactions/followers
     */
    @GetMapping("/followers")
    public InteractionUserListResponse getFollowers(
            @RequestParam(value = "current_page") Integer currentPage,
            @RequestParam(value = "page_size") Integer pageSize,
            Authentication authentication) {
        return interactionService.getFollowers(currentPage, pageSize, authentication);
    }

    /**
     * 我的好友列表
     * GET /interactions/friends
     */
    @GetMapping("/friends")
    public InteractionUserListResponse getFriends(
            @RequestParam(value = "current_page") Integer currentPage,
            @RequestParam(value = "page_size") Integer pageSize,
            Authentication authentication) {
        return interactionService.getFriends(currentPage, pageSize, authentication);
    }

    /**
     * 查询是否为好友
     * GET /interactions/friends/{user_id}
     */
    @GetMapping("/friends/{user_id}")
    public FriendStatusResponse getFriendStatus(@PathVariable("user_id") Long userId, Authentication authentication) {
        return interactionService.getFriendStatus(userId, authentication);
    }

    /**
     * 会话列表
     * GET /interactions/conversation
     */
    @GetMapping("/conversation")
    public ConversationListResponse getConversations(Authentication authentication) {
        return interactionService.getConversations(authentication);
    }

    /**
     * 创建会话
     * POST /interactions/conversation
     */
    @PostMapping("/conversation")
    public ConversationCreateResponse createConversation(@Valid @RequestBody ConversationCreateRequest request,
                                                         Authentication authentication) {
        return interactionService.createConversation(request, authentication);
    }

    /**
     * 会话消息列表
     * GET /interactions/conversation/{conversation_id}/messages
     */
    @GetMapping("/conversation/{conversation_id}/messages")
    public ConversationMessageListResponse getConversationMessages(@PathVariable("conversation_id") Long conversationId,
                                                                   @RequestParam(value = "before_id", required = false) Long beforeId,
                                                                   @RequestParam(value = "after_id", required = false) Long afterId,
                                                                   @RequestParam(value = "limit", required = false) Integer limit,
                                                                   Authentication authentication) {
        return interactionService.getConversationMessages(conversationId, beforeId, afterId, limit, authentication);
    }

    /**
     * 发送会话消息
     * POST /interactions/conversation/{conversation_id}/messages
     */
    @PostMapping("/conversation/{conversation_id}/messages")
    public MessageSendResponse sendConversationMessage(@PathVariable("conversation_id") Long conversationId,
                                                       @Valid @RequestBody MessageSendRequest request,
                                                       Authentication authentication) {
        return interactionService.sendConversationMessage(conversationId, request, authentication);
    }
}
