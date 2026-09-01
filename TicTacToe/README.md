# Tic-Tac-Toe Android Client (T04)

## Описание

Android-приложение для игры в крестики-нолики с поддержкой:
- Базовой авторизации (login/password)
- Регистрации новых пользователей
- Просмотра списка доступных игр
- Создания игры против компьютера или другого игрока
- Подключения к существующим играм
- Игрового процесса в реальном времени
- Локального кэширования данных (Room)

## Архитектура

### MVVM Pattern

Приложение построено по архитектурному паттерну **MVVM (Model-View-ViewModel)**:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌─────────────┐         ┌─────────────────────────┐    │
│  │    View     │ ◄─────► │      ViewModel          │    │
│  │ (Activity/  │  Data   │  (Business Logic +      │    │
│  │  Fragment)  │ Binding │   State Management)     │    │
│  └─────────────┘         └─────────────────────────┘    │
│                            │                              │
│                            │ Domain Models                │
│                            ▼                              │
├─────────────────────────────────────────────────────────┤
│                     Domain Layer                         │
│  ┌─────────────────────────────────────────────────┐    │
│  │              Repositories (Interfaces)          │    │
│  │         (AuthRepository, GameRepository,        │    │
│  │              UserRepository)                    │    │
│  └─────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────┤
│                      Data Layer                          │
│  ┌──────────────────┐      ┌────────────────────────┐   │
│  │   Room (Local)   │      │   Retrofit (Remote)    │   │
│  │   Entity Models  │      │   DTO Models           │   │
│  │   DAO Interfaces │      │   API Services         │   │
│  └──────────────────┘      └────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Три основных слоя

#### 1. Data Layer (`data.*`)

**Назначение:** Работа с источниками данных (локальная БД и сеть)

**Компоненты:**
- `data.local` — Room database, DAO, Entity модели
- `data.remote` — Retrofit API, DTO модели, Interceptor
- `data.mapper` — Мапперы между слоями (Dto↔Domain, Entity↔Domain)

**Особенности:**
- Basic Auth Interceptor автоматически добавляет заголовок `Authorization: Basic base64(login:password)`
- Singleton паттерн для Database и API через Dagger
- Асинхронные операции через RxJava

#### 2. Domain Layer (`domain.*`)

**Назначение:** Бизнес-логика приложения

**Компоненты:**
- `domain.model` — Чистые модели данных (User, Game, GameStatus и т.д.)
- `domain.repository` — Интерфейсы репозиториев
- `domain.usecase` — Репозитории с реализацией (AuthRepository, GameRepository, UserRepository)

**Особенности:**
- Не зависит от Android SDK
- Содержит бизнес-правила и валидацию
- Объединяет данные из разных источников (сеть + БД)

#### 3. Presentation Layer (`presentation.*`)

**Назначение:** UI и взаимодействие с пользователем

**Компоненты:**
- `presentation.ui` — Activities, Fragments, Adapters
- `presentation.viewmodel` — ViewModel для каждого экрана
- `presentation.model` — ViewData модели для UI

**Особенности:**
- Data Binding между View и ViewModel
- Команды вместо событий (UserAction)
- Поддержка portrait/landscape ориентаций
- Progress indicators для длительных операций

### Dependency Injection (Dagger 2)

```
┌─────────────────────────────────────────┐
│           ApplicationComponent          │
│  ┌─────────────────────────────────┐   │
│  │  AppModule                      │   │
│  │  - Database (singleton)         │   │
│  │  - API (singleton)              │   │
│  │  - Services                     │   │
│  └─────────────────────────────────┘   │
│  ┌─────────────────────────────────┐   │
│  │  RepositoryModule               │   │
│  │  - AuthRepository               │   │
│  │  - GameRepository               │   │
│  │  - UserRepository               │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

## Структура проекта

```
app/
├── src/main/java/ru/school21/tictactoe/
│   ├── data/
│   │   ├── local/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── GameDao.kt
│   │   │   │   └── CurrentUserDao.kt
│   │   │   └── entity/
│   │   │       ├── UserEntity.kt
│   │   │       ├── GameEntity.kt
│   │   │       └── CurrentUserEntity.kt
│   │   ├── remote/
│   │   │   ├── ApiClient.kt
│   │   │   ├── ApiService.kt
│   │   │   ├── AuthInterceptor.kt
│   │   │   └── dto/
│   │   │       ├── UserDto.kt
│   │   │       ├── GameDto.kt
│   │   │       ├── AuthRequestDto.kt
│   │   │       └── ...
│   │   └── mapper/
│   │       ├── DtoMapper.kt
│   │       └── EntityMapper.kt
│   ├── domain/
│   │   ├── model/
│   │   │   ├── User.kt
│   │   │   ├── Game.kt
│   │   │   ├── GameStatus.kt
│   │   │   └── Cell.kt
│   │   └── repository/
│   │       ├── AuthRepository.kt
│   │       ├── GameRepository.kt
│   │       └── UserRepository.kt
│   ├── presentation/
│   │   ├── ui/
│   │   │   ├── auth/
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   └── RegisterActivity.kt
│   │   │   ├── games/
│   │   │   │   ├── GamesListActivity.kt
│   │   │   │   ├── CreateGameActivity.kt
│   │   │   │   └── GameActivity.kt
│   │   │   └── adapter/
│   │   │       └── GamesAdapter.kt
│   │   ├── viewmodel/
│   │   │   ├── AuthViewModel.kt
│   │   │   ├── RegisterViewModel.kt
│   │   │   ├── GamesListViewModel.kt
│   │   │   └── GameViewModel.kt
│   │   └── model/
│   │       ├── UserViewData.kt
│   │       └── GameViewData.kt
│   ├── di/
│   │   ├── AppModule.kt
│   │   ├── RepositoryModule.kt
│   │   └── AppComponent.kt
│   └── TicTacToeApplication.kt
├── res/
│   ├── layout/
│   ├── values/
│   └── ...
└── build.gradle
```

## Сборка и запуск

### Требования

- Android Studio 2022.1.1 или новее
- JDK 11+
- Android SDK 32
- Эмулятор или устройство с Android 8.0+

### Шаг 1: Клонирование/создание проекта

1. Откройте Android Studio
2. `File → New → New Project`
3. Выберите "Empty Activity"
4. Укажите:
   - Name: `TicTacToe`
   - Package: `ru.school21.tictactoe`
   - Language: `Kotlin`
   - Minimum SDK: `API 26`
5. Скопируйте все файлы из этого репозитория в соответствующие папки

### Шаг 2: Настройка Gradle

Файлы `build.gradle` уже содержат все зависимости:
- Room 2.5.0
- Retrofit 2.9.0
- Gson 2.9.0
- Dagger 2.45
- RxJava 2 / RxAndroid 2
- Material 2

### Windows setup

Для запуска на Windows не используйте жёстко зашитые пути к SDK или JDK в репозитории. Android Studio создаст локальный файл `local.properties` автоматически, либо его можно создать вручную:

```properties
sdk.dir=C:\Users\<YOUR_USERNAME>\AppData\Local\Android\Sdk
```

Если вы используете другой JDK, можно явно задать JAVA_HOME в системной среде:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
```

Или при необходимости через переменные среды:

```powershell
setx ANDROID_HOME "%LOCALAPPDATA%\Android\Sdk"
setx ANDROID_SDK_ROOT "%LOCALAPPDATA%\Android\Sdk"
```

Далее запустите сборку из командной строки:

```powershell
./gradlew.bat assembleDebug
```

### Шаг 3: Настройка сервера

В файле `ApiClient.kt` укажите URL вашего backend:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/" // для эмулятора
// или
private const val BASE_URL = "http://localhost:8080/" // для localhost
```

**Важно:** Для эмулятора используйте `10.0.2.2` вместо `localhost`.

### Шаг 4: Запуск сервера

Убедитесь, что ваш Spring Boot сервер запущен:

```bash
./gradlew bootRun
```

Проверьте доступность:

```bash
curl -i http://localhost:8080/games
# Ожидается: 401 Unauthorized
```

### Шаг 5: Сборка приложения

```bash
./gradlew clean build
```

### Шаг 6: Запуск на эмуляторе

1. Создайте эмулятор (API 32 recommended)
2. Нажмите `Run` в Android Studio
3. Приложение установится и запустится

## Тестирование

### Сценарий 1: Регистрация и вход

1. Запустите приложение
2. Нажмите "Register"
3. Введите login/password, подтвердите пароль
4. Нажмите "Register" → переход на экран входа
5. Введите login/password
6. Нажмите "Login" → переход к списку игр

### Сценарий 2: Игра против компьютера

1. На экране игр нажмите "Create Game"
2. Выберите "Play vs Computer"
3. Дождитесь создания игры
4. Сделайте ход (тапните по клетке)
5. Компьютер ответит автоматически
6. Играйте до победы/ничьей

### Сценарий 3: PvP игра (два устройства)

**Устройство 1:**
1. Войдите как Player1
2. Создайте игру "Play vs Player"
3. Запомните UUID игры

**Устройство 2:**
1. Войдите как Player2
2. Найдите игру в списке по UUID
3. Нажмите на игру → присоединение
4. Играйте по очереди

### Проверка 401

1. Выйдите из аккаунта (кнопка Logout)
2. База данных очищается
3. Переход на экран авторизации

## API Endpoints

| Метод | Endpoint | Описание | Auth |
|-------|----------|----------|------|
| POST | `/auth/signup` | Регистрация | Нет |
| POST | `/auth/signin` | Вход | Basic |
| GET | `/games` | Список игр | Basic |
| POST | `/games` | Создать игру | Basic |
| GET | `/games/{id}` | Информация об игре | Basic |
| POST | `/games/{id}/join` | Присоединиться | Basic |
| POST | `/games/{id}/moves` | Сделать ход | Basic |
| GET | `/users/{id}` | Информация о пользователе | Basic |

## Модели данных

### DTO (Data Transfer Objects)

```kotlin
data class UserDto(
    val id: String,
    val login: String
)

data class GameDto(
    val id: String,
    val playerXId: String?,
    val playerOId: String?,
    val currentPlayerId: String?,
    val status: String,
    val board: Array<IntArray>,
    val vsComputer: Boolean
)
```

### Entity (Room)

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val login: String,
    val password: String
)

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val playerXId: String?,
    val playerOId: String?,
    val currentPlayerId: String?,
    val status: String,
    val board: String, // JSON
    val vsComputer: Boolean
)
```

### Domain Models

```kotlin
data class User(
    val id: String,
    val login: String,
    val password: String
)

data class Game(
    val id: String,
    val playerX: User?,
    val playerO: User?,
    val currentPlayer: User?,
    val status: GameStatus,
    val board: Array<CellArray>,
    val vsComputer: Boolean
)

enum class GameStatus {
    WAITING_FOR_PLAYERS,
    IN_PROGRESS,
    DRAW,
    WIN_X,
    WIN_O
}
```

### ViewData (UI)

```kotlin
data class UserViewData(
    val id: String,
    val login: String
)

data class GameViewData(
    val id: String,
    val playerXLogin: String?,
    val playerOLogin: String?,
    val currentPlayerLogin: String?,
    val statusText: String,
    val board: Array<IntArray>,
    val isMyTurn: Boolean,
    val isBoardEnabled: Boolean
)
```

## MVVM в деталях

### Data Binding

```xml
<!-- activity_login.xml -->
<layout>
    <data>
        <variable name="viewModel" type="AuthViewModel" />
    </data>
    
    <LinearLayout>
        <EditText
            android:text="@={viewModel.login}" />
        <EditText
            android:text="@={viewModel.password}"
            android:inputType="password" />
        <Button
            android:onClick="@{() -> viewModel.onLoginClick()}" />
    </LinearLayout>
</layout>
```

### ViewModel Commands

```kotlin
sealed class UserAction {
    object NavigateToRegister : UserAction()
    object NavigateToGames : UserAction()
    data class ShowError(val message: String) : UserAction()
    object ClearDatabase : UserAction()
}

// В View (Activity)
viewModel.userAction.observe(this) { action ->
    when (action) {
        is UserAction.NavigateToRegister -> {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        is UserAction.ShowError -> {
            Toast.makeText(this, action.message, Toast.LENGTH_SHORT).show()
        }
        // ...
    }
}
```

### Асинхронность (RxJava)

```kotlin
// Repository
fun login(login: String, password: String): Single<User> {
    return apiService.login(login, password)
        .flatMap { apiService.signIn(login, password) }
        .map { userId -> 
            val user = User(userId, login, password)
            userDao.insert(user.toEntity())
            user
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}
```

## Отладка

### Логирование

Включите логирование Retrofit в `ApiClient.kt`:

```kotlin
val logging = HttpLoggingInterceptor()
logging.level = HttpLoggingInterceptor.Level.BODY
```

### Проверка БД

```kotlin
// В любом месте кода
val users = database.userDao().getAll()
Log.d("DB", "Users: $users")
```

### Network Inspection

Android Studio → App Inspection → Network Inspector

## Возможные ошибки и решения

### 401 Unauthorized

- Проверьте логин/пароль
- Убедитесь, что сервер запущен
- Проверьте `AuthInterceptor` (правильность Base64 кодирования)

### Connection refused

- Для эмулятора: используйте `10.0.2.2` вместо `localhost`
- Проверьте, что сервер слушает правильный порт

### Room migration error

При изменении Entity увеличьте версию БД:

```kotlin
@Database(entities = [...], version = 2)
```

## Подготовка к защите

### Вопросы по архитектуре

1. **Почему MVVM?** — Разделение ответственности, тестируемость, Data Binding
2. **Зачем Dagger?** — Внедрение зависимостей, singleton, тестируемость
3. **Почему Room?** — Локальное кэширование, работа офлайн, типобезопасность
4. **Зачем мапперы?** — Разделение слоев, независимость domain от data

### Демонстрация

1. Покажите структуру проекта (пакеты data/domain/presentation)
2. Продемонстрируйте работу всех экранов
3. Объясните поток данных: View → ViewModel → Repository → API/DB
4. Покажите маппинг: Dto → Domain → ViewData

## Лицензия

Учебный проект для School 21 / 42.