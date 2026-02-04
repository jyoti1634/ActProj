# Backend deployment (Docker + Render)

This file describes a minimal, repeatable workflow to build and deploy the backend using Docker and Render.

## 1) Build & test locally
1. Build the app locally (maven):
   mvn -DskipTests package

2. Run tests or start locally for quick smoke test (optional):
   mvn -Dspring-boot.run.profiles=local -DskipTests spring-boot:run

## 2) Build Docker image (local)
From the `accounting-backend` folder:

1. Build image:
   docker build -t cashbook-backend:local .

2. Run container (example, pass DB envs):
   docker run --rm -p 8080:8080 \
     -e SPRING_DATASOURCE_URL="jdbc:mysql://HOST:PORT/DB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
     -e SPRING_DATASOURCE_USERNAME="user" \
     -e SPRING_DATASOURCE_PASSWORD="pass" \
     cashbook-backend:local

3. Test API: http://localhost:8080/actuator/health

## 3) Deploy on Render using Dockerfile
1. Create a new **Web Service** on Render (Docker). Point it to this repo and the `accounting-backend` folder.
2. In Render service settings, set the Environment Variables (use Railway values):
   - SPRING_DATASOURCE_URL
   - SPRING_DATASOURCE_USERNAME
   - SPRING_DATASOURCE_PASSWORD
   - SPRING_PROFILES_ACTIVE=prod
   - SPRING_FLYWAY_ENABLED=false  # keep migrations manual
3. Set Health Check Path: `/actuator/health` (optional)
4. Deploy.

## 4) Run Flyway manually (recommended before service start)
Use the same DB envs and run from your machine (or a CI/monthly job):

Preview pending:
  mvn org.flywaydb:flyway-maven-plugin:info -Dflyway.url="$SPRING_DATASOURCE_URL" -Dflyway.user="$SPRING_DATASOURCE_USERNAME" -Dflyway.password="$SPRING_DATASOURCE_PASSWORD"

Apply migrations:
  mvn org.flywaydb:flyway-maven-plugin:migrate -Dflyway.url="$SPRING_DATASOURCE_URL" -Dflyway.user="$SPRING_DATASOURCE_USERNAME" -Dflyway.password="$SPRING_DATASOURCE_PASSWORD"

(Use PowerShell-safe quoting or the `load-env.ps1` helper to load values.)

## 5) Notes
- Keep secrets only in Render environment variables (do not commit `.env.local`).
- If you want automatic Flyway on deploy, set `SPRING_FLYWAY_ENABLED=true`, but only after migrations are tested on staging.
- If you need a one-off Flyway run inside Render, create a one-off job / shell in the Render dashboard and run the migrate command there.

---
