# Feedback — Tasks

**Spec ID:** DOMAIN-FEEDBACK-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-25
**Implements design:** docs/specs/domain/feedback/design.md
**Branching:** feature branch from `staging`, named `feedback-module-hardening` (or `<TICKET-ID>-<Description>` once ticketed)
**Target merge:** `staging` (then promoted to `main` per release process)

> Completed tasks are removed from this list once shipped; git history holds them. T02 is a decision gate:
> **T12–T17 and T20 must not start until the open questions in `requirements.md` §8 are answered.** T03–T09 are
> behaviour-preserving and can proceed in parallel with that gate.

---

## 1. Execution Context

- Java 17. Local runs via `scripts/run-marlo-java17.sh` (`.bat` on Windows). `marlo-parent/pom.xml` is the
  verification source for the active Java level.
- Spring profile: `marlo-dev` (`marlo-web/src/main/resources/config/marlo-dev.properties`, `marlo.production=false`).
  Bootstrap credentials from `marlo-test.properties`; never commit `marlo-${profile}.properties`.
- Database: a MySQL instance with all `V2_6_0_*` feedback migrations applied. A global unit with
  `feedback_active = true` in `custom_parameters` is required to exercise anything.
- Test tenant: AICCRA (`global_unit_id = 45`) is the only tenant with seeded grants, and the only one whose admin
  menu shows the two screens (`action.isAiccra()`).
- Gate: `mvn -q checkstyle:check` must pass before every commit.
- Reference docs to keep open: `docs/specs/domain/feedback/agent-context.md`,
  `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md` (both admin screens are accordion lists),
  `reports/ai-context/save-validation-matrix.md`, `reports/ai-context/interceptor-validator-playbook.md`.

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` reviewed and moved to **Approved**.
- [ ] `requirements.md` §8 open questions OQ-001 … OQ-007 answered and recorded in the Decision Log.
- [ ] `git checkout staging && git pull` then branch from `staging` (never from `main`, never commit to `main`).
- [ ] Local instance runs and the two admin screens load for the AICCRA global unit.
- [ ] Baseline capture: for the target database, export
      `feedback_qa_commentable_fields`, `feedback_roles_permissions`, and the `section_name` distribution —
      needed for R-01, R-02, and R-03 in `design.md` §16. Also export `cluster_types` and each grant's
      resolved cluster-type name beside its `description` — needed for R-07 / FN-040.
- [ ] Confirm no other branch is mid-flight on `feedbackAutoImplementation.js`.

## 3. Task List

### DOMAIN-FEEDBACK-001-T02 — Resolve open questions OQ-001 … OQ-007

- **Module:** docs
- **Files touched:** `docs/specs/domain/feedback/requirements.md` (§8, §9)
- **Constitutional checks:** each answer appended to the Decision Log as `YYYY-MM-DD — decision — rationale`.
- **Acceptance:** all remaining questions answered; T12–T17 and T20 unblocked or explicitly deferred.
- **Verification:** IBD team lead plus one of PMU lead / QA lead / Tech lead sign off.

### DOMAIN-FEEDBACK-001-T03 — Bind DAO query parameters (NF-003, NF-004)

- **Module:** marlo-data
- **Files touched:**
  - `data/dao/mysql/FeedbackQACommentableFieldsMySQLDAO.java` — `findBySectionName`, `findAllByGlobalUnit`
  - `data/dao/mysql/FeedbackRolesPermissionMySQLDAO.java` — `existsByRoleIdsAndPermissionName`,
    `findObjectsByRoleIdsAndPermissionName` (fix the missing `frp` alias or delete the unused method)
- **Constitutional checks:** no downgrade of behaviour; layered pattern untouched; Checkstyle.
- **Acceptance:** no feedback DAO concatenates a caller-supplied value into HQL or SQL; the four capability
  gates return identical results to the baseline.
- **Verification:** capability matrix from the Testing Plan re-run and diffed against the pre-change run;
  `getAnsweredCommentByPhaseToStudy` / `getCommentStatusByPhaseToStudy` still return the same rows.

### DOMAIN-FEEDBACK-001-T05 — Remove the dead `*Old()` capability methods (FN-031)

- **Depends on:** T03
- **Module:** marlo-web
- **Files touched:** `action/BaseAction.java` — delete `canManageFeedbackOld`, `canApproveCommentsOld`,
  `canLeaveCommentsOld`, `canTrackCommentsOld`
- **Constitutional checks:** confirm zero references in Java, FTL, and JS before deleting; Checkstyle;
  `BaseAction.java` stays under the 3500-line cap.
- **Acceptance:** `grep -rn "canManageFeedbackOld\|canApproveCommentsOld\|canLeaveCommentsOld\|canTrackCommentsOld" marlo-web/src marlo-data/src` returns nothing.
- **Verification:** full compile; capability matrix unchanged.

### DOMAIN-FEEDBACK-001-T06 — De-duplicate the permission filter handler

- **Module:** marlo-web
- **Files touched:**
  - `webapp/crp/js/admin/feedbackRolesPermissionsManagement.js` — `#feedbackPermissionFilter` is bound twice,
    with two different matching strategies: the first handler reads the `.feedbackPermission` select value and
    calls `show()`/`hide()` over all `.srfSlo`; the second reads the `data-permission-id` attribute and calls
    `toggle()` over `.srfSlo:not(.is-template)`. Both fire on every change; the second wins.
  - `webapp/WEB-INF/crp/views/admin/feedbackRolesPermissionsManagement.ftl` — line 91 closes the `class`
    attribute early and leaves `is-template` as a bare attribute rather than a class:
    `class="srfSlo borderBox ${isNew}" ${isTemplate?string('is-template','')}"`. The `.not('.is-template')`
    guard therefore matches nothing, so the hidden template block is processed like a real row.
- **Suspected effect (not reproduced):** filtering by a permission and then clearing the filter should leave
  the template visible as an empty phantom block, because `toggle(true)` reaches it. If an administrator fills
  it in and saves, the manual binder would take it as a real row. Reproduce before fixing.
- **Constitutional checks:** `updateIndexes()` behaviour preserved exactly — the server-side manual binder
  depends on contiguous indexes (NF-002, ADR-FB-002).
- **Acceptance:** the filter works from a single handler; the template never becomes visible; add / edit /
  remove / save round-trips unchanged.
- **Verification:** filter by a permission, clear the filter, confirm no empty block appears; then create 5
  blocks, remove the 3rd, save, reload, and confirm 4 rows persisted with the expected values.
- **Note:** the dead-code half of this task (the SLO-copy handlers, `datePickerConfig` / `date`, and every
  `console.log` in the three feedback JS files) shipped 2026-08-26, verified working on the app. Only the
  filter de-duplication is left, because it is the one part that changes behaviour.

### DOMAIN-FEEDBACK-001-T09 — Add administrator help text to both screens (FN-013)

- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/crp/views/admin/feedbackManagement.ftl` — `help=` on all five controls
  - `webapp/WEB-INF/crp/views/admin/feedbackRolesPermissionsManagement.ftl` — `help=` on all four controls
  - `resources/global.properties` — new `feedbackManagement.*.help` and `feedbackPermissions.*.help` keys
- **Constitutional checks:** all strings i18n-keyed (constitutional rule 8); English only; also add the keys to
  `custom/*.properties` only where a tenant needs an override.
- **Acceptance:** each help text states whether the value is a human label or a technical identifier and what it
  must match — in particular that **Field Description** is the form control's `name` attribute and
  **Field Name** is the label shown in the popup.
- **Verification:** an administrator who has not read this spec configures one new commentable field correctly
  on the first attempt.

### DOMAIN-FEEDBACK-001-T12 — Harden the save on Feedback Fields Management (FN-010)

- **Depends on:** T02
- **Module:** marlo-web
- **Files touched:** `action/crp/admin/FeedbackManagementAction.java` — wrap **each upsert** in `try/catch` so one
  failing row cannot abort the whole save, and translate a `field_id` `ON DELETE RESTRICT` violation into an i18n
  action message naming the blocked field
- **Constitutional checks:** two-pass save with the pre-save snapshot preserved (ADR-FB-003); writes stay on the
  manager chain; new i18n keys added to `global.properties`.
- **Acceptance:** AC-006 in `requirements.md` §6 passes.
- **Verification:** attempt to delete a field that has comments; confirm an actionable message and that all other
  rows still saved.
- **Note:** the delete pass already runs outside the `isEmpty()` guard, and the delete side is already wrapped and
  logged. Only the per-row upsert `try/catch` is outstanding.

### DOMAIN-FEEDBACK-001-T13 — Add validators to both admin screens (NF-006, FN-022)

- **Depends on:** T12
- **Module:** marlo-web
- **Files touched:**
  - `validation/crp/admin/FeedbackManagementValidator.java` (new)
  - `validation/crp/admin/FeedbackRolesPermissionsManagementValidator.java` (new)
  - `action/crp/admin/FeedbackManagementAction.java` — populate `validate()` under `if (save)`
  - `action/crp/admin/FeedbackRolesPermissionsManagementAction.java` — same
  - `resources/global.properties` — validation message keys
- **Constitutional checks:** GPL header on both new files; the mandated pipeline
  `Action.validate()` guarded by `if (save)` → `Validator` → manager chain (constitutional rule 2 — this task is
  what closes the deviation recorded in `requirements.md` §9); errors surfaced through `generalMessages.ftl`.
- **Acceptance:** rules as specified in `design.md` §7. AC-014 passes.
- **Verification:** submit each invalid combination and confirm a field-level message and no persisted row;
  submit a valid form and confirm it still saves.

### DOMAIN-FEEDBACK-001-T15 — Honour `requiresProjectAssociation` (FN-021)

- **Depends on:** T02 (OQ-004), T13
- **Module:** marlo-web, marlo-data
- **Files touched:**
  - `action/BaseAction.java` — `canManageFeedback` reads the grant's `requiresProjectAssociation` instead of
    `Arrays.asList("PL", "PC")`
  - `data/dao/mysql/FeedbackRolesPermissionMySQLDAO.java` — expose the flag on the matching path
  - `webapp/WEB-INF/crp/views/admin/feedbackRolesPermissionsManagement.ftl` — add a checkbox for it
  - `resources/global.properties` — label and help key
- **Constitutional checks:** no migration needed (column exists); behaviour change gated on the OQ-004 answer
  (whether the flag applies to `can_react_comments` only or to all four gates).
- **Acceptance:** AC-009 still passes for PL/PC via the column rather than a hardcoded list; a grant with the
  flag cleared no longer requires project association.
- **Verification:** capability matrix re-run with the flag set and cleared for a PL user on a project they are
  and are not associated with.

### DOMAIN-FEEDBACK-001-T17 — Retire `parentFieldIdentifier` and `feedbackParent.do` (FN-012, FN-039)

- **Depends on:** T02 (OQ-005)
- **Module:** marlo-web, marlo-data
- **Files touched (if OQ-005 says retire):**
  - `action/json/project/FeedbackParentIdAction.java` (delete)
  - `resources/struts-json.xml` (remove the `feedbackParent` mapping)
  - `action/json/project/CommentableFieldsBySectionNameAndParents.java` (drop the `identifierField` key)
  - `action/crp/admin/FeedbackManagementAction.java` (drop the `parentFieldIdentifier` binding)
  - the model/HBM property is **kept**; the column stays until a later cleanup migration
- **Constitutional checks:** confirm no remaining caller of `feedbackParent.do` or reader of `identifierField`
  across `marlo-web/src/main/webapp` before deleting.
- **Acceptance:** no dead endpoint and no unread JSON key; comment flows unaffected.
- **Verification:** grep for both names returns only the model/HBM; regression pass on all five instrumented
  sections.

### DOMAIN-FEEDBACK-001-T18 — i18n the runtime prompt strings (NF-010)

- **Module:** marlo-web
- **Files touched:** `webapp/crp/js/feedback/feedbackAutoImplementation.js` (read labels from a
  data-attribute or a JS i18n bundle instead of literals such as `"Reason for disagreement:"`,
  `"Reason for agreement (optional):"`, `"Where clarification is needed:"`,
  `"A justification is required to proceed"`, `"Comment on …"`); `resources/global.properties` (new keys);
  the popup macros in `webapp/WEB-INF/global/macros/forms.ftl` to carry the keys
- **Constitutional checks:** constitutional rule 8 — user-facing strings must be i18n-keyed; cache-buster bump.
- **Acceptance:** no user-visible English literal remains in the feedback JS.
- **Verification:** exercise all six statuses and confirm every prompt renders from `global.properties`.

### DOMAIN-FEEDBACK-001-T20 — Reconcile the cluster-type catalog with the seeded grants (FN-040)

- **Depends on:** T02 (OQ-007)
- **Module:** marlo-web (migration)
- **Reframed 2026-08-25 after reading `aiccradb1`:** the live data is **correct** — `cluster_types` id 2 is
  `Theme` and every grant matches its description. The real defect is that the migration history produces
  `Flagship` for id 2, so a fresh environment does not reproduce production. Scope is now: align the catalog
  migration with the live values, and stop using description-based `LIKE` backfill (which silently skips
  `'thematic'`). No production data change is needed.
- **Files touched:**
  - a read-only pre-flight query (not committed) joining `feedback_roles_permissions` to `cluster_types` and
    listing each row's `description` beside the resolved `cluster_types.name`
  - `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__ReconcileFeedbackGrantClusterTypes.sql` —
    per the OQ-007 answer, either insert the missing cluster type or repoint the `FPL`/`FPM`
    `can_react_comments` grants, **scoped by explicit `id` list**, never by description matching
- **Constitutional checks:** migration naming and location per constitutional rule 5; forward-only. If OQ-007
  chooses to insert a `cluster_types` row, note that the catalog is shared with `projects_info.type_id` and every
  other cluster-type consumer — that is a wider change than it looks and needs its own impact check.
- **Acceptance:** AC-016 passes — every grant's resolved cluster-type name is consistent with its description.
- **Verification:** re-run the pre-flight query; then re-run the capability matrix for `FPL` and `FPM` on
  projects of each cluster type and confirm the reaction controls appear where intended and only there.
- **Note:** the defect is inferred from the migration chain, not from a database read. Confirm the live state
  first — the admin screen has been able to correct these rows by hand since 2025-06 (R-07).

### DOMAIN-FEEDBACK-001-T23 — Spot-check the study capability markers

- **Module:** marlo-web (verification only — the four markers are already rendered by
  `webapp/WEB-INF/global/macros/studiesTemplates.ftl`)
- **Verification:** open a study with a `PL`/`PC` user and confirm no tracking icon is offered on their own
  comment, matching the deliverable and innovation sections.

### DOMAIN-FEEDBACK-001-T19 — Update the ai-context companions

- **Depends on:** T12, T13, T20
- **Module:** docs
- **Files touched:** `reports/ai-context/save-validation-matrix.md` (the two new validators),
  `reports/ai-context/struts-critical-routing-catalog.md` (if T17 removes a route),
  `reports/ai-context/frontend-composition-map.md` (the feedback overlay contract),
  `docs/specs/domain/feedback/agent-context.md` (retire the fixed items from its Known Defects list)
- **Constitutional checks:** CLAUDE.md step 10 — update the ai-context docs when routing, validation, or
  composition contracts change; commit prefixed `[SPEC:docs/specs/domain/feedback]` where the spec folder is touched.
- **Acceptance:** no ai-context doc still describes superseded behaviour.
- **Verification:** read each changed doc against the merged code.

## 4. Dependency Graph

```
T02 ── decision gate ──┬─ T12 ─ T13 ─── T15   (also needs OQ-004)
                       ├─ T17                 (OQ-005)
                       └─ T20                 (OQ-007)
T03 ─── T05
T06
T18
T09
T23   (code done, UI spot-check pending)

T19 ← T12, T13, T20
```

Parallel-safe from day one: **T03, T06, T09, T18**.
Blocked on T02: **T12, T13, T15, T17, T20**.
T12 is partly shipped: the delete semantics are closed; FN-010 and the per-row upsert `try/catch` are not.

## 5. Testing Plan

### Unit

- New validators (T13): one test per rule, valid and invalid, including the uniqueness rules.
- `ProjectSectionsEnum.getValue` null path in the comment-link and tracking-email builders.
- DAO parameter binding (T03): assert the four capability queries return identical results to the
  string-concatenated versions for a fixed fixture.

### Integration

- Admin round-trip per screen: add 3 blocks → save → reload → edit 1 → remove 1 → save → reload. Assert
  persisted rows, and that `global_unit_id` is always the current global unit.
- Delete-all-rows case (T12) and FK-blocked delete case (T12).
- `recentlyCreatedFRP` session flag: after a save that creates rows, the next render badges exactly those rows
  and the session attribute is cleared.

### Capability matrix (the core regression suite — run before and after T03, T05, T15)

For each of `PMU`, `FPL`, `FPM`, `RPL`, `RPM`, `PL`, `PC`, `SuperAdmin` × each cluster type
(`Country`, `Regional`, `Theme`, `Management`, and a project with no cluster type) × each of the four gates,
record the boolean and diff against the baseline. For `PL`/`PC`, run each cell twice: associated with the
project as a partner person with contact type `PL`/`PC`, and not associated.

### Runtime regression (manual, all five instrumented sections)

Deliverable, Innovation, Study, Outcome (`projectContributionCrp`), Safeguard:

- Comment icon appears next to the control whose `name` equals `field_description`; popup title equals `field_name`.
- Create, edit, delete a comment; reply; each of agree / disagree / clarification / dismiss; the draft →
  approve path with `feedback_draft_active` on and off.
- Comment-count bubble; tracking toggle and both tracking emails.
- Deep link: open the URL fragment produced in `feedback_qa_comments.link` and confirm the popup opens on the
  right field, including when that field sits in a non-default tab.
- Toggle `feedback_active` off → project `Feedback` menu item and all icons disappear (AC-012).

### Cross-cutting

- `mvn -q checkstyle:check`.
- Console clean on every instrumented page.
- Cache-busters bumped on every touched asset.

## 6. Operational Steps

- **Migrations:** none.
- **Specificities:** no new keys. Verify per-tenant `custom_parameters` values for `feedback_active`,
  `feedback_draft_active`, `feedback_clarification_needed_active`, `feedback_new_comment_field_active`, and
  `crp_cluster_bi_feedback_report_name` are unchanged after deploy.
- **BI coordination:** none required, but confirm the report named by `crp_cluster_bi_feedback_report_name`
  still exists in `bi_reports` — a drift silently empties the project `Feedback` tab (R-06).
- **i18n:** new keys land in `global.properties`; add to `custom/*.properties` only for tenants needing an override.
- **Config / env vars:** none.

## 7. Rollback Plan

- Code: revert the feature branch merge commit. Every remaining task is code-only.
- The tenant-scoping predicate on grant matching is the only shipped change that removes access. If a tenant
  reports lost capability, the mitigation is to insert the correctly scoped grant rows, not to revert the query.
- T13 is the only task that can block a previously working save. If administrators are blocked, relax the
  specific rule rather than reverting the whole validator.
- Assets: bumping the cache-buster back is not required on rollback; browsers will fetch the reverted file
  under the reverted query string.

## 8. Definition of Done

- [ ] T02 complete; all seven open questions answered and recorded in the Decision Log.
- [ ] Every task either merged to `staging` or explicitly deferred with a reason in `requirements.md` §9.
- [ ] Every acceptance criterion AC-001 … AC-017 in `requirements.md` §6 verified, or recorded as deferred.
- [ ] Capability matrix diffed against the baseline, with every intentional change traced to T15 or T20.
- [ ] Runtime regression passed on all five instrumented sections.
- [ ] `mvn -q checkstyle:check` clean; no new Checkstyle suppressions.
- [ ] Constitutional rule 2 deviation closed by T13, or the deviation re-affirmed with a new Decision Log entry.
- [ ] `agent-context.md` Known Defects list reduced to what genuinely remains.
- [ ] `reports/ai-context/*` companions updated (T19).
- [ ] `requirements.md`, `design.md`, and this file moved to their terminal status
      (**Implemented** / **Done**), with verification notes recorded per task.
