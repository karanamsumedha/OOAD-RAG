# API Samples

Base URL: `http://localhost:8080/api`

## 1) Register
`POST /register`

Request:
```json
{
  "fullName": "Alice Researcher",
  "email": "alice@example.com",
  "password": "Alice@1234"
}
```

Response:
```json
{
  "accessToken": "<jwt-token>",
  "tokenType": "Bearer",
  "userId": 4,
  "email": "alice@example.com",
  "fullName": "Alice Researcher",
  "roles": ["ROLE_RESEARCHER"]
}
```

## 2) Login
`POST /login`

Request:
```json
{
  "email": "user@demo.com",
  "password": "User@123"
}
```

Response:
```json
{
  "accessToken": "<jwt-token>",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "user@demo.com",
  "fullName": "Demo Researcher",
  "roles": ["ROLE_RESEARCHER"]
}
```

## 3) Search papers
`GET /papers?q=transformer&domain=Machine Learning&year=2017`

Response:
```json
[
  {
    "id": 1,
    "title": "Attention Is All You Need",
    "authors": "Vaswani et al.",
    "publicationYear": 2017,
    "domain": "Machine Learning",
    "journal": "NeurIPS",
    "doi": "10.5555/3295222.3295349",
    "url": "https://arxiv.org/abs/1706.03762",
    "abstractText": "Transformer architecture for sequence modeling.",
    "keywords": "transformer, attention, nlp"
  }
]
```

## 4) Add paper (curator/admin)
`POST /papers`

Headers: `Authorization: Bearer <token>`

Request:
```json
{
  "title": "A New Paper",
  "authors": "Jane Doe",
  "publicationYear": 2026,
  "domain": "Software Engineering",
  "journal": "SE Journal",
  "doi": "10.1000/newpaper",
  "url": "https://example.org/paper",
  "abstractText": "Paper abstract...",
  "keywords": "se, architecture, ooad"
}
```

## 5) Recommendations
`GET /recommendations/{userId}`

Headers: `Authorization: Bearer <token>`

Response:
```json
[
  {
    "paperId": 2,
    "title": "BERT: Pre-training of Deep Bidirectional Transformers",
    "authors": "Devlin et al.",
    "publicationYear": 2019,
    "domain": "Natural Language Processing",
    "score": 15.5,
    "reason": "Matched your history (domain/keywords)"
  }
]
```

## 6) Add to library
`POST /library/add`

Headers: `Authorization: Bearer <token>`

Request:
```json
{ "paperId": 1 }
```

Response:
```json
{
  "id": 9,
  "paperId": 1,
  "paperTitle": "Attention Is All You Need",
  "authors": "Vaswani et al.",
  "publicationYear": 2017,
  "domain": "Machine Learning",
  "status": "NOT_STARTED",
  "progressPercent": 0,
  "savedAt": "2026-04-18T09:22:10.115Z"
}
```

## 7) Generate citation
`POST /citation/generate`

Headers: `Authorization: Bearer <token>`

Request:
```json
{
  "paperId": 1,
  "format": "APA"
}
```

Response:
```json
{
  "id": 13,
  "paperId": 1,
  "format": "APA",
  "renderedText": "Vaswani et al.. (2017). Attention Is All You Need. NeurIPS. https://doi.org/10.5555/3295222.3295349",
  "createdAt": "2026-04-18T09:25:18.116Z"
}
```

## 8) Admin reports
`GET /admin/reports`

Headers: `Authorization: Bearer <admin-token>`

Response:
```json
{
  "totalUsers": 4,
  "totalPapers": 3,
  "totalLibraryItems": 6,
  "totalFeedback": 4,
  "totalInteractions": 28
}
```
