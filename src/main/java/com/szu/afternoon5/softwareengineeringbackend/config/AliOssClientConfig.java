package com.szu.afternoon5.softwareengineeringbackend.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 客户端配置。
 *
 * <p>负责基于 {@link AliOssConfig} 创建单例 {@link OSS} 客户端，
 * 并交由 Spring 容器管理其生命周期（在应用关闭时自动调用 {@code shutdown()}）。</p>
 */
@Configuration
public class AliOssClientConfig {

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(AliOssConfig aliOssConfig) {
        return new OSSClientBuilder().build(
                aliOssConfig.getEndpoint(),
                aliOssConfig.getAccessKeyId(),
                aliOssConfig.getAccessKeySecret()
        );
    }
}
