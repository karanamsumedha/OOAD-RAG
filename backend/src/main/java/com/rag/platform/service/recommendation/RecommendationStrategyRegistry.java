package com.rag.platform.service.recommendation;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * DIP-friendly registry: callers depend on abstraction, not concrete strategy classes.
 */
@Component
public class RecommendationStrategyRegistry {
  private final List<RecommendationStrategy> strategies;

  public RecommendationStrategyRegistry(List<RecommendationStrategy> strategies) {
    this.strategies = strategies;
  }

  public RecommendationStrategy defaultStrategy() {
    // For now there is only one strategy, but this stays extensible (OCP).
    return strategies.stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("No recommendation strategies configured"));
  }
}

