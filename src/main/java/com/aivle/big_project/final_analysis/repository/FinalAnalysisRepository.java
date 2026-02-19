package com.aivle.big_project.final_analysis.repository;


import com.aivle.big_project.final_analysis.entity.FinalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinalAnalysisRepository extends JpaRepository<FinalAnalysis, UUID> {
    @Query("""
    select f
    from FinalAnalysis f
    where f.aCase is not null
      and f.aCase.caseId = :caseId
    order by f.createdAt desc
""")
    List<FinalAnalysis> findLatestByCaseId(@Param("caseId") UUID caseId);

    @Query("""
        select count(f) > 0
        from FinalAnalysis f
        where f.aCase.caseId = :caseId
    """)
    boolean existsFinalAnalysisByCaseId(@Param("caseId") UUID caseId);
}
