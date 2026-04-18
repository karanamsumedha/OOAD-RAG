package com.rag.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(
    name = "citations",
    indexes = {
        @Index(name = "idx_citations_user", columnList = "user_id"),
        @Index(name = "idx_citations_paper", columnList = "paper_id")
    }
)
public class Citation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "paper_id", nullable = false)
  private ResearchPaper paper;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private CitationFormat format;

  @Column(nullable = false, columnDefinition = "text")
  private String renderedText;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected Citation() {}

  public Citation(User user, ResearchPaper paper, CitationFormat format, String renderedText) {
    this.user = user;
    this.paper = paper;
    this.format = format;
    this.renderedText = renderedText;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public ResearchPaper getPaper() {
    return paper;
  }

  public CitationFormat getFormat() {
    return format;
  }

  public String getRenderedText() {
    return renderedText;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

