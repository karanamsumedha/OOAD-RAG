package com.rag.platform.service.impl;

import com.rag.platform.dto.rag.RagQueryResponse;
import com.rag.platform.dto.rag.RagSourceResponse;
import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.service.RagService;
import com.rag.platform.service.interaction.InteractionRecorder;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RagServiceImpl implements RagService {
  private final ResearchPaperRepository paperRepository;
  private final InteractionRecorder interactionRecorder;

  public RagServiceImpl(ResearchPaperRepository paperRepository, InteractionRecorder interactionRecorder) {
    this.paperRepository = paperRepository;
    this.interactionRecorder = interactionRecorder;
  }

  @Override
  @Transactional(readOnly = true)
  public RagQueryResponse query(String question, Long actorUserId) {
    String normalized = question.trim();
    if (actorUserId != null) {
      interactionRecorder.record(actorUserId, null, InteractionType.SEARCH, normalized);
    }

    List<ResearchPaper> hits = paperRepository.search(
        normalized,
        "",
        null,
        "",
        true,
        false,
        false,
        false,
        PageRequest.of(0, 5)
    ).getContent();

    String answer = buildAnswer(normalized, hits);
    List<RagSourceResponse> sources = hits.stream().map(p -> new RagSourceResponse(
        p.getId(),
        p.getTitle(),
        p.getAuthors(),
        p.getPublicationYear(),
        p.getDomain(),
        p.getUrl()
    )).toList();

    return new RagQueryResponse(normalized, answer, sources);
  }

  private String buildAnswer(String question, List<ResearchPaper> hits) {
    if (hits.isEmpty()) {
      return "No strong paper matches were found for your query. Try adding domain-specific terms or synonyms.";
    }
    String topSummary = hits.stream()
        .limit(3)
        .map(p -> {
          String abs = p.getAbstractText() == null ? "" : p.getAbstractText().trim();
          if (abs.length() > 180) {
            abs = abs.substring(0, 180) + "...";
          }
          return "- " + p.getTitle() + " (" + p.getPublicationYear() + "): " + abs;
        })
        .reduce((a, b) -> a + "\n" + b)
        .orElse("");

    return "Query: \"" + question.toLowerCase(Locale.ROOT) + "\"\n"
        + "Top retrieved papers and concise insights:\n"
        + topSummary
        + "\n\nUse the listed sources for full details and citation.";
  }
}

