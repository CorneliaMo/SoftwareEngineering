package com.szu.afternoon5.softwareengineeringbackend.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.szu.afternoon5.softwareengineeringbackend.config.AliOssConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云 OSS 业务服务。
 *
 * <p>封装与 OSS 交互的核心能力，包括：</p>
 * <ul>
 *     <li>上传文件（InputStream / byte[] / File / MultipartFile）；</li>
 *     <li>删除 OSS 对象；</li>
 *     <li>生成公网访问 URL 或带有效期的签名 URL；</li>
 *     <li>根据配置自动拼接 CDN 域名。</li>
 * </ul>
 *
 * <p>建议在业务代码中只依赖本服务，而不直接使用底层 {@link OSS} 客户端。</p>
 */
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSS ossClient;
    private final AliOssConfig aliOssConfig;

    /**
     * 使用 InputStream 上传文件。
     *
     * @param objectKey OSS 上的对象 key，例如 "posts/123/cover.png"
     * @param input     输入流
     * @param publicRead 是否设置为公共读（多数前端直接访问的资源建议设为 true）
     * @return 该对象的访问 URL（若配置了 CDN，则返回 CDN 地址）
     */
    public String upload(String objectKey, InputStream input, boolean publicRead) {
        PutObjectRequest request = new PutObjectRequest(
                aliOssConfig.getBucketName(),
                objectKey,
                input
        );

        ossClient.putObject(request);

        if (publicRead) {
            ossClient.setObjectAcl(
                    aliOssConfig.getBucketName(),
                    objectKey,
                    CannedAccessControlList.PublicRead
            );
        }

        return generateUrl(objectKey);
    }

    /**
     * 使用 byte[] 上传。
     */
    public String upload(String objectKey, byte[] bytes, boolean publicRead) {
        return upload(objectKey, new ByteArrayInputStream(bytes), publicRead);
    }

    /**
     * 使用 MultipartFile 上传（适配 Spring Web）。
     */
    public String upload(String objectKey, MultipartFile file, boolean publicRead) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return upload(objectKey, inputStream, publicRead);
        }
    }

    /**
     * 使用本地 File 上传（通常不建议在 Web 环境中直接使用）。
     */
    public String upload(String objectKey, File file, boolean publicRead) {
        ossClient.putObject(aliOssConfig.getBucketName(), objectKey, file);

        if (publicRead) {
            ossClient.setObjectAcl(
                    aliOssConfig.getBucketName(),
                    objectKey,
                    CannedAccessControlList.PublicRead
            );
        }

        return generateUrl(objectKey);
    }

    /**
     * 删除 OSS 中的对象。
     *
     * @param objectKey 对象 key
     */
    public void delete(String objectKey) {
        ossClient.deleteObject(aliOssConfig.getBucketName(), objectKey);
    }

    /**
     * 生成公网访问 URL（不带签名）。
     * <p>
     * 若已配置 CDN，则优先返回 CDN 地址；
     * 否则拼接 Bucket + Endpoint 形成默认公网地址。
     */
    public String generateUrl(String objectKey) {
        String cdnHost = aliOssConfig.getCdnHost();
        if (cdnHost != null && !cdnHost.isBlank()) {
            // 确保中间只有一个 "/"
            if (cdnHost.endsWith("/")) {
                return cdnHost + objectKey;
            }
            return cdnHost + "/" + objectKey;
        }

        // 使用默认的 OSS 公网访问域名
        String endpoint = aliOssConfig.getEndpoint();
        // endpoint 可能是 "https://oss-cn-xxx.aliyuncs.com" 或 "oss-cn-xxx.aliyuncs.com"
        String normalizedEndpoint = endpoint.replaceFirst("^https?://", "");
        return "https://" + aliOssConfig.getBucketName() + "." + normalizedEndpoint + "/" + objectKey;
    }

    /**
     * 为私有 Bucket 生成带有效期的签名 URL。
     *
     * @param objectKey     对象 key
     * @param expireSeconds 有效期（秒）
     * @return 带签名参数的临时访问 URL
     */
    public String generateSignedUrl(String objectKey, long expireSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);
        URL url = ossClient.generatePresignedUrl(
                aliOssConfig.getBucketName(),
                objectKey,
                expiration
        );
        return url.toString();
    }
}
