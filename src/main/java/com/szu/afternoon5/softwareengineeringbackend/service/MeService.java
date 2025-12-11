package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.auth.UserDetail;
import com.szu.afternoon5.softwareengineeringbackend.dto.me.*;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.MediaInfo;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostInfo;
import com.szu.afternoon5.softwareengineeringbackend.entity.Comment;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.entity.User;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.CommentRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostMediaRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostRepository;
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
 * 个人中心服务，负责处理用户个人信息管理、我的帖子、我的评论等功能。
 */
@Service
public class MeService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMediaRepository postMediaRepository;
    private final PageableUtils pageableUtils;

    /**
     * 构造个人中心服务，注入所需仓储与工具。
     */
    public MeService(UserRepository userRepository, PostRepository postRepository,
                     CommentRepository commentRepository, PostMediaRepository postMediaRepository,
                     PageableUtils pageableUtils) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postMediaRepository = postMediaRepository;
        this.pageableUtils = pageableUtils;
    }

    /**
     * 获取当前登录用户的详细信息。
     *
     * @param authentication 认证信息
     * @return 用户详细信息
     */
    public UserDetail getUserInfo(Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Optional<User> userOptional = userRepository.findByUserId(loginPrincipal.getUserId());
            if (userOptional.isEmpty()) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
            } else {
                User user = userOptional.get();
                return new UserDetail(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getAvatarUrl(),
                    user.getCreatedTime(),
                    user.getPostCount(),
                    user.getRatingCount(),
                    user.getCommentCount()
                );
            }
        }
    }

    /**
     * 更新当前登录用户的信息。
     *
     * @param request        更新请求
     * @param authentication 认证信息
     * @return 更新后的用户详细信息
     */
    @Transactional
    public UserDetail updateUserInfo(@Valid UpdateUserInfoRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Optional<User> userOptional = userRepository.findByUserId(loginPrincipal.getUserId());
            if (userOptional.isEmpty()) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
            } else {
                User user = userOptional.get();
                
                // 检查用户名是否重复（如果要修改用户名）
                if (request.getUsername() != null && !request.getUsername().isEmpty() && 
                    !request.getUsername().equals(user.getUsername())) {
                    if (userRepository.existsByUsername(request.getUsername())) {
                        throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "用户名已被占用");
                    }
                    user.setUsername(request.getUsername());
                }
                
                // 更新昵称
                if (request.getNickname() != null && !request.getNickname().isEmpty()) {
                    user.setNickname(request.getNickname());
                }
                
                // 更新头像
                if (request.getAvatarUrl() != null) {
                    user.setAvatarUrl(request.getAvatarUrl());
                }
                
                user.setUpdatedTime(Instant.now());
                User updatedUser = userRepository.save(user);
                return new UserDetail(
                    updatedUser.getUserId(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    updatedUser.getNickname(),
                    updatedUser.getAvatarUrl(),
                    updatedUser.getCreatedTime(),
                    updatedUser.getPostCount(),
                    updatedUser.getRatingCount(),
                    updatedUser.getCommentCount()
                );
            }
        }
    }

    /**
     * 获取当前登录用户的帖子列表。
     *
     * @param currentPage    当前页码
     * @param pageSize       每页数量
     * @param authentication 认证信息
     * @return 用户帖子的分页列表
     */
    public MyPostsResponse getMyPosts(Integer currentPage, Integer pageSize, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            List<String> sortColumns = List.of("postTitle", "createdTime", "updatedTime", "ratingCount", "commentCount");
            Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage, pageSize, "createdTime", "DESC");
            
            // 注意：这里使用false表示查询未删除的帖子
            Page<Post> postPage = postRepository.findByUserIdAndIsDeleted(loginPrincipal.getUserId(), false, pageable);
            
            List<MyPostsItem> posts = postPage.getContent().stream()
                .map(post -> {
                    MediaInfo coverMedia = null;
                    // 获取封面媒体
                    if (post.getCoverMediaId() != null) {
                        var media = postMediaRepository.findById(post.getCoverMediaId()).orElse(null);
                        if (media != null) {
                            coverMedia = new MediaInfo(
                                media.getMediaId(),
                                media.getMediaUrl(),
                                media.getMediaType().toString()
                            );
                        }
                    }
                    
                    return new MyPostsItem(
                        new PostInfo(
                            post.getPostId(),
                            post.getUserId(),
                            post.getPostTitle(),
                            post.getUpdatedTime(),
                            post.getRatingCount(),
                            post.getCommentCount()
                        ),
                        coverMedia
                    );
                })
                .toList();
            
            return new MyPostsResponse(
                postPage.getTotalPages(),
                (int) postPage.getTotalElements(),
                postPage.getNumber() + 1,
                postPage.getSize(),
                posts
            );
        }
    }

    /**
     * 获取当前登录用户的评论列表。
     *
     * @param currentPage    当前页码
     * @param pageSize       每页数量
     * @param authentication 认证信息
     * @return 用户评论的分页列表
     */
    public MyCommentsResponse getMyComments(Integer currentPage, Integer pageSize, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            // 设置默认分页参数
            int actualPage = currentPage != null ? currentPage : 1;
            int actualSize = pageSize != null ? pageSize : 10;
            
            Pageable pageable = pageableUtils.buildPageable(
                List.of("createdTime"), actualPage, actualSize, "createdTime", "DESC");
            
            Page<Comment> commentPage = commentRepository.findByUserIdAndIsDeletedFalse(
                loginPrincipal.getUserId(), pageable);
            
            List<MyCommentsItem> comments = commentPage.getContent().stream()
                .map(comment -> new MyCommentsItem(
                    comment.getCommentId(),
                    comment.getPostId(),
                    comment.getUserId(),
                    comment.getParentId(),
                    comment.getCommentText(),
                    comment.getCreatedTime(),
                    comment.getUpdatedTime()
                ))
                .toList();
            
            return new MyCommentsResponse(
                commentPage.getTotalPages(),
                (int) commentPage.getTotalElements(),
                commentPage.getNumber() + 1,
                commentPage.getSize(),
                comments
            );
        }
    }
}