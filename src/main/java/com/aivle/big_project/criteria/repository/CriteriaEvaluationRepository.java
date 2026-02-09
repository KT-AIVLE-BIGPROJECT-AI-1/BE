package com.aivle.big_project.criteria.repository;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationEntity;
import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CriteriaEvaluationRepository extends JpaRepository<CriteriaEvaluationEntity, UUID> {

    Optional<CriteriaEvaluationEntity>
    findTopByCriteria_CriteriaIdOrderByCreatedAtDesc(UUID criteriaId);

    List<CriteriaEvaluationEntity>
    findByCriteria_CriteriaIdOrderByCreatedAtDesc(UUID criteriaId);
}