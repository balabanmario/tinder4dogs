---
status: "{{STATUS}}"
date: {{DATE}}
decision-makers: <names or roles - TBD>
consulted: <optional: people/teams whose opinion was sought>
informed: <optional: people/teams kept up to date>
---

# ADR-{{NUMBER}}: {{TITLE}}

## Context and Problem Statement

<2-4 sentences: the situation, the forces at play, and the question that needs a decision. Neutral: do not argue for the outcome here. Link requirements or tickets, e.g. NFR-03, CON-02.>

## Decision Drivers

* <driver, ideally measurable - e.g. "p95 latency under 300 ms at 500 rps">
* <driver - constraint, team skill, cost, deadline, compliance ...>

## Considered Options

* <Option 1>
* <Option 2>
* <Option 3 - include "keep the status quo" when it is a genuine choice>

## Decision Outcome

Chosen option: "<Option N>", because <justification that ties back to the decision drivers and explains why it beats the others>.

### Consequences

* Good, because <positive consequence>
* Bad, because <negative consequence, trade-off, or new risk>
* Neutral / follow-up: <work this creates, things that become harder or easier>

### Confirmation

<Optional but valuable: how will we verify the decision is implemented and still holds? e.g. a fitness function, an architecture test, a review checklist item, a load test, a date to revisit.>

## Pros and Cons of the Options

### <Option 1>

* Good, because <argument>
* Bad, because <argument>

### <Option 2>

* Good, because <argument>
* Bad, because <argument>

## More Information

<Evidence, links to spikes/benchmarks/docs, related ADRs (supersedes / superseded by / relates to), conditions under which to revisit this decision.>
