package com.rag.platform.service.external;

/**
 * Adapter pattern: hides third-party API shape behind a stable interface.
 * You can replace the implementation (Crossref, Semantic Scholar, etc.) without changing callers.
 */
public interface ExternalMetadataAdapter {
  ExternalPaperMetadata fetchByDoi(String doi);
}

