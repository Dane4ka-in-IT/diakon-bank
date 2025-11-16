# diakon-bank

Умный финансовый ассистент, который не только управляет вашими деньгами, но и защищает их.

---

### ✨ Ключевые особенности

*   **Единая панель управления:** Объединяйте счета из нескольких банков в одном месте.
*   **Аналитика на базе ИИ:** Автоматическая категоризация транзакций, поиск забытых подписок и обнаружение аномалий.
*   **Сканер безопасности банковских API:** Встроенный модуль для проверки безопасности подключенных банковских API.
*   **Freemium-модель:** Понятная стратегия монетизации с бесплатным и премиальным тарифами.
*   **Telegram-бот ассистент:** Управляйте своими финансами и получайте аналитику прямо в Telegram.

---

### 💻 Технологический стек

| Область      | Технологии                                            |
|--------------|-------------------------------------------------------|
| **Бэкенд**   | `Java 17`, `Spring Boot 3`, `PostgreSQL`, `RabbitMQ`    |
| **Фронтенд** | `Vue.js 3`, `Vuetify`                                 |
| **AI Сервис**| `Python 3.11`, `FastAPI`, `Transformers`              |
| **Telegram-бот**| `Python 3.11`, `python-telegram-bot`, `SQLAlchemy` |
| **DevOps**   | `Docker`, `Docker Compose`                            |

---

### 🏁 Запуск проекта

Для запуска проекта необходимо сначала поднять базу данных с помощью Docker, а затем запустить каждый бэкенд-сервис и фронтенд вручную.

> **Необходимые условия:**
> *   `Docker` и `Docker Compose`
> *   Java 17 (JDK)
> *   Maven
> *   Node.js (v18+)
> *   Python 3.11+

#### Шаг 1: Запуск базы данных

1.  **Клонируйте репозиторий** (если вы этого еще не сделали).
    ```bash
    git clone https://github.com/IT-diakon/diakon-bank.git
    cd diakon-bank
    ```
2.  **Запустите контейнер с PostgreSQL:**
    В корневой директории проекта выполните команду:
    ```bash
    docker-compose up -d
    ```
    Это запустит базу данных в фоновом режиме.

#### Шаг 2: Запуск бэкенд-сервисов

Для каждого из перечисленных ниже сервисов откройте новый терминал, перейдите в его директорию и выполните команду `mvn spring-boot:run`. Сервисы можно запускать в любом порядке.

*   `diakon-bank-backend/auth-service`
*   `diakon-bank-backend/bank-api-service`
*   `diakon-bank-backend/financial-pulse-service`
*   `diakon-bank-backend/security-service`

*Пример для `auth-service`:*
```bash
cd diakon-bank-backend/auth-service
mvn spring-boot:run
```
Повторите это для остальных сервисов.

#### Шаг 3: Запуск фронтенда

1.  **Создайте файл `.env`:**
    *   Перейдите в директорию `frontend`.
    *   Создайте файл `.env` и добавьте в него следующие переменные. Эти URL-адреса соответствуют портам по умолчанию для Spring Boot сервисов.
    ```
    VITE_AUTH_API_BASE_URL=http://localhost:8080/auth/api/v1
    VITE_BANK_API_BASE_URL=http://localhost:8082/bank/api/v1
    VITE_PULSE_API_BASE_URL=http://localhost:8083/pulse/api/v1
    ```
2.  **Установите зависимости и запустите:**
    В директории `frontend` выполните:
    ```bash
    npm install
    npm run dev
    ```
3.  Откройте приложение в браузере по адресу, который будет указан в консоли (обычно `http://localhost:5173`).

#### Шаг 4: Запуск Telegram-бота

1.  **Установите зависимости Python:**
    Перейдите в директорию `telegram` и выполните команду для установки необходимых библиотек:
    ```bash
    pip install -r requirements.txt
    ```
2.  **Создайте файл `.env`:**
    *   В директории `telegram` создайте файл с именем `.env`.
    *   Добавьте в него следующие переменные. `DATABASE_URL` уже настроен для подключения к базе данных из Docker.
    ```env
    DATABASE_URL=postgresql+asyncpg://postgres:secret@localhost:5432/bankdb
    TELEGRAM_BOT_TOKEN=YOUR_TELEGRAM_BOT_TOKEN_HERE

    # Опционально: для функции "Задать вопрос" нужен доступ к LLM
    # LLM_API_BASE=
    # LLM_API_KEY=
    # LLM_MODEL=
    ```
    > Замените `YOUR_TELEGRAM_BOT_TOKEN_HERE` на ваш токен, полученный от [@BotFather](https://t.me/BotFather) в Telegram.

3.  **Запустите бота:**
    В директории `telegram` выполните:
    ```bash
    python main.py
    ```
    После этого ваш бот будет доступен в Telegram.

---

### ��️ Полезные ссылки

*   **[Доска задач](https://app.striveapp.ru/spaces/43632/143089/tasks)**
*   **[Дизайн в Figma](https://www.figma.com/design/MqpvfBNBLxMkNoIg9HQAHt/diakon-bank?node-id=0-1&t=wtnZY7mEgJGDtUns-1)**
*   **[Финальная презентация на Яндекс.Диске](https://disk.yandex.ru/d/iKjX0q4sVfy8XQ)**

