package com.example.kloset_lab.ai.controller;

import com.example.kloset_lab.ai.dto.TpoOutfitsRequest;
import com.example.kloset_lab.ai.dto.TpoOutfitsResponse;
import com.example.kloset_lab.ai.service.AiService;
import com.example.kloset_lab.global.response.ApiResponse;
import com.example.kloset_lab.global.response.ApiResponses;
import com.example.kloset_lab.global.response.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * TPO 코디 생성 요청 API
     *
     * @param userId 현재 로그인한 사용자 ID
     * @param request TPO 코디 생성 요청 내용 DTO
     * @return 생성된 TPO 코디 결과
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TpoOutfitsResponse>> createComment(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody TpoOutfitsRequest request) {
        TpoOutfitsResponse response = aiService.generateTpoOutfits(userId, request);
        return ApiResponses.ok(Message.TPO_OUTFITS_RETRIEVED, response);
    }
}
