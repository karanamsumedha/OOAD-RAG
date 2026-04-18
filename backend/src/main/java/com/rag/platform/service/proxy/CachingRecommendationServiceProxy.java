package com.rag.platform.service.proxy;

import com.rag.platform.dto.recommendation.RecommendationResponse;
import com.rag.platform.service.RecommendationService;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Proxy pattern: wraps recommendation service and adds caching without modifying core logic.
 * This keeps SRP and keeps the underlying service focused on business rules.
 */
@Service
@Primary
public class CachingRecommendationServiceProxy implements RecommendationService {
  private final RecommendationService delegate;

  private final Map<Long, CacheEntry> cache = new HashMap<>();
  private final Duration ttl = Duration.ofSeconds(30);

  public CachingRecommendationServiceProxy(com.rag.platform.service.impl.RecommendationServiceImpl delegate) {
    this.delegate = delegate;
  }

  @Override
  public synchronized List<RecommendationResponse> getRecommendationsForUser(Long userId) {
    CacheEntry ce = cache.get(userId);
    if (ce != null && !ce.isExpired(ttl)) {
      return ce.value;
    }
    List<RecommendationResponse> v = delegate.getRecommendationsForUser(userId);
    cache.put(userId, new CacheEntry(v, Instant.now()));
    return v;
  }

  @Override
  public synchronized void recomputeRecommendations(Long userId) {
    delegate.recomputeRecommendations(userId);
    cache.remove(userId);
  }

  private record CacheEntry(List<RecommendationResponse> value, Instant createdAt) {
    boolean isExpired(Duration ttl) {
      return createdAt.plus(ttl).isBefore(Instant.now());
    }
  }
}

