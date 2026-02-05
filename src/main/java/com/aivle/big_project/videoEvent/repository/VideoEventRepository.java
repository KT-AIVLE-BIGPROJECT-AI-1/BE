package com.aivle.big_project.videoEvent.repository;

import com.aivle.big_project.videoEvent.entity.VideoEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoEventRepository
        extends JpaRepository<VideoEventEntity, UUID> {

    List<VideoEventEntity> findByAnalysisId(UUID analysisId);
}