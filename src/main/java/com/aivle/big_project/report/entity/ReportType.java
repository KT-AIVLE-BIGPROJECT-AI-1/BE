package com.aivle.big_project.report.entity;

public enum ReportType {
    INTERIM, // (중간) 수사보고서
    FINAL,   // 수사결과보고서
    TRANSFER,   // 송치결정서
    NON_TRANSFER, // 불송치결정서
    SUSPENSION    // 수사중지결정서
}
