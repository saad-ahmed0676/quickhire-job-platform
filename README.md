# QuickHire

QuickHire is a desktop application that connects job seekers with local job providers for short-term, task-based work ("micro-jobs"). It covers the full lifecycle of a gig — from posting a job, applying, and getting accepted, through to invoicing, payment, dispute resolution, and rating the other party afterward.

Built as a JavaFX desktop client backed by a SQL Server database, with a layered architecture (UI → Service → DAO → Persistence) and several classic design patterns applied throughout (Singleton, Factory Method, DAO).

## Features

- **Dual roles** — Users register as either a Job Seeker or a Job Provider, each with a dedicated dashboard and workflow.
- **Job posting & discovery** — Providers post micro-jobs with category, location, and hourly rate; Seekers browse and filter open listings.
- **Application workflow** — Seekers apply with a note; Providers review applicants and accept/reject. Accepting one applicant automatically rejects the rest.
- **Progress tracking** — Providers can post progress updates while a job is in progress.
- **Invoicing** — Generate an invoice from hours worked and supply cost; Seekers confirm before payment.
- **Dispute resolution** — Either party can raise a dispute on an invoice, with a challenge / re-dispute cycle until resolved.
- **Ratings & reviews** — Both parties can rate and review each other after a completed job; profiles show a running average rating.
- **Notifications** — In-app notification indicators (e.g. pending applications) on login.
- **Skill tags** — Seekers can tag their profile with relevant skills.

## Tech Stack

| Layer | Technology |
|---|---|
| UI | JavaFX (FXML + CSS) |
| Language | Java 21 |
| Database | Microsoft SQL Server |
| Data Access | JDBC (mssql-jdbc) |
| Build Tool | Maven |

## Architecture

The codebase follows a layered architecture:

```
UI (FXML + Controllers)
        ↓
Service Layer (business logic, validation)
        ↓
DAO Layer (SQL queries)
        ↓
Persistence Layer (connection management)
        ↓
SQL Server
```

**Design patterns used:**
- **Singleton** — `DatabaseConnection` ensures a single shared connection instance across all DAOs.
- **Factory Method** — `PersistenceFactory` creates the appropriate `PersistenceHandler` implementation (`SqlServerPersistenceHandler`), and `UserDAO` decides whether to instantiate a `JobSeeker` or `JobProvider` based on the stored role.
- **DAO (Data Access Object)** — Each entity (`User`, `MicroJob`, `JobApplication`, `Review`, `Invoice`, `Payment`, `Dispute`, `ProgressUpdate`, `SkillTag`) has a dedicated DAO isolating SQL from business logic.

## Project Structure

```
QuickHire/
├── database/
│   └── schema.sql              # Full SQL Server schema (all 9 tables)
├── src/main/java/com/quickhire/
│   ├── Main.java                # Application entry point
│   ├── model/                   # Entity classes (User, MicroJob, Review, ...)
│   ├── dao/                     # Data access layer + DBConfig
│   ├── service/                 # Business logic layer
│   └── ui/                      # JavaFX controllers and app navigation
├── src/main/resources/com/quickhire/
│   ├── fxml/                    # Screen layouts
│   └── css/                     # Stylesheet
└── pom.xml
```

## Getting Started

### Prerequisites

- **JDK 21** or later
- **Maven 3.6+**
- **Microsoft SQL Server** (or SQL Server Express) running locally, with TCP/IP enabled on port 1433

### 1. Clone the repository

```bash
git clone https://github.com/YOUR-USERNAME/quickhire-job-platform.git
cd quickhire-job-platform
```

### 2. Set up the database

Run the schema script against your SQL Server instance:

```bash
sqlcmd -S localhost -U sa -P your_password -i database/schema.sql
```

Or open `database/schema.sql` in SQL Server Management Studio (SSMS) and execute it — it creates the `QuickHireDB` database and all required tables.

### 3. Configure database credentials

Connection details are read from environment variables (no credentials are stored in source code). Set at least your password before running:

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="your_sql_server_password"
```

**macOS/Linux:**
```bash
export DB_PASSWORD=your_sql_server_password
```

By default the app connects to `localhost:1433` with user `sa` and database `QuickHireDB`. To override these, set `DB_URL` and/or `DB_USER` the same way.

### 4. Run the application

```bash
mvn clean javafx:run
```

The login screen will launch on startup. Register a new account as either a Job Seeker or a Job Provider to get started.

## Authors

Built by Saad Ahmed, Aon Mohammad, and a third teammate as a Software Design & Architecture (SDA) course project at FAST University Islamabad.
