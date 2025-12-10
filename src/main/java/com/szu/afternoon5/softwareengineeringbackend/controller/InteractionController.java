package com.szu.afternoon5.softwareengineeringbackend.controller;

import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.dto.interactions.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户互动相关接口控制器，涵盖点赞、收藏、评论等行为。
 * <p>
 * 后续可在此实现幂等的点赞接口、评论发布与删除、行为统计等能力，并结合实体模型完善参数校验。
 */
@RestController
@RequestMapping("/interactions")
public class InteractionController {

    /**
     * 获取评分信息。
     *
     * @param postId 帖子标识
     * @return 评分统计信息，包含平均分、总人数及当前用户评分
     */
    @GetMapping("/rating/{post_id}")
    public RatingInfo getRating(@PathVariable("post_id") Long postId) {
        return null;
    }

    /**
     * 提交评分。
     *
     * @param postId  帖子标识
     * @param request 评分提交请求
     * @return 操作结果，包含更新后的评分统计
     */
    @PostMapping("/rating/{post_id}")
    public BaseResponse submitRating(@PathVariable("post_id") Long postId,
                                     @Valid @RequestBody SubmitRatingRequest request) {
        return null;
    }

    /**
     * 删除评分。
     *
     * @param postId 帖子标识
     * @return 操作结果
     */
    @DeleteMapping("/rating/{post_id}")
    public BaseResponse deleteRating(@PathVariable("post_id") Long postId) {
        return null;
    }

    /**
     * 发表评论。
     *
     * @param postId  帖子标识
     * @param request 评论提交请求
     * @return 操作结果，包含新评论的标识
     */
    @PostMapping("/comments/{post_id}")
    public BaseResponse submitComment(@PathVariable("post_id") Long postId,
                                      @Valid @RequestBody SubmitCommentRequest request) {
        return null;
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
        return null;
    }

    /**
     * 删除评论。
     *
     * @param postId  帖子标识
     * @param request 评论删除请求
     * @return 操作结果
     */
    @DeleteMapping("/comments/{post_id}")
    public BaseResponse deleteComment(@PathVariable("post_id") Long postId,
                                      @Valid @RequestBody DeleteCommentRequest request) {
        return null;
    }
}