# PRD — Tinder 4 Dogs

> **Version:** 1.0 · **Status:** Draft · **Date:** September 24, 2026

---

## Problem Statement

Dog owners looking for a playmate or a mating partner for their dog currently rely on chance: they go to the park hoping to meet a compatible dog. Alternatively, they use Facebook or WhatsApp groups, which are chaotic, full of irrelevant content, and often suggest dogs that are too far away. There is no dedicated, location-based tool that suggests genuinely compatible dogs using different criteria depending on the goal (play or mating).

---

## Target Users & Personas

### Persona 1 — Private owner looking for a playmate
- **Profile:** private individual with a dog, living in Switzerland or Italy, who wants to socialize their dog with similar dogs.
- **Pain points:** encounters at the park are random; in social groups, it is difficult to find nearby dogs that are compatible in size, energy, temperament, and age.
- **Goals:** quickly find compatible dogs nearby and arrange a meeting.

### Persona 2 — Private owner looking for a mating partner
- **Profile:** private individual with a dog (often a purebred dog, potentially with a pedigree) who wants to breed it. They may also be interested in play mode.
- **Pain points:** current channels are scattered and do not allow filtering by breed, sex, age, pedigree, neutering, and health.
- **Goals:** find a suitable partner nearby, with clear information about the dog, and contact the owner.

### Persona 3 — Breeder *(out of v1, future premium profile)*
- **Profile:** professional breeder managing multiple dogs.
- **Pain points:** managing multiple dogs and finding suitable mating partners.
- **Goals:** mating only; premium profile with management of multiple dogs.

---

## Value Proposition

Tinder 4 Dogs is the only space dedicated exclusively to bringing dogs together: it shows only nearby dogs (within a defined radius) and ranks them by compatibility, with criteria specific to play or mating. The swipe experience, familiar because it is inspired by Tinder, and chat available only after a match make contact quick, targeted, and free from the noise of social groups.

---

## Feature List

### F-01  Social login or magic-link access  · Priority: P0
**Description:** registration and access only through social login or an email magic link. No password.
**Personas served:** 1, 2
**User story:** As an owner, I want to log in without creating a password so that I can get started immediately.
**Success metrics:** registration completion rate; number of registered users.
**Constraints / notes:** on iOS, if third-party social logins are offered, Sign in with Apple must also be offered (App Store Review Guideline 4.8).

### F-02  Dog profile  · Priority: P0
**Description:** each private owner creates the profile of **one dog only** (v1), with photos and attributes: breed, size, sex, age, energy level, and character/temperament. For mating mode: pedigree, neutering, and health status, all **self-declared**.
**Personas served:** 1, 2
**User story:** As an owner, I want to describe my dog so that the app can suggest compatible dogs to me.
**Success metrics:** percentage of complete profiles; profile creation time.
**Constraints / notes:** no document verification in v1. Multiple dogs per owner: v2.

### F-03  Play and mating modes  · Priority: P0
**Description:** the owner activates one or both modes. Each mode has its own list of dogs and its own match score.
**Personas served:** 1, 2
**User story:** As an owner, I want to choose whether I am looking for a playmate, a mating partner, or both so that I see only relevant dogs.
**Success metrics:** distribution of users by mode; matches created by mode.
**Constraints / notes:** activating mating mode requires the additional fields from F-02.

### F-04  Compatibility match score  · Priority: P0
**Description:** calculation of a compatibility score between two dogs.
- **Play:** size, energy, temperament, age.
- **Mating:** breed, size, sex, age, pedigree, neutering, health.

**Personas served:** 1, 2
**User story:** As an owner, I want to see the most compatible dogs first so that I can find the right companion faster.
**Success metrics:** like rate and match rate by score range (higher scores should convert better).
**Constraints / notes:** in v1, the score is **not visible** to the user and is used only to order the list. A visible and explained score: premium, after v1.

### F-05  Location-based dog list  · Priority: P0
**Description:** only dogs within a radius of *x* km from the user's location are shown, ordered by match score from highest to lowest.
**Personas served:** 1, 2
**User story:** As an owner, I want to see only nearby dogs so that I can meet them easily.
**Success metrics:** average number of cards available per user; percentage of sessions with an empty list.
**Constraints / notes:** requires consent to geolocation (nLPD/GDPR). The value of *x* (default and range) is still to be defined.

### F-06  Search filters  · Priority: P0
**Description:** filters configurable by the user: breed, sex, temperament, and size.
**Personas served:** 1, 2
**User story:** As an owner, I want to narrow my search so that I see only the dogs I am interested in.
**Success metrics:** percentage of users who use at least one filter.
**Constraints / notes:** filters are applied together with the radius and score-based ordering.

### F-07  Swipe  · Priority: P0
**Description:** Tinder-style card interaction: swipe right to "like", swipe left to dismiss.
**Personas served:** 1, 2
**User story:** As an owner, I want to scroll through profiles with a quick gesture so that I can evaluate many dogs in a short time.
**Success metrics:** swipes per session; daily and weekly active users.
**Constraints / notes:** project requirement: UX very similar to Tinder.

### F-08  Matches and notifications  · Priority: P0
**Description:** a match occurs when both owners "like" each other. Both receive a notification.
**Personas served:** 1, 2
**User story:** As an owner, I want to know immediately when there is a match so that I can contact the other owner.
**Success metrics:** matches created; match notification open rate.
**Constraints / notes:** push notifications on iOS and Android.

### F-09  Chat after a match  · Priority: P0
**Description:** chat between the two owners, available **only** after a match, to arrange a meeting.
**Personas served:** 1, 2
**User story:** As an owner, I want to write to the other owner after a match so that we can arrange a meeting.
**Success metrics:** percentage of matches with at least one message; conversations with a reply.
**Constraints / notes:** no messaging is possible without a mutual match.

### F-10  User reporting and blocking  · Priority: P0
**Description:** the user can report a profile or conversation and block another user.
**Personas served:** 1, 2
**User story:** As an owner, I want to block or report people who behave improperly so that I can use the app safely.
**Success metrics:** reports handled within the SLA; percentage of reported users.
**Constraints / notes:** required by app-store rules for apps with user-generated content.

### F-11  Product metrics tracking  · Priority: P1
**Description:** tracking of the events needed for KPIs: downloads, registrations, swipes, matches, chats started, daily and weekly active users.
**Personas served:** product team
**User story:** As a team, we want to measure app usage so that we can understand whether the product is working.
**Success metrics:** all defined KPIs are available in a dashboard.
**Constraints / notes:** how to measure completed meetings and completed matings must be defined (see Open Questions). Tracking is subject to consent under nLPD/GDPR.

---

## Non-Functional Requirements

| ID | Category | Requirement | Acceptance criterion | Priority | Source |
|---|---|---|---|---|---|
| NFR-01 | Compliance | Compliance with nLPD (CH) and GDPR (EU/IT) | Published privacy policy; explicit and revocable consent for geolocation and analytics; access and deletion requests fulfilled within 30 days; records of processing activities and DPAs signed with all subprocessors | P0 | explicit |
| NFR-02 | Security | Protection of users' locations | Exact location is never exposed to other users or their APIs; displayed distance rounded to at least 1 km; stored location has reduced precision (≤ ~1 km) | P0 | implicit |
| NFR-03 | Security | Data encryption | TLS 1.2+ for all traffic; data at rest encrypted with AES-256 | P0 | implicit |
| NFR-04 | Security | Secure passwordless access | Single-use magic link with expiration ≤ 15 minutes; revocable sessions; OAuth tokens never stored in plain text on the device | P0 | implicit |
| NFR-05 | Compliance | App-store requirements | Sign in with Apple available on iOS; reporting and blocking available from every profile and chat; app approved on the App Store and Google Play on first submission | P0 | implicit |
| NFR-06 | Portability | Parity between iOS and Android | All P0 features available on both platforms at launch; support for the latest 3 major versions of iOS and Android | P0 | explicit |
| NFR-07 | Performance | Responsive dog list | List loading (radius + filters + score-based ordering) ≤ 2 s at p95; visual response to swipe ≤ 100 ms | P1 | implicit |
| NFR-08 | Performance | Real-time chat | Message delivered to an online recipient ≤ 1 s at p95 | P1 | implicit |
| NFR-09 | Reliability | Reliable match notifications | Match notification sent to both parties within 60 s in 99% of cases; no chat messages lost (guaranteed delivery with deduplication) | P1 | implicit |
| NFR-10 | Availability | Service availability | Monthly API uptime ≥ 99.5% | P1 | implicit |
| NFR-11 | Compliance | Data localization | Personal data hosted in Switzerland or the EEA (countries with reciprocal CH/EU adequacy) | P1 | implicit |
| NFR-12 | Security | Moderation | Reports handled within 24 h; a blocked user can no longer see the profile of the user who blocked them or write to them | P1 | implicit |
| NFR-13 | Observability | Measurable KPIs | Registration, swipe, match, and chat events tracked; dashboard updated at least every 24 h; app errors monitored with alerts within 5 min | P1 | explicit |
| NFR-14 | Usability | Accessible swiping | Swipe gestures accompanied by equivalent buttons; WCAG 2.1 AA compliance for contrast and target size | P1 | implicit |
| NFR-15 | Observability | Match-score quality | Like and match rates monitored by score decile; at least monthly reporting | P2 | implicit |
| NFR-16 | Maintainability | Configurable score | Match-score criterion weights modifiable server-side without releasing a new app version | P2 | implicit |

### Unaddressed Categories
These NFR categories do not yet have complete coverage in the specification:
- **Scalability**: expected volumes (users, swipes, messages) for the launch in Switzerland and Italy have not been defined; they are needed to size the infrastructure.
- **Usability (languages)**: the languages in which the app will be available have not been defined; Switzerland has multiple national languages.
- **Compliance (data retention)**: retention periods for chats, inactive accounts, and location data have not been defined.

### Open Questions
- **Q1 [Scalability]:** how many registered and active users do you expect 6 and 12 months after launch?
  *Impact:* infrastructure and cost sizing; NFR-07 and NFR-10 thresholds.
- **Q2 [Usability]:** will the app be available only in Italian at launch, or also in German, French, and English?
  *Impact:* translation scope and reachable Swiss markets.
- **Q3 [Compliance]:** how long should chats, inactive accounts, and locations be retained?
  *Impact:* privacy policy and automatic deletion policies (NFR-01).
- **Q4 [Security]:** is there a minimum registration age, and must it be verified?
  *Impact:* terms of use, consent under nLPD/GDPR, and app-store requirements.

---

## Out of Scope — v1
- Premium profile for breeders with multiple dogs (mating only).
- Multiple dogs per private owner (v2).
- Compatibility score visible on the card and explanation of the score (premium).
- Automatic verification of uploaded documents (pedigree, health).
- Web version.
- Markets other than Switzerland and Italy.

---

## Open Questions
- How are **meetings that actually took place** and **completed matings** measured, given that they happen outside the app? For example, with confirmation in the app or a survey after the chat.
- What is the default value of the *x* km radius? Can the user change it, and within what range?
- How are the criteria weighted in the match score? With fixed rules initially or with a model that learns from swipes?
- Which Swiss and Italian regulations govern mating and breeding dogs between private owners? Do they impose constraints or require warnings in the app?
- In play mode, does the sex filter have the same logic as in mating mode?
- What pricing model and features will the premium offering launch with, both for private owners (visible score) and breeders?
- How can sufficient density of nearby dogs be ensured at launch (the cold-start problem)? Should the launch begin in pilot cities?
