package com.rag.platform.service.impl;

import com.rag.platform.dto.citation.CitationGenerateRequest;
import com.rag.platform.dto.citation.CitationResponse;
import com.rag.platform.model.Citation;
import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.User;
import com.rag.platform.repository.CitationRepository;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.service.CitationService;
import com.rag.platform.service.citation.CitationFormatter;
import com.rag.platform.service.citation.CitationFormatterFactory;
import com.rag.platform.service.interaction.InteractionRecorder;
import com.rag.platform.util.ApiException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CitationServiceImpl implements CitationService {
  private final UserRepository userRepository;
  private final ResearchPaperRepository paperRepository;
  private final CitationRepository citationRepository;
  private final InteractionRecorder interactionRecorder;
  private final CitationFormatterFactory formatterFactory = new CitationFormatterFactory();

  public CitationServiceImpl(
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      CitationRepository citationRepository,
      InteractionRecorder interactionRecorder
  ) {
    this.userRepository = userRepository;
    this.paperRepository = paperRepository;
    this.citationRepository = citationRepository;
    this.interactionRecorder = interactionRecorder;
  }

  @Override
  @Transactional
  public CitationResponse generate(Long userId, CitationGenerateRequest req) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    ResearchPaper paper = paperRepository.findById(req.paperId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paper not found"));

    CitationFormatter formatter = formatterFactory.create(req.format());
    String rendered = formatter.format(paper);

    Citation saved = citationRepository.save(new Citation(user, paper, req.format(), rendered));
    interactionRecorder.record(userId, paper.getId(), InteractionType.CITE, null);
    return toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CitationResponse> listMyCitations(Long userId) {
    return citationRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream().map(CitationServiceImpl::toResponse).toList();
  }

  private static CitationResponse toResponse(Citation c) {
    return new CitationResponse(
        c.getId(),
        c.getPaper().getId(),
        c.getFormat(),
        c.getRenderedText(),
        c.getCreatedAt()
    );
  }
}

