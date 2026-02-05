package com.aivle.big_project.analysis.repository;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnalysisRepository
        extends JpaRepository<AnalysisEntity, UUID> {
}