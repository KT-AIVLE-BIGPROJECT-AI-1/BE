package com.aivle.big_project.question.repository;

import com.aivle.big_project.question.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface QuestionRepository extends JpaRepository<QuestionEntity, UUID> {

    @Query("""
    select q
    from QuestionEntity q
    where q.contradiction.contradictionId = :contradictionId
    order by q.createdAt asc
""")
    List<QuestionEntity> findByContradictionId(
            @Param("contradictionId") UUID contradictionId
    );

    List<QuestionEntity> findAllByAnalysis_AnalysisId(UUID analysisId);
}