package com.aivle.big_project.analysis.entity;

public enum AnalysisStatus {
    PENDING,      // 분석 요청만 생성된 상태
    PROCESSING,   // AI 분석 진행 중
    COMPLETED     // AI 분석 완료
}