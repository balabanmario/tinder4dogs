# AGENTS.md

Instructions for any coding agent working in this repository.

This file describes **this codebase**: how it is built, how it is laid out, and
what it must never contain. It is reviewed like code and it changes when the
code changes. Nothing personal belongs here — your own review prompts, your
checklists and your working habits travel with you, not with this repository.

## What this is

A small service that matches dogs with each other. Kotlin on Spring Boot,
PostgreSQL for storage, Maven for the build, mise for tool versions and tasks.

## Build and test

Use the project tasks. Do not invent your own command lines.

```bash
mise run build     # compile and package
mise run test      # run the test suite
mise run db        # start the application's PostgreSQL
mise run run       # start the application (needs the database)
```

`mise run test` does not need the database: the test suite is unit-level.

## Layout

```
src/main/kotlin/com/ai4dev/tinder4dogs/
├── Tinder4DogsApplication.kt   entry point
├── dog/                        the dog itself: model, storage, HTTP
└── match/                      matching between two dogs
```

One package per concept. A package owns its model, its persistence and its
HTTP surface. If a change needs both `dog` and `match`, say so in the commit
message rather than quietly coupling them.

## Conventions

- Kotlin official code style. Four spaces, no tabs, no wildcard imports.
- Constructor injection only. No field injection, no `@Autowired` on fields.
- Domain rules live in services, never in controllers and never in entities.
- Public functions that can reject their input do so with `require`, at the
  top, before any work.
- Prefer named constants to inline numbers in scoring and validation logic.
- Every schema change is a Liquibase changeset, written as **plain SQL** under
  `src/main/resources/db/changelog/changes/`, and added to the master index.
  Never `ddl-auto: update`.
- A changeset that has run anywhere is immutable. Fix a mistake by adding the
  next changeset, never by editing the last one: editing changes its checksum
  and the application refuses to start against a database that already ran it.

## Tests

- JUnit 5 and AssertJ.
- Name a test after the behaviour it pins, in backticks, in plain English.
- **An assertion must be able to fail.** A test that only checks a result is
  not null pins nothing, and it is worse than no test because it reads like
  coverage. If you cannot state which change would turn the test red, the
  test is not finished.

## What must never be in this repository

This is a product. It must build, run and ship on a machine that has never
heard of the course, the team, or anyone's personal tooling. Concretely, none
of the following belongs here:

- Personal prompts of any kind — review, commit, test generation.
- Personal command line tools and the wrappers around them.
- Model configuration, routing, credentials, or any client for them.
- Tracing, cost accounting or telemetry aimed at a developer's own tooling
  rather than at this application's behaviour in production.

The test for any file: **could this application still build and ship on a
laptop that has never heard of any of that?** If deleting the file changes
that answer, it belongs here. If it does not, it belongs to whoever wrote it.
