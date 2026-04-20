package com.rag.platform.service;

import com.rag.platform.dto.rag.RagQueryResponse;

public interface RagService {
  RagQueryResponse query(String question, Long actorUserId);
}

