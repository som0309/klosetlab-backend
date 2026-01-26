package com.example.kloset_lab.ai.dto;

import com.example.kloset_lab.global.ai.dto.OutfitResponse;
import lombok.Builder;

import java.util.List;

/**
 * TPO 코디 생성 결과 DTO
 *
 * @param outfitSummary 생성된 코디 요약
 * @param outfits 생성된 코디의 메타 정보 리스트
 */
@Builder
public record TpoOutfitsResponse(String outfitSummary, List<OutfitItem> outfits) {
    @Builder
    public record OutfitItem(Long outfitId, String outfitImageUrl, String aiComment, Long feedbackId) {}
}
