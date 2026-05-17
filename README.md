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

Перед запуском убедитесь, что в файле `src/main/resources/application.yml` настроены необходимые параметры (токен бота и
имя).

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

3. **Запуск с передачей токена, профиля, proxy переменных:**
   ```bash
   docker run -d -p 8080:8080 -e TELEGRAM_BOT_TOKEN=your_token_here -e SPRING_PROFILES_ACTIVE=profile -e PROXY_HOST=host -e PROXY_PORT=port -e PROXY_USER=user -e PROXY_PASSWORD=password helper-bot:1.0
   ```

## Решение проблем с подключением (Telegram API)

Если бот не может подключиться к Telegram API (ошибки `Network unreachable` или `Connection timed out`), используйте
следующие решения:

### 1. Принудительное использование IPv4

Если сервер пытается использовать IPv6, который не настроен, добавьте переменную окружения:

```bash
JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"
```

### 2. Использование прокси (например, через 3x-ui)

Если доступ к Telegram заблокирован, настройте HTTP/SOCKS5 прокси.

**Настройка в Coolify/Docker:**
Добавьте переменные окружения (для прокси без авторизации):

```bash
JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true -Dhttps.proxyHost=IP_PROXY -Dhttps.proxyPort=PORT"
```

**Настройка в коде (с авторизацией):**
Обновите `TelegramClientConfig.java`, используя `OkHttpClient` с `Proxy` и `Authenticator`.

## Структура проекта

- `src/main/java/org/job/bot` — логика Telegram бота.
- `src/main/java/org/job/config` — конфигурационные классы.
- `src/main/java/org/job/consumer` — обработка обновлений.
- `src/main/resources/application.yml` — настройки приложения.
