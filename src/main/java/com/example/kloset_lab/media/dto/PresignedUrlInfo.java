package com.example.kloset_lab.media.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlInfo {
    private String presignedUrl;
    private String objectKey;
}
