# Admin → Management — PMU Liaison Institution Missing Or Dangling — Requirements

**Spec ID:** BUG-ADMIN-PMULIAISON-001
**Status:** Implemented (partial — see §8)
**Owner:** IBD Team
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-02
**Related PRD sections:** docs/prd.md — administration and Global Unit onboarding
**Related System Design sections:** docs/system-design/design.md — admin module navigation
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (data model), §5 (save pipeline)
**Companion ai-context docs:** reports/ai-context/save-validation-matrix.md,
reports/ai-context/persistence-replication-managerimpl.md
**Companion agent context:** docs/specs/domain/admin/agent-context.md

## 1. Overview

This spec covers the **Admin → Management** screen (`/admin/{crp}/management`) as far as it touches the liaison
institution model: the Program Management Unit team, the flagship leaders it assigns, and the
`liaison_institutions` / `liaison_users` rows both produce. It does not attempt to specify the other eighteen actions
of the admin module.

It exists because a production incident showed that the screen assumed a piece of per-Global-Unit configuration
(`crp_cu`) always resolves, that its target row always exists, and that the tables it reads are never empty. None of
the three holds in a newly provisioned Global Unit, which is the shape of every AICCRA-derived environment created
after the platform started hosting more than one unit.

## 2. Problem Statement

An administrator adding a person to the Program Management Unit team of AICCRA_III could not complete the save. The
request died on an unhandled exception page:

```
org.hibernate.exception.ConstraintViolationException: could not execute statement
Caused by: java.sql.SQLIntegrityConstraintViolationException: Column 'institution_id' cannot be null
insert into liaison_users (global_unit_id, is_active, institution_id, user_id) values (?, ?, ?, ?)
  at CrpAdminManagmentAction.savePmuRoleData(CrpAdminManagmentAction.java:1224)
```

Three distinct defects sat behind it.

**The configuration was dangling.** `crp_cu` — the per-unit parameter holding the `liaison_institutions.id` of the
PMU record — was `336` in the affected environment, an id copied from the source AICCRA schema. That schema's
`liaison_institutions` rows were not copied, so nothing resolved. `LiaisonInstitutionMySQLDAO.find()` returns `null`
for a missing row, and the action assigned that `null` to the new `LiaisonUser` and saved it against a `NOT NULL`
column.

**The failure left partial state.** The user role and the "you have been assigned to the Project Management
Committee" e-mail were both committed *before* the failing insert, so every attempt added a role with no liaison
record behind it and notified the person about it.

**Empty tables crashed the same save.** `LiaisonUserMySQLDAO.findAll()` and `CrpProgramLeaderMySQLDAO.findAll()`
returned `null` rather than an empty list when their table had no rows, and the screen streams both results directly.
In a unit with no liaison users, the save failed with a `NullPointerException` before reaching any of the above.

Operationally this makes a new Global Unit impossible to configure through the UI: the PMU team cannot be populated,
and the one record that would fix it — the PMU liaison institution — has no screen anywhere in MARLO that can create
it.

## 3. In-Scope Requirements

### Functional

- BUG-ADMIN-PMULIAISON-001-FN-001 — The system MUST resolve the PMU liaison institution of the logged Global Unit before
  persisting any Program Management Unit role assignment.
- BUG-ADMIN-PMULIAISON-001-FN-002 — When `crp_cu` does not resolve to an existing `liaison_institutions` row, the system MUST
  attempt, in order: lookup by the acronym `PMU` within the logged Global Unit; the first active liaison institution
  of the unit that belongs to no `crp_program`; creation of a new one.
- BUG-ADMIN-PMULIAISON-001-FN-003 — A PMU liaison institution created by the system MUST carry `acronym = "PMU"`, because the
  Annual Report screens and the REST v2 items locate it by that literal.
- BUG-ADMIN-PMULIAISON-001-FN-004 — A PMU liaison institution created or resolved through a fallback MUST NOT set an
  `institution`, and MUST NOT set a `crp_program`.
- BUG-ADMIN-PMULIAISON-001-FN-005 — When a fallback is used, the system MUST repair the `crp_cu` custom parameter and the
  session value so that subsequent requests no longer carry the dangling id.
- BUG-ADMIN-PMULIAISON-001-FN-006 — When no PMU liaison institution can be resolved or created, the system MUST NOT persist a
  `LiaisonUser`, MUST NOT persist the user role, MUST NOT send the assignment notification, and MUST report the
  failure as an invalid field on the screen.
- BUG-ADMIN-PMULIAISON-001-FN-007 — Assigning a flagship or regional leader whose program has no liaison institution MUST be
  recorded in the log instead of silently producing a leader with no liaison user.
- BUG-ADMIN-PMULIAISON-001-FN-008 — Reading the Global Unit's liaison users or program leaders MUST tolerate an empty table.

### Non-Functional

- BUG-ADMIN-PMULIAISON-001-NF-001 — The screen MUST remain usable on a Global Unit that has no `crp_programs`, no
  `liaison_institutions` and no `liaison_users`.
- BUG-ADMIN-PMULIAISON-001-NF-002 — No fix in this spec may change the behaviour of a Global Unit whose `crp_cu` already
  resolves; the resolution chain MUST short-circuit on the first step in that case.

### Data

- BUG-ADMIN-PMULIAISON-001-DA-001 — `liaison_users.institution_id` (FK to `liaison_institutions`), `liaison_users.user_id`
  and `liaison_users.is_active` are `NOT NULL`; no code path may insert a row without all three.
- BUG-ADMIN-PMULIAISON-001-DA-002 — A PMU liaison institution is identified by `crp_program IS NULL AND institution_id IS
  NULL` for a given `global_unit_id`. That invariant MUST be preserved by every writer.
- BUG-ADMIN-PMULIAISON-001-DA-003 — `crp_cu` is a derived identifier. It MUST be set from the id of a row created for the
  same Global Unit, and MUST NOT be copied between schemas.
- BUG-ADMIN-PMULIAISON-001-DA-004 — No schema change is required by this spec. Not applicable beyond the invariants above.

### UI

- BUG-ADMIN-PMULIAISON-001-UI-001 — The failure surface of FN-006 MUST use an i18n key
  (`programManagement.pmuLiaisonInstitution.missing`), not literal text.

### Security

- BUG-ADMIN-PMULIAISON-001-SEC-001 — Creating or repairing the PMU liaison institution MUST happen only inside the
  `crpAdminStack`-gated save of the Management screen, never on a read path, a login, or a Global Unit switch, so that
  master data is never created implicitly by a read-only user.

### Operations

- BUG-ADMIN-PMULIAISON-001-OPS-001 — Every fallback, creation and parameter repair MUST emit a WARN naming the Global Unit,
  the old `crp_cu` and the new one.
- BUG-ADMIN-PMULIAISON-001-OPS-002 — A resolution failure MUST emit an ERROR naming the Global Unit and the unresolved id.

## 4. Out-of-Scope

- The other admin actions (`crpPhases`, `crpUsers`, `locations`, `targetUnits`, `siteIntegration`,
  `feedbackManagement`, `homepageBannerManagement`, `timelineManagement`, `portfolioManagement`, …).
- A UI to create or edit a PMU liaison institution directly. Noted as a product gap in §8.
- The `findAll()`-returns-null anti-pattern beyond the two DAOs this screen depends on. 433 of 446 DAOs share it; a
  blanket change is disproportionate to this incident.
- Retiring `crp_cu` in favour of the `acronym = "PMU"` convention already used by five other consumers. Noted in §8.
- Any data migration that moves existing `liaison_users` between liaison institutions.

## 5. Personas Affected

- **CRP Admin** (primary) — cannot populate the Program Management Unit team of a new Global Unit; every attempt
  leaves a role assigned, an e-mail sent, and no liaison record.
- **PMU member** (secondary) — receives an assignment notification for a role that the platform does not fully
  recognise. Without the liaison record they are not offered as a Management Liaison on a project and, when POWB /
  Annual Report are enabled, do not reach the PMU section.
- **Platform operator** — onboarding a new Global Unit stalls with no actionable message; the failure appears as a
  database constraint violation.

## 6. Acceptance Criteria

**AC for FN-001, FN-002, FN-005:**
- Given a Global Unit whose `crp_cu` points to a non-existent `liaison_institutions` row,
- When a CRP Admin adds a new person to the Program Management Unit team and saves,
- Then a PMU liaison institution for that Global Unit MUST exist afterwards,
- And `custom_parameters` for `crp_cu` MUST hold its id,
- And the session value for `crp_cu` MUST hold the same id,
- And a `liaison_users` row MUST exist with that `institution_id`, the person's `user_id`, the unit's
  `global_unit_id` and `is_active = 1`,
- And no exception page MUST be rendered.

**AC for FN-003, FN-004:**
- Given the system had to create the PMU liaison institution,
- Then the created row MUST have `acronym = 'PMU'`, `institution_id IS NULL` and `crp_program IS NULL`.

**AC for FN-006:**
- Given the PMU liaison institution can neither be resolved nor created,
- When a CRP Admin adds a person to the team and saves,
- Then no `liaison_users` row MUST be inserted,
- And no `user_roles` row MUST be inserted,
- And no assignment notification MUST be sent,
- And the screen MUST show the `programManagement.pmuLiaisonInstitution.missing` message,
- And an ERROR MUST be logged naming the Global Unit and the unresolved id.

**AC for FN-008, NF-001:**
- Given a Global Unit with zero `liaison_users` and zero active `crp_program_leaders`,
- When a CRP Admin saves the Management screen,
- Then the save MUST complete without a `NullPointerException`.

**AC for NF-002:**
- Given a Global Unit whose `crp_cu` resolves to an existing row,
- When a CRP Admin adds a person to the team and saves,
- Then no liaison institution MUST be created,
- And `crp_cu` MUST be unchanged,
- And no repair WARN MUST be logged.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — `liaison_institutions`, `liaison_users`, `user_roles` and
      `custom_parameters` are not phased tables.
- [x] Save validation: `CrpAdminManagmentAction.validate()` guarded by `if (save)`, results carried in
      `invalidFields`. No `Validator` class exists for this action; the incident did not warrant introducing one and
      doing so would change the error surface of the whole screen. Recorded in §9.
- [x] Permissions: no new action; the existing `crpAdminStack` mapping in `struts-admin.xml` is unchanged.
- [x] Specificity: no new feature flag. `crp_cu` is an existing parameter and its catalog row is untouched.
- [x] Migrations: no schema change, therefore no Flyway migration. Data repair happens at runtime, per SEC-001.
- [x] i18n: `programManagement.pmuLiaisonInstitution.missing` added to `global.properties`; no literal user-facing
      text in Java.
- [x] License header: no new Java file.
- [x] Code style: clean compile after removing `target/classes`; Checkstyle reports only the two warnings already
      present at the base commit in `CrpProgamRegionsAction` (line 650, and a method length that this change reduced
      from 234 to 227 lines).
- [x] REST: no `/api/*` change.
- [x] Audit: `LiaisonUser` and `LiaisonInstitution` are not `MarloAuditableEntity`; the new WARN / ERROR traces are
      the record of what happened.
- [x] Dependency floors preserved: no dependency change.
- [ ] Branching: the implementation commits were made on
      `A2-2452-Review-optimization-and-dead-code-cleanup-in-MARLO-BaseAction` and are now on `staging`. The branch
      name does not describe this work. Recorded in §9.

## 8. Open Questions

1. **Cross-unit `crp_cu`.** Step 1 of the resolution chain accepts whatever row the id resolves to, without checking
   ownership or type. On 2026-09-02, 8 of the 19 Global Units holding the parameter pointed at another unit's record
   (16, 17, 27, 28, 29 → CCAFS's row 1; 25, 26 → BigData's row 174; 47 → AICCRA's row 336). Seven are retired units;
   AICCRA_III is active. Adding the check is two conditions, but repointing AICCRA_III would split its PMU team across
   two records unless the existing `liaison_users` are migrated. **How many people and projects hang off row 336
   today, and should they be moved?**
2. **Dangling role parameters.** `crp_fpl_rol`, `crp_fpm_rol`, `crp_rpl_rol`, `crp_rpm_rol`, `crp_pl_rol` and
   `crp_pc_rol` are resolved and assigned with no null check, into `user_roles.role_id`, which is `NOT NULL`. This
   reproduces the same failure class on another table. Should it be fixed under this spec or a follow-up?
3. **Two definitions of a correct PMU.** `GlobalUnitCreationManagerImpl.createLiaisonInstitution()` and
   `CrpAdminManagmentAction.resolvePmuLiaisonInstitution()` both encode it. Extracting a shared
   `ensurePmuLiaisonInstitution(globalUnit)` would prevent drift.
4. **Dead form fields.** `GlobalUnitCreateAction` accepts `liaisonName` and `liaisonAcronym`, but no FTL or JS submits
   them, so every new unit gets a PMU record literally named `"PMU"`. Should the inputs be exposed, or the fields
   removed?
5. **Is `crp_cu` still warranted?** Five other consumers find the same record by `acronym = "PMU"`. Keeping a
   configurable id alongside a convention gives two sources of truth that can disagree.

## 9. Decision Log

- 2026-09-02 — Repair `crp_cu` at save time in the Management screen rather than at login or Global Unit switch —
  Rationale: the session loaders run for every user on every switch, including read-only ones; creating master data
  there would be an implicit privileged write on a hot read path. The Management save is the only real consumer of
  the parameter and already requires admin rights.
- 2026-09-02 — Create the PMU liaison institution rather than only reporting the misconfiguration — Rationale: no
  screen in MARLO can create that record, so reporting alone would leave the administrator with no remedy short of
  SQL.
- 2026-09-02 — Use `acronym = "PMU"` for the created record — Rationale: `ExternalPartnershipsAction`,
  `IndicatorsAction`, `ExternalPartnersSummaryAction` and the REST v2 `FinancialSummaryItem` /
  `ProgressTowardsItem` locate the record by that literal; any other acronym makes those five consumers blind to it.
- 2026-09-02 — Resolve the liaison institution before `saveUserRole` and the notification — Rationale: both commit
  independently, so resolving afterwards is what produced the partial state described in §2.
- 2026-09-02 — Fix `findAll()` in `LiaisonUserMySQLDAO` and `CrpProgramLeaderMySQLDAO` at the DAO rather than at the
  call sites — Rationale: `hibernateQuery.list()` never returns null and none of the five call sites relied on the
  null, so the DAO is the correct place; the remaining 431 DAOs with the same pattern are left alone because a
  blanket change carries risk out of proportion to this incident.
- 2026-09-02 — No `Validator` class introduced for `CrpAdminManagmentAction` — Rationale: the screen collects errors
  in `invalidFields` and still executes the save; converting it to the `Validator` pattern would change the error
  surface of every field on the screen, which is beyond this incident.
- 2026-09-02 — No Flyway migration to repair dangling `crp_cu` values platform-wide — Rationale: the Management save
  is the parameter's only real consumer, so the runtime repair already prevents the failure; a migration that
  fabricates master data across every environment, including production, is a larger risk than the defect it would
  pre-empt.
- 2026-09-02 — Filed under `docs/specs/bugfix/` rather than as a `domain/admin/` module spec — Rationale: this is
  bug-driven work with traceability needs, which is what CLAUDE.md reserves that folder for. A `DOMAIN-ADMIN-001`
  module spec would have to cover the other fifteen admin actions, none of which were investigated, and the
  requirements template requires every requirement to be testable and cited. When the admin module is specified
  properly, that spec should subdivide §3 per screen following `docs/specs/domain/feedback/requirements.md`, and
  cite this bugfix.
- 2026-09-02 — `docs/specs/domain/admin/` holds only `agent-context.md` — Deliberate deviation from the CLAUDE.md
  rule that every spec folder must contain `requirements.md`, `design.md` and `task.md`. Rationale: the durable
  knowledge produced by this incident is operational, which is what the agent-context is for, and the Mandatory
  Agent-Context Rule looks it up at exactly that path. Four of the seven existing domain folders — `activities`,
  `bi`, `funding-sources`, `parameters` — already hold only an agent-context, so this follows established practice.
  The three formal files for the admin module remain to be written.
- 2026-09-02 — Implementation landed on a branch named for an unrelated ticket — Rationale: that is where the work
  was in progress when the incident was reported. Flagged so that a future reader does not infer the branch name
  describes this change.
