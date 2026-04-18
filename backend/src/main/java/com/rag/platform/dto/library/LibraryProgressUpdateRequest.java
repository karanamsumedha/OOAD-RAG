package com.rag.platform.dto.library;

import com.rag.platform.model.ReadingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LibraryProgressUpdateRequest(
    @NotNull Long libraryItemId,
    @NotNull ReadingStatus status,
    @NotNull @Min(0) @Max(100) Integer progressPercent
) {}

