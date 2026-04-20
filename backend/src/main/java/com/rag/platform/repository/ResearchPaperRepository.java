package com.rag.platform.repository;

import com.rag.platform.model.ResearchPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {
  @Query("""
      SELECT p FROM ResearchPaper p
      WHERE (
        (:hasQ = true AND (
          LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
          OR LOWER(p.authors) LIKE LOWER(CONCAT('%', :q, '%'))
          OR LOWER(p.keywords) LIKE LOWER(CONCAT('%', :q, '%'))
        ))
        OR (:hasDomain = true AND LOWER(p.domain) = LOWER(:domain))
        OR (:hasYear = true AND p.publicationYear = :year)
        OR (:hasAuthor = true AND LOWER(p.authors) LIKE LOWER(CONCAT('%', :author, '%')))
        OR (:hasQ = false AND :hasDomain = false AND :hasYear = false AND :hasAuthor = false)
      )
      ORDER BY p.publicationYear DESC, p.id DESC
      """)
  Page<ResearchPaper> search(
      @Param("q") String q,
      @Param("domain") String domain,
      @Param("year") Integer year,
      @Param("author") String author,
      @Param("hasQ") boolean hasQ,
      @Param("hasDomain") boolean hasDomain,
      @Param("hasYear") boolean hasYear,
      @Param("hasAuthor") boolean hasAuthor,
      Pageable pageable
  );
}

