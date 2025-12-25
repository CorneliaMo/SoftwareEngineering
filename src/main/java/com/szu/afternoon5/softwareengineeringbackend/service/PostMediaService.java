package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.MediaInfo;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostMediaItem;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.UploadMediaRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.UploadMediaResponse;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostMediaRepository;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostRepository;
import com.szu.afternoon5.softwareengineeringbackend.security.LoginPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 帖子媒体服务，负责处理媒体文件上传、与帖子绑定及查询媒体信息等操作。
 */
@Service
public class PostMediaService {
    private final PostMediaRepository postMediaRepository;

    private static final DateTimeFormatter DATE_PATH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final OssService ossService;
    private final PostRepository postRepository;


    public PostMediaService(PostMediaRepository postMediaRepository, OssService ossService, PostRepository postRepository) {
        this.postMediaRepository = postMediaRepository;
        this.ossService = ossService;
        this.postRepository = postRepository;
    }

    // TODO：未来这里可以实现安全检查、图片视频预处理
    /**
     * 上传图片或视频并记录媒体信息。
     *
     * @param request        上传参数，包含媒体类型与文件内容
     * @param authentication 鉴权信息，用于确定上传者身份
     * @return 上传后的媒体资源信息
     */
    public UploadMediaResponse uploadMedia(@Valid UploadMediaRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            MultipartFile file = request.getFile();
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "上传文件不能为空");
            }

            // 校验文件类型，保证仅图片或视频可以上传
            String contentType = file.getContentType();
            if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "仅支持图片和视频上传");
            }

            // 生成唯一文件名
            String filePath = buildFileUploadPath(request.getType().toString(), file.getOriginalFilename());

            try {
                String url = ossService.upload(filePath, file, true);
                PostMedia postMedia = new PostMedia(null, loginPrincipal.getUserId(), url, request.getType(), null);
                PostMedia postMediaSaved = postMediaRepository.save(postMedia);
                return new UploadMediaResponse(new PostMediaItem(postMediaSaved));
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败，请稍后再试");
            }
        }
    }

    /**
     * 生成对象存储中的文件路径，格式为 uploads/user/{category}/yyyy/MM/dd/{uuid}.{ext}
     *
     * @param category         媒体分类（图片/视频）
     * @param originalFilename 原始文件名
     * @return 构建好的路径字符串
     */
    private String buildFileUploadPath(String category, String originalFilename) {
        // 1. 日期路径：2025/12/09
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);

        // 2. 文件扩展名
        String ext = getExtension(originalFilename);

        // 3. 随机文件名（避免重名）
        String randomName = UUID.randomUUID().toString().replace("-", "");

        // uploads/user/{category}/yyyy/MM/dd/{random}.{ext}
        return String.format("uploads/user/%s/%s/%s.%s",
                category,
                datePath,
                randomName,
                ext);
    }

    /**
     * 提取文件扩展名，用于保留原始后缀。
     *
     * @param filename 原始文件名
     * @return 扩展名（不含点）
     */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无法识别文件类型");
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * 将媒体批量绑定到帖子，若媒体被占用或不存在则抛出异常。
     *
     * @param postId   目标帖子 ID
     * @param mediaIds 媒体 ID 列表
     */
    @Transactional
    public void bindMediaToPost(Long postId, List<Long> mediaIds) {
        List<PostMedia> postMedias = postMediaRepository.findAllById(mediaIds);
        Set<Long> existingIds = postMedias.stream()
                .map(PostMedia::getMediaId)
                .collect(Collectors.toSet());
        if (existingIds.size() != mediaIds.size()) {
            // 一部分媒体id无效
            throw new BusinessException(ErrorCode.BAD_REQUEST, "附件列表无效");
        } else if (postMedias.stream().anyMatch(media -> media.getPostId() != null && !media.getPostId().equals(postId))) {
            // 存在已被其他帖子引用的媒体
            throw new BusinessException(ErrorCode.CONFLICT, "附件已被其他帖子引用");
        } else {
            if (!mediaIds.isEmpty()) {
                boolean hasImage = false, hasVideo = false;
                // fix：以mediaIds的顺序为基准
                Map<Long, Integer> orderMap =
                        IntStream.range(0, mediaIds.size())
                                .boxed()
                                .collect(Collectors.toMap(
                                        mediaIds::get,
                                        i -> i + 1
                                ));

                // 批量设置postId，记录提交顺序并保存
                for (PostMedia postMedia : postMedias) {
                    Integer order = orderMap.get(postMedia.getMediaId());
                    if (order != null) {
                        postMedia.setSortOrder(order);
                    }
                    postMedia.setPostId(postId);
                    if (postMedia.getMediaType().equals(PostMedia.MediaType.image)) hasImage = true;
                    if (postMedia.getMediaType().equals(PostMedia.MediaType.video)) hasVideo = true;
                }
                postMediaRepository.saveAll(postMedias);
                postRepository.updatePostCover(postId, mediaIds.get(0), hasImage, hasVideo);
            }
        }
    }

    /**
     * 查询帖子关联的媒体信息列表。
     *
     * @param postId 帖子 ID
     * @return 媒体信息集合
     */
    public List<MediaInfo> getPostMedia(Long postId) {
        List<PostMedia> postMedia = postMediaRepository.findAllByPostId(postId);
        return postMedia.stream().map(MediaInfo::new).toList();
    }
}
