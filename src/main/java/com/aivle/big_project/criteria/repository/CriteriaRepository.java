package com.aivle.big_project.criteria.repository;

import com.aivle.big_project.criteria.dto.CriteriaItem;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CriteriaRepository extends JpaRepository<CriteriaEntity, UUID> {

    //체크리스트 리스트 조회
    @Query("""
    select new com.aivle.big_project.criteria.dto.CriteriaItem(
        c.criteriaId,
        c.criterionText,
        i.issueName,
        i.relatedLaw,
        c.precedentCaseNo
    )
    from CriteriaEntity c
    join c.legalIssue i
    where i.caseEntity.caseId = :caseId
    """)
    List<CriteriaItem> findCriteriaItemsByCaseId(@Param("caseId") UUID caseId);

    //체크리스트 상세 조회 검증용
    @Query("""
    select c
    from CriteriaEntity c
    join fetch c.legalIssue i
    where i.caseEntity.caseId = :caseId
      and c.criteriaId = :criteriaId
""")
    Optional<CriteriaEntity> findByCaseIdAndCriteriaId(
            @Param("caseId") UUID caseId,
            @Param("criteriaId") UUID criteriaId
    );

    // LegalIssue를 함께 로딩해, LazyInitializationException을 방지
    // criteria.getLegalIssue().getIssueName() 접근 시 트랜잭션 밖이면 예외 발생 가능
    @EntityGraph(attributePaths = {"legalIssue"})
    List<CriteriaEntity> findAllWithLegalIssueByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(UUID caseId);

    List<CriteriaEntity> findAllByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(UUID caseId);
    Optional<CriteriaEntity> findByLegalIssue_CaseEntity_CaseIdAndCriteriaId(UUID caseId, UUID criteriaId);
}
