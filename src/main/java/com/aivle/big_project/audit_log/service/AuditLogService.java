package com.aivle.big_project.audit_log.service;

import com.aivle.big_project.audit_log.dto.AuditLogResponse;
import com.aivle.big_project.audit_log.dto.AuditLogSearchResponse;
import com.aivle.big_project.audit_log.dto.AuditTargetUserResponse;
import com.aivle.big_project.audit_log.entity.AuditAction;
import com.aivle.big_project.audit_log.entity.AuditLog;
import com.aivle.big_project.audit_log.entity.AuditTargetType;
import com.aivle.big_project.audit_log.repository.AuditLogRepository;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional AuditLog save(
            UUID userId,
            AuditAction action,
            AuditTargetType targetType,
            UUID targetId,
            Map<String, Object> metadata) {

        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .build();

        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public AuditLogSearchResponse search(
            UUID userId,
            String action,
            String targetType,
            LocalDateTime from,
            LocalDateTime to
    ) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("감사 대상 사용자를 찾을 수 없습니다."));

        AuditTargetUserResponse userDto = AuditTargetUserResponse.builder()
                .userId(targetUser.getUserId())
                .email(targetUser.getEmail())
                .name(targetUser.getName())
                .phoneNumber(targetUser.getPhoneNumber())
                .role(targetUser.getRole().name())
                .profileImg(targetUser.getProfileImg())
                .jurisdictionStation(targetUser.getJurisdictionStation())
                .department(targetUser.getDepartment())
                .rankName(targetUser.getRankName())
                .build();

        Specification<AuditLog> spec = Specification.where(
                ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId))
        );

        if (action != null && !action.isBlank()) {
            AuditAction parsed = AuditAction.valueOf(action);
            spec = spec.and(
                    ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("action"), parsed))
            );
        }

        if (targetType != null && !targetType.isBlank()) {
            AuditTargetType parsed = AuditTargetType.valueOf(targetType);
            spec = spec.and(
                    ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("targetType"), parsed))
            );
        }

        if (from != null) {
            spec = spec.and(
                    ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from))
            );
        }

        if (to != null) {
            spec = spec.and(
                    ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to))
            );
        }

        List<AuditLog> logs = auditLogRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<AuditLogResponse> items = logs.stream()
                .map(this::toResponse)
                .toList();

        return AuditLogSearchResponse.builder()
                .user(userDto)
                .logs(items)
                .build();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .auditId(log.getLogId())
                .action(log.getAction().name())
                .targetType(log.getTargetType().name())
                .targetId(log.getTargetId())
                .metadata(log.getMetadata())
                .createdAt(log.getCreatedAt())
                .build();
    }

}
