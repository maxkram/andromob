# Промежуточный статус проекта Tic-Tac-Toe

## Объект исследования
- Сервер: [server](server)
- Android клиент: [TicTacToe](TicTacToe)

## Что уже есть

### Сервер
- Сервер реализован на Kotlin + Ktor + Exposed + PostgreSQL.
- Основная конфигурация и маршруты находятся в [server/main/kotlin/ru/school21/tictactoe/web/module/ApplicationModule.kt](server/main/kotlin/ru/school21/tictactoe/web/module/ApplicationModule.kt).
- Поддерживаются основные эндпоинты:
  - auth/signup
  - auth/signin
  - GET /games
  - POST /games
  - POST /games/{uuid}/join
  - GET /games/{uuid}
  - POST /games/{uuid}/move
- Регистрация и логин работают через базовую авторизацию и hash пароля.
- Сервер собирается успешно в текущем окружении.

### Android клиент
- Проект уже имеет структуру по слоям data/domain/presentation.
- Включены зависимости: Room, Retrofit, Gson, Dagger, RxJava, SwipeRefreshLayout.
- Есть базовая DI-структура и репозитории для auth/game/user.
- Есть Activity/VM для логина и списка игр.
- Присутствуют entity/DAO/DTO/mapper, что соответствует задаче по clean architecture.

## Фактический статус сборки

### Сервер
Команда проверки:
```bash
cd /home/maxkram/JavaMobile/server && ./gradlew compileKotlin --no-daemon
```
Результат: BUILD SUCCESSFUL.

### Android
Проверка после настройки SDK и Java home:
```bash
cd /home/maxkram/JavaMobile/TicTacToe && ./gradlew app:assembleDebug --no-daemon
```
Результат: BUILD SUCCESSFUL in 49s.

### История блокера
До исправления сборка падала по двум причинам:
- неверный путь Java в [TicTacToe/gradle.properties](TicTacToe/gradle.properties)
- неверный sdk.dir в [TicTacToe/local.properties](TicTacToe/local.properties)

Исправления:
- [TicTacToe/gradle.properties](TicTacToe/gradle.properties) переведён на Linux-путь Java (/usr/lib/jvm/java-17-openjdk-amd64)
- [TicTacToe/local.properties](TicTacToe/local.properties) переведён на актуальный SDK путь /home/maxkram/Android/Sdk

## Текущий итог

1. Сервер компилируется успешно.
2. Android проект успешно собирается в debug-сборку.
3. Основной инфраструктурный набор (SDK/JDK/Gradle) настроен корректно.
4. Проект готов для дальнейшей проверки функциональности экрана и сетевого сценария в эмуляторе.

## Что важно для следующего агента
- Проверять логику приложения на реальных экранах и сценариях аутентификации/игры.
- При необходимости запускать эмулятор и проверять поведение UI.
- Сравнивать реализацию с требованиями ТЗ: login/register, games list, create/join game, board play, logout, auth 401 redirect.
- Для запуска на Windows использовать JDK 17 и Android SDK установленный через Android Studio. Не держать жёсткие Linux-пути в проекте.

## Windows compatibility note
- Android Studio создаёт/обновляет [TicTacToe/local.properties](TicTacToe/local.properties) автоматически.
- Для Windows правильная форма:
  ```properties
  sdk.dir=C:\Users\<YOUR_USERNAME>\AppData\Local\Android\Sdk
  ```
- Требуемая Java версия: JDK 17.
- Команда сборки:
  ```powershell
  ./gradlew.bat assembleDebug
  ```

## Краткая сводка
- Основная инфраструктура уже исправлена и подтверждена сборкой.
- Проект переведён в рабочее состояние для дальнейшей реализации/проверки пользовательских сценариев.
- Кроссплатформенная настройка для Windows задокументирована и готова к использованию.
