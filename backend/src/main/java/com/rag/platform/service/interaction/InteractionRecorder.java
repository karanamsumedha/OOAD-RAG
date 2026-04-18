package com.rag.platform.service.interaction;

import com.rag.platform.model.InteractionType;

public interface InteractionRecorder {
  void record(Long userId, Long paperId, InteractionType type, String queryText);
}

