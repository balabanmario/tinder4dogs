# Implementation Plan

- [ ] 1. Foundation: add the profile and binary-photo persistence schema
- [x] 1.1 Create an append-only Liquibase migration for dog profiles and photos
  - Add reusable owner identity, profile attributes, optional mating declarations, completeness metadata, and compatibility-safe defaults for existing dog rows.
  - Add the binary photo table with profile ownership, metadata, ordering, cascade deletion, and a unique owner constraint.
  - Register the migration in the master changelog with an explicit rollback that removes only the objects this migration introduced.
  - The migration can be applied and rolled back without editing prior changesets.
  - _Boundary: Database schema_
  - _Requirements: 1.1, 1.2, 1.4, 2.1, 3.3, 4.1, 5.4_

- [ ] 2. Core persistence model: represent profile data and photo ownership
- [x] 2.1 Expand the dog profile persistence model
  - Add the reusable UUID owner identifier and physical, behavioral, and optional mating fields while preserving `name`, `preferences`, and the match-compatible gender surface so the `match` package requires no change.
  - Represent self-declared mating information without a verified status.
  - Map the new `sex` field onto the existing gender semantics so existing scoring reads continue to work without editing `match` code.
  - The application can load an existing dog row and a new complete profile without Hibernate validation errors.
  - _Boundary: Dog_
  - _Depends: 1.1_
  - _Requirements: 1.1, 1.4, 2.1, 2.3, 2.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.3_

- [x] 2.2 Add profile and photo persistence queries
  - Add owner lookup required to detect an existing profile before creation.
  - Add a binary photo entity, the one-to-many photo association on `Dog`, and persistence operations scoped by both dog id and photo id.
  - Keep photo binary content out of ordinary profile loading and response mapping.
  - A photo belonging to another dog cannot be returned through the profile-scoped lookup.
  - _Boundary: DogPhoto, DogPhotoRepository, DogRepository_
  - _Depends: 2.1_
  - _Requirements: 1.2, 3.1, 3.3, 3.4_

- [ ] 3. Domain behavior: validate, create, and retrieve profiles
- [ ] 3.1 Implement profile validation, ownership, mating, and completeness behavior
  - Require a reusable UUID `ownerId` in the profile request and use it unchanged for uniqueness checks and persistence.
  - Validate required attributes and constrained values before persistence, including actionable missing-field and invalid-field results.
  - Allow non-mating profiles without mating fields and label supplied mating fields as self-declared.
  - Calculate complete status and exact missing required fields, and capture profile creation duration and completion outcome for observation.
  - A valid request produces one profile for the supplied owner id; a second request with that id is rejected without changing the existing profile.
  - _Boundary: DogProfileService_
  - _Depends: 2.1, 2.2_
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_

- [ ] 3.2 Implement binary photo validation and partial acceptance
  - Accept binary photo inputs with a named-constant per-file size limit, an accepted-media-type list, and a named-constant maximum photo count.
  - Return per-file validation errors for unsupported, oversized, or excess-count photos and omit only those invalid files.
  - Persist the valid profile and valid photos transactionally even when some submitted photos are invalid.
  - A profile with one valid and one invalid photo is created with the valid photo retained and the invalid photo reported.
  - _Boundary: DogProfileService, DogPhoto_
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [ ] 3.3 Implement service-owned profile and photo retrieval
  - Provide profile retrieval by id and photo retrieval scoped by both dog id and photo id through the domain service.
  - Return photo binary content with its stored media type only through the service retrieval contract.
  - Report a missing profile or a photo belonging to another dog as explicit domain outcomes for the controller to translate.
  - The controller can serve both retrieval endpoints without querying repositories directly.
  - _Boundary: DogProfileService_
  - _Depends: 2.1, 2.2_
  - _Requirements: 2.3, 2.4, 3.4, 4.2, 4.4, 5.1_

- [ ] 4. Integration: expose the profile and photo HTTP contracts
- [ ] 4.1 Wire multipart profile creation and service-delegated retrieval endpoints
  - Accept one `profile` JSON part and zero or more `photos` binary parts in a single multipart request; the JSON part carries `ownerId`, `name`, `preferences`, and all profile attributes.
  - Size the transport-level multipart maximum from the named-constant per-file limit and maximum photo count so the framework never rejects a request the service could validate.
  - Return profile attributes, completeness, missing fields, self-declared mating information, photo metadata, and per-file photo errors without embedding binary content.
  - Delegate retrieval to the domain service and map missing profiles, unrelated photos, duplicate owners, and malformed JSON parts to the documented HTTP responses while preserving existing dog reads and matching inputs.
  - A multipart profile creation request returns the created profile and photo metadata, and retrieval requests return profile metadata or the stored photo bytes for that profile only.
  - _Boundary: DogProfileController_
  - _Depends: 3.1, 3.2, 3.3_
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.4, 4.2, 4.4, 5.1, 5.2, 5.3_

- [ ] 5. Validation: verify profile behavior and regression safety
- [ ] 5.1 Add service tests for profile rules and photo handling
  - Test reusable owner identity, one-profile uniqueness, missing and invalid attributes, optional mating data, self-declared labeling, and completeness results.
  - Test valid binary photo acceptance, excess-count handling, and invalid-photo omission while the valid profile persists.
  - Test service-scoped retrieval outcomes, including rejection of a photo belonging to another dog.
  - Test creation timing and completion outcomes are observable without exposing profile content or binary data in logs.
  - The service test suite fails if duplicate-owner rejection, completeness calculation, self-declared labeling, partial photo acceptance, or retrieval scoping is removed.
  - _Boundary: DogProfileServiceTest_
  - _Depends: 3.1, 3.2, 3.3_
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.2, 2.3, 2.4, 3.2, 3.4, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_

- [ ] 5.2 Add HTTP contract tests for profile endpoints
  - Verify multipart creation with one JSON part and binary photo parts, metadata-only profile responses, per-file invalid-photo reporting, duplicate-owner conflict, and malformed JSON-part rejection.
  - Verify binary retrieval with the stored media type and rejection of a photo id belonging to another dog.
  - The HTTP test suite demonstrates the documented success and error responses for creation and retrieval.
  - _Boundary: DogProfileControllerTest_
  - _Depends: 4.1_
  - _Requirements: 1.2, 1.3, 2.3, 3.1, 3.2, 3.4, 4.4, 5.1_

- [ ] 5.3 Add migration compatibility verification
  - Verify the Liquibase migration applies alongside existing seed rows and that Hibernate validation passes with the fully expanded model, including the photo mapping.
  - Verify rollback removes only the objects the migration introduced and leaves prior changesets untouched.
  - The verification demonstrates the expanded schema coexists with existing dog reads and matching inputs.
  - _Boundary: Liquibase migration verification_
  - _Depends: 1.1, 2.2_
  - _Requirements: 1.1, 2.3, 3.3, 4.1, 5.4_

- [ ] 5.4 Run the project verification tasks and fix regressions
  - Run `mise run test` and `mise run build` using the repository-defined commands.
  - Confirm existing dog and match behavior remains compatible with expanded profile data.
  - Confirm no migration checksum or formatting errors are introduced.
  - Both project tasks complete successfully after the implementation and all profile regression tests remain green.
  - _Boundary: Integration validation_
  - _Requirements: 1.1, 2.3, 3.3, 4.1, 5.4_

## Implementation Notes

- Use `mise run test` / `mise run build`, never bare `mvn`: the default JDK on
  the machine is older than the toolchain mise pins, and surefire fails with
  "class file version 69.0" before any test runs.
- Maven is offline here. Do not reach for a library that is not already on the
  classpath — `spring-data-commons` resolves `PartTree` but not `PropertyPath`.
- `Dog.sex` carries the gender semantics `match` scores on, exposed as a
  `gender` alias plus a `gender`-named secondary constructor. `gender` has no
  default so it disambiguates from the all-default primary constructor. The
  `match` package and its tests are untouched; keep it that way.
- Any Hibernate `MetadataSources` test must register every entity Dog
  associates with (now `DogPhoto`), or boot fails with "does not belong to the
  same persistence unit".
