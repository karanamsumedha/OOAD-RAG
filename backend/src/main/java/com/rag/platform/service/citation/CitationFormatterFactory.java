package com.rag.platform.service.citation;

import com.rag.platform.model.CitationFormat;

/**
 * Factory pattern: central place that creates a formatter based on requested format.
 * This avoids spreading conditional object creation across the codebase.
 */
public class CitationFormatterFactory {
  public CitationFormatter create(CitationFormat format) {
    if (format == null) {
      throw new IllegalArgumentException("format must not be null");
    }
    return switch (format) {
      case APA -> new ApaCitationFormatter();
      case IEEE -> new IeeeCitationFormatter();
    };
  }
}

