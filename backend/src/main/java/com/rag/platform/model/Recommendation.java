package com.rag.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    name = "recommendations",
    indexes = {
        @Index(name = "idx_reco_user", columnList = "user_id"),
        @Index(name = "idx_reco_score", columnList = "score")
    }
)
public class Recommendation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "paper_id", nullable = false)
  private ResearchPaper paper;

  @Column(nullable = false)
  private Double score;

  @Column(nullable = false, length = 120)
  private String reason;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected Recommendation() {}

  public Recommendation(User user, ResearchPaper paper, Double score, String reason) {
    this.user = user;
    this.paper = paper;
    this.score = score;
    this.reason = reason;
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

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public String getReason() {
    return reason;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

