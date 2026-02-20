package com.aivle.big_project.user.service;

import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.storage.ObjectStorageService;
import com.aivle.big_project.user.dto.user.UserDetailResponse;
import com.aivle.big_project.user.dto.user.UserListResponse;
import com.aivle.big_project.user.entity.Role;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final ObjectStorageService objectStorageService;

    // 사용자 전체 목록 조회
    public UserListResponse listUsers(
            UUID adminId,
            String keyword,
            String department,
            String rank
    ) {
        User admin = userRepository.findById(adminId)
                .orElseThrow();
        String jurisdiction = admin.getJurisdictionStation();

        if(jurisdiction == null || jurisdiction.isBlank()) {
            throw new CustomException(ErrorCode.JURISDICTION_NOT_FOUND);
        }

        Specification<User> spec = Specification.where(jurisdictionEquals(jurisdiction));
        if (department != null && !department.isBlank()) spec = spec.and(departmentEquals(department));
        if (rank != null && !rank.isBlank()) spec = spec.and(rankEquals(rank));
        if (keyword != null && !keyword.isBlank()) spec = spec.and(keywordLike(keyword));

        List<User> result = userRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "name"));

        List<UserListResponse.UserItem> users = result.stream()
                .map(u -> UserListResponse.UserItem.builder()
                        .userId(u.getUserId())
                        .email(u.getEmail())
                        .name(u.getName())
                        .role(u.getRole())
                        .department(u.getDepartment())
                        .jurisdictionStation(u.getJurisdictionStation())
                        .rankName(u.getRankName())
                        .phoneNumber(u.getPhoneNumber())
                        .profileImg(toPresignedProfileImgUrl(u.getProfileImg()))
                        .build())
                .toList();

        return UserListResponse.builder()
                .users(users)
                .build();
    }

    private String toPresignedProfileImgUrl(String key) {
        if (key == null || key.isBlank()) return null;
        return objectStorageService.presignGetUrl(key);
    }

    private Specification<User> jurisdictionEquals(String jurisdictionStation) {
        return (root, query, cb) ->
                cb.equal(root.get("jurisdictionStation"), jurisdictionStation);
    }
    private Specification<User> departmentEquals(String department) {
        if(department == null || department.isBlank()) return null;
        String val = department.trim();
        return (root, query, cb) ->
                cb.equal(root.get("department"), val);
    }
    private Specification<User> rankEquals(String rankName) {
        if(rankName == null || rankName.isBlank()) return null;
        String val = rankName.trim();
        return (root, query, cb) ->
                cb.equal(root.get("rankName"), val);
    }
    private Specification<User> keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String like = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> {
            List<Predicate> or = new ArrayList<>();
            or.add(cb.like(cb.lower(root.get("email")), like));
            or.add(cb.like(cb.lower(root.get("name")), like));
            or.add(cb.like(cb.lower(root.get("department")), like));
            or.add(cb.like(cb.lower(root.get("rankName")), like));
            return cb.or(or.toArray(new Predicate[0]));
        };
    }

    // 사용자 상세 조회
    public UserDetailResponse getUserDetail(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return toDetail(user);
    }

    private UserDetailResponse toDetail(User user) {
        return UserDetailResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .profileImg(toPresignedProfileImgUrl(user.getProfileImg()))
                .jurisdictionStation(user.getJurisdictionStation())
                .department(user.getDepartment())
                .rankName(user.getRankName())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // 사용자 권한 설정
    @Transactional
    public UserDetailResponse changeRole(UUID userId, Role role) {
        User user = userRepository.findById(userId).orElseThrow();
        user.changeRole(role);
        return toDetail(user);
    }
}
