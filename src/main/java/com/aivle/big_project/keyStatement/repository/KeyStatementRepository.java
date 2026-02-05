package com.aivle.big_project.keyStatement.repository;

import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KeyStatementRepository
        extends JpaRepository<KeyStatementEntity, UUID> {

    List<KeyStatementEntity> findByAnalysisId(UUID analysisId);
}