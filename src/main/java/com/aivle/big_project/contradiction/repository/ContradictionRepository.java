package com.aivle.big_project.contradiction.repository;

import com.aivle.big_project.contradiction.entity.ContradictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ContradictionRepository
        extends JpaRepository<ContradictionEntity, UUID> {

    @Query("""
        select c
        from ContradictionEntity c
        join fetch c.statement1 s1
        join fetch c.statement2 s2
        where c.caseEntity.caseId = :caseId
    """)
    List<ContradictionEntity> findAllByCaseId(@Param("caseId") UUID caseId);

    Optional<ContradictionEntity> findByContradictionIdAndCaseEntity_CaseId(UUID contradictionId, UUID caseId);

    boolean existsByCaseEntity_CaseId(UUID caseId);
}