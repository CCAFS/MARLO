# Feedback — Tasks

**Spec ID:** DOMAIN-FEEDBACK-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-25
**Implements design:** docs/specs/domain/feedback/design.md
**Branching:** feature branch from `staging`, named `feedback-module-hardening` (or `<TICKET-ID>-<Description>` once ticketed)
**Target merge:** `staging` (then promoted to `main` per release process)

> T01 is documentation only and is already complete. T02 is a decision gate: **T10–T16 must not start until the
> open questions in `requirements.md` §8 are answered.** T03–T09 are behaviour-preserving and can proceed in
> parallel with that gate.

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
- [ ] `requirements.md` §8 open questions OQ-001 … OQ-006 answered and recorded in the Decision Log.
- [ ] `git checkout staging && git pull` then branch from `staging` (never from `main`, never commit to `main`).
- [ ] Local instance runs and the two admin screens load for the AICCRA global unit.
- [ ] Baseline capture: for the target database, export
      `feedback_qa_commentable_fields`, `feedback_roles_permissions`, and the `section_name` distribution —
      needed for R-01, R-02, and R-03 in `design.md` §16.
- [ ] Confirm no other branch is mid-flight on `feedbackAutoImplementation.js`.

## 3. Task List

### DOMAIN-FEEDBACK-001-T01 — Document the module as-built (this spec)

- **Depends on:** —
- **Module:** docs
- **Files touched:**
  - `docs/specs/domain/feedback/agent-context.md` (new)
  - `docs/specs/domain/feedback/requirements.md` (new)
  - `docs/specs/domain/feedback/design.md` (new)
  - `docs/specs/domain/feedback/task.md` (new)
- **Constitutional checks:** spec folder carries all three mandated files plus `agent-context.md`;
  English only; commit subject prefixed `[SPEC:docs/specs/domain/feedback]`.
- **Acceptance:** every configuration field of both admin screens has a documented meaning; every gap is a
  numbered requirement.
- **Verification:** read `agent-context.md` cold and configure one new commentable field without opening any
  Java or JS file.
- **Status:** Done — 2026-08-25.

### DOMAIN-FEEDBACK-001-T02 — Resolve open questions OQ-001 … OQ-006

- **Depends on:** T01
- **Module:** docs
- **Files touched:** `docs/specs/domain/feedback/requirements.md` (§8, §9)
- **Constitutional checks:** each answer appended to the Decision Log as `YYYY-MM-DD — decision — rationale`.
- **Acceptance:** all six questions answered; T10–T16 unblocked or explicitly deferred.
- **Verification:** IBD team lead plus one of PMU lead / QA lead / Tech lead sign off.

### DOMAIN-FEEDBACK-001-T03 — Bind DAO query parameters (NF-003, NF-004)

- **Depends on:** T01
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

### DOMAIN-FEEDBACK-001-T04 — Fix the HBM column name (NF-005)

- **Depends on:** T01
- **Module:** marlo-data
- **Files touched:** `resources/xmls/FeedbackRolesPermissions.hbm.xml`
- **Constitutional checks:** no schema change; mapping only.
- **Acceptance:** `<column name="requires_project_association"/>` with no trailing space.
- **Verification:** application boots; the Permissions screen lists rows; `requires_project_association`
  round-trips through a read.

### DOMAIN-FEEDBACK-001-T05 — Remove the dead `*Old()` capability methods (FN-031)

- **Depends on:** T03
- **Module:** marlo-web
- **Files touched:** `action/BaseAction.java` — delete `canManageFeedbackOld`, `canApproveCommentsOld`,
  `canLeaveCommentsOld`, `canTrackCommentsOld`
- **Constitutional checks:** confirm zero references in Java, FTL, and JS before deleting; Checkstyle;
  `BaseAction.java` stays under the 3500-line cap.
- **Acceptance:** `grep -rn "canManageFeedbackOld\|canApproveCommentsOld\|canLeaveCommentsOld\|canTrackCommentsOld" marlo-web/src marlo-data/src` returns nothing.
- **Verification:** full compile; capability matrix unchanged.

### DOMAIN-FEEDBACK-001-T06 — Clean the two admin JS files (NF-007)

- **Depends on:** T01
- **Module:** marlo-web
- **Files touched:**
  - `webapp/crp/js/admin/feedbackManagement.js` — drop `addIndicator`, `addTargets`, `addCrossCuttingIssue`,
    `datePickerConfig`, `date`, and the `console.log` calls; keep `addIdo`, `removeElement`, `updateIndexes`,
    the accordion handler
  - `webapp/crp/js/admin/feedbackRolesPermissionsManagement.js` — same removals; also de-duplicate the
    `#feedbackPermissionFilter` change handler, which is bound twice with two different matching strategies
    (`.feedbackPermission` value vs `data-permission-id`)
- **Constitutional checks:** `updateIndexes()` behaviour preserved exactly — the server-side manual binder
  depends on contiguous indexes (NF-002, ADR-FB-002).
- **Acceptance:** add / edit / remove / save round-trips on both screens with identical persisted results;
  no console output; the permission filter works from a single handler.
- **Verification:** on each screen, create 5 blocks, remove the 3rd, save, reload; confirm 4 rows persisted
  with the expected values.

### DOMAIN-FEEDBACK-001-T07 — Add the missing cache-buster (NF-008)

- **Depends on:** T06
- **Module:** marlo-web
- **Files touched:** `webapp/WEB-INF/crp/views/admin/feedbackManagement.ftl` (add `?YYYYMMDD` to the
  `js/admin/feedbackManagement.js` entry); bump `?YYYYMMDD` on
  `feedbackRolesPermissionsManagement.ftl` and on every FTL referencing
  `crp/js/feedback/feedbackAutoImplementation.js` (`projectDeliverable.ftl`, `projectInnovation.ftl`,
  `projectStudy.ftl`, `projectContributionCrp.ftl`, `safeguard.ftl`)
- **Constitutional checks:** repository convention — bump the `?YYYYMMDD` param whenever a CSS/JS asset changes.
- **Acceptance:** every feedback JS reference carries a cache-buster matching the edit date.
- **Verification:** hard-reload each page and confirm the browser requests the new query string.

### DOMAIN-FEEDBACK-001-T08 — Silence the runtime permission logging (NF-007)

- **Depends on:** T01
- **Module:** marlo-web
- **Files touched:** `webapp/crp/js/feedback/feedbackAutoImplementation.js` — remove the five
  `console.log` calls for `userCanManageFeedback`, `userCanLeaveComments`, `userCanApproveFeedback`,
  `usercanTrackComments`, `isSuperAdmin`
- **Constitutional checks:** no behaviour change; cache-buster bump via T07.
- **Acceptance:** clean console on an instrumented section page.
- **Verification:** open a deliverable with feedback active; console shows no feedback output.

### DOMAIN-FEEDBACK-001-T09 — Add administrator help text to both screens (FN-013)

- **Depends on:** T01
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

### DOMAIN-FEEDBACK-001-T10 — Reconcile the Safeguard section slug (FN-038)

- **Depends on:** T02 (OQ-006)
- **Module:** marlo-web (+ marlo-web migrations if OQ-006 chooses the data path)
- **Files touched:** depends on the OQ-006 answer —
  (a) `webapp/WEB-INF/crp/views/projects/safeguard.ftl` (`sectionNameToFeedback` → `safeguards`), or
  (b) `marlo-data/.../model/ProjectSectionsEnum.java` (add `SAFEGUARD("safeguard")`), or
  (c) `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__NormalizeFeedbackSafeguardSectionName.sql`
- **Constitutional checks:** migration naming `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`
  under `marlo-web/src/main/resources/database/migrations/`; forward-only.
- **Acceptance:** the Safeguard section resolves its commentable fields, and saving a comment there persists
  with a working deep link.
- **Verification:** save a comment on a safeguard field; confirm the row in `feedback_qa_comments` and that its
  `link` resolves back to the field.

### DOMAIN-FEEDBACK-001-T11 — Make section resolution null-safe (FN-038)

- **Depends on:** T10
- **Module:** marlo-web
- **Files touched:** `action/json/project/SaveFeedbackCommentsAction.java` — guard
  `switch (ProjectSectionsEnum.getValue(sectionName))` against a `null` enum and fall through to the generic
  link builder with a logged warning
- **Constitutional checks:** no swallowed exception; log at WARN with the offending slug.
- **Acceptance:** an unrecognised `section_name` produces a logged warning and a generic link, never a 500.
- **Verification:** temporarily configure a field with a bogus slug, save a comment, confirm the warning and
  a persisted comment.

### DOMAIN-FEEDBACK-001-T12 — Fix delete semantics on Feedback Fields Management (FN-009, FN-010)

- **Depends on:** T02
- **Module:** marlo-web
- **Files touched:** `action/crp/admin/FeedbackManagementAction.java` — move the delete loop outside the
  `!feedbackFields.isEmpty()` guard (mirroring `FeedbackRolesPermissionsManagementAction`), wrap each upsert and
  delete in `try/catch`, add an SLF4J logger, and translate a `field_id` `ON DELETE RESTRICT` violation into an
  i18n action message naming the blocked field
- **Constitutional checks:** two-pass save with the pre-save snapshot preserved (ADR-FB-003); writes stay on the
  manager chain; new i18n keys added to `global.properties`.
- **Acceptance:** AC-005 and AC-006 in `requirements.md` §6 pass.
- **Verification:** (a) delete all rows, save, confirm zero rows remain; (b) attempt to delete a field that has
  comments, confirm an actionable message and that all other rows still saved.

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

### DOMAIN-FEEDBACK-001-T14 — Turn Section Name into a select (FN-011)

- **Depends on:** T10, T13
- **Module:** marlo-web
- **Files touched:** `webapp/WEB-INF/crp/views/admin/feedbackManagement.ftl` — replace the
  `sectionName` `[@customForm.input]` with a `[@customForm.select listName="projectSections"]` driven by the
  list `FeedbackManagementAction.prepare()` already builds
- **Constitutional checks:** must not silently drop a configured value outside `ProjectSectionsEnum` — render
  any such value as a preserved, flagged option (see R-02).
- **Acceptance:** administrators can only choose a valid section; pre-existing out-of-enum values remain visible
  and are not lost on save.
- **Verification:** run T10 first; confirm every existing row still round-trips with its section intact.

### DOMAIN-FEEDBACK-001-T15 — Honour `requiresProjectAssociation` (FN-021)

- **Depends on:** T02 (OQ-004), T04, T13
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

### DOMAIN-FEEDBACK-001-T16 — Scope grant matching to the tenant (FN-030)

- **Depends on:** T02 (and T03, so the query is already parameterised)
- **Module:** marlo-data, marlo-web
- **Files touched:**
  - `data/dao/mysql/FeedbackRolesPermissionMySQLDAO.java` — add
    `AND (frp.global_unit_id IS NULL OR frp.global_unit_id = :globalUnitID)` to
    `existsByRoleIdsAndPermissionName`, and the same to `findRoleAcronymsByPermissionName` /
    `findRoleIdsByPermissionName`
  - a one-off pre-flight query (not committed) listing grants whose `global_unit_id` differs from their role's `crp`
- **Constitutional checks:** this **revokes** capabilities that currently work via the leak — do not ship without
  the pre-flight report and PMU/QA sign-off (R-01).
- **Acceptance:** AC-011 passes; no tenant loses a capability that its own grant rows legitimately confer.
- **Verification:** run the pre-flight report on a production-like dump; for every affected role, confirm an
  equivalent grant exists under the correct global unit before deploying.

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

- **Depends on:** T08
- **Module:** marlo-web
- **Files touched:** `webapp/crp/js/feedback/feedbackAutoImplementation.js` (read labels from a
  data-attribute or a JS i18n bundle instead of literals such as `"Reason for disagreement:"`,
  `"Reason for agreement (optional):"`, `"Where clarification is needed:"`,
  `"A justification is required to proceed"`, `"Comment on …"`); `resources/global.properties` (new keys);
  the popup macros in `webapp/WEB-INF/global/macros/forms.ftl` to carry the keys
- **Constitutional checks:** constitutional rule 8 — user-facing strings must be i18n-keyed; cache-buster bump.
- **Acceptance:** no user-visible English literal remains in the feedback JS.
- **Verification:** exercise all six statuses and confirm every prompt renders from `global.properties`.

### DOMAIN-FEEDBACK-001-T19 — Update the ai-context companions

- **Depends on:** T12, T13, T16
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
T01 (done)
 ├─ T02 ── decision gate ──┬─ T10 ─ T11
 │                         ├─ T12 ─ T13 ─┬─ T14   (also needs T10)
 │                         │             └─ T15   (also needs T04, OQ-004)
 │                         ├─ T16        (also needs T03)
 │                         └─ T17        (OQ-005)
 ├─ T03 ─┬─ T05
 │       └─ T16
 ├─ T04 ─── T15
 ├─ T06 ─── T07
 ├─ T08 ─── T18
 └─ T09

T19 ← T12, T13, T16
```

Parallel-safe from day one: **T03, T04, T06→T07, T08, T09**.
Blocked on T02: **T10–T17**.

## 5. Testing Plan

### Unit

- New validators (T13): one test per rule, valid and invalid, including the uniqueness rules.
- `ProjectSectionsEnum.getValue` null path used by T11.
- DAO parameter binding (T03): assert the four capability queries return identical results to the
  string-concatenated versions for a fixed fixture.

### Integration

- Admin round-trip per screen: add 3 blocks → save → reload → edit 1 → remove 1 → save → reload. Assert
  persisted rows, and that `global_unit_id` is always the current global unit.
- Delete-all-rows case (T12) and FK-blocked delete case (T12).
- `recentlyCreatedFRP` session flag: after a save that creates rows, the next render badges exactly those rows
  and the session attribute is cleared.

### Capability matrix (the core regression suite — run before and after T03, T05, T15, T16)

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
- Console clean on every instrumented page (T08).
- Cache-busters bumped on every touched asset (T07).

## 6. Operational Steps

- **Migrations:** at most one (T10 option c). Apply via the standard Flyway path on deploy; capture the
  pre-change `section_name` distribution first.
- **Pre-flight for T16:** run the cross-tenant grant report against a production-like dump and circulate it to
  PMU and QA before the deploy that ships T16. If any affected role has no equivalent grant under its own
  global unit, insert the missing grants (as a migration) **before** T16 ships.
- **Specificities:** no new keys. Verify per-tenant `custom_parameters` values for `feedback_active`,
  `feedback_draft_active`, `feedback_clarification_needed_active`, `feedback_new_comment_field_active`, and
  `crp_cluster_bi_feedback_report_name` are unchanged after deploy.
- **BI coordination:** none required, but confirm the report named by `crp_cluster_bi_feedback_report_name`
  still exists in `bi_reports` — a drift silently empties the project `Feedback` tab (R-06).
- **i18n:** new keys land in `global.properties`; add to `custom/*.properties` only for tenants needing an override.
- **Config / env vars:** none.

## 7. Rollback Plan

- Code: revert the feature branch merge commit. Every task is code-only except T10 option c.
- T10 option c: reverse migration `UPDATE feedback_qa_commentable_fields SET section_name = 'safeguard'
  WHERE section_name = 'safeguards'`, scoped by the captured pre-change id list.
- T16 is the only task that removes access. If a tenant reports lost capability after deploy, the immediate
  mitigation is to insert the correctly scoped grant rows, not to revert the query fix.
- T13 is the only task that can block a previously working save. If administrators are blocked, relax the
  specific rule rather than reverting the whole validator.
- Assets: bumping the cache-buster back is not required on rollback; browsers will fetch the reverted file
  under the reverted query string.

## 8. Definition of Done

- [ ] T01 and T02 complete; all six open questions answered and recorded in the Decision Log.
- [ ] Every task either merged to `staging` or explicitly deferred with a reason in `requirements.md` §9.
- [ ] Every acceptance criterion AC-001 … AC-015 in `requirements.md` §6 verified, or recorded as deferred.
- [ ] Capability matrix diffed against the baseline, with every intentional change traced to T15 or T16.
- [ ] Runtime regression passed on all five instrumented sections.
- [ ] `mvn -q checkstyle:check` clean; no new Checkstyle suppressions.
- [ ] Constitutional rule 2 deviation closed by T13, or the deviation re-affirmed with a new Decision Log entry.
- [ ] `agent-context.md` Known Defects list reduced to what genuinely remains.
- [ ] `reports/ai-context/*` companions updated (T19).
- [ ] `requirements.md`, `design.md`, and this file moved to their terminal status
      (**Implemented** / **Done**), with verification notes recorded per task.
