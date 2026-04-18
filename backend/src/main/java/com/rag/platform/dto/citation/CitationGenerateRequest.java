package com.rag.platform.dto.citation;

import com.rag.platform.model.CitationFormat;
import jakarta.validation.constraints.NotNull;

public record CitationGenerateRequest(
    @NotNull Long paperId,
    @NotNull CitationFormat format
) {}

