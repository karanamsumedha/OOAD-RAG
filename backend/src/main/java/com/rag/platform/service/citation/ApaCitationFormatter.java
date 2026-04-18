package com.rag.platform.service.citation;

import com.rag.platform.model.ResearchPaper;
import java.util.Objects;

public class ApaCitationFormatter implements CitationFormatter {
  @Override
  public String format(ResearchPaper paper) {
    // Simplified APA: "Authors. (Year). Title. Journal. DOI/URL"
    String authors = safe(paper.getAuthors());
    String year = paper.getPublicationYear() == null ? "n.d." : paper.getPublicationYear().toString();
    String title = safe(paper.getTitle());
    String journal = safe(paper.getJournal());
    String tail = paper.getDoi() != null && !paper.getDoi().isBlank()
        ? "https://doi.org/" + paper.getDoi().trim()
        : safe(paper.getUrl());

    String base = authors + ". (" + year + "). " + title + ".";
    if (!journal.isBlank()) {
      base += " " + journal + ".";
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

