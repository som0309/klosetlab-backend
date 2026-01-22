package com.example.kloset_lab.user.service;

import com.example.kloset_lab.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 닉네임 유효성 검사 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NicknameValidationService {

    private final UserProfileRepository userProfileRepository;

    /**
     * 닉네임 중복 여부 확인
     *
     * @param nickname 검사할 닉네임
     * @return 사용 가능 여부 (true: 사용 가능, false: 중복)
     */
    public boolean isNicknameAvailable(String nickname) {
        return !userProfileRepository.existsByNickname(nickname);
    }
}
