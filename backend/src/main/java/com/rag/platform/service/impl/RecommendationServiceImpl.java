package com.rag.platform.service.impl;

import com.rag.platform.dto.recommendation.RecommendationResponse;
import com.rag.platform.model.Recommendation;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.User;
import com.rag.platform.repository.RecommendationRepository;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.service.RecommendationService;
import com.rag.platform.service.recommendation.RecommendationCandidate;
import com.rag.platform.service.recommendation.RecommendationStrategy;
import com.rag.platform.service.recommendation.RecommendationStrategyRegistry;
import com.rag.platform.util.ApiException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationServiceImpl implements RecommendationService {
  private final UserRepository userRepository;
  private final ResearchPaperRepository paperRepository;
  private final RecommendationRepository recommendationRepository;
  private final RecommendationStrategyRegistry registry;

  public RecommendationServiceImpl(
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      RecommendationRepository recommendationRepository,
      RecommendationStrategyRegistry registry
  ) {
    this.userRepository = userRepository;
    this.paperRepository = paperRepository;
    this.recommendationRepository = recommendationRepository;
    this.registry = registry;
  }

  @Override
  @Transactional(readOnly = true)
  public List<RecommendationResponse> getRecommendationsForUser(Long userId) {
    // If none computed yet, compute on-demand.
    if (recommendationRepository.findTop20ByUserIdOrderByScoreDescCreatedAtDesc(userId).isEmpty()) {
      // Avoid nested tx surprises: caller is read-only, so compute in separate call by controller/event.
      // For demo simplicity, return empty; UI triggers recompute via background event.
      return List.of();
    }
    return recommendationRepository.findTop20ByUserIdOrderByScoreDescCreatedAtDesc(userId)
        .stream().map(r -> toResponse(r.getPaper(), r.getScore(), r.getReason())).toList();
  }

  @Override
  @Transactional
  public void recomputeRecommendations(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

    RecommendationStrategy strategy = registry.defaultStrategy();
    List<RecommendationCandidate> candidates = strategy.recommend(userId, 20);

    recommendationRepository.deleteAllByUserId(user.getId());

    List<Recommendation> toSave = new ArrayList<>();
    for (RecommendationCandidate c : candidates) {
      ResearchPaper p = paperRepository.findById(c.paperId()).orElse(null);
      if (p == null) {
        continue;
      }
      toSave.add(new Recommendation(user, p, c.score(), c.reason()));
    }
    recommendationRepository.saveAll(toSave);
  }

  private static RecommendationResponse toResponse(ResearchPaper p, Double score, String reason) {
    return new RecommendationResponse(
        p.getId(),
        p.getTitle(),
        p.getAuthors(),
        p.getPublicationYear(),
        p.getDomain(),
        score,
        reason
    );
  }
}

