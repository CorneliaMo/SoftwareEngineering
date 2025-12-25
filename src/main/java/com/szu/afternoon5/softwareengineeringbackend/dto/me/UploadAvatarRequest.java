package com.szu.afternoon5.softwareengineeringbackend.dto.me;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadAvatarRequest {

    @JsonProperty("avatar")
    private MultipartFile avatar;

}
