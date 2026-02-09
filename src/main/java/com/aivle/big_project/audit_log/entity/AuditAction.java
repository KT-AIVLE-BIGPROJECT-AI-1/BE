package com.aivle.big_project.audit_log.entity;

public enum AuditAction {
    // TargetType - AUTH
    LOGIN, LOGOUT, TOKEN_REFRESH,

    // 타 객체들 (챗 메시지, 사건 등)
    CREATE, READ, UPDATE, DELETE,

    // TargetType - FILE, REPORT
    UPLOAD, DOWNLOAD,

    // ANALYSIS, FINAL_ANALYSIS,
    AI_REQUEST, AI_CALLBACK,

    OTHER
}
