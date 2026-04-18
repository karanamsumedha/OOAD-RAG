package com.rag.platform.controller;

import com.rag.platform.dto.library.LibraryAddRequest;
import com.rag.platform.dto.library.LibraryItemResponse;
import com.rag.platform.dto.library.LibraryProgressUpdateRequest;
import com.rag.platform.service.LibraryService;
import com.rag.platform.util.SecurityUtil;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
  private final LibraryService libraryService;

  public LibraryController(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @PostMapping("/add")
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<LibraryItemResponse> add(@Valid @RequestBody LibraryAddRequest req) {
    Long userId = SecurityUtil.requireCurrentUserId();
    return ResponseEntity.ok(libraryService.addToLibrary(userId, req));
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<List<LibraryItemResponse>> get(@PathVariable Long userId) {
    Long current = SecurityUtil.requireCurrentUserId();
    if (!current.equals(userId) && !SecurityUtil.hasRole("ROLE_ADMIN")) {
      // Researchers can only see their own library; admin can see any user's library.
      userId = current;
    }
    return ResponseEntity.ok(libraryService.getLibrary(userId));
  }

  @PutMapping("/progress")
  @PreAuthorize("hasAnyRole('RESEARCHER','CURATOR','ADMIN')")
  public ResponseEntity<LibraryItemResponse> updateProgress(@Valid @RequestBody LibraryProgressUpdateRequest req) {
    Long userId = SecurityUtil.requireCurrentUserId();
    return ResponseEntity.ok(libraryService.updateProgress(userId, req));
  }
}

