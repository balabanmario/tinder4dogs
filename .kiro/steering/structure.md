# Project Structure

## Organization Philosophy

**Package per concept.** A package owns its model, its persistence and its HTTP
surface. There are no `controller/`, `service/`, `repository/` layer folders —
a feature is a vertical slice, and you read it top to bottom in one directory.

Package root is `com.ai4dev.tinder4dogs`; the directory layout mirrors it.
Source roots are `src/main/kotlin` and `src/test/kotlin`; there is no Java tree.

If a change needs two packages, say so in the commit message rather than
quietly coupling them. Dependencies between packages run one way only: today
`match` reads from `dog`, and `dog` knows nothing of `match`. Keep it acyclic.

## Directory Patterns

### Feature package
**Location**: `src/main/kotlin/com/ai4dev/tinder4dogs/<concept>/`
**Purpose**: Everything about one concept — entity, repository, service,
controller, DTOs.
**Example**: `dog/` holds `Dog.kt`, `DogRepository.kt`, `DogController.kt`.

### Migrations
**Location**: `src/main/resources/db/changelog/changes/`
**Purpose**: One plain-SQL file per schema step, `NNN-kebab-description.sql`,
registered in the master index. Never `ddl-auto: update`.

### Tests
**Location**: `src/test/kotlin/...`, mirroring the main package path.
**Purpose**: Unit tests of behaviour. `<ClassUnderTest>Test`.

## Naming Conventions

- **Entity**: the bare domain noun — `Dog`, not `DogEntity`.
- **Repository / Service / Controller**: suffixed by role — `DogRepository`,
  `MatchScoreService`, `DogController`.
- **DTOs**: `Request` / `Response` suffix — `DogRequest`, `DogResponse`.
  Never `Dto`.
- **Files**: named after the primary class. **Not** one class per file — a
  small enum or the DTOs a controller uses live in that file.
- **Constructor parameters**: named for their role, not their type —
  `private val dogs: DogRepository`, not `dogRepository`.

## Layer Responsibilities

**Controllers** map HTTP to calls and back. No domain rules. They translate
absence to `404`, unusable data to `422`, and build responses.

**Services** hold the domain rules. Stateless where possible — a service with
no dependencies can be instantiated directly in a test.

**Repositories** are Spring Data interfaces. Add query methods there, not SQL
in services.

**Entities** are persistence shape, not behaviour. JPA-pragmatic: a mutable
`class` with `var`. DTOs, by contrast, are immutable `data class`.

A package with no domain rules does not need a service; `dog/` has none today
and its controller talks to the repository directly. Add the service the moment
a rule appears.

## Validation

Three layers, each with a distinct job:

1. **Bean validation on the request DTO** — shape and range of input, at the
   boundary. Kotlin needs the use-site target: `@field:NotBlank`, `@field:Min(0)`,
   activated by `@Valid @RequestBody`.
2. **`require` at the top of a service function** — domain invariants, before
   any work. Public functions that can reject their input do so here.
3. **A companion predicate** — when callers need to filter before invoking a
   function that would throw, expose a total check alongside the partial one
   (`canScore` next to `score`).

Prefer named constants to inline numbers in scoring and validation logic.

## Mapping

Hand-written, no mapping library. Prefer a `of()` factory on the response's
companion object, used as a method reference. Request-to-entity construction
is inlined in the handler.

## REST Conventions

- Base path `/api/<plural-noun>`.
- Collection on the base path, item under `/{id}`.
- Creation is `@PostMapping` on the base with explicit
  `@ResponseStatus(HttpStatus.CREATED)`.

## Error Handling

There is no `@ControllerAdvice`. Controllers return `ResponseEntity` for
non-200 outcomes; services throw `IllegalArgumentException` from `require`,
which currently surfaces as an unstructured 500. If you add a centralised
handler, that is a structural change worth its own commit.

## Imports

Single block, no blank lines, strict lexicographic order across all imports
regardless of origin — the IntelliJ Kotlin default. No wildcard imports.

## Known Inconsistencies

Do not copy these; follow the dominant pattern instead.

- **Return types**: some handlers return bare types, others `ResponseEntity`,
  sometimes in the same class. Prefer `ResponseEntity` where a non-200 outcome
  is possible, bare types where it is not.
- **Two mapping idioms**: a companion `of()` factory in one place, a private
  controller method in another. Prefer the companion factory.
- **`MatchController` orchestrates candidate selection and ordering** — that is
  domain logic in a controller. It belongs in a service.
- **`MatchResponse` means two different things** depending on the endpoint.
  Split it rather than extending the overload.
- **`/api/matches/{id}` takes a dog id**, not a match id.
- **The whole HTTP and persistence surface is untested.** `spring-boot-starter-test`
  is already on the classpath; `@WebMvcTest` slices would close this without
  new dependencies.

---
_Document patterns, not file trees. New files following patterns shouldn't require updates_
