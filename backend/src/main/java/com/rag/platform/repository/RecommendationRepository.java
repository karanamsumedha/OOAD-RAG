package com.rag.platform.repository;

import com.rag.platform.model.Recommendation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
  List<Recommendation> findTop20ByUserIdOrderByScoreDescCreatedAtDesc(Long userId);

  @Modifying
  @Query("DELETE FROM Recommendation r WHERE r.user.id = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);
}

