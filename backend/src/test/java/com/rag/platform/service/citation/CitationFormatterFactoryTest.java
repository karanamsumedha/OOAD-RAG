package com.rag.platform.service.citation;

import static org.assertj.core.api.Assertions.assertThat;

import com.rag.platform.model.CitationFormat;
import com.rag.platform.model.ResearchPaper;
import org.junit.jupiter.api.Test;

class CitationFormatterFactoryTest {
  @Test
  void createsApaAndIeee() {
    CitationFormatterFactory factory = new CitationFormatterFactory();
    ResearchPaper p = new ResearchPaper(
        "Test Title",
        "Doe, J.",
        2024,
        "AI",
        "Journal X",
        "10.1000/xyz",
        "https://example.com",
        "Abstract.",
        "ml, nlp"
    );
    assertThat(factory.create(CitationFormat.APA).format(p)).contains("2024").contains("Test Title");
    assertThat(factory.create(CitationFormat.IEEE).format(p)).contains("Test Title");
  }
}
