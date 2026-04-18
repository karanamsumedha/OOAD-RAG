package com.rag.platform.repository;

import com.rag.platform.model.UserLibraryItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLibraryRepository extends JpaRepository<UserLibraryItem, Long> {
  List<UserLibraryItem> findByUserIdOrderBySavedAtDesc(Long userId);

  Optional<UserLibraryItem> findByUserIdAndPaperId(Long userId, Long paperId);
}

