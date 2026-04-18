package com.rag.platform.service;

import com.rag.platform.dto.recommendation.RecommendationResponse;
import java.util.List;

public interface RecommendationService {
  List<RecommendationResponse> getRecommendationsForUser(Long userId);

  void recomputeRecommendations(Long userId);
}

