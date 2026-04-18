package com.rag.platform.dto.paper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaperCreateRequest(
    @NotBlank @Size(max = 500) String title,
    @NotBlank @Size(max = 500) String authors,
    @NotNull Integer publicationYear,
    @NotBlank @Size(max = 200) String domain,
    @Size(max = 250) String journal,
    @Size(max = 120) String doi,
    @Size(max = 400) String url,
    String abstractText,
    @NotBlank @Size(max = 800) String keywords
) {}

