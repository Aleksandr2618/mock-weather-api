# Автотесты weatherapi.com

Автотесты эндпоинта погоды weatherapi.com (`GET /v1/current.json`).

## 1. Что покрывают тесты

### Позитивный сценарий

Набор городов задаётся файлом тестовых данных, код при добавлении города менять не нужно:

- регистрируется заглушка успешного ответа для города
- выполняется запрос HTTP 200
- ответ десериализуется в модель `WeatherResponse`
- имя города в ответе совпадает с запрошенным
- к заглушке выполнен ровно один запрос с корректными `q` и `key`
- все поля ответа сравниваются с ожиданием из тестовых данных
- таблица сравнения `Поле / Ожидаемое / Фактическое / Статус` прикладывается к Allure-отчёту
- выводится расхождения по результату сравнения по каждому значению в лог

### Негативные сценарии (ошибки API)

| Сценарий | HTTP | error.code | Сообщение |
|---|---|---|---|
| `missing_q` | 400 | 1003 | Parameter q is missing. |
| `location_not_found` | 400 | 1006 | No matching location found |
| `invalid_api_key` | 401 | 2006 | API key provided is invalid |
| `quota_exceeded` | 403 | 2007 | API key has exceeded calls per month quota |

Для каждого сценария регистрируется заглушка с точным мэтчем параметров, проверяются HTTP-статус, `error.code`, `error.message`.

---

## 2. Технологический стек

| Инструмент | Назначение |
|---|---|
| Java 21 |  |
| Maven | сборка и запуск |
| Cucumber 7 (JVM) | BDD-сценарии на русском |
| JUnit 5 Platform Suite | точка входа `RunCucumberTest`, интеграция движка Cucumber |
| RestAssured | HTTP-клиент |
| Jackson | маппинг snake_case JSON DTO-records |
| WireMock | эмуляция WeatherAPI.com, верификация запросов, near-miss анализ |
| Allure  | отчётность, аттачменты, environment |
| AssertJ | проверки, `SoftAssertions` |
| SLF4J + Logback | логирование |

---

## 3. Архитектура

Назначение пакетов:

| Пакет | Ответственность |
|---|---|
| `org.weather.runner` | `RunCucumberTest` |
| `org.weather.glue` | `Hooks` (жизненный цикл WireMock, near-miss контроль, журнал при падении), `ScenarioState` (контекст) |
| `org.weather.steps` | реализации шагов: `WeatherSteps`, `ApiErrorSteps`, `CommonSteps` |
| `org.weather.client` | `WeatherApiClient`, `HttpClientFactory` (Jackson-конфиг для RestAssured), `dto` |
| `org.weather.wiremock` | `WireMockServerHolder` (одиночный инстанс сервера), `stubs` |
| `org.weather.data` | провайдер тестовых данных (`cities.json`), фабрика запросов для сценариев ошибок |
| `org.weather.comparison` | рекурсивное сравнение фактического и ожидаемого ответов, `null` в ожидаемых полях пропускаются |
| `org.weather.config` | единая точка конфигурации |
| `org.weather.allure` | фильтр-аттачменты HTTP с маскированием ключа, запись `environment.properties` |

Диагностируемость (то, что упрощает разбор падений):

- в Allure прикладываются HTTP запрос и ответ каждого сценария, `api.key` в URL маскируется (`***`)
- если сценарий упал - прикладывается журнал запросов WireMock за сценарий
- если запрос не сматчился ни на одну заглушку - сценарий явно падает с near-miss деталями
- `environment.properties` записывается на старте сюиты (base URL WireMock, версия Java и т.д.).

---

## 4. Быстрый старт

```bash

# подтянуть проект

git clone <URL репозитория>

cd <каталог проекта>


# запустить все тесты

mvn clean test

# посмотреть отчёт

mvn allure:serve

```

---

## 5. Отчёт Allure

Результаты пишутся в `allure-results/` в корне проекта.

Что попадает в отчёт:

- текст Gherkin-сценария со статусами шагов
- аттачменты HTTP запрос
- таблица сравнения полей ответа с ожидаемым значением
- при падении сценария
- при наличии несматченных запросов
- секция Environment


---

## 6. Тестовые данные

`src/test/resources/testdata/current-weather/cities.json` - список кейсов

`src/test/resources/testdata/current-weather/responses/london|moscow|new_york|tokyo.json` - ответы WireMock
