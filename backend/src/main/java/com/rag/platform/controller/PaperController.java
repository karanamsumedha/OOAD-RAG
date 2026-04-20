package com.rag.platform.controller;

import com.rag.platform.dto.common.PagedResponse;
import com.rag.platform.dto.paper.PaperCreateRequest;
import com.rag.platform.dto.paper.PaperResponse;
import com.rag.platform.service.PaperService;
import com.rag.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/papers")
public class PaperController {
  private final PaperService paperService;

  public PaperController(PaperService paperService) {
    this.paperService = paperService;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<PaperResponse>> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String domain,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) String author,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Long actor = SecurityUtil.currentUserIdOrNull();
    return ResponseEntity.ok(paperService.search(q, domain, year, author, page, size, actor));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaperResponse> get(@PathVariable Long id) {
    Long actor = SecurityUtil.currentUserIdOrNull();
    return ResponseEntity.ok(paperService.getById(id, actor));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
  public ResponseEntity<PaperResponse> create(@Valid @RequestBody PaperCreateRequest req) {
    return ResponseEntity.ok(paperService.create(req));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
  public ResponseEntity<PaperResponse> update(@PathVariable Long id, @Valid @RequestBody PaperCreateRequest req) {
    return ResponseEntity.ok(paperService.update(id, req));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('CURATOR','ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    paperService.delete(id);
    return ResponseEntity.noContent().build();
  }
}

