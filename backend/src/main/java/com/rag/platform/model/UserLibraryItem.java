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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
    name = "user_library",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_library_user_paper", columnNames = {"user_id", "paper_id"})
    },
    indexes = {
        @Index(name = "idx_user_library_user", columnList = "user_id"),
        @Index(name = "idx_user_library_paper", columnList = "paper_id"),
        @Index(name = "idx_user_library_status", columnList = "status")
    }
)
public class UserLibraryItem {
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
  @Column(nullable = false, length = 20)
  private ReadingStatus status = ReadingStatus.NOT_STARTED;

  @Column(nullable = false)
  private Integer progressPercent = 0; // 0..100

  @Column(nullable = false)
  private Instant savedAt = Instant.now();

  protected UserLibraryItem() {}

  public UserLibraryItem(User user, ResearchPaper paper) {
    this.user = user;
    this.paper = paper;
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

  public ReadingStatus getStatus() {
    return status;
  }

  public void setStatus(ReadingStatus status) {
    this.status = status;
  }

  public Integer getProgressPercent() {
    return progressPercent;
  }

  public void setProgressPercent(Integer progressPercent) {
    this.progressPercent = progressPercent;
  }

  public Instant getSavedAt() {
    return savedAt;
  }
}

