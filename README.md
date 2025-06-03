## School Enrollment Microservice Architecture

This microservice handles student enrollment, stores data, and interacts with a mock CBSE API via Kafka and retry logic.

```mermaid
graph TD

%% Components
    User[User / API Request] --> EnrollService

    EnrollService[Enrollment Service] -->|Save Student Data| StudentDB[(Student DB)]
    EnrollService -->|Send Event| Kafka[(Kafka Broker)]

    Kafka --> CbseAdapter[CBSE Adapter Service]
    CbseAdapter -->|Call CBSE API| CBSEAPI[CBSE Mock API]
    CbseAdapter -->|Save Response| RetryDB[(Retry Task DB)]

    Scheduler[Retry Scheduler] -->|Check OPEN Tasks| RetryDB
    Scheduler -->|Retry CBSE API| CBSEAPI
    Scheduler -->|Update Response| RetryDB

%% Styling
    style EnrollService fill:#bbf,stroke:#333,stroke-width:2px
    style StudentDB fill:#eee,stroke:#333,stroke-width:1px
    style Kafka fill:#fcf,stroke:#333,stroke-width:1px
    style CbseAdapter fill:#cfc,stroke:#333,stroke-width:2px
    style CBSEAPI fill:#fff,stroke:#333,stroke-width:1px
    style RetryDB fill:#eee,stroke:#333,stroke-width:1px
    style Scheduler fill:#ff9,stroke:#333,stroke-width:2px
```