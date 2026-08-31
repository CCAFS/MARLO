# Phase Topology & Replication Manager — Requirements

**Spec ID:** EPIC-PHASE-TOPOLOGY
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** IBD team lead, PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-24
**Related PRD sections:** docs/prd.md — planning / reporting cycle management
**Related System Design sections:** docs/system-design/design.md — CRP Admin screens, phase timeline switcher
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (data model), §5 (save pipeline)
**Companion ai-context docs:** reports/ai-context/persistence-replication-managerimpl.md, reports/ai-context/save-validation-matrix.md, reports/ai-context/interceptor-validator-playbook.md

---

## 1. Overview

MARLO's phase replication topology is encoded twice: as a singly linked list (`phases.next_phase`) and as
hardcoded pointer arithmetic (`getNext()`, `getNext().getNext()`) repeated across ~130 source files. Because the
topology is implicit, phases cannot be created, reordered or retired from the application: a new cycle ships as a
hand-written Flyway migration, and any structural change silently reinterprets the replication destination of every
row already stored.

This epic converts the topology into explicit data (a replication edge graph, grouped into a frozen set of
profiles), routes every replication path through a single resolver, and only then exposes a Phases Manager in CRP
Admin that can create, reorder, retire and delete phases and edit which phase replicates to which.

The epic decomposes into four stages. Stages 1–3 produce no user-visible change; stage 4 is the feature. The
ordering is forced, not preferred (see MIG-002).

## 2. Problem Statement

Three operational pains, all traceable to the same root cause.

**Phases cannot be created without engineering.** Opening a new planning / reporting cycle requires a Flyway
migration authored by hand, one INSERT per phase, with `next_phase` chained manually. The existing
`CrpPhasesAction` only toggles `visible`, `editable`, dates and the default landing phase; `name`, `year`,
`description`, `upkeep` and `next` round-trip as hidden inputs, so the chain topology is not editable at all.
`PhaseManager.deletePhase()` exists and has zero callers.

**The replication rules are invisible and inconsistent.** Five distinct rule variants exist across the 119
`ManagerImpl` classes that walk the chain, plus 15 copies of the same ternary in 11 `marlo-web` actions. Nobody can
answer "which phase replicates to which, for this section" without reading the source. Several variants are
demonstrably accidental (see FN-012, and the Open Questions).

**Any structural change corrupts data silently.** The `Reporting` rule resolves its destination as
`phase.getNext().getNext()`, which does not mean "two phases forward" — it means "skip the following POWB and land
on the UpKeep". Inserting or reordering a phase re-points that arithmetic, so reporting saves start overwriting the
planning phase the rule exists to protect. The rows already written under the previous topology carry no marker
distinguishing them, and reordering back does not undo the writes.

Cited evidence for all three, with file and line references, is consolidated in `agent-context.md` in this folder.

## 3. In-Scope Requirements

### Functional

- **EPIC-PHASE-TOPOLOGY-FN-001** — A single resolver MUST be the only source of replication destinations. It MUST
  expose `resolveTargets(Phase source, ReplicationProfile profile)` returning an ordered `List<Phase>`.
- **EPIC-PHASE-TOPOLOGY-FN-002** — The set of replication profiles MUST be seeded from the variants that exist in
  the current code and MUST be frozen. Creating a profile MUST require a Decision Log entry in this spec.
- **EPIC-PHASE-TOPOLOGY-FN-003** — The seeded edge set MUST reproduce the current runtime behaviour exactly, for
  every phase of every Global Unit, before any manager is migrated.
- **EPIC-PHASE-TOPOLOGY-FN-004** — After migration, no `ManagerImpl` and no `marlo-web` action MAY read
  `Phase.getNext()` to derive a replication destination.
- **EPIC-PHASE-TOPOLOGY-FN-005** — Predicates that depend on the entity being saved rather than on the phase pair
  (currently `isPublication`, used by 13 managers) MUST stay in Java and MUST NOT be modelled as edges.
- **EPIC-PHASE-TOPOLOGY-FN-006** — A CRP Admin user with the admin base permission MUST be able to create a phase.
- **EPIC-PHASE-TOPOLOGY-FN-007** — A CRP Admin user MUST be able to reorder phases. Reordering MUST affect the
  presentation sequence only and MUST NOT alter the replication edge set.
- **EPIC-PHASE-TOPOLOGY-FN-008** — A CRP Admin user MUST be able to edit the replication edges of a profile, as a
  source-phase x target-phase matrix scoped to one Global Unit.
- **EPIC-PHASE-TOPOLOGY-FN-009** — A CRP Admin user MUST be able to set `visible` and `editable` independently, and
  the UI MUST state that a non-visible phase still receives replicated data.
- **EPIC-PHASE-TOPOLOGY-FN-010** — A CRP Admin user MUST be able to delete a phase only when it has no dependent
  rows in any phase-scoped table. Otherwise the system MUST refuse and report the blocking tables.
- **EPIC-PHASE-TOPOLOGY-FN-011** — Editing an edge MUST NOT move rows already written. The UI MUST report which
  phases gain and lose targets and MUST link to the existing bulk replication tooling.
- **EPIC-PHASE-TOPOLOGY-FN-012** — The system MUST NOT permit a cycle in the edge graph, and MUST NOT permit an
  edge whose source and target belong to different Global Units.

### Non-Functional

- **EPIC-PHASE-TOPOLOGY-NF-001** — `resolveTargets` MUST NOT recurse. Resolving destinations MUST cost one query
  plus an in-memory loop, replacing the current per-level `phaseDAO.find()` plus full `Phase` collection load.
- **EPIC-PHASE-TOPOLOGY-NF-002** — A malformed graph MUST NOT be able to raise `StackOverflowError` at save time.
- **EPIC-PHASE-TOPOLOGY-NF-003** — Setting `editable = 1` MUST surface its cost: the `getPermissions` stored
  procedure expands permissions across every editable phase in 27 sub-selects.

### Data

- **EPIC-PHASE-TOPOLOGY-DA-001** — A `phase_replication_profiles` table MUST hold the frozen profile set.
- **EPIC-PHASE-TOPOLOGY-DA-002** — A `phase_replication_edges` table MUST hold `(profile, source_phase,
  target_phase, sequence)` with the standard MARLO audit columns.
- **EPIC-PHASE-TOPOLOGY-DA-003** — A unique constraint MUST be added on `phases (global_unit_id, description,
  year, upkeep)`. `PhaseDAO.findCycle()` treats that tuple as a natural key and returns `list.get(0)` without
  checking uniqueness.
- **EPIC-PHASE-TOPOLOGY-DA-004** — `phases` MUST gain real audit columns (`created_by`, `modified_by`,
  `modification_justification`, `active_since`) and the matching `Phases.hbm.xml` mapping. `Phase.isActive()` is
  hardcoded to `true`, `getModifiedBy()` returns a fabricated `User` with id 3, and
  `getModificationJustification()` returns the empty string.
- **EPIC-PHASE-TOPOLOGY-DA-005** — `phases` MUST gain a `sequence` integer, unique per Global Unit, as the single
  presentation order. `startDate` MUST NOT be the ordering key.
- **EPIC-PHASE-TOPOLOGY-DA-006** — An `is_active` column MUST NOT be added to `phases`. A soft-deleted phase that
  remains a replication target would be more misleading than the current `visible` flag.
- **EPIC-PHASE-TOPOLOGY-DA-007** — `next_phase` MUST be retained until stage 3 completes, for parity verification,
  and MUST be dropped only after no code path reads it.

### UI

- **EPIC-PHASE-TOPOLOGY-UI-001** — All new user-facing strings MUST be i18n keys in `global.properties`, plus
  `custom/*.properties` where a program overrides them.
- **EPIC-PHASE-TOPOLOGY-UI-002** — The edge matrix MUST be readable at the scale of a real Global Unit (20+ phases
  x 4 profiles) and MUST scroll horizontally inside its own container.
- **EPIC-PHASE-TOPOLOGY-UI-003** — Any CSS or JS asset touched MUST have its `?YYYYMMDD` cache-busting parameter
  bumped in the referencing FTL.

### Security

- **EPIC-PHASE-TOPOLOGY-SEC-001** — Every new or modified admin route MUST declare `crpAdminStack`.
- **EPIC-PHASE-TOPOLOGY-SEC-002** — The save path MUST follow the constitutional pattern: `Action.validate()`
  guarded by `if (save)`, then a `CrpPhasesValidator`, then the manager chain. `CrpPhasesAction` currently has
  neither, while already writing `editable`.
- **EPIC-PHASE-TOPOLOGY-SEC-003** — Every phase and edge mutation MUST be attributable to a user and carry a
  justification.

### Operations

- **EPIC-PHASE-TOPOLOGY-OPS-001** — A parity comparator MUST exist that, for every (phase, profile) pair of every
  Global Unit, contrasts resolver output against the current hardcoded logic and reports differences. The
  repository contains 3 test files in total, so this comparator is the only available safety net.
- **EPIC-PHASE-TOPOLOGY-OPS-002** — Saving a phase MUST invalidate the cached phase lists (`crp_phases`,
  `crp_all_phases`, `crp_phases_impact`, `crp_current_phase`) for all sessions, not only the acting admin's.

### Migration

- **EPIC-PHASE-TOPOLOGY-MIG-001** — The edge seed MUST be derived from each Global Unit's live chain, not from a
  hardcoded assumption about the three-phases-per-year cycle.
- **EPIC-PHASE-TOPOLOGY-MIG-002** — The admin UI MUST NOT ship before every manager and action reads the resolver.
  Edge editing against unmigrated call sites would either be a no-op or diverge from the stored graph.

## 4. Out-of-Scope

- **The impact pathway family (16 managers).** Replication there is not in the save path: it is a separate
  `replicate(entity, initialPhase)` invoked by hand from the action layer, and parts of it do not work
  (`CrpAssumptionManagerImpl.replicate` is an empty loop; `CrpProgramOutcomeManagerImpl.replicate` has its
  sub-idos, milestones and indicators commented out). Bringing it into the graph is a redesign, tracked separately.
- **Retroactive replication.** Editing an edge does not move rows already written. Backfilling stays with the
  existing superadmin bulk replication tooling.
- **Automatic population of a new phase.** Which projects enter a new phase (`project_phases`) stays with
  `CrpProjectPhases`.
- **Creating new profiles from the UI.** Frozen set only (FN-002).
- **Removing the hardcoded year literals** (20 in Java, ~40 in FTL) unrelated to topology.
- **The `funding_sources_info` missing-justification defect**, already covered by
  `docs/specs/bugfix/funding-source-phase-replication`.

## 5. Personas Affected

- **CRP / Platform Admin (primary)** — gains the ability to open a cycle without an engineering request, and
  becomes accountable for the replication graph.
- **PMU lead** — owns the three rule decisions in Open Questions; they are product decisions, not technical ones.
- **Cluster coordinator / Project leader** — indirectly: a mis-set edge silently overwrites their planning data.
- **QA reviewer** — section statuses depend on phase membership; a new phase starts empty until bulk replication
  runs.
- **MARLO support** — gains a readable answer to "why did this value appear in the next cycle", currently only
  answerable by reading source.

## 6. Acceptance Criteria

**AC-001 (FN-003, OPS-001)** — Given every Global Unit's live phase chain, when the parity comparator runs before
any manager migration, then for every (phase, profile) pair the resolver's ordered target list MUST equal the list
produced by the current hardcoded logic, and the report MUST be empty.

**AC-002 (FN-004, NF-001)** — Given a migrated section, when an entity is saved in a phase with N forward targets,
then the destinations MUST come from one resolver call, and no `getNext()` call MUST appear in the stack.

**AC-003 (FN-012, NF-002)** — Given an attempt to save an edge set that forms a cycle, when the admin submits,
then validation MUST reject it with a field-level error, nothing MUST be persisted, and no save in any phase of
that Global Unit MUST be able to raise `StackOverflowError`.

**AC-004 (FN-006, DA-003)** — Given a Global Unit whose chain ends in a Reporting phase, when the admin creates a
phase whose `(description, year, upkeep)` duplicates an existing one, then the save MUST be rejected with the
duplicate named.

**AC-005 (FN-007)** — Given a reordering of the presentation sequence, when the admin saves, then the timeline
order MUST change, and a diff of `phase_replication_edges` MUST be empty.

**AC-006 (FN-010)** — Given a phase with at least one dependent row, when the admin requests deletion, then the
system MUST refuse and MUST list the blocking tables. Given a phase with none, deletion MUST succeed and MUST
leave no dangling `custom_parameters` value (`current_phase`, `crp_aiccra_af_start_phase`).

**AC-007 (FN-011)** — Given an edge change, when the admin saves, then the confirmation MUST list the phases that
gained and lost targets, MUST state that existing rows are unaffected, and MUST link to the bulk replication
screen.

**AC-008 (FN-009)** — Given a phase set to `visible = 0`, when a user saves in an upstream phase that has that
phase as a target, then the data MUST still be replicated into it, and the admin UI MUST have stated so before the
change was saved.

**AC-009 (SEC-002, DA-004)** — Given any phase or edge mutation, when it is persisted, then `modified_by` MUST
name the acting user and `modification_justification` MUST be non-empty.

**AC-010 (OPS-002)** — Given two concurrent sessions, when an admin saves a phase change, then the other
session's next request MUST see the new phase list without re-login.

**AC-011 (NF-003)** — Given a phase being switched to `editable = 1`, when the admin submits, then the UI MUST
have displayed the permission expansion warning before the save.

## 7. Constitutional Compliance Checklist

- [ ] Phase replication: this epic redefines the contract. `reports/ai-context/persistence-replication-managerimpl.md`
      MUST be rewritten in stage 3, and it currently documents `next.next` as the standing contract.
- [ ] Save validation: `CrpPhasesAction.validate()` guarded by `if (save)` + new `CrpPhasesValidator` +
      `crpAdminStack`. Currently absent (SEC-002).
- [ ] Permissions: every route declares `crpAdminStack`. No new permission strings; the `getPermissions` SP
      substitutes `{0}` with `acronym:description:year` and already covers new phases automatically.
- [ ] Specificity: not applicable — this is not a per-Global-Unit feature flag. If a phased rollout per Global Unit
      is required, it goes through `parameters` + `custom_parameters` with constants in both `APConstants.java`.
- [ ] Migrations: every schema change ships as Flyway under the `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__` name.
- [ ] i18n: no hardcoded user-facing strings (UI-001).
- [ ] License header: GPL header on every new Java file.
- [ ] Code style: 2-space indent, 120 char limit, Checkstyle gate. Note: `mvn checkstyle:check` cannot run in this
      checkout (`maven-checkstyle-plugin:2.9.1` is incompatible with Java 17); verify manually until fixed.
- [ ] REST: not applicable — no `/api/*` surface.
- [ ] Audit: DA-004 makes `Phase` genuinely auditable through `IAuditLog` / `HibernateAuditLogListener`.
- [ ] Dependency floors: untouched.
- [ ] Branching: feature branches from `staging`, merged back to `staging`. Never `main`.
- [ ] Constitutional change process: this epic itself is the vehicle, per CLAUDE.md. Requires review by the IBD
      team lead plus at least one of PMU lead, QA lead, Tech lead.

## 8. Open Questions

**Product decisions — blocking stage 1, owned by PMU lead.** Seeding the edge table forces each of these to be
made explicitly; they are currently decided by accident.

- **OQ-001** — Is the Reporting skip one-time or periodic? Today `AR(Y)` skips `POWB(Y+1)` but the recursion then
  writes `POWB(Y+2)`, `POWB(Y+3)` and onward, because the destination method never re-checks `description`. So the
  real invariant is "reporting does not write the immediately following planning", not "reporting never writes
  planning". Seeding as-is enshrines a probable bug; changing it alters 84 sections.
- **OQ-002** — Should the Highlights profile use `next` or `next.next`? `ProjectHighlightInfoManagerImpl:116-121`
  uses `next`, so reporting highlights do write into the following POWB — the opposite of the canonical rule.
- **OQ-003** — Does the impact pathway family enter the graph, and if so is its current partial behaviour the
  target or is it a defect to fix? Out-of-scope for v1 as written; needs confirmation.

**Environment and scope — blocking the plan shape.**

- **OQ-004** — Is there an environment with a production data copy where the OPS-001 comparator can run? Without
  it the migration has no safety net and stage 3 must be redesigned around per-section feature flags.
- **OQ-005** — Is the frozen profile set acceptable, or does the requirement include creating profiles and
  reassigning sections from the UI? The latter adds a section-to-profile assignment screen and makes ~100 managers
  resolve their profile from data rather than a constant.

**Data verification — cannot be answered from the repository.**

- **OQ-006** — `V2_6_0_20181126_1102__GenebankPhases.sql` clones Global Unit 24's phases into Global Unit 26 with
  an `INSERT ... SELECT` that copies `next_phase` verbatim, so unit 26's phases would point their `next` at unit
  24's phases. The follow-up migration `V2_6_0_20190212_1513` corrected years only. Is the cross-tenant chain live?
- **OQ-007** — Phase id 377 carries `year = 2029` (`V2_6_0_20190212_1513`), between neighbours at 2019 and 2020.
  Confirm it is a typo for 2019 and whether data was authored against it.

## 9. Decision Log

- 2026-08-24 — Model the topology as an explicit edge graph rather than deriving it from `next_phase` —
  Rationale: the `next.next` arithmetic makes every structural change reinterpret the destination of already
  stored rows, and materialised targets additionally remove the recursion and the cycle hazard.
- 2026-08-24 — Group the edges into a frozen set of profiles rather than one global graph — Rationale: the 119
  managers apply five distinct rule variants that map to domain families (funding sources, deliverable metadata,
  highlights, impact pathway). A single graph would silently change behaviour for ~15% of sections.
- 2026-08-24 — Keep `isPublication` in Java rather than modelling it as edges — Rationale: it is a per-entity
  predicate, not a property of the phase pair. Factoring it out also collapses a sixth observed group into the
  canonical profile, reducing six variants to five.
- 2026-08-24 — Do not add `is_active` to `phases` — Rationale: `phases` has no soft-delete today, and a
  soft-deleted phase that remained a replication target would be more misleading than `visible = 0`. The honest
  semantics are freeze (`editable = 0`), hide (`visible = 0`) and delete.
- 2026-08-24 — Replace `startDate` ordering with an explicit `sequence` column — Rationale: two orders exist today
  (`next_phase` semantic, `startDate` presentation) with nothing validating coherence, and the date inputs are
  already reachable in `crpPhases.ftl` behind `display:none`.
- 2026-08-24 — Gate the admin UI behind completion of the manager migration (MIG-002) — Rationale: edge editing
  against unmigrated call sites is either a no-op or a divergence. Consequence: stages 1-3, roughly 80% of the
  effort, produce no user-visible change.
- 2026-08-24 — Exclude the impact pathway family from v1 — Rationale: parts of it do not work today, so modelling
  it as edges would either enshrine broken behaviour or fix it silently. Both need a separate decision.
