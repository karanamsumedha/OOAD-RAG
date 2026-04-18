package com.rag.platform.service;

import com.rag.platform.dto.feedback.FeedbackCreateRequest;

public interface FeedbackService {
  void upsertFeedback(Long userId, FeedbackCreateRequest req);
}

