package com.aivle.big_project.final_analysis.repository;


import com.aivle.big_project.final_analysis.entity.FinalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FinalAnalysisRepository extends JpaRepository<FinalAnalysis, UUID> {
    Optional<FinalAnalysis> findTopByACase_CaseIdOrderByCreatedAtDesc(UUID caseId);
    boolean existsByACase_CaseId(UUID caseId);
}
