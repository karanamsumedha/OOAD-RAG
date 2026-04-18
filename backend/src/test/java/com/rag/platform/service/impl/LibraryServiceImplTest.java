package com.rag.platform.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.rag.platform.service.interaction.InteractionRecorder;
import com.rag.platform.util.ApiException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private ResearchPaperRepository paperRepository;
  @Mock
  private UserLibraryRepository libraryRepository;
  @Mock
  private InteractionRecorder interactionRecorder;

  private LibraryServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new LibraryServiceImpl(userRepository, paperRepository, libraryRepository, interactionRecorder);
  }

  @Test
  void addToLibrarySuccessRecordsInteraction() {
    User user = new User("User", "u@x.com", "hash");
    ReflectionTestUtils.setField(user, "id", 1L);

    ResearchPaper paper = new ResearchPaper(
        "Title",
        "Author",
        2024,
        "AI",
        "J",
        null,
        null,
        "abs",
        "k1,k2"
    );
    ReflectionTestUtils.setField(paper, "id", 10L);

    UserLibraryItem item = new UserLibraryItem(user, paper);
    ReflectionTestUtils.setField(item, "id", 100L);

    when(libraryRepository.findByUserIdAndPaperId(1L, 10L)).thenReturn(Optional.empty());
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(paperRepository.findById(10L)).thenReturn(Optional.of(paper));
    when(libraryRepository.save(any(UserLibraryItem.class))).thenReturn(item);

    LibraryItemResponse response = service.addToLibrary(1L, new LibraryAddRequest(10L));

    assertThat(response.paperId()).isEqualTo(10L);
    assertThat(response.status()).isEqualTo(ReadingStatus.NOT_STARTED);
    verify(interactionRecorder).record(1L, 10L, InteractionType.SAVE, null);
  }

  @Test
  void addToLibraryDuplicateThrowsConflict() {
    User user = new User("User", "u@x.com", "hash");
    ReflectionTestUtils.setField(user, "id", 1L);
    ResearchPaper paper = new ResearchPaper(
        "Title",
        "Author",
        2024,
        "AI",
        "J",
        null,
        null,
        "abs",
        "k1,k2"
    );
    ReflectionTestUtils.setField(paper, "id", 10L);
    UserLibraryItem existing = new UserLibraryItem(user, paper);
    ReflectionTestUtils.setField(existing, "id", 101L);

    when(libraryRepository.findByUserIdAndPaperId(1L, 10L)).thenReturn(Optional.of(existing));

    assertThatThrownBy(() -> service.addToLibrary(1L, new LibraryAddRequest(10L)))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("already in library");
  }

  @Test
  void updateProgressTo100MarksCompleted() {
    User user = new User("User", "u@x.com", "hash");
    ReflectionTestUtils.setField(user, "id", 1L);
    ResearchPaper paper = new ResearchPaper("Title", "Author", 2024, "AI", "J", null, null, "abs", "k1,k2");
    ReflectionTestUtils.setField(paper, "id", 10L);
    UserLibraryItem item = new UserLibraryItem(user, paper);
    ReflectionTestUtils.setField(item, "id", 55L);

    when(libraryRepository.findById(55L)).thenReturn(Optional.of(item));

    LibraryItemResponse res = service.updateProgress(
        1L,
        new LibraryProgressUpdateRequest(55L, ReadingStatus.IN_PROGRESS, 100)
    );

    assertThat(res.progressPercent()).isEqualTo(100);
    assertThat(res.status()).isEqualTo(ReadingStatus.COMPLETED);
  }
}
