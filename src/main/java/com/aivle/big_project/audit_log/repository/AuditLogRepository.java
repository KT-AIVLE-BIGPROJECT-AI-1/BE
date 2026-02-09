package com.aivle.big_project.audit_log.repository;

import com.aivle.big_project.audit_log.entity.AuditLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findAll(Specification<AuditLog> spec, Sort createdAt);
}
