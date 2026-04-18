# UML Diagram Artifacts

Use these Mermaid diagrams directly in your report/slides (or convert to UML tools).

## 1) Use Case Diagram (roles + interactions)

```mermaid
flowchart LR
  R[Researcher/Student]
  C[Content Curator]
  A[Admin]

  UC1((Register/Login))
  UC2((Search Papers))
  UC3((View Paper Details))
  UC4((Get Recommendations))
  UC5((Save to Library))
  UC6((Generate Citation APA/IEEE))
  UC7((Track Reading Progress))
  UC8((Give Feedback))
  UC9((Add/Update/Delete Paper))
  UC10((Manage Metadata))
  UC11((Manage Users))
  UC12((Monitor Usage))
  UC13((Generate Reports))

  R --> UC1
  R --> UC2
  R --> UC3
  R --> UC4
  R --> UC5
  R --> UC6
  R --> UC7
  R --> UC8

  C --> UC9
  C --> UC10
  C --> UC2
  C --> UC3

  A --> UC11
  A --> UC12
  A --> UC13
```

## 2) Class Diagram (core domain)

```mermaid
classDiagram
  class User {
    +Long id
    +String fullName
    +String email
    +String passwordHash
  }
  class Role {
    +Long id
    +RoleName name
  }
  class ResearchPaper {
    +Long id
    +String title
    +String authors
    +Integer publicationYear
    +String domain
    +String keywords
  }
  class UserLibraryItem {
    +Long id
    +ReadingStatus status
    +Integer progressPercent
  }
  class Feedback {
    +Long id
    +Integer rating
    +String commentText
  }
  class Citation {
    +Long id
    +CitationFormat format
    +String renderedText
  }
  class Recommendation {
    +Long id
    +Double score
    +String reason
  }
  class UserInteraction {
    +Long id
    +InteractionType type
    +String queryText
  }

  User "*" -- "*" Role : has
  User "1" -- "*" UserLibraryItem
  ResearchPaper "1" -- "*" UserLibraryItem
  User "1" -- "*" Feedback
  ResearchPaper "1" -- "*" Feedback
  User "1" -- "*" Citation
  ResearchPaper "1" -- "*" Citation
  User "1" -- "*" Recommendation
  ResearchPaper "1" -- "*" Recommendation
  User "1" -- "*" UserInteraction
  ResearchPaper "0..1" -- "*" UserInteraction
```

## 3) Activity Diagram (search to recommendation flow)

```mermaid
flowchart TD
  S([Start]) --> Q[Enter query/filter]
  Q --> P[Fetch papers]
  P --> V[View paper details]
  V --> D{Save / Rate / Cite?}
  D -- Yes --> I[Record interaction]
  I --> U[Observer triggers recommendation recompute]
  U --> R[Return personalized recommendations]
  D -- No --> E([End])
  R --> E
```

## 4) State Diagram (library item lifecycle)

```mermaid
stateDiagram-v2
  [*] --> NOT_STARTED : add to library
  NOT_STARTED --> IN_PROGRESS : start reading
  IN_PROGRESS --> COMPLETED : progress = 100%
  IN_PROGRESS --> NOT_STARTED : reset progress
  COMPLETED --> IN_PROGRESS : revisit/edit progress
```

## 5) Pattern mapping
- Factory: `CitationFormatterFactory`
- Strategy: `RecommendationStrategy` + `ContentBasedRecommendationStrategy`
- Observer: `UserInteractionRecordedEvent` + `RecommendationUpdateListener`
- Proxy: `CachingRecommendationServiceProxy`
- Adapter: `ExternalMetadataAdapter` + `CrossrefMetadataAdapter`
