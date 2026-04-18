package com.rag.platform.controller;

import com.rag.platform.dto.feedback.FeedbackCreateRequest;
import com.rag.platform.service.FeedbackService;
import com.rag.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
  private final FeedbackService feedbackService;

  public FeedbackController(FeedbackService feedbackService) {
    this.feedbackService = feedbackService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<?> upsert(@Valid @RequestBody FeedbackCreateRequest req) {
    Long userId = SecurityUtil.requireCurrentUserId();
    feedbackService.upsertFeedback(userId, req);
    return ResponseEntity.noContent().build();
  }
}

