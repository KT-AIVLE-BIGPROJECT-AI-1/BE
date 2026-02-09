package com.aivle.big_project.audit_log.service;

import com.aivle.big_project.audit_log.entity.AuditAction;
import com.aivle.big_project.audit_log.entity.AuditTargetType;
import com.aivle.big_project.security.service.JwtTokenService;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuditResolver {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    private static final Pattern UUID_IN_PATH =
            Pattern.compile(".*?/([0-9a-fA-F\\-]{36})(/.*)?$");

    // /cases/{caseId}/... 형태에서 caseId만 뽑아내는 패턴
    private static final Pattern CASE_ID_IN_PATH =
            Pattern.compile("^/cases/([0-9a-fA-F\\-]{36})(/.*)?$");

    public Resolved resolve(HttpServletRequest req) {
        final String path = req.getRequestURI();
        final String method = req.getMethod();

        // Polling API는 감사로그에서 제외(로그 오염 방지)
        if ("GET".equals(method)) {
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/analysis/[0-9a-fA-F\\-]{36}/status/?$")) {
                UUID analysisId = extractSubIdAfterCaseId(path);
                return new Resolved(AuditAction.READ, AuditTargetType.ANALYSIS, analysisId, true, null);
            }
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/criteria/[0-9a-fA-F\\-]{36}/status/?$")) {
                UUID criteriaId = extractSubIdAfterCaseId(path);
                return new Resolved(AuditAction.READ, AuditTargetType.CRITERIA_EVALUATION, criteriaId, true, null);
            }
        }

        // AUTH
        if (path.startsWith("/auth/")) {
            // 회원가입 (actor는 아직 없음)
            if (path.equals("/auth/signup")) {
                return new Resolved(AuditAction.CREATE, AuditTargetType.AUTH, null, false, null);
            }
            // 로그인: body.email로 userId 추출(성공/실패와 무관하게 시도)
            if (path.equals("/auth/login")) {
                UUID actor = resolveActorUserIdForLogin(req);
                return new Resolved(AuditAction.LOGIN, AuditTargetType.AUTH, null, false, actor);
            }
            // 로그아웃: 일반적으로 SecurityContext에서 userId를 얻을 수 있으므로 여기서는 null
            if (path.equals("/auth/logout")) {
                return new Resolved(AuditAction.LOGOUT, AuditTargetType.AUTH, null, false, null);
            }
            // 토큰 재발급: refresh token(subject)로 userId 추출
            if (path.equals("/auth/refresh")) {
                UUID actor = resolveActorUserIdForRefresh(req);
                return new Resolved(AuditAction.TOKEN_REFRESH, AuditTargetType.AUTH, null, false, actor);
            }
            return new Resolved(AuditAction.OTHER, AuditTargetType.AUTH, null, false, null);
        }

        // AUDIT LOG
        if (path.startsWith("/audit-logs")) {
            return new Resolved(toCrud(method), AuditTargetType.AUDIT_LOG, extractUuidFromPath(path), false, null);
        }

        // USERS
        if (path.startsWith("/users")) {
            return new Resolved(toCrud(method), AuditTargetType.USER, extractUuidFromPath(path), false, null);
        }

        // REPORTS
        if (path.startsWith("/reports")) {
            UUID reportId = extractReportId(path);
            AuditAction action = path.endsWith("/download") ? AuditAction.DOWNLOAD : toCrud(method);
            return new Resolved(action, AuditTargetType.REPORT, reportId, false, null);
        }

        // CASES and sub-resources
        if (path.startsWith("/cases")) {
            UUID caseId = extractCaseId(path);
            UUID subId = extractSubIdAfterCaseId(path);

            // 사건 활동 로그 조회
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/activity-log/?$")) {
                return new Resolved(toCrud(method), AuditTargetType.ACTIVITY_LOG, caseId, false, null);
            }

            // 사건 챗봇
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/chat/?$")) {
                AuditAction action = "POST".equals(method) ? AuditAction.CREATE : AuditAction.READ;
                return new Resolved(action, AuditTargetType.CHAT_LOG, caseId, false, null);
            }

            // 사건 파일 업로드/목록
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/files(/.*)?$")) {
                AuditAction action = "POST".equals(method) ? AuditAction.UPLOAD : AuditAction.READ;
                UUID fileId = subId; // /files/{fileId} 인 경우
                return new Resolved(action, AuditTargetType.INVESTIGATION_FILE, fileId != null ? fileId : caseId, false, null);
            }

            // 사건 분석 관련 (결과/전사 등)
            // 예: /cases/{caseId}/analysis/{analysisId}, /cases/{caseId}/analysis/{analysisId}/status, /transcript
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/analysis(/.*)?$")) {
                // 분석 생성 요청이 별도 엔드포인트라면 CREATE로, 결과 조회는 READ로 처리
                UUID analysisId = subId; // /analysis/{analysisId} 인 경우
                return new Resolved(toCrud(method), AuditTargetType.ANALYSIS, analysisId != null ? analysisId : caseId, false, null);
            }

            // 최종 통합 분석
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/final-analysis/?$")) {
                AuditAction action = "POST".equals(method) ? AuditAction.AI_REQUEST : toCrud(method);
                return new Resolved(action, AuditTargetType.FINAL_ANALYSIS, caseId, false, null);
            }

            // 기준 충족 평가(체크리스트 수정/상태조회 포함)
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/criteria/[0-9a-fA-F\\-]{36}(/.*)?$")) {
                UUID criteriaId = subId;
                return new Resolved(toCrud(method), AuditTargetType.CRITERIA_EVALUATION, criteriaId != null ? criteriaId : caseId, false, null);
            }

            // 법리 판단 기준(체크리스트) 목록 조회
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/criteria(/.*)?$")) {
                return new Resolved(toCrud(method), AuditTargetType.LEGAL_CRITERIA, caseId, false, null);
            }

            // 추천 질문 목록/생성 (AI 요청 포함)
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/contradiction/[0-9a-fA-F\\-]{36}/questions(/.*)?$")) {
                AuditAction action = "GET".equals(method) ? AuditAction.READ : ("POST".equals(method) ? AuditAction.AI_REQUEST : toCrud(method));
                UUID contradictionId = subId;
                return new Resolved(action, AuditTargetType.QUESTION, contradictionId != null ? contradictionId : caseId, false, null);
            }

            // 모순 단건 상태 변경/조회
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/contradiction/[0-9a-fA-F\\-]{36}(/.*)?$")) {
                UUID contradictionId = subId;
                return new Resolved(toCrud(method), AuditTargetType.CONTRADICTION, contradictionId != null ? contradictionId : caseId, false, null);
            }

            // 모순 목록 조회
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/contradiction(/.*)?$")) {
                return new Resolved(toCrud(method), AuditTargetType.CONTRADICTION, caseId, false, null);
            }

            // 사건 보고서(케이스 하위)
            if (path.matches("^/cases/[0-9a-fA-F\\-]{36}/reports(/.*)?$")) {
                AuditAction action;
                if (path.endsWith("/download")) action = AuditAction.DOWNLOAD;
                else action = toCrud(method);
                UUID reportId = subId;
                return new Resolved(action, AuditTargetType.REPORT, reportId != null ? reportId : caseId, false, null);
            }

            // 그 외 /cases 하위는 기본적으로 CASE로 로깅
            return new Resolved(toCrud(method), AuditTargetType.CASE, caseId, false, null);
        }
        // Fallback
        return new Resolved(toCrud(method), AuditTargetType.OTHER, extractUuidFromPath(path), false, null);
    }

    private AuditAction toCrud(String method) {
        return switch (method) {
            case "POST" -> AuditAction.CREATE;
            case "PUT", "PATCH" -> AuditAction.UPDATE;
            case "DELETE" -> AuditAction.DELETE;
            case "GET" -> AuditAction.READ;
            default -> AuditAction.OTHER;
        };
    }

    private UUID extractCaseId(String path) {
        Matcher m = CASE_ID_IN_PATH.matcher(path);
        if (!m.matches()) return null;
        try {
            return UUID.fromString(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }

    private UUID extractUuidFromPath(String path) {
        Matcher m = UUID_IN_PATH.matcher(path);
        if (!m.matches()) return null;
        try {
            return UUID.fromString(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }

    // /cases/{caseId}/{segment}/{subId}/... 에서 subId만 뽑아냄
    private UUID extractSubIdAfterCaseId(String path) {
        // 예: /cases/{caseId}/analysis/{analysisId}/status
        Pattern p = Pattern.compile("^/cases/[0-9a-fA-F\\-]{36}/[^/]+/([0-9a-fA-F\\-]{36})(/.*)?$");
        Matcher m = p.matcher(path);
        if (!m.matches()) return null;
        try {
            return UUID.fromString(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }

    // /reports/{reportId}/... (top-level)에서 reportId 추출
    private UUID extractReportId(String path) {
        Pattern p = Pattern.compile("^/reports/([0-9a-fA-F\\-]{36})(/.*)?$");
        Matcher m = p.matcher(path);
        if (!m.matches()) return null;
        try {
            return UUID.fromString(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> readJsonBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper wrapper)) return null;
        byte[] bytes = wrapper.getContentAsByteArray();
        if (bytes == null || bytes.length == 0) return null;
        try {
            return objectMapper.readValue(bytes, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private UUID resolveActorUserIdForLogin(HttpServletRequest request) {
        Map<String, Object> body = readJsonBody(request);
        if (body == null) return null;
        String email = asString(body.get("email"));
        if (email == null || email.isBlank()) return null;
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(com.aivle.big_project.user.entity.User::getUserId).orElse(null);
    }

    private UUID resolveActorUserIdForRefresh(HttpServletRequest request) {
        String refreshToken = null;

        Map<String, Object> body = readJsonBody(request);
        if (body != null) {
            refreshToken = asString(body.get("refresh_token"));
            if (refreshToken == null) refreshToken = asString(body.get("refreshToken"));
        }

        if (refreshToken == null) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                refreshToken = header.substring("Bearer ".length()).trim();
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) return null;

        try {
            String sub = jwtTokenService.parseClaims(refreshToken).getSubject();
            return (sub == null || sub.isBlank()) ? null : UUID.fromString(sub);
        } catch (Exception e) {
            return null;
        }
    }

    public record Resolved(
            AuditAction action,
            AuditTargetType targetType,
            UUID targetId,
            boolean skip,
            UUID actorUserId
    ) {}
}