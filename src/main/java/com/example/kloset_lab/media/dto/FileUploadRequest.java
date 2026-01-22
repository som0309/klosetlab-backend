package com.example.kloset_lab.media.dto;

import com.example.kloset_lab.media.entity.Purpose;
import java.util.List;
import lombok.Getter;

@Getter
public class FileUploadRequest {
    private Purpose purpose;
    private List<FileUploadInfo> files;
}
