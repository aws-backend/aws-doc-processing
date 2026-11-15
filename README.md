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
├── aws-docproc-contracts/
│   └── src/main/java/
│       └── .../contracts/
│           ├── DocumentProcessingMessage.java
│           └── DocumentProcessingResult.java
│
├── aws-docproc-domain/
│   └── src/main/java/
│       └── .../domain/
│           ├── model/
│           │   ├── Document.java
│           │   └── DocumentStatus.java
│           ├── service/
│           │   └── DocumentProcessingService.java
│           └── exception/
│
├── aws-docproc-api/
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
├── aws-docproc-lambda/
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


## Зачем нужен MognoDB?
MongoDB нужен в нашем учебном проекте как локальная замена AWS DocumentDB.

Мы используем его, чтобы разрабатывать и тестировать приложение на компьютере, не подключаясь постоянно к AWS.

### MongoDB и AWS DocumentDB — какая связь?

### Local environment
Spring Boot Application
- Java 21 · Spring Data MongoDB

MongoDB
- Docker · localhost:27017

### AWS environment
Spring Boot Application
- Java 21 · Spring Data MongoDB

Amazon DocumentDB
- AWS managed database


Оба варианта используют MongoDB-compatible API и MongoDB Java Driver. Однако DocumentDB — не просто MongoDB в AWS: у него собственная реализация и есть отличия в поддерживаемых возможностях.

### Что именно мы храним в базе?
В нашем проекте Document Processing Platform база хранит документы и информацию об их обработке.

Например:
```json
{
  "_id": "673a1f...",
  "fileName": "invoice-2026.pdf",
  "contentType": "application/pdf",
  "status": "PENDING",
  "createdAt": "2026-11-15T10:00:00Z",
  "processedAt": null,
  "errorMessage": null
}
```

Основные статусы:
- PENDING — документ принят.
- PROCESSING — документ обрабатывается Lambda.
- COMPLETED — обработка завершена.
- FAILED — обработка завершилась ошибкой.

### Почему не использовать DocumentDB локально?
Потому что DocumentDB — управляемый AWS-сервис. Для его использования нужны AWS-инфраструктура, сеть, настройки безопасности и расходы.

MongoDB в Docker позволяет:
- разрабатывать без подключения к AWS;
- запускать интеграционные тесты;
- быстро проверять изменения;
- не тратить деньги на AWS во время локальной разработки.


### Какую архитектуру мы реализуем?
| Компонент | Local | AWS |
| :--- | :--- | :--- |
| **Application** | Spring Boot | ECS Fargate |
| **Database** | MongoDB Docker | DocumentDB |
| **Messaging** | LocalStack SQS (планируется) | AWS SQS |
| **Processing** | Локальная имитация / Lambda | AWS Lambda |
| **Secrets** | Local config | Secrets Manager |
| **Logs** | Console | CloudWatch |

Важный момент: MongoDB не является обязательным компонентом AWS-архитектуры. Это локальная инфраструктура для разработки. В AWS-профиле приложение должно подключаться непосредственно к DocumentDB.

Таким образом, мы сможем писать код один раз и запускать его в двух режимах: local и aws.