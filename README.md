# aws-doc-processing

AWS Console - https://eu-north-1.console.aws.amazon.com/console/home?region=eu-north-1

Git repo: https://github.com/aws-backend/aws-doc-processing

Git Issues: https://github.com/aws-backend/aws-doc-processing/issues

Git Project: https://github.com/orgs/aws-backend/projects/3


## 1. Архитектура проекта
Пользователь отправляет документ на обработку через REST API.

Например:
```http request
POST /api/v1/documents
Content-Type: application/json
```
```json
{
  "fileName": "invoice-2026.pdf",
  "contentType": "application/pdf",
  "content": "BASE64_ENCODED_CONTENT"
}
```

Система:
Принимает документ.
1. Сохраняет метаданные в DocumentDB.
2. Создаёт сообщение в SQS.
3. Возвращает HTTP 202 Accepted.
4. Lambda получает сообщение из SQS.
5. Lambda обрабатывает документ.
6. Обновляет статус документа в DocumentDB.
7. Если обработка не удалась, SQS повторяет доставку.
8. После исчерпания попыток сообщение попадает в DLQ.

Общая архитектура AWS

                         CLIENT
                            |
                            v
                 +---------------------+
                 |   ECS Fargate       |
                 |   Spring Boot API   |
                 +---------------------+
                     |             |
                     |             |
                     v             v
              +------------+  +-----------+
              | DocumentDB |  |    SQS    |
              |            |  |  Main     |
              +------------+  +-----------+
                                    |
                                    v
                            +---------------+
                            | AWS Lambda    |
                            | Java 21       |
                            +---------------+
                                    |
                                    v
                              +------------+
                              | DocumentDB |
                              +------------+

                    SQS Main
                       |
                 Max Receive Count
                       |
                       v
                 +-----------+
                 | SQS DLQ   |
                 +-----------+

       +--------------------+   +------------------+
       | Secrets Manager    |   | CloudWatch Logs  |
       +--------------------+   +------------------+


Важное архитектурное решение: Lambda вызывается не напрямую из Spring Boot через AWS SDK Lambda Client.

Вместо этого:

Spring Boot → SQS → Lambda

Lambda подключается к SQS через Event Source Mapping.

Это предпочтительнее для нашего сценария, потому что:
- SQS отвечает за доставку сообщений.
- Lambda обрабатывает сообщения асинхронно.
- SQS управляет повторными попытками.
- DLQ сохраняет сообщения, которые не удалось обработать.
- API не зависит от доступности Lambda в момент запроса.

При этом AWS SDK для SQS будет использоваться непосредственно в Spring Boot.

Зависимости:

```text
aws-docproc
│
├── aws-docproc-api
│   ├── aws-docproc-contracts
│   └── aws-docproc-domain
│
├── aws-docproc-contracts
│
├── aws-docproc-domain
│   └── aws-docproc-contracts
│
└── aws-docproc-lambda
├── aws-docproc-contracts
└── aws-docproc-domain`
```

Правила архитектуры:
1. domain не зависит от Spring Framework и AWS SDK.
2. contracts не содержит бизнес-логики.
3. api отвечает за REST и инфраструктурные адаптеры.
4. lambda отвечает за AWS Lambda runtime и интеграцию с SQS.
5. Бизнес-логика обработки документа находится в domain и переиспользуется API и Lambda.

Так мы избежим дублирования бизнес-логики.


## Структура каталогов
```text
aws-docproc/
│
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── .gitignore
├── docker-compose.yml
├── README.md
│
├── aws-docproc-contracts/
│   ├── build.gradle.kts
│   └── src/main/java/com/gmail/alexei28/awsdocproc/contracts/
│       ├── document/
│       │   ├── CreateDocumentRequest.java
│       │   ├── DocumentResponse.java
│       │   └── DocumentStatus.java
│       └── event/
│           └── DocumentProcessingEvent.java
│
├── aws-docproc-domain/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/java/com/gmail/alexei28/awsdocproc/domain/
│       │   ├── model/
│       │   │   └── Document.java
│       │   ├── exception/
│       │   │   ├── DocumentNotFoundException.java
│       │   │   └── DocumentProcessingException.java
│       │   ├── port/
│       │   │   └── DocumentRepository.java
│       │   └── service/
│       │       ├── DocumentService.java
│       │       └── DocumentProcessor.java
│       └── test/java/com/gmail/alexei28/awsdocproc/domain/
│
├── aws-docproc-api/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/com/gmail/alexei28/awsdocproc/api/
│       │   │   ├── AwsDocprocApplication.java
│       │   │   ├── config/
│       │   │   │   ├── MongoConfig.java
│       │   │   │   ├── AwsConfig.java
│       │   │   │   └── SchedulingConfig.java
│       │   │   ├── controller/
│       │   │   │   └── DocumentController.java
│       │   │   ├── service/
│       │   │   │   └── DocumentApplicationService.java
│       │   │   ├── persistence/
│       │   │   │   ├── DocumentMongoEntity.java
│       │   │   │   ├── OutboxMongoEntity.java
│       │   │   │   ├── SpringDataDocumentRepository.java
│       │   │   │   └── DocumentRepositoryAdapter.java
│       │   │   ├── messaging/
│       │   │   │   ├── SqsDocumentEventPublisher.java
│       │   │   │   └── OutboxPublisher.java
│       │   │   └── exception/
│       │   │       └── GlobalExceptionHandler.java
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-aws.yml
│       │       └── application-default.yml
│       └── test/
│
├── aws-docproc-lambda/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/java/com/gmail/alexei28/awsdocproc/lambda/
│       │   ├── DocumentProcessingLambda.java
│       │   ├── config/
│       │   │   └── LambdaMongoConfig.java
│       │   ├── persistence/
│       │   │   ├── LambdaDocumentMongoEntity.java
│       │   │   └── LambdaDocumentRepository.java
│       │   └── processor/
│       │       └── DocumentProcessingService.java
│       └── test/
│
├── local/
│   ├── localstack/
│   │   └── init-aws.sh
│   └── mongodb/
│       └── init-replica-set.js
│
└── infra/
    └── aws/
        ├── 01-network.sh
        ├── 02-documentdb.sh
        ├── 03-sqs.sh
        ├── 04-secrets-manager.sh
        ├── 05-iam.sh
        ├── 06-lambda.sh
        ├── 07-ecr.sh
        ├── 08-ecs.sh
        └── 09-monitoring.sh

```

- local/ — локальная инфраструктура.
- infra/aws/ — скрипты создания AWS-инфраструктуры.
- application-default.yml — локальная конфигурация.
- application-aws.yml — конфигурация AWS.
- Lambda и API имеют собственные точки входа.