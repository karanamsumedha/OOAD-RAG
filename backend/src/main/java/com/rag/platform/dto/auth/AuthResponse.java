package com.rag.platform.dto.auth;

import java.util.List;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long userId,
    String email,
    String fullName,
    List<String> roles
) {}

