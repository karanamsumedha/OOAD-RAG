package com.rag.platform.service.recommendation;

import java.util.List;

/**
 * Strategy pattern: allows swapping the recommendation algorithm without changing callers.
 */
public interface RecommendationStrategy {
  String name();

  List<RecommendationCandidate> recommend(Long userId, int limit);
}

