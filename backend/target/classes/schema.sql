-- Optional manual schema script for documentation/submission.
-- Runtime schema is managed by Hibernate (ddl-auto=update).

CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL REFERENCES users(id),
  role_id BIGINT NOT NULL REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS research_papers (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  authors VARCHAR(500) NOT NULL,
  publication_year INT NOT NULL,
  domain VARCHAR(200) NOT NULL,
  journal VARCHAR(250),
  doi VARCHAR(120),
  url VARCHAR(400),
  abstract_text TEXT,
  keywords VARCHAR(800) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_papers_domain ON research_papers(domain);
CREATE INDEX IF NOT EXISTS idx_papers_year ON research_papers(publication_year);
CREATE INDEX IF NOT EXISTS idx_papers_author ON research_papers(authors);
CREATE INDEX IF NOT EXISTS idx_papers_title ON research_papers(title);

CREATE TABLE IF NOT EXISTS user_library (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  paper_id BIGINT NOT NULL REFERENCES research_papers(id),
  status VARCHAR(20) NOT NULL,
  progress_percent INT NOT NULL,
  saved_at TIMESTAMP NOT NULL,
  UNIQUE (user_id, paper_id)
);

CREATE INDEX IF NOT EXISTS idx_user_library_user ON user_library(user_id);
CREATE INDEX IF NOT EXISTS idx_user_library_paper ON user_library(paper_id);
CREATE INDEX IF NOT EXISTS idx_user_library_status ON user_library(status);

CREATE TABLE IF NOT EXISTS feedback (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  paper_id BIGINT NOT NULL REFERENCES research_papers(id),
  rating INT NOT NULL,
  comment_text TEXT,
  created_at TIMESTAMP NOT NULL,
  UNIQUE (user_id, paper_id)
);

CREATE INDEX IF NOT EXISTS idx_feedback_paper ON feedback(paper_id);

CREATE TABLE IF NOT EXISTS user_interactions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  paper_id BIGINT REFERENCES research_papers(id),
  type VARCHAR(20) NOT NULL,
  query_text VARCHAR(500),
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_interactions_user ON user_interactions(user_id);
CREATE INDEX IF NOT EXISTS idx_interactions_paper ON user_interactions(paper_id);
CREATE INDEX IF NOT EXISTS idx_interactions_type ON user_interactions(type);
CREATE INDEX IF NOT EXISTS idx_interactions_created ON user_interactions(created_at);

CREATE TABLE IF NOT EXISTS recommendations (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  paper_id BIGINT NOT NULL REFERENCES research_papers(id),
  score DOUBLE PRECISION NOT NULL,
  reason VARCHAR(120) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reco_user ON recommendations(user_id);
CREATE INDEX IF NOT EXISTS idx_reco_score ON recommendations(score);

CREATE TABLE IF NOT EXISTS citations (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  paper_id BIGINT NOT NULL REFERENCES research_papers(id),
  format VARCHAR(10) NOT NULL,
  rendered_text TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_citations_user ON citations(user_id);
CREATE INDEX IF NOT EXISTS idx_citations_paper ON citations(paper_id);
