package com.rag.platform.dto.recommendation;

public record RecommendationResponse(
    Long paperId,
    String title,
    String authors,
    Integer publicationYear,
    String domain,
    Double score,
    String reason
) {}

