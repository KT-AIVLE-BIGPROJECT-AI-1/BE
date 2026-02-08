package com.aivle.big_project.chat_log.repository;

import com.aivle.big_project.chat_log.entity.ChatLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatLogRepository extends JpaRepository<ChatLogEntity, UUID> {

    @Query("""
        select c
        from ChatLogEntity c
        where c.caseEntity.caseId = :caseId
        order by c.createdAt desc
    """)
    List<ChatLogEntity> findByCaseIdOrderByCreatedAtDesc(
            @Param("caseId") UUID caseId
    );
}