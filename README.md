# aws-doc-processing

AWS Console - https://eu-north-1.console.aws.amazon.com/console/home?region=eu-north-1

Git repo: https://github.com/aws-backend/aws-doc-processing

Git Issues: https://github.com/aws-backend/aws-doc-processing/issues

Git Project: https://github.com/orgs/aws-backend/projects/3


## Основной сценарий
1. Пользователь отправляет документ

   POST /api/v1/documents
2. Spring Boot API

   Валидирует запрос, генерирует ID и сохраняет документ со статусом PENDING.
3. AWS DocumentDB

   Хранит документы с гибкой JSON-структурой.
4. AWS SQS

   Получает сообщение о необходимости обработки документа.
5. AWS Lambda

   Получает сообщение из SQS, выполняет обработку и обновляет документ.
6. AWS DocumentDB

   Сохраняет результат обработки и статус COMPLETED либо FAILED.
7. Spring Boot API

   Позволяет получить актуальный статус документа.

Инфраструктура
- ECS Fargate — запуск Spring Boot контейнера.
- Secrets Manager — хранение credentials.
- CloudWatch Logs — логи Spring Boot и Lambda.
- SQS DLQ — изоляция сообщений, которые не удалось обработать.

## Пример бизнес-сценария
Пользователь отправляет:
```json
{
  "type": "INVOICE",
  "metadata": {
    "source": "ERP",
    "department": "FINANCE"
  },
  "payload": {
    "invoiceNumber": "INV-2026-001",
    "amount": 1500.50,
    "currency": "EUR",
    "items": [
      {
        "name": "Laptop",
        "quantity": 2,
        "price": 750.25
      }
    ]
  }
}
```
Система должна:
1. Сохранить документ.
2. Поставить задачу в очередь.
3. Асинхронно обработать документ.
4. Проверить бизнес-правила.
5. Сохранить результат.
6. Предоставить API для получения результата

## Архитектурные компоненты:
| Компонент | Ответственность |
| :--- | :--- |
| **Spring Boot** | REST API и бизнес-логика |
| **DocumentDB** | Хранение документов |
| **SQS** | Асинхронная доставка задач |
| **SQS DLQ** | Необработанные сообщения |
| **Lambda** | Обработка документов |
| **Secrets Manager** | Credentials и конфигурационные секреты |
| **ECS Fargate** | Запуск Spring Boot |
| **ECR** | Хранение Docker image |
| **CloudWatch Logs** | Централизованные логи |


Важное архитектурное решение: 

Lambda будет отдельным deployable-компонентом, но её исходный код будет находиться в том же Gradle multi-module проекте.
Это позволит использовать общие Java-модели и не дублировать контракты сообщений.

## Ответственность модулей
| Модуль | Тип | Spring Boot |
| :--- | :--- | :--- |
| **docproc-contracts** | Java library | Нет |
| **docproc-domain** | Java library | Нет |
| **docproc-api** | Application | Да |
| **docproc-lambda** | AWS Lambda | Нет |

Почему именно такое разделение:
- contracts — единый контракт между API и Lambda.
- domain — бизнес-правила не зависят от AWS.
- api — приложение, которое принимает запросы.
- lambda — независимый deployment artifact.

 Таким образом, API и Lambda можно собирать и разворачивать независим

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