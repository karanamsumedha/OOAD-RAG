package com.rag.platform.repository;

import com.rag.platform.model.Citation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitationRepository extends JpaRepository<Citation, Long> {
  List<Citation> findByUserIdOrderByCreatedAtDesc(Long userId);
}

