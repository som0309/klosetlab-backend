package com.example.kloset_lab.feed.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 피드 수정 요청 DTO
 *
 * @param content    피드 내용 (null이면 수정 안함)
 * @param clothesIds 매핑할 옷 ID 배열 (null이면 수정 안함, 빈 배열이면 매핑 삭제)
 */
public record FeedUpdateRequest(
        String content, @Size(max = 10, message = "옷 태그는 최대 10개까지 가능합니다") List<Long> clothesIds) {}
