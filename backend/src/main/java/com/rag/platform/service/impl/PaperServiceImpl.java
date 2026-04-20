package com.rag.platform.service.impl;

import com.rag.platform.dto.common.PagedResponse;
import com.rag.platform.dto.paper.PaperCreateRequest;
import com.rag.platform.dto.paper.PaperResponse;
import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.UserInteraction;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserInteractionRepository;
import com.rag.platform.service.PaperService;
import com.rag.platform.service.interaction.InteractionRecorder;
import com.rag.platform.util.ApiException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaperServiceImpl implements PaperService {
  private final ResearchPaperRepository paperRepository;
  private final UserInteractionRepository interactionRepository;
  private final InteractionRecorder interactionRecorder;

  public PaperServiceImpl(
      ResearchPaperRepository paperRepository,
      UserInteractionRepository interactionRepository,
      InteractionRecorder interactionRecorder
  ) {
    this.paperRepository = paperRepository;
    this.interactionRepository = interactionRepository;
    this.interactionRecorder = interactionRecorder;
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<PaperResponse> search(
      String q,
      String domain,
      Integer year,
      String author,
      int page,
      int size,
      Long actorUserId
  ) {
    if (actorUserId != null && q != null && !q.isBlank()) {
      interactionRecorder.record(actorUserId, null, InteractionType.SEARCH, q);
    }

    int pageNumber = Math.max(page, 0);
    int pageSize = Math.min(Math.max(size, 1), 100);
    String qValue = blankToEmpty(q);
    String domainValue = blankToEmpty(domain);
    String authorValue = blankToEmpty(author);
    boolean hasQ = !qValue.isBlank();
    boolean hasDomain = !domainValue.isBlank();
    boolean hasYear = year != null;
    boolean hasAuthor = !authorValue.isBlank();

    // Fetch a broad candidate set, then intelligently re-rank by relevance + user history.
    Page<ResearchPaper> result = paperRepository.search(
        qValue,
        domainValue,
        year,
        authorValue,
        hasQ,
        hasDomain,
        hasYear,
        hasAuthor,
        PageRequest.of(0, 500)
    );

    List<ResearchPaper> ranked = rankIntelligently(
        result.getContent(),
        qValue,
        domainValue,
        year,
        authorValue,
        actorUserId
    );

    int totalElements = ranked.size();
    int from = Math.min(pageNumber * pageSize, totalElements);
    int to = Math.min(from + pageSize, totalElements);
    List<ResearchPaper> pageSlice = ranked.subList(from, to);

    int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
    return new PagedResponse<>(
        pageSlice.stream().map(PaperServiceImpl::toResponse).toList(),
        pageNumber,
        pageSize,
        totalElements,
        totalPages
    );
  }

  @Override
  @Transactional(readOnly = true)
  public PaperResponse getById(Long id, Long actorUserId) {
    ResearchPaper p = paperRepository.findById(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paper not found"));
    if (actorUserId != null) {
      interactionRecorder.record(actorUserId, id, InteractionType.VIEW, null);
    }
    return toResponse(p);
  }

  @Override
  @Transactional
  public PaperResponse create(PaperCreateRequest req) {
    ResearchPaper p = new ResearchPaper(
        req.title(),
        req.authors(),
        req.publicationYear(),
        req.domain(),
        req.journal(),
        req.doi(),
        req.url(),
        req.abstractText(),
        req.keywords()
    );
    return toResponse(paperRepository.save(p));
  }

  @Override
  @Transactional
  public PaperResponse update(Long id, PaperCreateRequest req) {
    ResearchPaper p = paperRepository.findById(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paper not found"));
    p.setTitle(req.title());
    p.setAuthors(req.authors());
    p.setPublicationYear(req.publicationYear());
    p.setDomain(req.domain());
    p.setJournal(req.journal());
    p.setDoi(req.doi());
    p.setUrl(req.url());
    p.setAbstractText(req.abstractText());
    p.setKeywords(req.keywords());
    return toResponse(p);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!paperRepository.existsById(id)) {
      throw new ApiException(HttpStatus.NOT_FOUND, "Paper not found");
    }
    try {
      paperRepository.deleteById(id);
    } catch (Exception e) {
      throw new ApiException(HttpStatus.CONFLICT, "Paper cannot be deleted (referenced by other records)");
    }
  }

  private static PaperResponse toResponse(ResearchPaper p) {
    return new PaperResponse(
        p.getId(),
        p.getTitle(),
        p.getAuthors(),
        p.getPublicationYear(),
        p.getDomain(),
        p.getJournal(),
        p.getDoi(),
        p.getUrl(),
        p.getAbstractText(),
        p.getKeywords()
    );
  }

  private String blankToEmpty(String s) {
    return (s == null || s.isBlank()) ? "" : s;
  }

  private List<ResearchPaper> rankIntelligently(
      List<ResearchPaper> candidates,
      String q,
      String domain,
      Integer year,
      String author,
      Long actorUserId
  ) {
    Map<String, Integer> domainAffinity = new HashMap<>();
    Map<String, Integer> keywordAffinity = new HashMap<>();
    if (actorUserId != null) {
      List<UserInteraction> interactions = interactionRepository.findTop50ByUserIdOrderByCreatedAtDesc(actorUserId);
      for (UserInteraction ui : interactions) {
        int w = interactionWeight(ui.getType());
        if (ui.getPaper() != null) {
          domainAffinity.merge(normalize(ui.getPaper().getDomain()), w, Integer::sum);
          for (String kw : tokenize(ui.getPaper().getKeywords())) {
            keywordAffinity.merge(kw, w, Integer::sum);
          }
        }
        for (String t : tokenize(ui.getQueryText())) {
          keywordAffinity.merge(t, 1, Integer::sum);
        }
      }
    }

    List<ScoredPaper> scored = new ArrayList<>(candidates.size());
    for (ResearchPaper p : candidates) {
      double score = baseTextualScore(p, q, domain, year, author);
      score += personalizedBoost(p, domainAffinity, keywordAffinity);
      // Tie-break by recency with a small additive boost.
      score += (p.getPublicationYear() - 2000) * 0.02;
      scored.add(new ScoredPaper(p, score));
    }

    scored.sort(Comparator.comparingDouble(ScoredPaper::score).reversed());
    return scored.stream().map(ScoredPaper::paper).toList();
  }

  private double baseTextualScore(ResearchPaper p, String q, String domain, Integer year, String author) {
    double score = 0.0;
    Set<String> queryTokens = new HashSet<>(tokenize(q));
    String title = normalize(p.getTitle());
    String authors = normalize(p.getAuthors());
    String keywords = normalize(p.getKeywords());
    String abstractText = normalize(p.getAbstractText());

    for (String t : queryTokens) {
      if (title.contains(t)) score += 4.0;
      if (keywords.contains(t)) score += 3.5;
      if (abstractText.contains(t)) score += 2.0;
      if (authors.contains(t)) score += 1.0;
    }

    if (!domain.isBlank() && normalize(p.getDomain()).equals(normalize(domain))) {
      score += 4.0;
    }
    if (year != null && year.equals(p.getPublicationYear())) {
      score += 3.0;
    }
    if (!author.isBlank() && authors.contains(normalize(author))) {
      score += 2.5;
    }
    return score;
  }

  private double personalizedBoost(
      ResearchPaper p,
      Map<String, Integer> domainAffinity,
      Map<String, Integer> keywordAffinity
  ) {
    double boost = 0.0;
    boost += domainAffinity.getOrDefault(normalize(p.getDomain()), 0) * 0.6;
    for (String kw : tokenize(p.getKeywords())) {
      boost += keywordAffinity.getOrDefault(kw, 0) * 0.25;
    }
    return boost;
  }

  private int interactionWeight(InteractionType type) {
    return switch (type) {
      case SAVE -> 5;
      case RATE -> 4;
      case CITE -> 4;
      case VIEW -> 2;
      case SEARCH -> 1;
    };
  }

  private List<String> tokenize(String text) {
    String n = normalize(text);
    if (n.isBlank()) return List.of();
    String[] parts = n.replaceAll("[^a-z0-9 ]", " ").split("\\s+");
    List<String> out = new ArrayList<>();
    for (String p : parts) {
      if (p.length() >= 3) out.add(p);
    }
    return out;
  }

  private String normalize(String s) {
    return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
  }

  private record ScoredPaper(ResearchPaper paper, double score) {}
}

