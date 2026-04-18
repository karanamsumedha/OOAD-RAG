package com.rag.platform.service.recommendation;

public record RecommendationCandidate(Long paperId, double score, String reason) {}

