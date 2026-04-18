package com.rag.platform.service.recommendation;

import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.UserInteraction;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserInteractionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ContentBasedRecommendationStrategy implements RecommendationStrategy {
  private final UserInteractionRepository interactionRepository;
  private final ResearchPaperRepository paperRepository;

  public ContentBasedRecommendationStrategy(
      UserInteractionRepository interactionRepository,
      ResearchPaperRepository paperRepository
  ) {
    this.interactionRepository = interactionRepository;
    this.paperRepository = paperRepository;
  }

  @Override
  public String name() {
    return "content-based-v1";
  }

  @Override
  public List<RecommendationCandidate> recommend(Long userId, int limit) {
    List<UserInteraction> interactions = interactionRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId);
    if (interactions.isEmpty()) {
      return fallbackTopRecent(limit);
    }

    Set<Long> alreadySeen = new HashSet<>();
    Map<String, Integer> keywordWeight = new HashMap<>();
    Map<String, Integer> domainWeight = new HashMap<>();

    for (UserInteraction ui : interactions) {
      if (ui.getPaper() != null) {
        alreadySeen.add(ui.getPaper().getId());
      }

      int w = switch (ui.getType()) {
        case VIEW -> 2;
        case SAVE -> 4;
        case RATE -> 3;
        case CITE -> 3;
        case SEARCH -> 1;
      };

      if (ui.getPaper() != null) {
        ResearchPaper p = ui.getPaper();
        domainWeight.merge(normalize(p.getDomain()), w, Integer::sum);
        for (String kw : splitKeywords(p.getKeywords())) {
          keywordWeight.merge(kw, w, Integer::sum);
        }
      }

      if (ui.getType() == InteractionType.SEARCH && ui.getQueryText() != null) {
        for (String token : tokenize(ui.getQueryText())) {
          keywordWeight.merge(token, 1, Integer::sum);
        }
      }
    }

    List<ResearchPaper> all = paperRepository.findAll();
    List<ScoredPaper> scored = new ArrayList<>(all.size());
    for (ResearchPaper p : all) {
      if (alreadySeen.contains(p.getId())) {
        continue;
      }
      double s = 0.0;
      String d = normalize(p.getDomain());
      if (!d.isBlank()) {
        s += domainWeight.getOrDefault(d, 0) * 1.5;
      }
      for (String kw : splitKeywords(p.getKeywords())) {
        s += keywordWeight.getOrDefault(kw, 0) * 1.0;
      }

      if (s > 0) {
        scored.add(new ScoredPaper(p.getId(), s, "Matched your history (domain/keywords)"));
      }
    }

    scored.sort((a, b) -> Double.compare(b.score, a.score));
    List<RecommendationCandidate> out = new ArrayList<>();
    for (int i = 0; i < Math.min(limit, scored.size()); i++) {
      ScoredPaper sp = scored.get(i);
      out.add(new RecommendationCandidate(sp.paperId, sp.score, sp.reason));
    }
    if (out.isEmpty()) {
      return fallbackTopRecent(limit);
    }
    return out;
  }

  private List<RecommendationCandidate> fallbackTopRecent(int limit) {
    List<ResearchPaper> all = paperRepository.findAll();
    all.sort((a, b) -> b.getPublicationYear().compareTo(a.getPublicationYear()));
    List<RecommendationCandidate> out = new ArrayList<>();
    for (int i = 0; i < Math.min(limit, all.size()); i++) {
      ResearchPaper p = all.get(i);
      out.add(new RecommendationCandidate(p.getId(), 1.0, "Popular recent papers"));
    }
    return out;
  }

  private List<String> splitKeywords(String keywords) {
    if (keywords == null || keywords.isBlank()) {
      return List.of();
    }
    String[] parts = keywords.split(",");
    List<String> out = new ArrayList<>();
    for (String p : parts) {
      String k = normalize(p);
      if (!k.isBlank()) {
        out.add(k);
      }
    }
    return out;
  }

  private List<String> tokenize(String text) {
    if (text == null) return List.of();
    String[] parts = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").split("\\s+");
    List<String> out = new ArrayList<>();
    for (String p : parts) {
      String t = normalize(p);
      if (t.length() >= 3) {
        out.add(t);
      }
    }
    return out;
  }

  private String normalize(String s) {
    return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
  }

  private record ScoredPaper(Long paperId, double score, String reason) {}
}

