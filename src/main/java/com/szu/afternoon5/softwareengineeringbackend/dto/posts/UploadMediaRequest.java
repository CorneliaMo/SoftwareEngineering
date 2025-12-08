package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒体上传请求体，支持图片或视频文件上传。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UploadMediaRequest {

    @JsonProperty("file")
    private MultipartFile file;

    @NotNull
    @JsonProperty("type")
    private String type;

    public UploadMediaRequest(MultipartFile file, String type) {
        this.file = file;
        this.type = type;
    }
}
