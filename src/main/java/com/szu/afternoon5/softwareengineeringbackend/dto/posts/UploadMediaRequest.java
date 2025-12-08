package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒体上传请求体，支持图片或视频文件上传。
 */
@Data
public class UploadMediaRequest {

    @JsonProperty("file")
    private MultipartFile file;

    @JsonProperty("type")
    private String type;
}
