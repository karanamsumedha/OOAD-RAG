package com.rag.platform.service;

import com.rag.platform.dto.auth.AuthResponse;
import com.rag.platform.dto.auth.LoginRequest;
import com.rag.platform.dto.auth.RegisterRequest;

public interface AuthService {
  AuthResponse register(RegisterRequest req);

  AuthResponse login(LoginRequest req);
}

