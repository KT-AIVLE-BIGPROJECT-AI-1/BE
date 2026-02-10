package com.aivle.big_project.cases.service;

import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.entity.CaseType;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
public class CaseSpecifications {
    public static Specification<CaseEntity> assigneeIs(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("userId"), userId);
    }

    public static Specification<CaseEntity> titleKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<CaseEntity> typeEquals(CaseType type) {
        if (type == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<CaseEntity> occurredAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        if (start != null && end != null) {
            return (root, query, cb) -> cb.between(root.get("occurredAt"), start, end);
        }
        if (start != null) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("occurredAt"), start);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("occurredAt"), end);
    }

    public static Specification<CaseEntity> assigneeJurisdictionEquals(String jurisdictionStation) {
        if (jurisdictionStation == null || jurisdictionStation.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) ->
                cb.equal(root.get("user").get("jurisdictionStation"), jurisdictionStation);
    }
}
