# Technology Stack

## Architecture

A single Spring Boot service over PostgreSQL. Synchronous servlet MVC, JPA for
persistence, Liquibase as the only schema authority. Matches are computed on
request and never stored.

## Core Technologies

- **Language**: Kotlin 2.3, JVM target 25
- **Runtime**: Temurin JDK 25
- **Framework**: Spring Boot **4.1** — not 3.x, see below
- **Database**: PostgreSQL 18
- **Build**: Maven 3.9
- **Tool versions and tasks**: mise

## Key Libraries

Only the ones that shape how code is written:

- `spring-boot-starter-web` — servlet MVC, `@RestController`. No WebFlux.
- `spring-boot-starter-data-jpa` — Hibernate 7, Spring Data repositories.
- `spring-boot-starter-validation` — Jakarta Bean Validation on request DTOs.
- `spring-boot-liquibase` **and** `liquibase-core` — both are required.
- `jackson-module-kotlin` — JSON for Kotlin data classes.

Kotlin compiler plugins `all-open` (spring) and `no-arg` (jpa) are configured,
so entities and beans need no manual `open` or no-arg constructor.
`-Xjsr305=strict` is on: platform types from Java are treated as non-null.

## Development Standards

### Type Safety

Kotlin null safety is the type system. Nullability that "cannot happen" —
a persisted entity's id — is failed loudly with `error(...)`, never with `!!`.

### Code Quality

**No automated formatter or linter is wired up.** Style is enforced by
convention only (see `AGENTS.md`: Kotlin official style, four spaces, no
wildcard imports). Note that `mise run format` invokes `spotless:apply` but
no spotless plugin is declared in `pom.xml` — **that task currently fails**.
Either declare the plugin or drop the task.

### Testing

- JUnit 5 and AssertJ. Mockito is on the classpath but unused; there is no
  MockK and no Testcontainers.
- Tests are **unit-level**: services are instantiated directly, no Spring
  context, no database. This is why `mise run test` needs no database.
- Name a test after the behaviour it pins, in backticks, in plain English:
  `` `dogs of different gender score higher than dogs of the same gender` ``
- Assert **relations** (`isGreaterThan`, `isBetween`), not magic numbers that
  restate the implementation.
- An assertion must be able to fail. If you cannot name the change that would
  turn the test red, the test is not finished.

## Development Environment

```bash
mise run build      # mvn clean package
mise run test       # mvn test — no database needed
mise run db         # docker compose up -d
mise run db:stop    # docker compose down
mise run run        # mvn spring-boot:run — needs the database
```

Use these tasks. Do not invent command lines.

## Key Technical Decisions

**Spring Boot 4 splits auto-configuration into per-technology modules.**
`liquibase-core` alone gives you the library with no Boot wiring: no bean, no
startup hook, no warning, and an empty schema when Hibernate validates. Hence
both artifacts are declared. Expect the same trap for other integrations —
when adding one, check that its Boot module is present, not just its library.

**Liquibase owns the schema; Hibernate only checks it.** `ddl-auto: validate`
means schema drift fails startup instead of being silently patched.

**Migrations are plain SQL under a YAML index.** The master changelog is YAML
because formatted-SQL has no `include` directive; every changeset itself is
`--liquibase formatted sql` with a `--comment:` and an explicit `--rollback`.
Files are `NNN-kebab-description.sql`; note the file number is not the
changeset id — one file may hold several changesets.

**A changeset that has run anywhere is immutable.** Editing it changes its
checksum and the application refuses to start against a database that already
ran it. Fix a mistake with the next changeset.

**`open-in-view: false`**, paired with eager fetching of the preferences
collection so serialization never touches a closed session. If you add a lazy
association, map it inside the transaction — do not re-enable open-in-view.

**Configuration comes from environment variables with local defaults.** The
committed values are development conveniences, not secrets. Never commit real
credentials.

**There is deliberately bad data in the database.** A seed changeset inserts a
dog with a negative age, and the `age` column intentionally has no `CHECK`
constraint. It exists so that endpoints reading every row must cope with a row
the API layer would have rejected. Do not "fix" it by adding the constraint.

---
_Document standards and patterns, not every dependency_
