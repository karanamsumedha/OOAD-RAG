package com.rag.platform.dto.library;

import jakarta.validation.constraints.NotNull;

public record LibraryAddRequest(@NotNull Long paperId) {}

