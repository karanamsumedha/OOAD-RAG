package com.rag.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(
    name = "research_papers",
    indexes = {
        @Index(name = "idx_papers_domain", columnList = "domain"),
        @Index(name = "idx_papers_year", columnList = "publicationYear"),
        @Index(name = "idx_papers_author", columnList = "authors"),
        @Index(name = "idx_papers_title", columnList = "title")
    }
)
public class ResearchPaper {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 500)
  private String title;

  @Column(nullable = false, length = 500)
  private String authors; // stored as a single string for simplicity

  @Column(nullable = false)
  private Integer publicationYear;

  @Column(nullable = false, length = 200)
  private String domain;

  @Column(length = 250)
  private String journal;

  @Column(length = 120)
  private String doi;

  @Column(length = 400)
  private String url;

  @Column(columnDefinition = "text")
  private String abstractText;

  @Column(nullable = false, length = 800)
  private String keywords; // comma-separated; used for content-based recommendations

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected ResearchPaper() {}

  public ResearchPaper(
      String title,
      String authors,
      Integer publicationYear,
      String domain,
      String journal,
      String doi,
      String url,
      String abstractText,
      String keywords
  ) {
    this.title = title;
    this.authors = authors;
    this.publicationYear = publicationYear;
    this.domain = domain;
    this.journal = journal;
    this.doi = doi;
    this.url = url;
    this.abstractText = abstractText;
    this.keywords = keywords;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthors() {
    return authors;
  }

  public void setAuthors(String authors) {
    this.authors = authors;
  }

  public Integer getPublicationYear() {
    return publicationYear;
  }

  public void setPublicationYear(Integer publicationYear) {
    this.publicationYear = publicationYear;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getJournal() {
    return journal;
  }

  public void setJournal(String journal) {
    this.journal = journal;
  }

  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getAbstractText() {
    return abstractText;
  }

  public void setAbstractText(String abstractText) {
    this.abstractText = abstractText;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

