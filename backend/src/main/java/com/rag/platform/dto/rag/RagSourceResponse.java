package com.rag.platform.dto.rag;

public record RagSourceResponse(
    Long paperId,
    String title,
    String authors,
    Integer publicationYear,
    String domain,
    String url
) {}

