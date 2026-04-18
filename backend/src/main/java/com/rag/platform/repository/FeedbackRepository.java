package com.rag.platform.repository;

import com.rag.platform.model.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  Optional<Feedback> findByUserIdAndPaperId(Long userId, Long paperId);

  List<Feedback> findByPaperId(Long paperId);
}

