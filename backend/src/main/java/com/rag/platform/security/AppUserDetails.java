package com.rag.platform.security;

import com.rag.platform.model.Role;
import com.rag.platform.model.User;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Wraps domain {@link User} for Spring Security without leaking JPA entities into filters.
 */
public class AppUserDetails implements UserDetails {
  private final Long userId;
  private final String email;
  private final String passwordHash;
  private final Collection<? extends GrantedAuthority> authorities;

  public AppUserDetails(User user) {
    this.userId = user.getId();
    this.email = user.getEmail();
    this.passwordHash = user.getPasswordHash();
    this.authorities = user.getRoles().stream()
        .map(Role::getName)
        .map(Enum::name)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
  }

  public Long getUserId() {
    return userId;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
