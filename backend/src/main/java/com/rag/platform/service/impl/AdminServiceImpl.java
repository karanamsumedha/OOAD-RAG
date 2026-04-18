package com.rag.platform.service.impl;

import com.rag.platform.dto.admin.UsageReportResponse;
import com.rag.platform.dto.admin.UserSummaryResponse;
import com.rag.platform.model.User;
import com.rag.platform.repository.FeedbackRepository;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserInteractionRepository;
import com.rag.platform.repository.UserLibraryRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.service.AdminService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {
  private final UserRepository userRepository;
  private final ResearchPaperRepository paperRepository;
  private final UserLibraryRepository libraryRepository;
  private final FeedbackRepository feedbackRepository;
  private final UserInteractionRepository interactionRepository;

  public AdminServiceImpl(
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      UserLibraryRepository libraryRepository,
      FeedbackRepository feedbackRepository,
      UserInteractionRepository interactionRepository
  ) {
    this.userRepository = userRepository;
    this.paperRepository = paperRepository;
    this.libraryRepository = libraryRepository;
    this.feedbackRepository = feedbackRepository;
    this.interactionRepository = interactionRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryResponse> listUsers() {
    return userRepository.findAll().stream().map(AdminServiceImpl::toSummary).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public UsageReportResponse usageReport() {
    return new UsageReportResponse(
        userRepository.count(),
        paperRepository.count(),
        libraryRepository.count(),
        feedbackRepository.count(),
        interactionRepository.count()
    );
  }

  private static UserSummaryResponse toSummary(User u) {
    return new UserSummaryResponse(
        u.getId(),
        u.getFullName(),
        u.getEmail(),
        u.getRoles().stream().map(r -> r.getName().name()).sorted().toList(),
        u.getCreatedAt()
    );
  }
}

