package com.szu.afternoon5.softwareengineeringbackend.dto.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.afternoon5.softwareengineeringbackend.dto.BaseResponse;
import com.szu.afternoon5.softwareengineeringbackend.error.ErrorCode;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class UploadMediaRequest extends BaseResponse {

    @JsonProperty("file")
    private MultipartFile file;

    @JsonProperty("type")
    private String type;

    public UploadMediaRequest(MultipartFile file, String type) {
        super();
        this.file = file;
        this.type = type;
    }

    public UploadMediaRequest(ErrorCode errorCode, String errMsg, MultipartFile file, String type) {
        super(errorCode, errMsg);
        this.file = file;
        this.type = type;
    }
}
