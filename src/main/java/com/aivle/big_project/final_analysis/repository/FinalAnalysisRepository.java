package com.aivle.big_project.final_analysis.repository;


import com.aivle.big_project.final_analysis.entity.FinalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FinalAnalysisRepository extends JpaRepository<FinalAnalysis, UUID> {
    Optional<FinalAnalysis> findTopByACase_CaseIdOrderByCreatedAtDesc(UUID caseId);

    @Query("""
        select count(f) > 0
        from FinalAnalysis f
        where f.aCase.caseId = :caseId
    """)
    boolean existsFinalAnalysisByCaseId(@Param("caseId") UUID caseId);
}
