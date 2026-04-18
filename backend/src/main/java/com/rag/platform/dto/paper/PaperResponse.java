package com.rag.platform.dto.paper;

public record PaperResponse(
    Long id,
    String title,
    String authors,
    Integer publicationYear,
    String domain,
    String journal,
    String doi,
    String url,
    String abstractText,
    String keywords
) {}

