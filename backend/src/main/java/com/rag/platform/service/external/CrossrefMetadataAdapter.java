package com.rag.platform.service.external;

import org.springframework.stereotype.Component;

/**
 * Demo adapter. In production you would call Crossref REST APIs.
 * For OOAD evaluation, the key is the adapter abstraction.
 */
@Component
public class CrossrefMetadataAdapter implements ExternalMetadataAdapter {
  @Override
  public ExternalPaperMetadata fetchByDoi(String doi) {
    // Intentionally minimal: return empty metadata rather than hard-failing.
    return new ExternalPaperMetadata(null, null, null, null, null);
  }
}

