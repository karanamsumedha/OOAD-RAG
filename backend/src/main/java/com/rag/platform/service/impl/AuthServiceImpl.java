package com.rag.platform.service.impl;

import com.rag.platform.dto.auth.AuthResponse;
import com.rag.platform.dto.auth.LoginRequest;
import com.rag.platform.dto.auth.RegisterRequest;
import com.rag.platform.model.Role;
import com.rag.platform.model.RoleName;
import com.rag.platform.model.User;
import com.rag.platform.repository.RoleRepository;
import com.rag.platform.repository.UserRepository;
import com.rag.platform.service.AuthService;
import com.rag.platform.util.ApiException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final com.rag.platform.config.JwtService jwtService;

  public AuthServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      com.rag.platform.config.JwtService jwtService
  ) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest req) {
    if (userRepository.existsByEmail(req.email())) {
      throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
    }

    Role researcherRole = roleRepository.findByName(RoleName.ROLE_RESEARCHER)
        .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Roles not seeded"));

    User user = new User(req.fullName(), req.email(), passwordEncoder.encode(req.password()));
    user.getRoles().add(researcherRole);

    User saved = userRepository.save(user);
    String token = jwtService.issueToken(saved);

    return new AuthResponse(token, "Bearer", saved.getId(), saved.getEmail(), saved.getFullName(),
        saved.getRoles().stream().map(r -> r.getName().name()).sorted().toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest req) {
    User user = userRepository.findByEmail(req.email())
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String token = jwtService.issueToken(user);
    List<String> roles = user.getRoles().stream().map(r -> r.getName().name()).sorted().toList();
    return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName(), roles);
  }
}

