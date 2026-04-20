package com.rag.platform.dto.rag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RagQueryRequest(
    @NotBlank @Size(min = 3, max = 500) String question
) {}

