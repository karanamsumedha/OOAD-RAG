package com.rag.platform.service;

import com.rag.platform.dto.library.LibraryAddRequest;
import com.rag.platform.dto.library.LibraryItemResponse;
import com.rag.platform.dto.library.LibraryProgressUpdateRequest;
import java.util.List;

public interface LibraryService {
  LibraryItemResponse addToLibrary(Long userId, LibraryAddRequest req);

  List<LibraryItemResponse> getLibrary(Long userId);

  LibraryItemResponse updateProgress(Long userId, LibraryProgressUpdateRequest req);
}

