package com.rag.platform.repository;

import com.rag.platform.model.InteractionType;
import com.rag.platform.model.UserInteraction;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
  List<UserInteraction> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

  long countByCreatedAtAfter(Instant since);

  long countByType(InteractionType type);
}

