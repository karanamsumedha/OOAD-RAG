package com.rag.platform.repository;

import com.rag.platform.model.ResearchPaper;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {
  @Query("""
      SELECT p FROM ResearchPaper p
      WHERE (:q IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(p.authors) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(p.keywords) LIKE LOWER(CONCAT('%', :q, '%')))
        AND (:domain IS NULL OR LOWER(p.domain) = LOWER(:domain))
        AND (:year IS NULL OR p.publicationYear = :year)
        AND (:author IS NULL OR LOWER(p.authors) LIKE LOWER(CONCAT('%', :author, '%')))
      ORDER BY p.publicationYear DESC, p.id DESC
      """)
  List<ResearchPaper> search(
      @Param("q") String q,
      @Param("domain") String domain,
      @Param("year") Integer year,
      @Param("author") String author
  );
}

