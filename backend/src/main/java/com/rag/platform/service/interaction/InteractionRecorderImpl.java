package com.rag.platform.service.interaction;

import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.User;
import com.rag.platform.model.UserInteraction;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserInteractionRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.util.ApiException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteractionRecorderImpl implements InteractionRecorder {
  private final UserRepository userRepository;
  private final ResearchPaperRepository paperRepository;
  private final UserInteractionRepository interactionRepository;
  private final ApplicationEventPublisher events;

  public InteractionRecorderImpl(
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      UserInteractionRepository interactionRepository,
      ApplicationEventPublisher events
  ) {
    this.userRepository = userRepository;
    this.paperRepository = paperRepository;
    this.interactionRepository = interactionRepository;
    this.events = events;
  }

  /**
   * Uses REQUIRES_NEW so writes succeed even when invoked from {@code @Transactional(readOnly=true)}
   * service methods (e.g. paper search/view).
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void record(Long userId, Long paperId, InteractionType type, String queryText) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    ResearchPaper paper = null;
    if (paperId != null) {
      paper = paperRepository.findById(paperId)
          .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paper not found"));
    }

    interactionRepository.save(new UserInteraction(user, paper, type, queryText));
    events.publishEvent(new UserInteractionRecordedEvent(userId, paperId, type));
  }
}

