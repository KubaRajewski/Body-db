# Body-DB — Project Context for Claude Code

> Ten plik jest automatycznie wczytywany przez Claude Code przy każdej sesji.
> Zawiera stały kontekst projektu, konwencje i ograniczenia.

---

## 🎯 Co to za projekt

**Body-DB** to prywatny system śledzenia danych zdrowotnych. Jedno miejsce zbierające:
- dane z Apple Watch / Apple Health (kroki, sen, treningi, tętno, HRV, SpO2, Vitals)
- dane żywieniowe (kalorie, makro) — z aplikacji typu Cronometer lub spiętej z Apple Health
- własne logi treningowe (ćwiczenia, serie, powtórzenia, ciężary)

Cel: prosty, czytelny zbiór danych do analizy nawyków zdrowotnych — bez zbędnego szumu.

**Użytkownik:** jedna osoba (właściciel). To NIE jest aplikacja multi-tenant.

---

## 🛠️ Stack techniczny

| Warstwa | Technologia |
|---|---|
| Język | Java 21 |
| Framework | **Micronaut 4.x** |
| Build | **Gradle (Kotlin DSL)** |
| Baza danych | **PostgreSQL 16** |
| Migracje | **Flyway** |
| ORM | **Micronaut Data JDBC** (nie JPA — lżejsze) |
| Walidacja | Jakarta Bean Validation |
| Testy | JUnit 5 + Testcontainers + REST-assured |
| Deployment | **Docker Compose** (app + postgres) |
| CI | GitLab CI (placeholder w MVP) |

**Dlaczego Micronaut a nie Spring Boot:** kompilacja AOT, szybki startup, mniej runtime-magic, użytkownik preferuje. Trzymamy się idiomów Micronaut — `@Controller`, `@Repository`, `@Singleton`, `@Inject` (lub konstruktorowo).

---

## 📁 Struktura repo

```
body-db/
├── CLAUDE.md                    # ten plik
├── INSTRUCTIONS.md              # plan implementacji krok po kroku
├── README.md                    # quick start dla człowieka
├── .gitignore
├── .env.example
├── docker-compose.yml
├── Dockerfile
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── src/
│   ├── main/
│   │   ├── java/com/bodydb/
│   │   │   ├── Application.java
│   │   │   ├── config/          # konfiguracja, security
│   │   │   ├── health/          # moduł: dane HealthKit
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── domain/      # entities
│   │   │   │   └── dto/         # request/response DTOs
│   │   │   ├── workout/         # moduł: treningi z Apple Watch
│   │   │   ├── exercise/        # moduł: własne logi ćwiczeń
│   │   │   ├── nutrition/       # moduł: żywienie
│   │   │   └── summary/         # moduł: agregaty/views
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback.xml
│   │       └── db/migration/    # Flyway: V001__init.sql, V002__...
│   └── test/
│       └── java/com/bodydb/
└── shortcuts/                   # iOS Shortcuts JSON + dokumentacja
    ├── README.md
    ├── health-dump.shortcut.json
    └── nutrition-dump.shortcut.json
```

---

## 📐 Konwencje kodu

- **Pakiety per moduł** (feature-based, nie layer-based). Każdy moduł ma własne `controller/service/repository/domain/dto`.
- **DTO ≠ Entity.** Nigdy nie wystawiamy encji w API. Mapowanie ręczne (mała aplikacja, MapStruct overkill).
- **UUID jako PK** — generowane po stronie aplikacji (`UUID.randomUUID()`), nie w DB.
- **Daty** — `LocalDate` dla dni, `Instant` dla timestampów (UTC). Zero `Date`/`Calendar`.
- **Nazwy** — angielskie w kodzie i DB. Polskie tylko w docs/komentarzach jeśli sensowne.
- **SQL** — `snake_case`, Java — `camelCase`. Micronaut Data sam mapuje.
- **Walidacja** — wszystkie request DTO mają `@Valid` + `@NotNull`/`@Positive` itp.
- **Idempotencja POST-ów dziennych** — `UNIQUE(date)` w DB + UPSERT w repozytorium. Wysłanie tego samego dnia dwa razy nadpisuje, nie dubluje.
- **Brak Lomboka** w MVP — używamy Java records dla DTO i klasycznych klas dla entity.

---

## 🔐 Bezpieczeństwo

- Aplikacja prywatna, ale wystawiona w internecie → **API key obowiązkowy** na każdym endpoincie.
- Mechanizm: filtr Micronaut sprawdza header `X-API-Key` i porównuje ze zmienną środowiskową `BODYDB_API_KEY`.
- Brak user managementu, OAuth, JWT — to byłoby over-engineering.
- HTTPS załatwia reverse proxy na VPS (nginx/caddy) — backend słucha tylko po HTTP wewnętrznie.
- Sekrety: `.env` (gitignored) + `.env.example` w repo.

---

## 🗄️ Model danych (high-level)

Pełny SQL jest w `src/main/resources/db/migration/V001__init.sql`. Krótki przegląd:

- **`daily_health`** — jeden rekord na dzień: kroki, kalorie aktywne/spoczynkowe, tętno spoczynkowe, HRV, SpO2, stand hours, sen (total/REM/deep/core), VO2max, wiek serca.
- **`workouts`** — wiele na dzień: typ, czas, kalorie, tętno avg/max, dystans (jeśli jest).
- **`nutrition_daily`** — jeden rekord na dzień: kalorie, białko, węgle, tłuszcze, błonnik, woda.
- **`exercise_sessions`** — własna sesja siłowa, ma wiele `exercise_sets`.
- **`exercise_sets`** — pojedyncza seria: ćwiczenie, powtórzenia, ciężar, RPE, czas (dla cardio/plank).

Wszystkie tabele mają `id UUID PK`, `created_at`, `updated_at`. Dzienne mają `UNIQUE(date)`.

---

## 🔌 API — kontrakt

| Method | Path | Opis |
|---|---|---|
| POST | `/api/health/daily` | Dziennie z Shortcuts (HealthKit) — UPSERT po `date` |
| POST | `/api/health/workouts` | Batch insert treningów z dnia |
| POST | `/api/nutrition/daily` | Makro dnia — UPSERT po `date` |
| POST | `/api/exercise/sessions` | Logowanie własnej sesji siłowej |
| GET | `/api/exercise/sessions/{id}` | Jedna sesja z setami |
| GET | `/api/summary/day?date=YYYY-MM-DD` | Pełny widok dnia |
| GET | `/api/summary/week?from=YYYY-MM-DD` | 7 dni od `from` |
| GET | `/api/health/status` | Healthcheck (bez API key) |

Wszystkie poza `/health/status` wymagają `X-API-Key`.

---

## 🧪 Strategia testowa

- **Repository** → Testcontainers + prawdziwy Postgres (nie H2 — różnice w SQL boli)
- **Service** → mocki repozytoriów, JUnit
- **Controller** → `@MicronautTest` + REST-assured, e2e przez cały stack
- **Pokrycie** — nie ścigamy się z procentami, kluczowe ścieżki muszą być testowane

---

## 🚢 Deployment

- `docker-compose.yml` zawiera dwa serwisy: `app` (z Dockerfile) i `postgres:16-alpine`.
- Volume `pgdata` na bazę, volume `applogs` na logi.
- Aplikacja czyta konfig z env vars (Micronaut wspiera natywnie).
- Healthcheck w compose patrzy na `/api/health/status`.
- Reverse proxy (nginx/caddy) konfigurujesz osobno na VPS — to NIE jest część repo.

---

## ⚠️ Czego NIE robimy w MVP

- Brak frontu (może w v2).
- Brak user accountów / multi-tenancy.
- Brak OAuth/JWT — tylko API key.
- Brak realtime/WebSocketów.
- Brak Kafki/eventów — zwykłe HTTP POST.
- Brak cache'a (Redis itp.) — Postgres wystarczy.
- Brak sync/integracji bezpośrednio z Cronometer API — wpychamy CSV ręcznie albo przez Shortcuts.

---

## 📚 Komendy które warto pamiętać

```bash
# Build
./gradlew build

# Uruchom lokalnie (potrzebuje pg na porcie 5432 lub docker compose up -d postgres)
./gradlew run

# Testy
./gradlew test

# Docker compose up
docker compose up -d

# Logi
docker compose logs -f app

# Migracje (Flyway uruchamia się automatycznie przy starcie aplikacji,
# ale można ręcznie:)
./gradlew flywayMigrate
```

---

## 🧭 Gdy w wątpliwości

1. Najpierw przeczytaj `INSTRUCTIONS.md` — tam jest plan krok po kroku.
2. Trzymaj się stacku z tej sekcji. Nie wprowadzaj nowych dependencies bez powodu.
3. Małe commity, jasne wiadomości po angielsku (Conventional Commits: `feat:`, `fix:`, `chore:`).
4. Jeśli coś jest niejasne — zapytaj zamiast zgadywać.
