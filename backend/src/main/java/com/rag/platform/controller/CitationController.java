package com.rag.platform.controller;

import com.rag.platform.dto.citation.CitationGenerateRequest;
import com.rag.platform.dto.citation.CitationResponse;
import com.rag.platform.service.CitationService;
import com.rag.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/citation")
public class CitationController {
  private final CitationService citationService;

  public CitationController(CitationService citationService) {
    this.citationService = citationService;
  }

  @PostMapping("/generate")
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<CitationResponse> generate(@Valid @RequestBody CitationGenerateRequest req) {
    Long userId = SecurityUtil.requireCurrentUserId();
    return ResponseEntity.ok(citationService.generate(userId, req));
  }

  @GetMapping("/my")
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<List<CitationResponse>> myCitations() {
    Long userId = SecurityUtil.requireCurrentUserId();
    return ResponseEntity.ok(citationService.listMyCitations(userId));
  }
}

