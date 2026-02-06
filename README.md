# Accounting Project 

 A small accounting application consisting of a Spring Boot backend (`accounting-backend`) and a React + Vite frontend (`ui`). This README covers setup, local development, migrations, and useful tips for Windows environments. 

---

## Project layout 

- `accounting-backend/` — Spring Boot service, Flyway migrations, Dockerfile, and Maven build
  - `src/main/resources/db/migration/` — SQL migrations (Flyway)
  - `application-local.properties` — local config (DB, security, Flyway)
  - `flyway/` — bundled Flyway CLI
- `ui/` — React + Vite frontend
- `index.html`, `test.txt` — misc files

---

## Prerequisites 

- Java 17 (required by backend)
- Maven 3.6+
- Node.js (LTS recommended; Node 18+ tested)
- npm or yarn
- MySQL (or another DB configured via JDBC)
- (Optional) Docker

> Note: The backend `pom.xml` sets `<java.version>17</java.version>` and the Flyway Maven plugin is present for migrations.

---

## Backend (local development) 

1. Open a PowerShell terminal (Windows):

   - Load local environment variables (dot-source to import vars into current session):

     ```powershell
     . .\accounting-backend\load-env.ps1
     ```


2. Build and run:

   ```powershell
   cd accounting-backend
   mvn clean package
   mvn spring-boot:run
   # or: java -jar target/accounting-backend-0.0.1-SNAPSHOT.jar
   ```

3. Config notes:

   - Local DB settings are in `src/main/resources/application-local.properties` (e.g. `spring.datasource.url=jdbc:mysql://localhost:3306/accounting_db_flyway`).
   - Flyway is present but disabled by default for local dev (`spring.flyway.enabled=false`). You can run migrations manually (see below).

---

## Running Flyway migrations 

You can run migrations using the Maven plugin or the bundled Flyway CLI.

- Maven (from `accounting-backend`):

```powershell
# Provide DB connection via CLI properties if you need to override values
mvn -Dflyway.url=jdbc:mysql://localhost:3306/accounting_db -Dflyway.user=root -Dflyway.password=yourpass flyway:migrate
```

- Flyway CLI (Windows):

```powershell
# Using bundled CLI
cd accounting-backend\flyway
.\flyway.cmd -url=jdbc:mysql://localhost:3306/accounting_db -user=root -password=yourpass migrate
```

Migration scripts live in `accounting-backend/src/main/resources/db/migration/` (e.g. `V2__add_closing_balance.sql`).

---

## Frontend (UI) 

1. Install dependencies and run dev server:

```bash
cd ui
npm install
npm run dev
# Open browser at http://localhost:5173 (Vite default)
```

2. Build for production:

```bash
npm run build
npm run preview
```

---

## Docker (optional) 

Backend contains a `Dockerfile` in `accounting-backend/`. A typical build + run:

```bash
cd accounting-backend
mvn clean package -DskipTests
docker build -t accounting-backend:local .
# then run with your env + ports
```

---

## Troubleshooting & Tips 

- If Flyway reports baseline issues locally, set `spring.flyway.baseline-on-migrate=true` (already set in `application-local.properties`).
- Ensure your MySQL server is available and the user has privileges to create/alter the dev database.
- Keep secrets out of source control — `application-local.properties` includes example local values and should not be committed with real secrets.

---



## Screenshots 
<img width="1913" height="911" alt="image" src="https://github.com/user-attachments/assets/36f2f959-9b54-42e8-a4e8-d70153df20f4" />


<img width="1894" height="798" alt="image" src="https://github.com/user-attachments/assets/72af3f01-eb36-4514-a812-9e0bba9acf08" />




