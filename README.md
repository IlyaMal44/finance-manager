# Finance Manager
Система управления личными финансами с бюджетированием и аналитикой.

## Функциональность
- Регистрация и аутентификация пользователей
- Учет доходов и расходов по категориям
- Установка бюджетных лимитов по категориям
- Детальная статистика и аналитика
- Переводы между пользователями
- Уведомления о превышении бюджетов
- Экспорт отчетов в JSON
---
## Технологии
- Java 17
- Spring Boot 3.x
- H2 Database
- JPA/Hibernate
- Lombok
- Spring Security (только PasswordEncoder)
- Gradle
---
### Требования:
- Java 17+
- Gradle
---
## Запуск приложения и тестов:
#### Запуск приложения:
1) **Через IDE (IntelliJ)**: Откройте `FinanceManagerApplication.java` и нажмите `Run`
2) **Через консоль**:
   ```bash
   ./gradlew build
   java -jar build/libs/finance-manager-0.0.1-SNAPSHOT.jar
#### Запуск тестов:
1) Через IDE (IntelliJ): Нажмите Run на пакете test → Произойдет запуск всех тестов
2) Через коносоль: (`./gradlew test`)
----
## Архитектура

1. **cli/** - CommandLineInterface (CLI команды)
2. **config/** - SecurityConfig (PasswordEncoder)
3. **controller/** - AuthController, FinanceController (REST API)
4. **domain/dto/** - Data Transfer Objects (User, Transaction, Budget, Statistics)
5. **domain/entity/** - UserEntity, WalletEntity, TransactionEntity, BudgetEntity (JPA сущности)
6. **domain/repository/** - UserRepository, WalletRepository, TransactionRepository, BudgetRepository (доступ к БД)
7. **domain/mapper/** -  мапперы DTO/Entity
8. **domain/exception/** - кастомные исключения
9. **service/** - UserService, FinanceService, NotificationService, ExportService (бизнес-логика)
10. **test/** - тесты (JUnit 5 + Mockito)
11. **resources/application.yml** - конфигурация приложения
12. **.github/workflows/build.yml** - CI/CD конфигурация

----

## CI/CD (GitHub Actions)

Проект настроен с автоматической системой непрерывной интеграции:

### Автоматические проверки:
- **При каждом push** и **pull request**
- **Сборка и тестирование** на Ubuntu + Java 17
- **Проверка качества кода**

### Workflow этапы:
1. **Checkout** - получение кода из репозитория
2. **JDK 17** - установка Java окружения
3. **Permissions** - настройка прав для Gradle Wrapper
4. **Tests** - запуск всех unit-тестов (`./gradlew test`)
5. **Build** - сборка приложения (`./gradlew build`)

### Файл конфигурации:
`.github/workflows/build.yml`

### Мониторинг:
- Детальные логи доступны в Actions tab
- Уведомления о failed builds
----

## API Endpoints

1. POST `/api/auth/register` - Регистрация пользователя
---
2. POST `/api/auth/login` - Аутентификация пользователя
---
3. POST `/api/finance/{walletId}/transaction` - Добавление финансовой операции

Параметры:
 - walletId (обязательный) - UUID кошелька
---
4. POST `/api/finance/{walletId}/budget` - Установка бюджета для категории
  
Параметры:
- walletId (обязательный) - UUID кошелька
---

5. POST `/api/finance/{walletId}/budgets` - Массовая установка бюджетов

Параметры:
- walletId (обязательный) - UUID кошелька
---

6. GET `/api/finance/{walletId}/statistics` - Получение финансовой статистики
   
Параметры:
- walletId (обязательный) - UUID кошелька
- categories (опциональный) - фильтр по категориям (через запятую)
- startDate (опциональный) - начало периода (формат: 2025-11-03T00:00:00)
- endDate (опциональный) - конец периода (формат: 2025-11-03T23:59:59)
---
7. POST `/api/finance/transfer` - Перевод средств между пользователями
   
Параметры:
- fromUser (обязательный) - логин отправителя
- toUser (обязательный) - логин получателя
- amount (обязательный) - сумма перевода
- description (опциональный) - описание операции (по умолчанию: "Transfer")
---
8. POST `/api/finance/{walletId}/export/download` - Экспорт финансовой статистики с автоматическим скачиванием

Параметры:
- walletId (обязательный) - UUID кошелька для анализа
- startDate (обязательный) - начальная дата периода в формате ГГГГ-ММ-ДД
- endDate (обязательный) - конечная дата периода в формате ГГГГ-ММ-ДД
- filename (опциональный) - название файла без расширения (по умолчанию: "report")
---
9. DELETE `/api/finance/{walletId}/budget` - Удаляет бюджетное ограничение для указанной категории расходов

Параметры:
- walletId (path, обязательный) - UUID кошелька
- category (query, обязательный) - Название категории для удаления бюджета
