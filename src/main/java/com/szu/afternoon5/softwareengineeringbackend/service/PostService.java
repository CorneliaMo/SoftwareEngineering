package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.*;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
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

@Service
public class PostService {
    private final TagService tagService;
    private final PostMediaService postMediaService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PageableUtils pageableUtils;

    public PostService(TagService tagService, PostMediaService postMediaService, PostRepository postRepository, UserRepository userRepository, PageableUtils pageableUtils) {
        this.tagService = tagService;
        this.postMediaService = postMediaService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pageableUtils = pageableUtils;
    }

    @Transactional
    public PublishPostResponse publish(@Valid PublishPostRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            // 先写入post获取自增id postId
            Post post = new Post(loginPrincipal.getUserId(), request.getPostTitle(), request.getPostText());
            Post postSaved = postRepository.save(post);
            // 通过postId绑定相应tag和media
            tagService.bindTagsToPost(postSaved.getPostId(), request.getTags());
            postMediaService.bindMediaToPost(postSaved.getPostId(), request.getMediaIds());
            return new PublishPostResponse(new PostInfo(post));
        }
    }

    public PostListResponse getPostList(Integer currentPage, Integer pageSize, Long userId) {
        if (userRepository.existsByUserId(userId)) {
            List<String> sortColumns = List.of("post_title", "created_time", "updated_time", "rating_count", "comment_count");

            // TODO：当前默认按帖子创建时间倒序排列，后续可以让前端自定义排序
            Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage, pageSize, "created_time", "DESC");
            Page<PostWithCover> postWithCoverPage = postRepository.findByUserIdAndIsDeletedWithCover(userId, false, pageable);

            return new PostListResponse(
                    postWithCoverPage.getTotalPages(),
                    (int) postWithCoverPage.getTotalElements(),
                    postWithCoverPage.getNumber() + 1,
                    postWithCoverPage.getSize(),
                    // 将数据库JOIN帖子和封面媒体之后的Projection映射到最终接口结构
                    postWithCoverPage.get().map(postWithCover -> new PostSummaryItem(new PostInfo(postWithCover.getPost()), new MediaInfo(postWithCover.getPostMedia()))).toList()
            );
        } else {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
    }

    public PostDetailResponse getPostDetail(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        } else {
            Post post = postOptional.get();
            return new PostDetailResponse(new PostDetail(post), postMediaService.getPostMedia(post.getPostId()));
        }
    }

    @Transactional
    public void updatePostDetail(Long postId, @Valid PostUpdateRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isEmpty()) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
            } else {
                PostUpdateContent postUpdateContent = request.getPost();
                Post post = postOptional.get();
                if ((loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user) && loginPrincipal.getUserId().equals(post.getUserId())) || (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.admin))) {
                    if (postUpdateContent.getPostTitle() != null && !postUpdateContent.getPostTitle().isEmpty()) {
                        post.setPostTitle(postUpdateContent.getPostTitle());
                    }
                    if (postUpdateContent.getPostText() != null && !postUpdateContent.getPostText().isEmpty()) {
                        post.setPostText(postUpdateContent.getPostText());
                    }
                    postRepository.save(post);
                    tagService.updatePostTags(post.getPostId(), request.getTags());
                } else {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "只有帖子作者或管理员可以修改");
                }
            }
        }
    }

    @Transactional
    public void deletePost(Long postId, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isEmpty()) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
            } else {
                Post post = postOptional.get();
                if ((loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user) && loginPrincipal.getUserId().equals(post.getUserId())) || (loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.admin))) {
                    if (post.getIsDeleted()) {
                        throw new BusinessException(ErrorCode.CONFLICT, "帖子已删除");
                    } else {
                        // 设置软删除并记录删除时间
                        post.setIsDeleted(true);
                        post.setDeletedTime(Instant.now());
                        postRepository.save(post);
                    }
                } else {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "只有帖子作者或管理员可以删除");
                }
            }
        }
    }
}
