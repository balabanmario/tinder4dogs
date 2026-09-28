# Worked Example

A complete MADR-format ADR showing the expected level of detail, tone, and honesty about trade-offs. Note the balanced options, the concrete drivers, the negative consequences, and the revisit condition. Everything here is illustrative.

**Input the user might give:**
> We're a team of 6 building a new booking platform. We argued for a week about microservices vs one deployable. We're going with a single deployable split into modules for now. Ops is one person part-time. Launch is in 5 months.

**Resulting ADR** (`0002-adopt-a-modular-monolith-for-the-first-release.md`):

```markdown
---
status: "accepted"
date: 2026-09-28
decision-makers: Engineering team (6 developers), tech lead
consulted: Operations
informed: Product owner
---

# ADR-0002: Adopt a modular monolith for the first release

## Context and Problem Statement

We are building a new booking platform with a team of six developers and one part-time operations engineer, with launch planned in five months. The domain is not yet well understood, so service boundaries are likely to change. We must decide whether to build the system as separately deployed microservices or as a single deployable unit.

## Decision Drivers

* Launch within five months with a six-person team
* Very limited operational capacity (one part-time engineer)
* Domain boundaries still uncertain and expected to shift
* Ability to extract parts into separate services later if scale requires it

## Considered Options

* Microservices from the start
* Modular monolith (single deployable, enforced module boundaries)
* Unstructured monolith (single deployable, no enforced boundaries)

## Decision Outcome

Chosen option: "Modular monolith", because it fits the team's operational capacity and timeline while keeping the option to extract services later, whereas microservices would add deployment and observability overhead we cannot currently staff, and an unstructured monolith would make later extraction costly.

### Consequences

* Good, because one deployment pipeline and one runtime keep operations within what one part-time engineer can handle.
* Good, because changing module boundaries is a refactoring, not a cross-service migration, which suits an evolving domain.
* Bad, because all modules scale and deploy together; a heavy module cannot be scaled independently.
* Bad, because module boundaries are only as strong as the discipline and tooling that enforce them; without checks they erode.
* Follow-up: define the module structure and dependency rules; introduce automated checks (see Confirmation).

### Confirmation

Module dependency rules are verified by an automated architecture test in CI. The decision is reviewed after launch or if a single module's load requires independent scaling, whichever comes first.

## Pros and Cons of the Options

### Microservices from the start

* Good, because services scale and deploy independently.
* Good, because team ownership boundaries are explicit.
* Bad, because it requires service discovery, distributed tracing, per-service pipelines and more operations effort than available.
* Bad, because wrong early boundaries are expensive to fix across services.

### Modular monolith

* Good, because simplest deployment and debugging with the team we have.
* Good, because boundaries can be moved cheaply while the domain is learned.
* Bad, because no independent scaling or deployment per module.
* Bad, because boundary discipline must be actively enforced.

### Unstructured monolith

* Good, because fastest to start.
* Bad, because coupling grows unchecked and later extraction becomes a rewrite.

## More Information

Revisit if launch load or team growth changes the assumptions above. Related: none yet.
```

**What to notice**
- The context does not argue for the outcome; the drivers and the "because" do.
- Each option has a real upside; the rejected ones are not strawmen.
- Negative consequences are stated plainly.
- Nothing is invented: names, numbers and dates come from the input (the date is today's when the user did not give one; decision-makers are inferred from "we" and would be flagged for confirmation in the reply).
