package com.rag.platform.util;

import com.rag.platform.security.AppUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
  private SecurityUtil() {}

  public static Long requireCurrentUserId() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated() || !(a.getPrincipal() instanceof AppUserDetails ud)) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }
    return ud.getUserId();
  }

  public static Long currentUserIdOrNull() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated() || !(a.getPrincipal() instanceof AppUserDetails ud)) {
      return null;
    }
    return ud.getUserId();
  }

  public static boolean hasRole(String roleAuthority) {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated()) {
      return false;
    }
    for (GrantedAuthority ga : a.getAuthorities()) {
      if (ga.getAuthority().equals(roleAuthority)) {
        return true;
      }
    }
    return false;
  }
}
