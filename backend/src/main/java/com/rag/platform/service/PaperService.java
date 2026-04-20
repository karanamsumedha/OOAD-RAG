package com.rag.platform.service;

import com.rag.platform.dto.paper.PaperCreateRequest;
import com.rag.platform.dto.paper.PaperResponse;
import com.rag.platform.dto.common.PagedResponse;

public interface PaperService {
  PagedResponse<PaperResponse> search(
      String q,
      String domain,
      Integer year,
      String author,
      int page,
      int size,
      Long actorUserId
  );

  PaperResponse getById(Long id, Long actorUserId);

  PaperResponse create(PaperCreateRequest req);

  PaperResponse update(Long id, PaperCreateRequest req);

  void delete(Long id);
}

