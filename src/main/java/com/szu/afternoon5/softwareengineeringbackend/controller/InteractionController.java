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
}
