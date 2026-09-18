# Introduction to Microservices

Two Spring Boot services:

| Service            | Port | Database                        | Responsibility                                     |
|--------------------|------|---------------------------------|----------------------------------------------------|
| `resource-service` | 8080 | `resource-db` (localhost:5432)  | Stores MP3 files, extracts tags, calls Song Service |
| `song-service`     | 8081 | `song-db` (localhost:5433)      | Stores song metadata                                |

## Prerequisites

- Docker
- Java 21 and Maven 3.9+ (only for running the services locally)

## Run everything in Docker

```
docker compose up -d --build
```

Builds both service images from their `Dockerfile`s and starts the four containers.
Configuration for the containerized environment comes from `.env`.

## Run services locally

1. Start only the databases:

   ```
   docker compose up -d resource-db song-db
   ```

2. Start each service (separate terminals):

   ```
   cd song-service && mvn spring-boot:run
   cd resource-service && mvn spring-boot:run
   ```

The services fall back to the `localhost` defaults in `application.properties`, so no profile switching is needed.

## Database schema

Tables are created by the SQL scripts in `init-scripts/` when a database container starts for the first time.
Hibernate schema generation is disabled (`spring.jpa.hibernate.ddl-auto=none`). Data is not persisted between restarts.

## API

Resource Service:

- `POST /resources` — body: raw MP3 bytes, `Content-Type: audio/mpeg` → `{"id": 1}`
- `GET /resources/{id}` → MP3 bytes
- `DELETE /resources?id=1,2,3` → `{"ids": [1, 2]}`

Song Service:

- `POST /songs` — JSON metadata → `{"id": 1}`
- `GET /songs/{id}` → metadata
- `DELETE /songs?id=1,2,3` → `{"ids": [1, 2]}`

## Postman

Set the collection variables to:

- `resource_service_url` = `http://localhost:8080`
- `song_service_url` = `http://localhost:8081`
