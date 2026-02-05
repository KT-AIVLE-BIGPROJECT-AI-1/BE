package com.aivle.big_project.investigationFile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "investigation_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InvestigationFileEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "file_id", columnDefinition = "uuid")
    private UUID fileId;

    @Column(name = "case_id", nullable = false, columnDefinition = "uuid")
    private UUID caseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "file_path", nullable = false, columnDefinition = "text")
    private String filePath;

    @Column(name = "origin_name", nullable = false, length = 255)
    private String originName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}