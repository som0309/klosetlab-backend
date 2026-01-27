package com.example.kloset_lab.user.controller;

import com.example.kloset_lab.global.response.ApiResponse;
import com.example.kloset_lab.global.response.ApiResponses;
import com.example.kloset_lab.global.response.Message;
import com.example.kloset_lab.user.dto.*;
import com.example.kloset_lab.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 후 추가 정보 저장 API
     * POST /api/v1/users
     *
     * @param userId  인증된 회원 ID (임시 토큰에서 추출)
     * @param request 회원가입 요청 정보
     * @return 201 Created
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerUserProfile(
            @AuthenticationPrincipal Long userId, @RequestBody @Valid UserRegisterRequest request) {
        userService.registerUserProfile(userId, request);
        return ApiResponses.created(Message.USER_CREATED);
    }

    /**
     * 닉네임 유효성 검사 API
     * GET /api/v1/users/validation?nickname={nickname}
     *
     * @param request 닉네임 검사 요청
     * @return 사용 가능 여부
     */
    @GetMapping("/validation")
    public ResponseEntity<ApiResponse<NicknameValidationResponse>> validateNickname(
            @Valid NicknameValidationRequest request) {
        NicknameValidationResult result = userService.validateNicknameWithMessage(request.nickname());
        NicknameValidationResponse response = new NicknameValidationResponse(result.isAvailable());

        return ApiResponses.ok(result.message(), response);
    }

    /**
     * 생년월일 유효성 검사 API
     * GET /api/v1/users/validation/birth-date?birthDate={birthDate}
     *
     * @param request 생년월일 검사 요청 (yyyy-MM-dd 형식)
     * @return 유효 여부
     */
    @GetMapping("/validation/birth-date")
    public ResponseEntity<ApiResponse<BirthDateValidationResponse>> validateBirthDate(
            @Valid BirthDateValidationRequest request) {
        BirthDateValidationResult result = userService.validateBirthDate(request.birthDate());
        BirthDateValidationResponse response = new BirthDateValidationResponse(result.isValid());

        return ApiResponses.ok(result.message(), response);
    }

    /**
     * 특정 유저 프로필 조회 API
     * GET /api/v1/users/{userId}
     *
     * @param userId        조회할 유저 ID
     * @param currentUserId 현재 로그인한 유저 ID
     * @return 유저 프로필 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileInfoResponse>> getUserProfile(
            @PathVariable Long userId, @AuthenticationPrincipal Long currentUserId) {
        UserProfileInfoResponse response = userService.getUserProfile(userId, currentUserId);
        return ApiResponses.ok(Message.PROFILE_RETRIEVED, response);
    }
}
