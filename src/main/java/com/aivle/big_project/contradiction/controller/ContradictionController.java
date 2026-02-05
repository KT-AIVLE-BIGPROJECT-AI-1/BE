package com.aivle.big_project.contradiction.controller;

import com.aivle.big_project.contradiction.dto.ContradictionListResponse;
import com.aivle.big_project.contradiction.dto.UpdateContradictionStatusRequest;
import com.aivle.big_project.contradiction.dto.UpdateContradictionStatusResponse;
import com.aivle.big_project.contradiction.service.ContradictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cases/{caseId}/contradiction")
@RequiredArgsConstructor
public class ContradictionController {

    private final ContradictionService contradictionService;

    @GetMapping
    public ResponseEntity<ContradictionListResponse> getContradictions(
            @PathVariable UUID caseId
    ) {
        return ResponseEntity.ok(
                contradictionService.getContradictions(caseId)
        );
    }

    @PatchMapping("/{contradictionId}")
    public ResponseEntity<UpdateContradictionStatusResponse> updateStatus(
            @PathVariable UUID caseId,
            @PathVariable UUID contradictionId,
            @RequestBody UpdateContradictionStatusRequest request
    ) {
        return ResponseEntity.ok(
                contradictionService.updateStatus(caseId, contradictionId, request)
        );
    }
}
