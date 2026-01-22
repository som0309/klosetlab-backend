package com.example.kloset_lab.media.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {
    private Long fileId;
    private String objectKey;
    private String presignedUrl;
}
