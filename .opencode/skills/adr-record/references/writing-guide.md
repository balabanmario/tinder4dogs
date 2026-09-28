# ADR Writing Guide

Read this before drafting. It explains how to write each section so the record is useful years later.

## Contents
1. What deserves an ADR
2. Title
3. Context and problem statement
4. Decision drivers
5. Considered options
6. Decision outcome
7. Consequences
8. Confirmation and revisit conditions
9. Common anti-patterns
10. Turning messy input into an ADR

## 1. What deserves an ADR

Write one when the decision is architecturally significant: costly to reverse, or it shapes structure, quality attributes (performance, security, availability, maintainability), dependencies, interfaces or how the system is built and deployed. Typical examples: choice of database, messaging style, deployment model, authentication approach, API style, module boundaries, build vs buy, adopting or dropping a major framework, data ownership rules.

Usually not worth one: a formatting rule, a minor library upgrade, an easily reversible local choice. When unsure, ask: "Would someone new be surprised by this and ask why?" If yes, record it.

## 2. Title

Short, present tense, naming both the problem area and the solution: "Use PostgreSQL for order data", "Adopt a modular monolith for the first release", "Replace polling with webhooks for payment status". Avoid vague titles like "Database decision".

## 3. Context and problem statement

- Describe the situation and forces *as they were at decision time*: business need, technical constraints, team size and skills, deadlines, existing systems, relevant requirements.
- End with the question being decided, phrased as a question or a clear problem statement.
- Stay neutral. If the context already argues for the answer, the reader can't judge the decision.
- 2-6 sentences is normal. Link out for depth (design docs, tickets, incident reports) rather than copying.

## 4. Decision drivers

The criteria the options are judged by. This is the bridge between context and decision, so make them specific and, where possible, measurable: "recover from failure within 15 minutes", "no licence cost above X", "usable by a team with no Kafka experience". Order roughly by importance. If the input gives no criteria, infer only the obvious ones from context and mark them `(inferred - confirm)`.

## 5. Considered options

- At least two, ideally 2-4. Include the status quo or "do nothing" when it is a real alternative.
- Each option must be a plausible choice someone reasonable could have made. No strawmen.
- Name options concretely ("Managed PostgreSQL (RDS)", not "a relational database") when the decision is technology-specific; describe the approach when it is a pattern.
- In the pros/cons, be concrete and comparable across options: same criteria, honest about each option's real strengths. Tie arguments to drivers.

## 6. Decision outcome

Format: `Chosen option: "X", because <reasons>.` The "because" must refer to the drivers and explain why X beats the alternatives, not merely list X's virtues. If the decision was a compromise, or the deciding factor was non-technical (deadline, budget, an existing licence), say so plainly; that honesty is valuable later.

If the user is still undecided, set status `proposed` and write `Recommended option:` instead, clearly labelled as a recommendation for the decision-makers.

## 7. Consequences

Cover all three kinds:
- **Good**: what improves, tied to drivers.
- **Bad**: what gets worse, costs, new risks, lock-in, skills to acquire, operational burden, things that become harder.
- **Neutral / follow-up**: migration work, new ADRs this triggers, changes needed elsewhere.

An ADR with only positive consequences is a warning sign that trade-offs were not really weighed.

## 8. Confirmation and revisit conditions

State how compliance will be checked (architecture test, code review checklist, load test, monitoring alert) and, where relevant, what would make the team reconsider (a scale threshold, a licence change, a new team, a review date). Not needed for every ADR, but it turns a static record into a living guardrail.

## 9. Common anti-patterns

- **Bundled decisions**: several choices in one record. Split them.
- **Solution-first context**: the problem statement is really a sales pitch for the outcome.
- **No rejected alternatives**: the reader cannot tell if anything else was considered.
- **Adjectives instead of trade-offs**: "more scalable", "modern", "cleaner". Say scalable in which dimension, at what load, at what cost.
- **Essay length**: an ADR that needs ten pages is probably a design document. Summarise and link.
- **Rewriting history**: editing an accepted ADR to match a later reality. Supersede instead.
- **Fabricated evidence**: made-up benchmarks or "the team evaluated X". Mark unknowns.

## 10. Turning messy input into an ADR

Input is often meeting notes, a chat thread or a verbal description. Then:
1. Find the decision sentence ("so we're going with...") and the open questions.
2. Extract the arguments people made for and against, and attribute them to options, not to people.
3. Separate facts (constraints, measurements) from opinions; keep opinions only if they explain the reasoning.
4. Check whether the decision was actually made or just leaned toward; set status accordingly.
5. List what you could not find (decision-makers, dates, evidence) as TBD and report it to the user.
