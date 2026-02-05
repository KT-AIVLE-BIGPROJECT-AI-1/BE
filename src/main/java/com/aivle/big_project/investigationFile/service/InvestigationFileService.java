package com.aivle.big_project.investigationFile.service;

import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.investigationFile.dto.InvestigationFileListItem;
import com.aivle.big_project.investigationFile.dto.InvestigationFileListResponse;
import com.aivle.big_project.investigationFile.dto.InvestigationFileResponse;
import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import com.aivle.big_project.investigationFile.repository.InvestigationFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestigationFileService {

    private final InvestigationFileRepository investigationFileRepository;

    private static final long DOC_MAX   = 20 * 1024 * 1024;
    private static final long IMG_MAX   = 10 * 1024 * 1024;
    private static final long AUDIO_MAX = 100 * 1024 * 1024;
    private static final long VIDEO_MAX = 500 * 1024 * 1024;

    // 여러 파일 업로드
    public List<InvestigationFileResponse> upload(
            UUID caseId,
            List<MultipartFile> files
    ) {
        // 파일 리스트 자체가 비어 있는 경우
        if (files == null || files.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_PROVIDED);
        }

        List<InvestigationFileResponse> results = new ArrayList<>();

        // 각 파일을 순차적으로 처리
        for (MultipartFile file : files) {
            results.add(uploadSingle(caseId, file));
        }

        return results;
    }

    //단일 파일 업로드
    private InvestigationFileResponse uploadSingle(
            UUID caseId,
            MultipartFile file
    ) {
        // 파일이 아예 없거나 비어 있는 경우
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_PROVIDED);
        }

        // 원본 파일명 추출
        String originName = file.getOriginalFilename();

        // 확장자가 없는 파일은 허용하지 않음
        if (originName == null || !originName.contains(".")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 확장자 추출 (소문자 통일)
        String extension =
                originName.substring(originName.lastIndexOf('.') + 1)
                        .toLowerCase();

        // 확장자를 기준으로 서버에서 파일 유형 자동 판별
        FileType fileType = resolveFileType(extension);

        // 파일 유형별 용량 검증
        validateFileSize(fileType, file.getSize());

        if (investigationFileRepository
                .existsByCaseIdAndOriginName(caseId, originName)) {
            throw new CustomException(ErrorCode.FILE_ALREADY_EXISTS);
        }

        // 임시 로컬 저장!!!!!
        String filePath = saveToLocal(caseId, file);

        // S3 연동시 사용 예정!!!!!
        /*
        String filePath = "cases/" + caseId + "/" + originName;

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(file.getContentType())
                .build(),
            RequestBody.fromInputStream(
                file.getInputStream(),
                file.getSize()
            )
        );
        */
        LocalDateTime now = LocalDateTime.now();

        InvestigationFileEntity entity =
                InvestigationFileEntity.builder()
                        .caseId(caseId)
                        .fileType(fileType)
                        .filePath(filePath)
                        .originName(originName)
                        .fileSize(file.getSize())
                        .createdAt(now)
                        .build();

        InvestigationFileEntity saved =
                investigationFileRepository.save(entity);

        return new InvestigationFileResponse(
                saved.getFileId(),
                saved.getCaseId(),
                saved.getFileType(),
                saved.getOriginName(),
                saved.getFilePath(),
                saved.getFileSize(),
                saved.getCreatedAt()
        );
    }

    // 임시 로컬 저장!!!!!
    private String saveToLocal(UUID caseId, MultipartFile file) {
        try {
            String originName = file.getOriginalFilename();

            // 기본 저장 경로 생성
            Path baseDir = Paths.get("uploads", "cases", caseId.toString());
            Files.createDirectories(baseDir);

            // 최종 파일 경로
            Path targetPath = baseDir.resolve(originName);

            // 파일 복사 (같은 이름이면 덮어쓰기)
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return targetPath.toString();

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // FileType 판별 메서드
    private FileType resolveFileType(String ext) {
        return switch (ext) {
            case "pdf", "docx", "xlsx", "xls", "txt", "log", "csv"
                    -> FileType.DOCUMENT;

            case "jpg", "jpeg", "png", "bmp", "tiff", "webp", "gif"
                    -> FileType.IMAGE;

            case "mp3", "wav", "m4a", "flac", "aac", "ogg"
                    -> FileType.AUDIO;

            case "mp4", "avi", "mov", "mkv", "wmv", "flv"
                    -> FileType.VIDEO;

            default -> throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        };
    }

    private void validateFileSize(FileType fileType, long size) {
        long limit = switch (fileType) {
            case DOCUMENT -> DOC_MAX;
            case IMAGE    -> IMG_MAX;
            case AUDIO    -> AUDIO_MAX;
            case VIDEO    -> VIDEO_MAX;
        };

        if (size > limit) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    // 자료 목록 조회
    public InvestigationFileListResponse getFiles(
            UUID caseId,
            String type
    ) {
        List<InvestigationFileEntity> entities;

        if (type != null) {
            FileType fileType;
            try {
                fileType = FileType.valueOf(type);
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
            }

            entities = investigationFileRepository
                    .findByCaseIdAndFileTypeOrderByCreatedAtDesc(
                            caseId,
                            fileType
                    );
        } else {
            entities = investigationFileRepository
                    .findByCaseIdOrderByCreatedAtDesc(caseId);
        }

        List<InvestigationFileListItem> data =
                entities.stream()
                        .map(e -> new InvestigationFileListItem(
                                e.getFileId(),
                                e.getFileType().name(),
                                e.getOriginName(),
                                e.getCreatedAt()
                        ))
                        .collect(Collectors.toList());

        return new InvestigationFileListResponse(data);
    }
}