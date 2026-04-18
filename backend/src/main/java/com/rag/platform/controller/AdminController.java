package com.rag.platform.controller;

import com.rag.platform.dto.admin.UsageReportResponse;
import com.rag.platform.dto.admin.UserSummaryResponse;
import com.rag.platform.service.AdminService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserSummaryResponse>> users() {
    return ResponseEntity.ok(adminService.listUsers());
  }

  @GetMapping("/reports")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsageReportResponse> reports() {
    return ResponseEntity.ok(adminService.usageReport());
  }
}

