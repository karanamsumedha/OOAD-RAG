package com.rag.platform.dto.citation;

import com.rag.platform.model.CitationFormat;
import java.time.Instant;

public record CitationResponse(
    Long id,
    Long paperId,
    CitationFormat format,
    String renderedText,
    Instant createdAt
) {}

