package com.rag.platform.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.rag.platform.model.Role;
import com.rag.platform.model.RoleName;
import com.rag.platform.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  @Test
  void issueAndParseTokenContainsExpectedClaims() {
    JwtProperties props = new JwtProperties(
        "change-me-to-a-long-random-secret-change-me-to-a-long-random-secret",
        "rag-platform",
        60
    );
    JwtService jwtService = new JwtService(props);

    User user = new User("Demo User", "demo@example.com", "hash");
    user.getRoles().add(new Role(RoleName.ROLE_RESEARCHER));
    ReflectionTestUtils.setField(user, "id", 42L);

    String token = jwtService.issueToken(user);
    Claims claims = jwtService.parseAndValidate(token);

    assertThat(claims.getIssuer()).isEqualTo("rag-platform");
    assertThat(claims.getSubject()).isEqualTo("42");
    assertThat(claims.get("email", String.class)).isEqualTo("demo@example.com");
    assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
  }
}
