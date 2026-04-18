package com.rag.platform.controller;

import com.rag.platform.dto.recommendation.RecommendationResponse;
import com.rag.platform.service.RecommendationService;
import com.rag.platform.util.SecurityUtil;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
  private final RecommendationService recommendationService;

  public RecommendationController(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<List<RecommendationResponse>> get(@PathVariable Long userId) {
    Long current = SecurityUtil.requireCurrentUserId();
    if (!current.equals(userId) && !SecurityUtil.hasRole("ROLE_ADMIN")) {
      userId = current;
    }
    // Ensure recommendations exist.
    recommendationService.recomputeRecommendations(userId);
    return ResponseEntity.ok(recommendationService.getRecommendationsForUser(userId));
  }
}

