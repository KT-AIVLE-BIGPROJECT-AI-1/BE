package com.aivle.big_project.audit_log.repository;

import com.aivle.big_project.audit_log.entity.AuditAction;
import com.aivle.big_project.audit_log.entity.AuditLog;
import com.aivle.big_project.audit_log.entity.AuditTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @Query("""
    SELECT a FROM AuditLog a
    WHERE a.userId = :userId
      AND (:action IS NULL OR a.action = :action)
      AND (:targetType IS NULL OR a.targetType = :targetType)
      AND (:from IS NULL OR a.createdAt >= :from)
      AND (:to IS NULL OR a.createdAt <= :to)
    ORDER BY a.createdAt DESC
""")
    List<AuditLog> searchLogs(
            @Param("userId") UUID userId,
            @Param("action") AuditAction action,
            @Param("targetType") AuditTargetType targetType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
