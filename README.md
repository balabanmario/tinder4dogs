# tinder4dogs

A matching service for dogs. Two dogs go in, a compatibility score comes out.

Kotlin · Spring Boot · PostgreSQL · Maven · mise

## Requirements

- [mise](https://mise.jdx.dev) — installs and pins the JDK and Maven
- A container runtime for the database: OrbStack, Docker Desktop or Podman

Everything else is handled by `mise`.

## Getting started

```bash
mise install       # JDK and Maven, at the versions this project needs
mise run db        # start PostgreSQL
mise run run       # start the application on http://localhost:8080
```

Check it is alive:

```bash
curl http://localhost:8080/api/dogs
```

## Tasks

| Command | What it does |
| --- | --- |
| `mise run build` | Compile and package |
| `mise run test` | Run the test suite (no database needed) |
| `mise run run` | Start the application |
| `mise run db` | Start the application's PostgreSQL |
| `mise run db:stop` | Stop it |

## API

| Method | Path | Body / Result |
| --- | --- | --- |
| `GET` | `/api/dogs` | every dog |
| `GET` | `/api/dogs/{id}` | one dog |
| `POST` | `/api/dogs` | create a dog |
| `GET` | `/api/matches/{id}` | the other dogs, best match first |
| `GET` | `/api/matches/{aId}/{bId}` | the score between two dogs |

## Conventions

See [AGENTS.md](AGENTS.md). It applies to people as much as to agents.
