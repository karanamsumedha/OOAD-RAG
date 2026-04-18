# Database Schema

This project uses PostgreSQL (compatible with MySQL with minor syntax changes).

## Tables

### `roles`
- `id` BIGSERIAL PK
- `name` VARCHAR(32) UNIQUE NOT NULL

### `users`
- `id` BIGSERIAL PK
- `full_name` VARCHAR(120) NOT NULL
- `email` VARCHAR(190) UNIQUE NOT NULL
- `password_hash` VARCHAR(255) NOT NULL
- `created_at` TIMESTAMP NOT NULL

### `user_roles`
- `user_id` BIGINT FK -> `users.id`
- `role_id` BIGINT FK -> `roles.id`
- Composite PK (`user_id`, `role_id`)

### `research_papers`
- `id` BIGSERIAL PK
- `title` VARCHAR(500) NOT NULL
- `authors` VARCHAR(500) NOT NULL
- `publication_year` INT NOT NULL
- `domain` VARCHAR(200) NOT NULL
- `journal` VARCHAR(250)
- `doi` VARCHAR(120)
- `url` VARCHAR(400)
- `abstract_text` TEXT
- `keywords` VARCHAR(800) NOT NULL
- `created_at` TIMESTAMP NOT NULL

Indexes:
- `idx_papers_domain(domain)`
- `idx_papers_year(publication_year)`
- `idx_papers_author(authors)`
- `idx_papers_title(title)`

### `user_library`
- `id` BIGSERIAL PK
- `user_id` BIGINT FK -> `users.id` NOT NULL
- `paper_id` BIGINT FK -> `research_papers.id` NOT NULL
- `status` VARCHAR(20) NOT NULL (`NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`)
- `progress_percent` INT NOT NULL
- `saved_at` TIMESTAMP NOT NULL
- UNIQUE (`user_id`, `paper_id`)

Indexes:
- `idx_user_library_user(user_id)`
- `idx_user_library_paper(paper_id)`
- `idx_user_library_status(status)`

### `feedback`
- `id` BIGSERIAL PK
- `user_id` BIGINT FK -> `users.id` NOT NULL
- `paper_id` BIGINT FK -> `research_papers.id` NOT NULL
- `rating` INT NOT NULL (1..5)
- `comment_text` TEXT
- `created_at` TIMESTAMP NOT NULL
- UNIQUE (`user_id`, `paper_id`)

Index:
- `idx_feedback_paper(paper_id)`

### `user_interactions`
- `id` BIGSERIAL PK
- `user_id` BIGINT FK -> `users.id` NOT NULL
- `paper_id` BIGINT FK -> `research_papers.id` NULL (null for SEARCH)
- `type` VARCHAR(20) NOT NULL (`SEARCH`, `VIEW`, `SAVE`, `RATE`, `CITE`)
- `query_text` VARCHAR(500)
- `created_at` TIMESTAMP NOT NULL

Indexes:
- `idx_interactions_user(user_id)`
- `idx_interactions_paper(paper_id)`
- `idx_interactions_type(type)`
- `idx_interactions_created(created_at)`

### `recommendations`
- `id` BIGSERIAL PK
- `user_id` BIGINT FK -> `users.id` NOT NULL
- `paper_id` BIGINT FK -> `research_papers.id` NOT NULL
- `score` DOUBLE PRECISION NOT NULL
- `reason` VARCHAR(120) NOT NULL
- `created_at` TIMESTAMP NOT NULL

Indexes:
- `idx_reco_user(user_id)`
- `idx_reco_score(score)`

### `citations`
- `id` BIGSERIAL PK
- `user_id` BIGINT FK -> `users.id` NOT NULL
- `paper_id` BIGINT FK -> `research_papers.id` NOT NULL
- `format` VARCHAR(10) NOT NULL (`APA`, `IEEE`)
- `rendered_text` TEXT NOT NULL
- `created_at` TIMESTAMP NOT NULL

Indexes:
- `idx_citations_user(user_id)`
- `idx_citations_paper(paper_id)`

## Cardinality summary
- User : Role = Many-to-Many (`user_roles`)
- User : ResearchPaper = Many-to-Many through:
  - `user_library`
  - `feedback`
  - `recommendations`
  - `citations`
  - `user_interactions`
