package com.rag.platform.service.citation;

import com.rag.platform.model.ResearchPaper;
import java.util.Objects;

public class IeeeCitationFormatter implements CitationFormatter {
  @Override
  public String format(ResearchPaper paper) {
    // Simplified IEEE: "A. Author, \"Title,\" Journal, Year. DOI/URL"
    String authors = safe(paper.getAuthors());
    String title = safe(paper.getTitle());
    String journal = safe(paper.getJournal());
    String year = paper.getPublicationYear() == null ? "" : paper.getPublicationYear().toString();
    String tail = paper.getDoi() != null && !paper.getDoi().isBlank()
        ? "doi: " + paper.getDoi().trim()
        : safe(paper.getUrl());

    String base = authors + ", \"" + title + ",\"";
    if (!journal.isBlank()) {
      base += " " + journal + ",";
    }
    if (!year.isBlank()) {
      base += " " + year + ".";
    }
    if (!tail.isBlank()) {
      base += " " + tail;
    }
    return base.trim();
  }

  private String safe(String s) {
    return Objects.requireNonNullElse(s, "").trim();
  }
}

