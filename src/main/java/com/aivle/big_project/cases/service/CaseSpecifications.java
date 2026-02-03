package com.aivle.big_project.cases.service;

import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.cases.entity.CaseType;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
public class CaseSpecifications {
    public static Specification<Case> assigneeIs(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("userId"), userId);
    }

    public static Specification<Case> titleKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    public static Specification<Case> typeEquals(CaseType type) {
        if (type == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Case> occuredAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        if (start != null && end != null) {
            return (root, query, cb) -> cb.between(root.get("occuredAt"), start, end);
        }
        if (start != null) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("occuredAt"), start);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("occuredAt"), end);
    }

    public static Specification<Case> assigneeJurisdictionEquals(String jurisdictionStation) {
        if (jurisdictionStation == null || jurisdictionStation.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) ->
                cb.equal(root.get("user").get("jurisdictionStation"), jurisdictionStation);
    }
}
