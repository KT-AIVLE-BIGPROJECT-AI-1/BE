package com.aivle.big_project.criteria.controller;

import com.aivle.big_project.criteria.dto.CriteriaDetailResponse;
import com.aivle.big_project.criteria.dto.CriteriaListResponse;
import com.aivle.big_project.criteria.service.CriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cases/{caseId}/criteria")
public class CriteriaController {

    private final CriteriaService criteriaService;

    // 체크리스트 리스트 조회
    @GetMapping
    public ResponseEntity<CriteriaListResponse> getCriteriaList(
            @PathVariable UUID caseId
    ) {
        return ResponseEntity.ok(
                criteriaService.getCriteriaList(caseId)
        );
    }

    // 체크리스트 상세 조회 (팝업 / 드로어)
    @GetMapping("/{criteriaId}")
    public ResponseEntity<CriteriaDetailResponse> getCriteriaDetail(
            @PathVariable UUID caseId,
            @PathVariable UUID criteriaId
    ) {
        return ResponseEntity.ok(
                criteriaService.getCriteriaDetail(
                        caseId,
                        criteriaId
                )
        );
    }
}