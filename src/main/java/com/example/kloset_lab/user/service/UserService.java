package com.example.kloset_lab.user.service;

import com.example.kloset_lab.global.exception.CustomException;
import com.example.kloset_lab.global.exception.ErrorCode;
import com.example.kloset_lab.global.response.Message;
import com.example.kloset_lab.media.entity.MediaFile;
import com.example.kloset_lab.media.repository.MediaFileRepository;
import com.example.kloset_lab.user.dto.NicknameValidationResult;
import com.example.kloset_lab.user.dto.UserProfileDto;
import com.example.kloset_lab.user.dto.UserProfileInfoResponse;
import com.example.kloset_lab.user.dto.UserRegisterRequest;
import com.example.kloset_lab.user.entity.User;
import com.example.kloset_lab.user.entity.UserProfile;
import com.example.kloset_lab.user.repository.UserProfileRepository;
import com.example.kloset_lab.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MediaFileRepository mediaFileRepository;
    private final NicknameValidationService nicknameValidationService;

    /**
     * 회원가입 후 추가 정보 저장
     *
     * @param userId  회원 ID
     * @param request 회원가입 요청 정보
     */
    @Transactional
    public void registerUserProfile(Long userId, UserRegisterRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isRegistrationComplete()) {
            throw new CustomException(ErrorCode.NOT_PENDING_STATE);
        }

        if (!nicknameValidationService.isNicknameAvailable(request.nickname())) {
            throw new CustomException(ErrorCode.EXISTING_NICKNAME);
        }

        MediaFile profileFile = Optional.ofNullable(request.profileFileId())
                .map(fileId -> mediaFileRepository
                        .findById(fileId)
                        .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND)))
                .orElse(null);

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .nickname(request.nickname())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .profileFile(profileFile)
                .build();

        user.completeRegistration();
        userProfileRepository.save(userProfile);
    }

    /**
     * 특정 유저 프로필 조회
     *
     * @param targetUserId  조회할 유저 ID
     * @param currentUserId 현재 로그인한 유저 ID
     * @return 유저 프로필 정보
     */
    public UserProfileInfoResponse getUserProfile(Long targetUserId, Long currentUserId) {
        userRepository.findById(targetUserId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserProfile userProfile = userProfileRepository
                .findByUserId(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // TODO: S3 URL 생성 로직 추가 필요 (현재는 objectKey 반환)
        String profileImageUrl = Optional.ofNullable(userProfile.getProfileFile())
                .map(MediaFile::getObjectKey)
                .orElse(null);
        boolean isMe = targetUserId.equals(currentUserId);

        UserProfileDto profileDto = new UserProfileDto(targetUserId, profileImageUrl, userProfile.getNickname());

        return new UserProfileInfoResponse(profileDto, isMe);
    }

    /**
     * 닉네임 유효성 검사 및 메시지 반환
     *
     * @param nickname 검사할 닉네임
     * @return 사용 가능 여부와 메시지를 담은 결과 객체
     */
    public NicknameValidationResult validateNicknameWithMessage(String nickname) {
        boolean isAvailable = nicknameValidationService.isNicknameAvailable(nickname);
        String message = isAvailable ? Message.NICKNAME_CHECKED_UNIQUE : Message.NICKNAME_CHECKED_DUPLICATE;
        return new NicknameValidationResult(isAvailable, message);
    }
}
