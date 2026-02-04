package com.aivle.big_project.criteria.repository;

import com.aivle.big_project.criteria.entity.LegalIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LegalIssueRepository
        extends JpaRepository<LegalIssueEntity, UUID> {
}