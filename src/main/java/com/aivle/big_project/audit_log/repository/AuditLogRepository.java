package com.aivle.big_project.audit_log.repository;

import com.aivle.big_project.audit_log.entity.AuditAction;
import com.aivle.big_project.audit_log.entity.AuditLog;
import com.aivle.big_project.audit_log.entity.AuditTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID>,
        JpaSpecificationExecutor<AuditLog> {
}
