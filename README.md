# Helper Bot

Telegram бот на базе Spring Boot 3.5.13 и Java 21.

## Технологии
- **Java 21**
- **Spring Boot 3.5.13**
- **Gradle**
- **Telegram Bots Spring Boot Starter**
- **Lombok**

## Требования
- JDK 21 или выше
- Docker (опционально)

## Настройка
Перед запуском убедитесь, что в файле `src/main/resources/application.yml` настроены необходимые параметры (токен бота и имя).

## Запуск локально

Используйте Gradle Wrapper для сборки и запуска:

```bash
./gradlew bootRun
```

Или соберите JAR-файл:

```bash
./gradlew bootJar
java -jar build/libs/helper-bot-0.0.1-SNAPSHOT.jar
```

## Запуск через Docker

1. **Сборка образа:**
   ```bash
   docker build -t helper-bot:1.0 .
   ```

2. **Запуск с передачей токена через переменную окружения:**
   ```bash
   docker run -p 8080:8080 -e TELEGRAM_BOT_TOKEN=your_token_here helper-bot:1.0
   docker run -d -p 8080:8080 -e TELEGRAM_BOT_TOKEN=your_token_here helper-bot:1.0
   ```

## Структура проекта
- `src/main/java/org/job/bot` — логика Telegram бота.
- `src/main/java/org/job/config` — конфигурационные классы.
- `src/main/java/org/job/consumer` — обработка обновлений.
- `src/main/resources/application.yml` — настройки приложения.
