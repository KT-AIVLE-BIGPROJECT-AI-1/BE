package com.aivle.big_project.investigationFile.repository;

import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvestigationFileRepository
        extends JpaRepository<InvestigationFileEntity, UUID> {
}