package com.aivle.big_project.contradiction.controller;

import com.aivle.big_project.contradiction.dto.ContradictionListResponse;
import com.aivle.big_project.contradiction.service.ContradictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
