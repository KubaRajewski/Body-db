# Body-DB — Implementation Instructions

> **Tryb pracy:** uruchom Claude Code w **plan mode** (`/plan`) i pracuj sekwencyjnie po fazach.
> Po każdej fazie pokaż diff/podsumowanie i poczekaj na akceptację.
> Nie skacz do przodu — fazy mają zależności.

---

## Faza 0 — Setup repo

**Cel:** Pusty, ale poprawnie skonfigurowany projekt Micronaut+Gradle, który się buduje.

**Kroki:**
1. Zainicjuj `git` i podstawowe pliki:
   - `.gitignore` (Java + Gradle + IDE + `.env` + `build/`, `out/`, `.idea/`, `*.iml`)
   - `README.md` — krótki, w stylu "Body-DB — private health tracking. See `CLAUDE.md` and `INSTRUCTIONS.md`."
   - `.env.example` z kluczami: `BODYDB_API_KEY`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`, `POSTGRES_HOST`, `POSTGRES_PORT`.
2. Wygeneruj projekt Micronaut (Java 21, Gradle Kotlin DSL) z featurami:
   - `data-jdbc`, `flyway`, `jdbc-hikari`, `postgres`, `validation`, `serialization-jackson`, `http-client-test`, `testcontainers`, `assertj`, `micronaut-test-junit5`.
   - Można użyć Micronaut Launch CLI lub ręcznie napisać `build.gradle.kts`.
3. Sprawdź `./gradlew build` — musi przejść (nawet jeśli jest tylko `Application.java` z `Micronaut.run`).
4. Pakiet bazowy: `com.bodydb`.

**Definition of Done:**
- `./gradlew build` zielone.
- `git status` pokazuje czysty tree po `git add . && git commit`.
- Pierwszy commit: `chore: initial micronaut scaffold`.

---

## Faza 1 — Docker Compose + Postgres

**Cel:** Lokalnie i na prodzie odpalamy `docker compose up -d` i mamy bazę + appkę.

**Kroki:**
1. `docker-compose.yml`:
   - serwis `postgres` z obrazem `postgres:16-alpine`
   - volume `pgdata` na `/var/lib/postgresql/data`
   - env z `.env` (`POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`)
   - healthcheck: `pg_isready -U $POSTGRES_USER`
   - port `5432` mapowany TYLKO lokalnie (`127.0.0.1:5432:5432`)
   - serwis `app` zbudowany z `Dockerfile`
   - app zależy od postgres (`depends_on: condition: service_healthy`)
   - port `8080` lokalnie
   - healthcheck na `/api/health/status`
   - volume `applogs` na `/app/logs`
2. `Dockerfile` — multi-stage:
   - stage 1: `eclipse-temurin:21-jdk` + `./gradlew build -x test`
   - stage 2: `eclipse-temurin:21-jre` + skopiowany fat-jar + `ENTRYPOINT java -jar app.jar`
3. Konfiguracja Micronaut: `application.yml` czyta DB url z env vars. Ustaw datasource `default` z `jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}`.
4. Healthcheck endpoint: użyj wbudowanego `micronaut-management` (`/health`) ALBO napisz własny `GET /api/health/status` zwracający `{"status":"UP"}`. Wybierz to drugie — chcemy własną ścieżkę pod API key whitelisting.

**DoD:**
- `docker compose up -d` startuje, oba kontenery healthy.
- `curl localhost:8080/api/health/status` → `{"status":"UP"}`.
- Commit: `feat: docker compose with postgres`.

---

## Faza 2 — Schema bazy (Flyway)

**Cel:** Pełna schema w jednej migracji V001.

**Kroki:**
1. Utwórz `src/main/resources/db/migration/V001__init.sql`.
2. Tabele do utworzenia:

   ```sql
   -- daily_health: jeden rekord = jeden dzień, dane z HealthKit
   CREATE TABLE daily_health (
       id UUID PRIMARY KEY,
       date DATE NOT NULL UNIQUE,
       steps INTEGER,
       active_calories INTEGER,
       resting_calories INTEGER,
       resting_heart_rate INTEGER,
       hrv_ms NUMERIC(6,2),
       spo2_pct NUMERIC(5,2),
       respiratory_rate NUMERIC(5,2),
       wrist_temperature_delta_c NUMERIC(4,2),
       vo2_max NUMERIC(5,2),
       stand_hours INTEGER,
       exercise_minutes INTEGER,
       sleep_total_min INTEGER,
       sleep_rem_min INTEGER,
       sleep_deep_min INTEGER,
       sleep_core_min INTEGER,
       sleep_awake_min INTEGER,
       mindful_minutes INTEGER,
       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
       updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
   );

   -- workouts: dane z Apple Watch, wiele na dzień
   CREATE TABLE workouts (
       id UUID PRIMARY KEY,
       date DATE NOT NULL,
       started_at TIMESTAMPTZ,
       type VARCHAR(64) NOT NULL,
       duration_min INTEGER NOT NULL,
       calories INTEGER,
       avg_heart_rate INTEGER,
       max_heart_rate INTEGER,
       distance_m INTEGER,
       source VARCHAR(32) NOT NULL DEFAULT 'apple_watch',
       external_id VARCHAR(128),  -- HK UUID jeśli dostępne, do dedup
       created_at TIMESTAMPTZ NOT NULL DEFAULT now()
   );
   CREATE INDEX idx_workouts_date ON workouts(date);
   CREATE UNIQUE INDEX idx_workouts_external_id ON workouts(external_id) WHERE external_id IS NOT NULL;

   -- nutrition_daily: makro dnia
   CREATE TABLE nutrition_daily (
       id UUID PRIMARY KEY,
       date DATE NOT NULL UNIQUE,
       calories_kcal INTEGER,
       protein_g NUMERIC(6,2),
       carbs_g NUMERIC(6,2),
       fat_g NUMERIC(6,2),
       fiber_g NUMERIC(6,2),
       sugar_g NUMERIC(6,2),
       sodium_mg INTEGER,
       water_ml INTEGER,
       source VARCHAR(32) NOT NULL DEFAULT 'cronometer',
       notes TEXT,
       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
       updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
   );

   -- exercise_sessions: własna sesja siłowa (logowana ręcznie)
   CREATE TABLE exercise_sessions (
       id UUID PRIMARY KEY,
       date DATE NOT NULL,
       started_at TIMESTAMPTZ,
       ended_at TIMESTAMPTZ,
       title VARCHAR(128),
       notes TEXT,
       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
       updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
   );
   CREATE INDEX idx_exercise_sessions_date ON exercise_sessions(date);

   -- exercise_sets: pojedyncza seria w sesji
   CREATE TABLE exercise_sets (
       id UUID PRIMARY KEY,
       session_id UUID NOT NULL REFERENCES exercise_sessions(id) ON DELETE CASCADE,
       set_order INTEGER NOT NULL,
       exercise_name VARCHAR(128) NOT NULL,
       reps INTEGER,
       weight_kg NUMERIC(6,2),
       duration_sec INTEGER,
       distance_m INTEGER,
       rpe NUMERIC(3,1),
       notes TEXT,
       created_at TIMESTAMPTZ NOT NULL DEFAULT now()
   );
   CREATE INDEX idx_exercise_sets_session ON exercise_sets(session_id);

   -- trigger updated_at
   CREATE OR REPLACE FUNCTION touch_updated_at() RETURNS TRIGGER AS $$
   BEGIN NEW.updated_at = now(); RETURN NEW; END;
   $$ LANGUAGE plpgsql;

   CREATE TRIGGER tr_daily_health_updated_at BEFORE UPDATE ON daily_health
       FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
   CREATE TRIGGER tr_nutrition_daily_updated_at BEFORE UPDATE ON nutrition_daily
       FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
   CREATE TRIGGER tr_exercise_sessions_updated_at BEFORE UPDATE ON exercise_sessions
       FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
   ```

3. Skonfiguruj Flyway w `application.yml`:
   ```yaml
   flyway:
     datasources:
       default:
         enabled: true
         locations: classpath:db/migration
   ```

**DoD:**
- Po `docker compose up -d` Flyway uruchamia migrację, wszystkie tabele istnieją.
- Test: `psql -h localhost -U bodydb -d bodydb -c '\dt'` pokazuje 5 tabel.
- Commit: `feat: initial database schema`.

---

## Faza 3 — Security (API key filter)

**Cel:** Wszystkie endpointy poza `/api/health/status` wymagają `X-API-Key`.

**Kroki:**
1. Utwórz `config/ApiKeyFilter.java` — `HttpServerFilter` lub `@ServerFilter`.
2. Logika:
   - Jeśli ścieżka == `/api/health/status` → przepuść.
   - Pobierz nagłówek `X-API-Key`.
   - Porównaj z `bodydb.api-key` (wstrzyknięte z `BODYDB_API_KEY` env).
   - Jeśli brak/błędny → zwróć `401 Unauthorized` z body `{"error":"invalid_api_key"}`.
3. Konfiguracja w `application.yml`:
   ```yaml
   bodydb:
     api-key: ${BODYDB_API_KEY:}
   ```
4. Test integracyjny: jeden test sprawdza 401 bez headera, drugi 200 z headerem.

**DoD:**
- Test integracyjny przechodzi.
- Manualny check: `curl localhost:8080/api/summary/day?date=2026-04-28` → 401, z `-H "X-API-Key: ..."` → 200 (lub 404 ale nie 401).
- Commit: `feat: api key authentication filter`.

---

## Faza 4 — Moduł `health` (HealthKit daily)

**Cel:** `POST /api/health/daily` przyjmuje JSON z Shortcuts, robi UPSERT po `date`.

**Kroki:**
1. Struktura pakietu `com.bodydb.health`:
   - `domain/DailyHealth.java` — entity z `@MappedEntity`, wszystkie pola z migracji.
   - `dto/DailyHealthRequest.java` — Java record z walidacją (`@NotNull date`, reszta opcjonalna, `@PositiveOrZero` na liczbach).
   - `dto/DailyHealthResponse.java` — record.
   - `repository/DailyHealthRepository.java` — `@JdbcRepository` rozszerzający `CrudRepository<DailyHealth, UUID>` + custom `Optional<DailyHealth> findByDate(LocalDate date)`.
   - `service/DailyHealthService.java` — metoda `upsert(DailyHealthRequest)`: szukaj po dacie, jak jest update, jak nie ma insert.
   - `controller/HealthController.java` — `POST /api/health/daily`.
2. Mapowanie request→entity ręczne, w prostej metodzie statycznej w DTO lub service.
3. Endpoint zwraca 200 z `DailyHealthResponse` (id + zapisane dane).

**Testy:**
- Repository test (Testcontainers): insert → findByDate → update → findByDate.
- Controller test: POST z poprawnym body → 200, weryfikacja że dane są w DB.
- Test idempotencji: dwa POST-y z tą samą datą → jeden wiersz.
- Test walidacji: brak `date` → 400.

**DoD:**
- Wszystkie testy zielone.
- Manualny smoke: `curl -X POST -H "X-API-Key: ..." -H "Content-Type: application/json" -d '{"date":"2026-04-28","steps":12345}' localhost:8080/api/health/daily`.
- Commit: `feat(health): daily health upsert endpoint`.

---

## Faza 5 — Moduł `workout`

**Cel:** `POST /api/health/workouts` przyjmuje listę treningów z dnia (batch), deduplikuje po `external_id`.

**Kroki:**
1. Pakiet `com.bodydb.workout` z analogiczną strukturą.
2. Request DTO: `WorkoutsBatchRequest(LocalDate date, List<WorkoutDto> workouts)`.
3. `WorkoutDto`: `external_id` (opcjonalny ale zalecany), `type`, `started_at`, `duration_min`, `calories`, `avg_heart_rate`, `max_heart_rate`, `distance_m`.
4. Service: dla każdego workout — jeśli `external_id` istnieje i już jest w DB, pomiń; w przeciwnym razie insert.
5. Endpoint zwraca: `{ "inserted": N, "skipped": M, "ids": [...] }`.

**Testy:**
- Batch z 3 elementami, 1 duplikat → `inserted=2, skipped=1`.
- Pusty batch → 200, `inserted=0`.
- Workout bez `external_id` zawsze inserowany.

**DoD:**
- Testy zielone.
- Commit: `feat(workout): batch workout import with dedup`.

---

## Faza 6 — Moduł `nutrition`

**Cel:** `POST /api/nutrition/daily` UPSERT po dacie, analogicznie do `daily_health`.

**Kroki:**
1. Pakiet `com.bodydb.nutrition` ze strukturą jak w fazie 4.
2. Request DTO: wszystkie pola z `nutrition_daily` opcjonalne poza `date`.
3. Walidacja: `@PositiveOrZero` na wszystkich liczbach.

**Testy:** analogiczne do `health`.

**DoD:**
- Testy zielone.
- Commit: `feat(nutrition): daily nutrition upsert endpoint`.

---

## Faza 7 — Moduł `exercise` (logowanie sesji siłowych)

**Cel:** Pełny CRUD dla `exercise_sessions` + `exercise_sets`.

**Endpointy:**
- `POST /api/exercise/sessions` — tworzy sesję wraz z setami (jeden request, transakcyjnie).
- `GET /api/exercise/sessions/{id}` — pobiera sesję + sety.
- `GET /api/exercise/sessions?date=YYYY-MM-DD` — wszystkie sesje danego dnia.
- `PUT /api/exercise/sessions/{id}` — update metadanych sesji (title/notes/ended_at). Sety osobno.
- `POST /api/exercise/sessions/{id}/sets` — dodaje pojedynczy set (do logowania na żywo na siłowni).
- `DELETE /api/exercise/sets/{id}` — usuwa set.

**Kroki:**
1. Pakiet `com.bodydb.exercise` z `domain/ExerciseSession.java`, `domain/ExerciseSet.java`.
2. Repozytoria: `ExerciseSessionRepository`, `ExerciseSetRepository`.
3. Service `ExerciseService` z metodą `createSession(CreateSessionRequest)` która w jednej transakcji robi insert sesji + wszystkich setów.
4. DTO request:
   ```java
   record CreateSessionRequest(
       LocalDate date,
       Instant startedAt,
       String title,
       String notes,
       List<SetDto> sets
   ) {}
   record SetDto(
       int setOrder,
       String exerciseName,
       Integer reps,
       BigDecimal weightKg,
       Integer durationSec,
       Integer distanceM,
       BigDecimal rpe,
       String notes
   ) {}
   ```
5. Walidacje: `setOrder >= 1`, `exerciseName not blank`, przynajmniej jedno z `reps`/`durationSec` musi być wypełnione (custom validator albo sprawdzenie w service).

**Testy:**
- Tworzenie sesji z 5 setami → wszystko zapisane, get zwraca komplet.
- Dodanie kolejnego setu do istniejącej sesji.
- Usunięcie setu.
- Get po dacie zwraca tylko sesje z tego dnia.

**DoD:**
- Testy zielone.
- Commit: `feat(exercise): manual exercise session logging`.

---

## Faza 8 — Moduł `summary`

**Cel:** GET-y agregujące dane z innych modułów.

**Endpointy:**
- `GET /api/summary/day?date=YYYY-MM-DD` — zwraca:
  ```json
  {
    "date": "2026-04-28",
    "health": { ... DailyHealthResponse ... },
    "workouts": [ ... ],
    "nutrition": { ... },
    "exerciseSessions": [ ... z setami ... ]
  }
  ```
- `GET /api/summary/week?from=YYYY-MM-DD` — 7 dni od `from`, lista takich obiektów + sekcja `aggregates`:
  ```json
  {
    "from": "2026-04-22", "to": "2026-04-28",
    "days": [...],
    "aggregates": {
      "totalSteps": 87234,
      "avgSteps": 12462,
      "totalActiveCalories": 4321,
      "totalWorkouts": 5,
      "totalWorkoutMinutes": 280,
      "avgSleepMin": 432,
      "avgRestingHeartRate": 58,
      "avgCalories": 2150,
      "avgProteinG": 145.3
    }
  }
  ```

**Kroki:**
1. Pakiet `com.bodydb.summary` — tylko service+controller, repozytoriów nie potrzeba (używa istniejących).
2. Service wstrzykuje wszystkie 4 repozytoria, składa response.
3. Agregaty obliczane w Javie (proste streamy) — przy 7 dniach to są mikroskopijne ilości danych.
4. DTO odpowiedzi jako Java records z nullowalnymi polami (`null` jeśli brak danych za dany dzień).

**Testy:**
- Setup: insert 7 dni różnych danych → GET week → sprawdź agregaty.
- Day bez żadnych danych → wszystko `null` ale 200, nie 404.

**DoD:**
- Testy zielone.
- Commit: `feat(summary): day and week aggregation endpoints`.

---

## Faza 9 — Dokumentacja iOS Shortcuts

**Cel:** Dwa pliki w `shortcuts/` z opisem jak skonfigurować skróty na iPhone.

**Kroki:**
1. `shortcuts/README.md`:
   - Jak otworzyć Apple Shortcuts.
   - Jak dodać akcję "Get Health Sample" dla każdego typu.
   - Jak ustawić Automation: "At 23:00 every day, run Health Dump".
   - Jak wstawić swój API key (Text action → "Get Contents of URL" headers).
   - Lista typów HealthKit do pobrania, w mapowaniu na pola `daily_health`:
     ```
     HKQuantityTypeIdentifier.stepCount → steps
     HKQuantityTypeIdentifier.activeEnergyBurned → active_calories
     HKQuantityTypeIdentifier.basalEnergyBurned → resting_calories
     HKQuantityTypeIdentifier.restingHeartRate → resting_heart_rate
     HKQuantityTypeIdentifier.heartRateVariabilitySDNN → hrv_ms
     HKQuantityTypeIdentifier.oxygenSaturation → spo2_pct
     HKQuantityTypeIdentifier.respiratoryRate → respiratory_rate
     HKQuantityTypeIdentifier.appleSleepingWristTemperature → wrist_temperature_delta_c
     HKQuantityTypeIdentifier.vo2Max → vo2_max
     HKQuantityTypeIdentifier.appleStandTime → stand_hours
     HKQuantityTypeIdentifier.appleExerciseTime → exercise_minutes
     HKCategoryTypeIdentifier.sleepAnalysis → sleep_total_min, sleep_rem_min, sleep_deep_min, sleep_core_min, sleep_awake_min
     ```
   - Mapowanie typów workout (HKWorkoutActivityType) na string `type`.
2. `shortcuts/health-dump.shortcut.json` — placeholder z komentarzem, jak wyeksportować skrót i dodać tutaj.
3. `shortcuts/nutrition-dump.shortcut.json` — analogicznie.

**DoD:**
- Pliki istnieją, README jest wystarczający żeby człowiek skonfigurował to sam.
- Commit: `docs(shortcuts): ios shortcuts setup guide`.

> **Uwaga dla Claude Code:** nie próbuj generować JSON-a Shortcuts od zera — format jest binarny (.shortcut to plist) i tworzony przez iOS. Tutaj tylko dokumentacja.

---

## Faza 10 — README + final touches

**Cel:** README dla człowieka, który widzi to repo pierwszy raz.

**Kroki:**
1. `README.md` powinien zawierać:
   - Krótki opis (1 akapit).
   - "Quick start": clone → cp .env.example .env → wpisz wartości → docker compose up -d → curl healthcheck.
   - Sekcja API z tabelą endpointów (skopiuj z CLAUDE.md).
   - Link do `CLAUDE.md` i `INSTRUCTIONS.md` jako "Architecture & development".
   - Link do `shortcuts/README.md`.
2. Sprawdź `.env.example` — musi mieć WSZYSTKIE wymagane zmienne.
3. Dodaj prosty `Makefile` (opcjonalnie):
   ```makefile
   up:    ; docker compose up -d
   down:  ; docker compose down
   logs:  ; docker compose logs -f app
   test:  ; ./gradlew test
   build: ; ./gradlew build
   ```

**DoD:**
- Świeży clone + przejście quick startu = działająca apka.
- Commit: `docs: readme and makefile`.

---

## ✅ Definition of Done dla całego MVP

- [ ] `docker compose up -d` startuje całość bez błędów.
- [ ] Wszystkie testy zielone (`./gradlew test`).
- [ ] Manualne POST-y na każdy z 6 endpointów działają.
- [ ] `GET /api/summary/week` zwraca sensowne dane po wprowadzeniu testowych.
- [ ] API key blokuje dostęp bez headera.
- [ ] README pozwala obcej osobie postawić projekt w 10 minut.
- [ ] `shortcuts/README.md` opisuje konfigurację na iPhone.

---

## 🔮 Backlog post-MVP (NIE robić w MVP)

- Frontend (React/Vue dashboard).
- Bezpośrednia integracja Cronometer API (gdy się wykupi premium).
- Webhook od Cronometer.
- Eksport do CSV / Parquet do analizy w Pythonie.
- Notyfikacje (np. "nie zsynchronizowałeś wczorajszych danych").
- Backup bazy do S3.
- Grafana dashboard podpięty do Postgres.
- Slownik ćwiczeń (tabela `exercises` z FK z `exercise_sets`).
- Body composition (waga, % tkanki tłuszczowej z wagi smart).

---

## 📝 Zasady pracy w plan mode

1. **Przed każdą fazą** — pokaż plan: jakie pliki utworzysz/zmienisz, jakie testy napiszesz.
2. **Po każdej fazie** — pokaż diff i wynik `./gradlew test`.
3. **Nie idź dalej** dopóki użytkownik nie zaakceptuje fazy.
4. **Małe commity** — jeden commit per faza minimum, w razie czego więcej.
5. **Conventional Commits** — `feat:`, `fix:`, `docs:`, `chore:`, `test:`, `refactor:`.
6. **Pytaj** — jeśli faza wymaga decyzji (np. nazwa pakietu, konkretna biblioteka), zapytaj zamiast zgadywać.
