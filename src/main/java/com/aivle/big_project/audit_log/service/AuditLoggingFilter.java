package com.aivle.big_project.audit_log.service;

import com.aivle.big_project.security.service.JwtTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.aivle.big_project.security.SecurityUtils.getCurrentUserIdOrNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditResolver auditResolver;
    private final AuditLogService auditLogService;
    private final JwtTokenService jwtTokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Polling API - 저장 X
        // - 법리기준 평가 상태 조회
        if (path.matches("^/cases/[^/]+/criteria/[^/]+/status$") && "GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // - 분석 상태 조회
        if (path.matches("^/cases/[^/]+/analysis/[^/]+/status$") && "GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startNs = System.nanoTime();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 1024 * 1024);

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            try {
                AuditResolver.Resolved resolved = auditResolver.resolve(wrappedRequest);
                if (resolved == null) return;
                if (resolved.skip()) return;

                UUID actorUserId = resolveActorUserId(wrappedRequest, resolved);
                if (actorUserId == null) return;

                int statusCode = response.getStatus();
                long durationMs = (System.nanoTime() - startNs) / 1_000_000;

                Map<String, Object> metadata = new LinkedHashMap<>();
                metadata.put("method", wrappedRequest.getMethod());
                metadata.put("path", wrappedRequest.getRequestURI());
                metadata.put("query", wrappedRequest.getQueryString());
                metadata.put("status_code", statusCode);
                metadata.put("duration_ms", durationMs);
                metadata.put("ip", wrappedRequest.getRemoteAddr());
                metadata.put("user_agent", wrappedRequest.getHeader("User-Agent"));

                UUID targetId = resolved.targetId();
                if (targetId == null) {
                    String headerTargetId = response.getHeader("X-Audit-Target-Id");
                    if (headerTargetId != null && !headerTargetId.isBlank()) {
                        try {
                            targetId = UUID.fromString(headerTargetId);
                        } catch (Exception ignored) {}
                    }
                }

                auditLogService.save(
                        actorUserId,
                        resolved.action(),
                        resolved.targetType(),
                        targetId,
                        metadata
                );

            } catch (Exception e) {
                // 로그 저장에 실패해도, 실제 요청은 성공해야 함
                log.warn("audit log save failed", e);
            }
        }
    }

    private UUID resolveActorUserId(HttpServletRequest request, AuditResolver.Resolved resolved) {
        // 1) SecurityContext (정상 인증 요청)
        UUID actor = getCurrentUserIdOrNull();
        if (actor != null) return actor;

        // 2) Authorization: Bearer <access token> (SecurityContext가 아직 비어있는 경우 보정)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length()).trim();
            if (!token.isBlank()) {
                try {
                    String sub = jwtTokenService.parseClaims(token).getSubject();
                    if (sub != null && !sub.isBlank()) return UUID.fromString(sub);
                } catch (Exception ignored) {
                    // ignore
                }
            }
        }

        // 3) Resolver가 제공한 actor (로그인/리프레시 등)
        if (resolved != null && resolved.actorUserId() != null) {
            return resolved.actorUserId();
        }
        return null;
    }
}
