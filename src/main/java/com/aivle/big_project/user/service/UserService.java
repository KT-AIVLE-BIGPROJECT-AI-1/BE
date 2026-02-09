package com.aivle.big_project.user.service;

import com.aivle.big_project.storage.ObjectStorageService;
import com.aivle.big_project.user.dto.user.MyDetailResponse;
import com.aivle.big_project.user.dto.user.UpdateMeRequest;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ObjectStorageService objectStorageService;

    // 본인 정보 조회
    public MyDetailResponse getMyDetail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        return toMyDetail(user);
    }

    // 본인 정보 수정
    @Transactional
    public MyDetailResponse updateMe(UUID userId, UpdateMeRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        String uploadedKey = null;
        MultipartFile profileImg = req.getProfileImg();

        if(profileImg != null && !profileImg.isEmpty()) {
            try {
                String key = "users/" + user.getUserId() + "/profile";
                uploadedKey = objectStorageService.upload(profileImg, key);
                user.changeProfileImg(uploadedKey);
            } catch (Exception e) {
                log.warn("profile upload failed. user_id={}", user.getUserId(), e);
            }
        }

        user.updateMe(
                uploadedKey,
                req.getPhoneNumber(),
                req.getJurisdictionStation(),
                req.getDepartment(),
                req.getRankName()
        );

        return toMyDetail(user);
    }

    private MyDetailResponse toMyDetail(User user) {
        return MyDetailResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImg(user.getProfileImg())
                .role(user.getRole())
                .jurisdictionStation(user.getJurisdictionStation())
                .department(user.getDepartment())
                .rankName(user.getRankName())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}
