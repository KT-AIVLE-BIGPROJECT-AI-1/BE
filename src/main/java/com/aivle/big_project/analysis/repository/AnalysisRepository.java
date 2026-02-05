package com.aivle.big_project.analysis.repository;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {
    @Query("""
        select a
        from AnalysisEntity a
        join fetch a.file f
        where a.analysisId = :analysisId
    """)
    Optional<AnalysisEntity> findByIdWithFile(@Param("analysisId") UUID analysisId);
}