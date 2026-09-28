---
name: adr-record
description: Create, review, supersede and index Architecture Decision Records (ADRs) - short, immutable documents that capture one architecturally significant decision, its context, the options considered, the outcome and the consequences. Use this skill whenever the user asks to write, draft, generate, document, review, update, number, or index an ADR or "architecture decision record" (also "decision log", "design decision", "MADR", "Nygard format", "Y-statement"), or says things like "document why we chose X over Y", "record this technology choice", "we decided to use Kafka, write it up", or pastes meeting notes / a design discussion and wants the decision captured, even if they never say the word "ADR". Do NOT use for requirements specifications, general design documents, or RFCs.
---

# ADR (Architecture Decision Record) Creator

An ADR records **one decision, why it was made, and what follows from it**. Its value is for the person who joins in two years and asks "why on earth did we do it this way?". So the *reasoning and the rejected alternatives* matter more than the decision itself: the decision is visible in the code, the reasoning is not.

An ADR is deliberately small and focused: it is not a design document or a requirements spec. Link to those (and to tickets, pull requests and other ADRs) instead of copying their content.

## Workflow

1. **Identify the decision(s).** One ADR = one decision. If the input contains several ("we chose Kafka, and also Postgres, and also a monorepo"), say so and write separate ADRs; bundled decisions can't be superseded independently. Skip things that aren't architecturally significant (variable naming, minor library bumps): a decision qualifies if it is hard or costly to reverse, or affects structure, quality attributes, dependencies, interfaces or construction techniques.
2. **Gather inputs.** Use what the user gave: notes, chat, code, existing ADRs, uploaded files (read them first if not in context). Extract facts; do not invent them. If an ADR folder already exists, read a couple of the existing ADRs and **match their format, language and numbering** rather than imposing a new one.
3. **Ask sparingly.** Only ask about what blocks the record: what the decision is (or that it is still open), the main alternatives, and the key drivers. Everything else: proceed and mark the gap (see "Handling missing information"). Prefer one round of at most three questions.
4. **Pick the format** (see table). Default to **MADR** unless the project already uses something else.
5. **Draft** from the template in `assets/` (read it first). Read `references/writing-guide.md` for how to write each section well and `references/example.md` for a complete worked example.
6. **Number and file it.** Use `scripts/adr_tool.py` when a filesystem is available (see "Numbering and files"); otherwise choose `NNNN-kebab-title.md` manually.
7. **Self-check** against the quality checklist, then deliver.

## Choosing a format

| Format | Template | Use when |
|---|---|---|
| **MADR** (default) | `assets/madr.md` | Real options were compared; the reasoning deserves a pros/cons section. Most decisions. |
| **MADR minimal** | `assets/madr-minimal.md` | Decision is clear-cut, few alternatives, low stakes; you want ~1 page. |
| **Nygard** | `assets/nygard.md` | The project already uses Michael Nygard's Context / Decision / Consequences form, or the user asks for the classic short style. |
| **Y-statement** | `assets/y-statement.md` | The user wants a one-sentence summary, a table row, or a decision embedded in another document. |

If the user names a format, use it. Don't mix formats within one repository.

## Status lifecycle

`proposed` -> `accepted` -> (`deprecated` | `superseded by ADR-NNNN`). Also `rejected` for a considered-and-declined proposal (worth keeping: it stops the same debate being reopened).

- If the user reports a decision **already made**, status is `accepted`. If they are still weighing options and want help, status is `proposed` and the chosen option is your **recommendation**, labelled as such; never present your preference as a decision the team already took.
- **Accepted ADRs are immutable** apart from status, typo fixes and links. To change a decision, write a *new* ADR that supersedes the old one (`scripts/adr_tool.py supersede`). History is the point; silently rewriting it defeats the purpose.
- For **retroactive** ADRs (decision made long ago), set the date to when it was decided if known, add a line "Recorded retroactively on <date>", and be honest about what is reconstructed versus documented. Mark unknown context as such.

## Handling missing information

Never fabricate benchmarks, costs, stakeholder names, dates, incident history or "the team considered X" when the input doesn't say so. Instead:
- Put missing facts in the ADR as `TBD` / `(to be confirmed)` at the exact place they are needed.
- Mark numbers you estimated `(estimate - to be validated)`.
- If a genuinely considered alternative isn't in the input but is an obvious one (e.g. "do nothing / keep the current approach", "buy instead of build"), you may add it labelled `(added for completeness - confirm it was considered)`.
- In your reply, list what you assumed and what needs confirming.

## Numbering and files

- Location convention: `docs/adr/` (or `doc/adr/`, `docs/decisions/`). Reuse whatever exists in the repo.
- Filename: `NNNN-kebab-case-title.md`, four digits, never reused, never renumbered. The title states the problem-and-solution briefly, in present tense (`0007-use-postgresql-for-order-data.md`).
- Helper script (stdlib Python, no install; run from anywhere):

```bash
S=/path/to/adr-record/scripts/adr_tool.py
python3 $S new "Use PostgreSQL for order data" --dir docs/adr --format madr --status accepted
python3 $S set-status 7 accepted --dir docs/adr
python3 $S supersede 3 12 --dir docs/adr     # ADR-0003 is superseded by ADR-0012
python3 $S index --dir docs/adr              # (re)build docs/adr/README.md
```
`new` picks the next free number, fills the template header and prints the file path; you then fill in the body. `supersede` updates both files' status/links and rebuilds the index. If you can't run scripts, do the same steps by hand.

## Output format

- Default: Markdown file(s) created in `/mnt/user-data/outputs/` (mirror `docs/adr/` if the user has a repo layout) and presented to the user. For several ADRs, also produce the `README.md` index.
- Word/PDF only if the user asks (then read `/mnt/skills/public/docx/SKILL.md` or the pdf skill).
- Write in the user's language (e.g. Italian, English), keeping template field names understandable in that language; ADR-NNNN identifiers stay unchanged. If existing ADRs use one language, match it.

## Quality checklist (run before delivering)

- Exactly **one decision**, stated in a sentence a newcomer can understand without the rest.
- Context explains the forces and constraints *at the time*, without arguing for the outcome.
- **At least two real options** were considered (the status quo counts), each with honest pros and cons; the rejected ones are not strawmen.
- The decision follows from the drivers: the "because" is explicit and refers to them.
- **Consequences include negatives and risks**, not only benefits; follow-up work and things that become harder are named.
- Trade-offs are concrete (quality attributes, cost, team skills, operational load), not adjectives like "better" or "modern".
- No invented facts; unknowns are marked; estimates are labelled.
- Written in a neutral, past/present-tense voice; short (MADR normally 1-2 pages).
- Status, date and decision-makers are present; links to related ADRs / requirement IDs / tickets are included where known.
- Filename and number follow the repository convention; index updated if one exists.

## Reply style

After creating the file(s): one or two sentences on what was produced (number, title, status), the assumptions made, and the top items to confirm. Don't repeat the ADR in chat. If you found several decisions in the input, list the ADRs you produced in one line each.
