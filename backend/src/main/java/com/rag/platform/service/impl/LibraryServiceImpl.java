package com.rag.platform.service.impl;

import com.rag.platform.dto.library.LibraryAddRequest;
import com.rag.platform.dto.library.LibraryItemResponse;
import com.rag.platform.dto.library.LibraryProgressUpdateRequest;
import com.rag.platform.model.InteractionType;
import com.rag.platform.model.ReadingStatus;
import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.User;
import com.rag.platform.model.UserLibraryItem;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.UserLibraryRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.service.LibraryService;
import com.rag.platform.service.interaction.InteractionRecorder;
import com.rag.platform.util.ApiException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryServiceImpl implements LibraryService {
  private final UserRepository userRepository;
  private final ResearchPaperRepository paperRepository;
  private final UserLibraryRepository libraryRepository;
  private final InteractionRecorder interactionRecorder;

  public LibraryServiceImpl(
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      UserLibraryRepository libraryRepository,
      InteractionRecorder interactionRecorder
  ) {
    this.userRepository = userRepository;
    this.paperRepository = paperRepository;
    this.libraryRepository = libraryRepository;
    this.interactionRecorder = interactionRecorder;
  }

  @Override
  @Transactional
  public LibraryItemResponse addToLibrary(Long userId, LibraryAddRequest req) {
    if (libraryRepository.findByUserIdAndPaperId(userId, req.paperId()).isPresent()) {
      throw new ApiException(HttpStatus.CONFLICT, "Paper already in library");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    ResearchPaper paper = paperRepository.findById(req.paperId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paper not found"));

    UserLibraryItem item = libraryRepository.save(new UserLibraryItem(user, paper));
    interactionRecorder.record(userId, paper.getId(), InteractionType.SAVE, null);
    return toResponse(item);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LibraryItemResponse> getLibrary(Long userId) {
    return libraryRepository.findByUserIdOrderBySavedAtDesc(userId)
        .stream().map(LibraryServiceImpl::toResponse).toList();
  }

  @Override
  @Transactional
  public LibraryItemResponse updateProgress(Long userId, LibraryProgressUpdateRequest req) {
    UserLibraryItem item = libraryRepository.findById(req.libraryItemId())
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Library item not found"));
    if (!item.getUser().getId().equals(userId)) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Cannot update another user's library");
    }

    if (req.progressPercent() < 0 || req.progressPercent() > 100) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Progress percent must be between 0 and 100");
    }

    ReadingStatus status = req.status();
    int progress = req.progressPercent();
    if (progress == 0 && status == ReadingStatus.IN_PROGRESS) {
      progress = 1;
    }
    if (progress == 100) {
      status = ReadingStatus.COMPLETED;
    }

    item.setProgressPercent(progress);
    item.setStatus(status);
    return toResponse(item);
  }

  private static LibraryItemResponse toResponse(UserLibraryItem item) {
    ResearchPaper p = item.getPaper();
    return new LibraryItemResponse(
        item.getId(),
        p.getId(),
        p.getTitle(),
        p.getAuthors(),
        p.getPublicationYear(),
        p.getDomain(),
        item.getStatus(),
        item.getProgressPercent(),
        item.getSavedAt()
    );
  }
}

