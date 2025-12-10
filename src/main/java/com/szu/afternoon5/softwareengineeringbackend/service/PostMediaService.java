package com.szu.afternoon5.softwareengineeringbackend.service;

import com.szu.afternoon5.softwareengineeringbackend.dto.posts.MediaInfo;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.PostMediaItem;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.UploadMediaRequest;
import com.szu.afternoon5.softwareengineeringbackend.dto.posts.UploadMediaResponse;
import com.szu.afternoon5.softwareengineeringbackend.entity.PostMedia;
import com.szu.afternoon5.softwareengineeringbackend.error.BusinessException;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import com.szu.afternoon5.softwareengineeringbackend.repository.PostMediaRepository;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostMediaService {
    private final PostMediaRepository postMediaRepository;

    private static final DateTimeFormatter DATE_PATH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final OssService ossService;


    public PostMediaService(PostMediaRepository postMediaRepository, OssService ossService) {
        this.postMediaRepository = postMediaRepository;
        this.ossService = ossService;
    }

    // TODO：未来这里可以实现安全检查、图片视频预处理
    public UploadMediaResponse uploadMedia(@Valid UploadMediaRequest request, Authentication authentication) {
        LoginPrincipal loginPrincipal = (LoginPrincipal) authentication.getPrincipal();
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        } else {
            MultipartFile file = request.getFile();
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "上传文件不能为空");
            }

            // 校验文件类型
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

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "无法识别文件类型");
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    @Transactional
    public void bindMediaToPost(Long postId, List<Long> mediaIds) {
        List<PostMedia> postMedias = postMediaRepository.findAllById(mediaIds);
        Set<Long> existingIds = postMedias.stream()
                .map(PostMedia::getMediaId)
                .collect(Collectors.toSet());
        if (existingIds.size() != mediaIds.size()) {
            // 一部分媒体id无效
            throw new BusinessException(ErrorCode.BAD_REQUEST, "附件列表无效");
        } else if (postMedias.stream().anyMatch(media -> media.getPostId() != null)){
            // 存在已被其他帖子引用的媒体
            throw new BusinessException(ErrorCode.CONFLICT, "附件已被其他帖子引用");
        } else {
            // 批量设置postId，记录提交顺序并保存
            int sortOrder = 0;
            for (PostMedia postMedia : postMedias) {
                postMedia.setSortOrder(sortOrder++);
                postMedia.setPostId(postId);
            }
            postMediaRepository.saveAll(postMedias);
        }
    }

    public List<MediaInfo> getPostMedia(Long postId) {
        List<PostMedia> postMedia = postMediaRepository.findAllByPostId(postId);
        return postMedia.stream().map(MediaInfo::new).toList();
    }
}
