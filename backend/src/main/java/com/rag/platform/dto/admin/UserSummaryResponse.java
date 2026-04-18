package com.rag.platform.dto.admin;

import java.time.Instant;
import java.util.List;

public record UserSummaryResponse(
    Long id,
    String fullName,
    String email,
    List<String> roles,
    Instant createdAt
) {}

