package com.rag.platform.service;

import com.rag.platform.dto.citation.CitationGenerateRequest;
import com.rag.platform.dto.citation.CitationResponse;
import java.util.List;

public interface CitationService {
  CitationResponse generate(Long userId, CitationGenerateRequest req);

  List<CitationResponse> listMyCitations(Long userId);
}

