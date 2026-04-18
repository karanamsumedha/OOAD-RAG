package com.rag.platform.service.external;

public record ExternalPaperMetadata(
    String title,
    String authors,
    Integer publicationYear,
    String journal,
    String url
) {}

