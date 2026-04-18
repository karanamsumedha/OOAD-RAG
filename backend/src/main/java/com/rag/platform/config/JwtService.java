package com.rag.platform.config;

import com.rag.platform.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/**
 * Issues and parses JWT access tokens (jjwt 0.11.x API).
 */
@Service
public class JwtService {
  private final JwtProperties props;
  private final SecretKey signingKey;

  public JwtService(JwtProperties props) {
    this.props = props;
    byte[] keyBytes = props.secret().getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      byte[] padded = new byte[32];
      System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
      keyBytes = padded;
    }
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String issueToken(User user) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.accessTokenMinutes() * 60);
    List<String> roles = user.getRoles().stream().map(r -> r.getName().name()).sorted().toList();
    return Jwts.builder()
        .setIssuer(props.issuer())
        .setSubject(String.valueOf(user.getId()))
        .claim("email", user.getEmail())
        .claim("roles", roles)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(signingKey)
        .compact();
  }

  public Claims parseAndValidate(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
    if (!props.issuer().equals(claims.getIssuer())) {
      throw new io.jsonwebtoken.JwtException("Invalid issuer");
    }
    return claims;
  }

  public Long extractUserId(String token) {
    String sub = parseAndValidate(token).getSubject();
    return Long.parseLong(sub);
  }
}
