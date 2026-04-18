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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
    name = "feedback",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_feedback_user_paper", columnNames = {"user_id", "paper_id"})
    },
    indexes = {
        @Index(name = "idx_feedback_paper", columnList = "paper_id")
    }
)
public class Feedback {
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
  private Integer rating; // 1..5

  @Column(columnDefinition = "text")
  private String commentText;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected Feedback() {}

  public Feedback(User user, ResearchPaper paper, Integer rating, String commentText) {
    this.user = user;
    this.paper = paper;
    this.rating = rating;
    this.commentText = commentText;
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

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getCommentText() {
    return commentText;
  }

  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

