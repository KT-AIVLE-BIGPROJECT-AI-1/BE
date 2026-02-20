package com.aivle.big_project.audit_log.service;

import com.aivle.big_project.audit_log.entity.AuditAction;
import com.aivle.big_project.audit_log.entity.AuditLog;
import com.aivle.big_project.audit_log.entity.AuditTargetType;
import com.aivle.big_project.audit_log.repository.AuditLogRepository;
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

    @Transactional
    AuditLog save(
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
    public List<AuditLog> search(
            UUID userId,
            AuditAction action,
            AuditTargetType targetType,
            LocalDateTime from,
            LocalDateTime to
    ) {

        Specification<AuditLog> spec = Specification.where((root, query, cb) -> cb.equal(root.get("userId"), userId));
        if (action != null) {

            spec = spec.and((root, query, cb) -> cb.equal(root.get("action"), action));

        }

        if (targetType != null) {

            spec = spec.and((root, query, cb) -> cb.equal(root.get("targetType"), targetType));

        }

        if (from != null) {

            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from));

        }

        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));

        }

        return auditLogRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

    }
}
