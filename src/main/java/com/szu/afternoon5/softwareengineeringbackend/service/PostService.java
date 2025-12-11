package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.*;
import com.szu.afternoon5.softwareengineeringbackend.entity.Post;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.UserRepository;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import com.szu.afternoon5.softwareengineeringbackend.utils.PageableUtils;
import com.szu.afternoon5.softwareengineeringbackend.utils.SearchTextUtil;
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
 * 帖子服务，负责处理帖子发布、查询、更新和删除等核心业务逻辑，
 * 并在需要时协调标签、媒体等子服务完成关联操作。
 */
@Service
public class PostService {
    private final TagService tagService;
    private final PostMediaService postMediaService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PageableUtils pageableUtils;
    private final JiebaService jiebaService;

    public PostService(TagService tagService, PostMediaService postMediaService, PostRepository postRepository, UserRepository userRepository, PageableUtils pageableUtils, JiebaService jiebaService) {
        this.tagService = tagService;
        this.postMediaService = postMediaService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pageableUtils = pageableUtils;
        this.jiebaService = jiebaService;
    }

    /**
     * 发布帖子：校验用户身份后保存帖子主体，再绑定标签与媒体。
     *
     * @param request         发布请求，包含正文、标题、标签与媒体信息
     * @param authentication   Spring 安全上下文，提供登录用户信息
     * @return 发布结果，包含新帖的基础信息
     */
    @Transactional
    public PublishPostResponse publish(@Valid PublishPostRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null || !loginPrincipal.getLoginType().equals(LoginPrincipal.LoginType.user)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            Post post = new Post(loginPrincipal.getUserId(), request.getPostTitle(), request.getPostText());
            List<String> titleSegment = jiebaService.cutForIndex(post.getPostTitle());
            List<String> textSegment = jiebaService.cutForIndex(post.getPostText());

            // 先写入post获取自增id postId
            Long newPostId = postRepository.insertPostWithIndex(
                    post.getUserId(),
                    post.getPostTitle(),
                    post.getPostText(),
                    post.getIsDeleted(),
                    post.getDeletedTime(),
                    post.getCreatedTime(),
                    post.getUpdatedTime(),
                    post.getRatingCount(),
                    post.getCommentCount(),
                    post.getCoverMediaId(),
                    SearchTextUtil.joinTokens(titleSegment),
                    SearchTextUtil.joinTokens(textSegment)
            );
            post.setPostId(newPostId);
            // 通过postId绑定相应tag和media
            tagService.bindTagsToPost(newPostId, request.getTags());
            postMediaService.bindMediaToPost(newPostId, request.getMediaIds());
            return new PublishPostResponse(new PostInfo(post));
        }
    }

    /**
     * 分页查询用户的帖子列表，默认按创建时间倒序。
     *
     * @param currentPage 当前页码（从 1 开始）
     * @param pageSize    每页数量
     * @param userId      目标用户 ID
     * @return 帖子列表及分页信息
     */
    public PostListResponse getPostList(Integer currentPage, Integer pageSize, Long userId) {
        if (userRepository.existsByUserId(userId)) {
            List<String> sortColumns = List.of("post_title", "created_time", "updated_time", "rating_count", "comment_count");

            // TODO：当前默认按帖子创建时间倒序排列，后续可以让前端自定义排序
            Pageable pageable = pageableUtils.buildPageable(sortColumns, currentPage - 1, pageSize, "created_time", "DESC");
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

    /**
     * 获取帖子详情，包括正文、统计信息与媒体资源列表。
     *
     * @param postId 帖子 ID
     * @return 帖子详情数据
     */
    public PostDetailResponse getPostDetail(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "帖子不存在");
        } else {
            Post post = postOptional.get();
            return new PostDetailResponse(new PostDetail(post), postMediaService.getPostMedia(post.getPostId()));
        }
    }

    /**
     * 更新帖子内容及标签，需要帖子作者或管理员权限。
     *
     * @param postId         帖子 ID
     * @param request        更新内容请求
     * @param authentication 鉴权信息
     */
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

                    // 更新分词索引
                    List<String> titleSegment = jiebaService.cutForIndex(post.getPostTitle());
                    List<String> textSegment = jiebaService.cutForIndex(post.getPostText());

                    postRepository.updatePostWithIndex(
                            post.getPostId(),
                            post.getUserId(),
                            post.getPostTitle(),
                            post.getPostText(),
                            post.getIsDeleted(),
                            post.getDeletedTime(),
                            post.getCreatedTime(),
                            post.getUpdatedTime(),
                            post.getRatingCount(),
                            post.getCommentCount(),
                            post.getCoverMediaId(),
                            SearchTextUtil.joinTokens(titleSegment),
                            SearchTextUtil.joinTokens(textSegment)
                    );
                    // 在保存基础信息后同步更新标签
                    tagService.updatePostTags(post.getPostId(), request.getTags());
                } else {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "只有帖子作者或管理员可以修改");
                }
            }
        }
    }

    /**
     * 软删除帖子，要求操作者为作者本人或管理员。
     *
     * @param postId         帖子 ID
     * @param authentication 鉴权信息
     */
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
