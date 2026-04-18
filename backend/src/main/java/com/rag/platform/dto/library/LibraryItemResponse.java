package com.rag.platform.dto.library;

import com.rag.platform.model.ReadingStatus;
import java.time.Instant;

public record LibraryItemResponse(
    Long id,
    Long paperId,
    String paperTitle,
    String authors,
    Integer publicationYear,
    String domain,
    ReadingStatus status,
    Integer progressPercent,
    Instant savedAt
) {}

