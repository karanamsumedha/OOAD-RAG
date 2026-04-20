package com.rag.platform.dto.rag;

import java.util.List;

public record RagQueryResponse(
    String question,
    String answer,
    List<RagSourceResponse> sources
) {}

