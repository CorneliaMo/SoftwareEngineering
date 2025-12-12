package com.szu.afternoon5.softwareengineeringbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置类。
 *
 * <p>该类用于绑定 Spring Boot 配置文件中以 {@code ali.oss} 为前缀的所有配置项，
 * 包含 OSS 的基础访问参数（endpoint、accessKeyId、accessKeySecret）、
 * 默认 Bucket 名称以及业务使用的 CDN 加速域名。</p>
 *
 * <p>本配置类通常与 OSS 客户端初始化类（如 {@code AliOssClientConfig}）
 * 或业务上传服务（如 {@code OssService}）配合使用，
 * 以便在系统中统一管理 OSS 配置，并支持多环境（dev / prod）切换。</p>
 *
 * 示例配置（来自 application.yml）：
 * <pre>
 * ali:
 *   oss:
 *     endpoint: https://oss-cn-shenzhen.aliyuncs.com
 *     access-key-id: xxx
 *     access-key-secret: xxx
 *     bucket-name: my-bucket
 *     cdn-host: https://cdn.example.com
 * </pre>
 *
 * <p>所有字段均通过 Spring Boot 自动注入，无需在代码中硬编码任何敏感参数。</p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ali.oss")
public class AliOssConfig {

    /**
     * OSS 服务接入点（Region Endpoint）。
     */
    private String endpoint;

    /**
     * 阿里云 AccessKey ID。
     */
    private String accessKeyId;

    /**
     * 阿里云 AccessKey Secret。
     */
    private String accessKeySecret;

    /**
     * OSS Bucket 名称。
     */
    private String bucketName;

    /**
     * CDN 加速域名，用于拼接外链访问 URL。
     */
    private String cdnHost;
}