# aws-doc-processing

AWS Console - https://eu-north-1.console.aws.amazon.com/console/home?region=eu-north-1

Git repo: https://github.com/aws-backend/aws-doc-processing

Git Issues: https://github.com/aws-backend/aws-doc-processing/issues

Git Project: https://github.com/orgs/aws-backend/projects/3

## Структура исходного кода
```text
aws-doc-processing/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
│
├── docproc-contracts/
│   └── src/main/java/
│       └── .../contracts/
│           ├── DocumentProcessingMessage.java
│           └── DocumentProcessingResult.java
│
├── docproc-domain/
│   └── src/main/java/
│       └── .../domain/
│           ├── model/
│           │   ├── Document.java
│           │   └── DocumentStatus.java
│           ├── service/
│           │   └── DocumentProcessingService.java
│           └── exception/
│
├── docproc-api/
│   └── src/main/
│       ├── java/.../api/
│       │   ├── DocumentApiApplication.java
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── service/
│       │   ├── repository/
│       │   ├── messaging/
│       │   ├── outbox/
│       │   └── exception/
│       └── resources/
│           ├── application.yml
│           ├── application-local.yml
│           └── application-aws.yml
│
├── docproc-lambda/
│   └── src/
│       ├── main/java/.../lambda/
│       │   ├── DocumentProcessingHandler.java
│       │   ├── DocumentProcessor.java
│       │   ├── DocumentRepository.java
│       │   └── config/
│       └── test/
│
└── infrastructure/
├── docker/
│   └── Dockerfile
├── local/
│   └── docker-compose.yml
└── terraform/
├── main.tf
├── variables.tf
├── networking.tf
├── documentdb.tf
├── sqs.tf
├── lambda.tf
├── ecs.tf
├── secrets.tf
└── iam.tf
```