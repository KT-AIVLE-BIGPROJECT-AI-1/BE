package com.aivle.big_project.transcript.repository;

import com.aivle.big_project.transcript.entity.TranscriptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TranscriptRepository
        extends JpaRepository<TranscriptEntity, UUID> {

    List<TranscriptEntity> findAllByAnalysis_AnalysisIdOrderByCreatedAtAsc(UUID analysisId);
}