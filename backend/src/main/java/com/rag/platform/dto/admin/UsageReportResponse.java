package com.rag.platform.dto.admin;

public record UsageReportResponse(
    long totalUsers,
    long totalPapers,
    long totalLibraryItems,
    long totalFeedback,
    long totalInteractions
) {}

