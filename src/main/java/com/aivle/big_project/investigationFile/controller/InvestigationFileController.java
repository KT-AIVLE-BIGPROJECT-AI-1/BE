package com.aivle.big_project.investigationFile.controller;

import com.aivle.big_project.investigationFile.dto.InvestigationFileListResponse;
import com.aivle.big_project.investigationFile.dto.InvestigationFileResponse;
import com.aivle.big_project.investigationFile.service.InvestigationFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/cases/{caseId}/files")
@RequiredArgsConstructor
public class InvestigationFileController {

    private final InvestigationFileService investigationFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<InvestigationFileResponse>> upload(
            @PathVariable UUID caseId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(
                investigationFileService.upload(caseId, files)
        );
    }

    @GetMapping
    public ResponseEntity<InvestigationFileListResponse> getFiles(
            @PathVariable UUID caseId,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(
                investigationFileService.getFiles(caseId, type)
        );
    }
}