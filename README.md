# Introduction to Microservices

Two Spring Boot services:

| Service            | Port | Database                        | Responsibility                                     |
|--------------------|------|---------------------------------|----------------------------------------------------|
| `resource-service` | 8080 | `resource-db` (localhost:5432)  | Stores MP3 files, extracts tags, calls Song Service |
| `song-service`     | 8081 | `song-db` (localhost:5433)      | Stores song metadata                                |

## Prerequisites

- Java 21
- Maven 3.9+
- Docker

## Run

1. Start the databases:

   ```
   docker compose up -d
   ```

2. Start each service (separate terminals):

   ```
   mvn -pl song-service spring-boot:run
   mvn -pl resource-service spring-boot:run
   ```

Schemas are created by Hibernate on startup (`spring.jpa.hibernate.ddl-auto=update`).

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
