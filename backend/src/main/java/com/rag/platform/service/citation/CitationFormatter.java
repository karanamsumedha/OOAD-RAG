package com.rag.platform.service.citation;

import com.rag.platform.model.ResearchPaper;

public interface CitationFormatter {
  String format(ResearchPaper paper);
}

