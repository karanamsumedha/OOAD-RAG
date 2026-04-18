package com.rag.platform.service.impl;

import com.rag.platform.dto.paper.PaperCreateRequest;
import com.rag.platform.dto.paper.PaperResponse;
import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.service.PaperService;
import com.rag.platform.service.interaction.InteractionRecorder;
import com.rag.platform.util.ApiException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaperServiceImpl implements PaperService {
  private final ResearchPaperRepository paperRepository;
  private final InteractionRecorder interactionRecorder;

  public PaperServiceImpl(ResearchPaperRepository paperRepository, InteractionRecorder interactionRecorder) {
    this.paperRepository = paperRepository;
    this.interactionRecorder = interactionRecorder;
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaperResponse> search(String q, String domain, Integer year, String author, Long actorUserId) {
    if (actorUserId != null && q != null && !q.isBlank()) {
      interactionRecorder.record(actorUserId, null, InteractionType.SEARCH, q);
    }
    return paperRepository.search(blankToNull(q), blankToNull(domain), year, blankToNull(author))
        .stream().map(PaperServiceImpl::toResponse).toList();
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

  private String blankToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}

