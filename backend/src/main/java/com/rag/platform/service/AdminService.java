package com.rag.platform.service;

import com.rag.platform.dto.admin.UsageReportResponse;
import com.rag.platform.dto.admin.UserSummaryResponse;
import java.util.List;

public interface AdminService {
  List<UserSummaryResponse> listUsers();

  UsageReportResponse usageReport();
}

