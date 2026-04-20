package com.rag.platform.controller;

import com.rag.platform.dto.rag.RagQueryRequest;
import com.rag.platform.dto.rag.RagQueryResponse;
import com.rag.platform.service.RagService;
import com.rag.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {
  private final RagService ragService;

  public RagController(RagService ragService) {
    this.ragService = ragService;
  }

  @PostMapping("/query")
  public ResponseEntity<RagQueryResponse> query(@Valid @RequestBody RagQueryRequest req) {
    Long actor = SecurityUtil.currentUserIdOrNull();
    return ResponseEntity.ok(ragService.query(req.question(), actor));
  }
}

