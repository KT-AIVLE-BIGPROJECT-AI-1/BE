package com.aivle.big_project.transcript.controller;

import com.aivle.big_project.transcript.dto.TranscriptResponse;
import com.aivle.big_project.transcript.service.TranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/cases/{caseId}/analysis/{analysisId}/transcripts")
@RequiredArgsConstructor
public class TranscriptController {

    private final TranscriptService transcriptService;

    @GetMapping
    public ResponseEntity<TranscriptResponse> getTranscripts(
            @PathVariable UUID caseId,
            @PathVariable UUID analysisId
    ) {
        TranscriptResponse response = transcriptService.getTranscripts(analysisId);
        return ResponseEntity.ok(response);
    }
}