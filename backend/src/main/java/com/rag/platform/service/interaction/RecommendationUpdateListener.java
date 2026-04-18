package com.rag.platform.service.interaction;

import com.rag.platform.model.InteractionType;
import com.rag.platform.service.RecommendationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RecommendationUpdateListener {
  private final RecommendationService recommendationService;

  public RecommendationUpdateListener(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  @Async
  @EventListener
  public void onInteraction(UserInteractionRecordedEvent event) {
    // Update recommendations on meaningful interactions.
    if (event.type() == InteractionType.VIEW
        || event.type() == InteractionType.SAVE
        || event.type() == InteractionType.RATE
        || event.type() == InteractionType.CITE) {
      recommendationService.recomputeRecommendations(event.userId());
    }
  }
}

