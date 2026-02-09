package com.aivle.big_project.audit_log.controller;

import com.aivle.big_project.audit_log.service.AuditResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.UUID;

@ControllerAdvice
@RequiredArgsConstructor
public class AuditHeaderAdvice implements ResponseBodyAdvice<Object> {

    private final AuditResolver auditResolver;
    private static final String HEADER_TARGET_ID = "X-Audit-Target-Id";

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // JSON 응답에 대해서만 적용
        return false;
    }

    @Override
    public @Nullable Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(request instanceof ServletServerHttpRequest sr) ||
                !(response instanceof ServletServerHttpResponse ssr)) {
            return body;
        }

        HttpServletRequest servletRequest = sr.getServletRequest();
        HttpServletResponse servletResponse = ssr.getServletResponse();

        // JSON이 아닌 응답은 스킵
        if (!MediaType.APPLICATION_JSON.includes(selectedContentType)) {
            return body;
        }

        // 이미 세팅돼 있으면 중복 세팅 방지
        if (servletResponse.containsHeader(HEADER_TARGET_ID)) {
            return body;
        }

        // Resolver로 action/targetType 판단 (request body는 여기서 필요 없음)
        AuditResolver.Resolved resolved = auditResolver.resolve(servletRequest);
        if (resolved == null) return body;

        // CREATE가 아니면 스킵 (변경 가능)
        if (resolved.action() != com.aivle.big_project.audit_log.entity.AuditAction.CREATE) {
            return body;
        }

        // 이미 targetId가 추출 가능하면 굳이 body에서 찾지 않음
        if (resolved.targetId() != null) {
            servletResponse.setHeader(HEADER_TARGET_ID, resolved.targetId().toString());
            return body;
        }

        // body에서 ~Id 추출 (CamelCase 기준)
        UUID targetId = tryExtractIdFromBody(body, resolved.targetType().name());
        if (targetId != null) {
            servletResponse.setHeader(HEADER_TARGET_ID, targetId.toString());
        }
        return body;
    }

    private UUID tryExtractIdFromBody(Object body, String targetTypeName) {
        if (body == null) return null;

        // 타겟 타입에 따라 기대하는 프로퍼티명을 매핑
        String prop = switch (targetTypeName) {
            case "AUDIT_LOG" -> "logId";
            case "ACTIVITY_LOG" -> "logId";
            case "CHAT_LOG" -> "chatId";
            case "USER" -> "userId";
            case "CASE" -> "caseId";
            case "REPORT" -> "reportId";
            case "ANALYSIS" -> "analysisId";
            case "INVESTIGATION_FILE" -> "fileId";
            case "FINAL_ANALYSIS" -> "finalId";
            case "LEGAL_CRITERIA" -> "criteriaId";
            case "CRITERIA_EVALUATION" -> "evaluationId";
            case "CONTRADICTION" -> "contradictionId";
            case "TRANSCRIPT" -> "transcriptId";
            case "QUESTION" -> "questionId";

            default -> null;
        };
        if (prop == null) return null;

        Object raw = readProperty(body, prop);
        if (raw == null) return null;

        try {
            return (raw instanceof UUID u) ? u : UUID.fromString(raw.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Object readProperty(Object body, String prop) {
        // 1) getXxx() 우선
        String getter = "get" + Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
        try {
            Method m = body.getClass().getMethod(getter);
            return m.invoke(body);
        } catch (Exception ignored) {}

        // 2) 필드 접근 (Lombok @Getter 없거나 record가 아닌 경우 대비)
        try {
            var f = body.getClass().getDeclaredField(prop);
            f.setAccessible(true);
            return f.get(body);
        } catch (Exception ignored) {}

        return null;
    }
}
