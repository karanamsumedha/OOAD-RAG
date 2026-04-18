package com.rag.platform.service.impl;

import com.rag.platform.dto.feedback.FeedbackCreateRequest;
import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.User;
import com.rag.platform.repository.FeedbackRepository;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.service.FeedbackService;
import com.rag.platform.service.interaction.InteractionRecorder;
import com.rag.platform.util.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackServiceImpl implements FeedbackService {
  private final UserRepository userRepository;
  private final ResearchPaperRepository paperRepository;
  private final FeedbackRepository feedbackRepository;
  private final InteractionRecorder interactionRecorder;

  public FeedbackServiceImpl(
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      FeedbackRepository feedbackRepository,
      InteractionRecorder interactionRecorder
  ) {
    this.userRepository = userRepository;
    this.paperRepository = paperRepository;
    this.feedbackRepository = feedbackRepository;
    this.interactionRecorder = interactionRecorder;
  }

  @Override
  @Transactional
  public void upsertFeedback(Long userId, FeedbackCreateRequest req) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    ResearchPaper paper = paperRepository.findById(req.paperId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paper not found"));

    feedbackRepository.findByUserIdAndPaperId(userId, req.paperId())
        .ifPresentOrElse(existing -> {
          existing.setRating(req.rating());
          existing.setCommentText(req.commentText());
          feedbackRepository.save(existing);
        }, () -> feedbackRepository.save(new com.rag.platform.model.Feedback(user, paper, req.rating(), req.commentText())));

    interactionRecorder.record(userId, paper.getId(), InteractionType.RATE, null);
  }
}

