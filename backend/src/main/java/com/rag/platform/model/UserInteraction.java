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
    name = "user_interactions",
    indexes = {
        @Index(name = "idx_interactions_user", columnList = "user_id"),
        @Index(name = "idx_interactions_paper", columnList = "paper_id"),
        @Index(name = "idx_interactions_type", columnList = "type"),
        @Index(name = "idx_interactions_created", columnList = "createdAt")
    }
)
public class UserInteraction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "paper_id")
  private ResearchPaper paper;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private InteractionType type;

  @Column(length = 500)
  private String queryText; // for SEARCH interactions

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected UserInteraction() {}

  public UserInteraction(User user, ResearchPaper paper, InteractionType type, String queryText) {
    this.user = user;
    this.paper = paper;
    this.type = type;
    this.queryText = queryText;
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

  public InteractionType getType() {
    return type;
  }

  public String getQueryText() {
    return queryText;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

