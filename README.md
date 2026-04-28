# Body-DB

Private health tracking system. One place for Apple Health, workouts, nutrition, and training logs.

See [CLAUDE.md](CLAUDE.md) for architecture and [INSTRUCTIONS.md](INSTRUCTIONS.md) for development plan.
See [shortcuts/README.md](shortcuts/README.md) for iOS Shortcuts setup.

---

## Quick start

```bash
git clone <repo>
cd body-db
cp .env.example .env
# edit .env — set BODYDB_API_KEY, POSTGRES_PASSWORD
docker compose up -d
curl localhost:8080/api/health/status
```

---

## API

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/health/daily` | Daily HealthKit data — UPSERT by date |
| POST | `/api/health/workouts` | Batch workout import |
| POST | `/api/nutrition/daily` | Daily nutrition — UPSERT by date |
| POST | `/api/exercise/sessions` | Log a strength session |
| GET  | `/api/exercise/sessions/{id}` | Get session with sets |
| GET  | `/api/summary/day?date=YYYY-MM-DD` | Full day view |
| GET  | `/api/summary/week?from=YYYY-MM-DD` | 7-day summary |
| GET  | `/api/health/status` | Healthcheck (no API key required) |

All endpoints except `/api/health/status` require `X-API-Key` header.

---

## Development

```bash
./gradlew build
./gradlew test
./gradlew run
docker compose up -d
docker compose logs -f app
```
