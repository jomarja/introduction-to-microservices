# Introduction to Microservices

Three Spring Boot applications:

| Application        | Port | Database                        | Responsibility                                      |
|--------------------|------|---------------------------------|-----------------------------------------------------|
| `eureka-server`    | 8761 | —                               | Service registry and discovery                      |
| `resource-service` | 8080 | `resource-db` (localhost:5432)  | Stores MP3 files, extracts tags, calls Song Service |
| `song-service`     | 8081 | `song-db` (localhost:5433)      | Stores song metadata                                |

## Service discovery

Both services register with Eureka on startup. Resource Service never uses a Song Service host name:
it calls `http://song-service` through a load-balanced `RestClient`, so requests are spread over the
registered instances. The registry dashboard is at http://localhost:8761.

The registry URL comes from `EUREKA_SERVER_URL` and falls back to `http://localhost:8761/eureka/`,
which is why the same build works locally and in Docker Compose.

## Prerequisites

- Docker
- Java 21 and Maven 3.9+ (only for running the services locally)

## Run everything in Docker

```
docker compose down
docker compose up -d --build
```

Builds the three images from their `Dockerfile`s and starts the containers, with two Song Service
replicas (`deploy.replicas`) published on host ports 8081 and 8082. Configuration for the
containerized environment comes from `.env`.

Verify:

```
docker ps
docker compose logs -f resource-service song-service
```

Then open http://localhost:8761 and check that one `RESOURCE-SERVICE` and two `SONG-SERVICE`
instances are registered.

Scaling with dynamic host ports works too, but then Postman needs the port shown by `docker ps`:

```
docker compose up -d --build --scale song-service=2
```

## Run services locally

1. Start only the databases:

   ```
   docker compose up -d resource-db song-db
   ```

2. Start the registry and the services (separate terminals, registry first):

   ```
   cd eureka-server && mvn spring-boot:run
   cd song-service && mvn spring-boot:run
   cd resource-service && mvn spring-boot:run
   ```

The applications fall back to the `localhost` defaults in `application.properties`, so no profile
switching is needed.

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
- `song_service_url` = `http://localhost:8081` (or `:8082` to hit the second Song Service replica)
