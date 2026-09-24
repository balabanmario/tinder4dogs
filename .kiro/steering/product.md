# Product Overview

**tinder4dogs** is a REST backend that stores dog profiles and computes a
compatibility score between two dogs. It serves private dog owners looking for
a playmate or a mating partner for their dog, replacing chance encounters at
the park and unstructured social-media groups.

The vision is documented in `docs/PRD.md`. **What is built today is a narrow
slice of it**: dog profiles plus a scoring algorithm. Treat the PRD as intent,
not as a description of the system.

## Core Capabilities

- **Dog profiles** — name, breed, gender, age, and a set of free-text
  preference tags. Created and read over HTTP; no update or delete yet.
- **Compatibility scoring** — a deterministic, rule-based score in `[0.0, 1.0]`
  between any two dogs.
- **Ranked candidates** — for a given dog, every other dog sorted best-first.
- **Pairwise lookup** — the score between two specific dogs.
- **Tolerance of bad data** — profiles that cannot be scored are skipped in
  lists rather than failing the whole request.

## Target Use Cases

- An owner registers their dog with breed, gender, age and play preferences.
- An owner asks "which dogs suit mine best?" and gets a ranked list.
- A client checks compatibility between two specific known dogs.

## Value Proposition

A transparent, tunable compatibility score. The rules are named constants in
one service, not a model: anyone can read why two dogs scored what they scored,
and reweighting is a one-line change. No ML, no opaque ranking.

## Domain Rules

These are product decisions, not implementation details. They live in
`match/MatchScoreService.kt`.

**Scoring** — four weighted criteria, normalised against a derived maximum:

| Criterion | Weight |
| --- | --- |
| Age closeness | up to 20 |
| Same breed | 25 |
| Opposite genders | 20 |
| Each shared preference | 5, capped at 15 |

- Age closeness decays linearly and reaches zero at a gap of 10 years.
- Same breed is the single heaviest criterion.
- Shared preferences are matched by exact string, case-sensitive.
- The maximum is **derived from the weights**, never hard-coded, so reweighting
  cannot desynchronise the denominator.

**Boundaries**

- A negative age is corrupt data, not a poor match: `score` rejects it with
  `require`. `canScore` lets callers filter first.
- A dog is never offered as a match for itself.
- An unscorable *candidate* is silently dropped from a list; an unscorable
  *subject* is a `422`.

## Known Gaps

Worth knowing before planning work, in rough order of risk:

- **No auth, no geolocation, no swipe, no mutual match, no chat.** The PRD
  describes all of these; none exist.
- The PRD's mating criteria (pedigree, neutering, health) and play criteria
  (size, energy, temperament) have **no fields** in the model.
- There is no play-vs-mating mode, yet the opposite-gender weight biases every
  score toward mating.
- The PRD states the score is invisible to users in v1; the API returns it.

---
_Focus on patterns and purpose, not exhaustive feature lists_
