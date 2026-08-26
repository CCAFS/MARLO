# Feedback — Requirements

**Spec ID:** DOMAIN-FEEDBACK-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-25
**Related PRD sections:** docs/prd.md — quality assurance / review workflows
**Related System Design sections:** docs/system-design/design.md — project section layout, component inventory
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (data model), §5 (save pipeline), §security model
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md, reports/ai-context/struts-critical-routing-catalog.md, reports/ai-context/save-validation-matrix.md

---

## 1. Overview

This spec documents the **as-built** MARLO Feedback module: the field-level commenting workflow on project
items, its two CRP-admin configuration screens (Feedback Fields Management and Feedback Permissions
Management), and its role/cluster-based permission model. It exists because the module was delivered
incrementally between 2022 and 2025 across ~40 migrations with no spec, and because the meaning of the
configuration fields is not discoverable from the UI labels — the two "field name" columns are effectively
inverted relative to both their labels and their database comments. This spec is the reference an
administrator or developer needs before configuring or changing the module, and the baseline against which the
defects listed in §8 and §9 can be scheduled.

Scope is descriptive first, corrective second: unmarked requirements record current behaviour; requirements
marked **(GAP)** record behaviour that is missing, inconsistent, or defective and is proposed for remediation.

## 2. Problem Statement

Reviewer roles (PMU, flagship and regional leaders/managers) need to raise precise, auditable observations on
specific fields of a project deliverable, innovation, study, outcome, or safeguard, and project teams (PL/PC)
need to respond to each observation individually. Free-text section comments were too coarse: they could not
be counted, tracked, or reported per field, and there was no record of whether a project team agreed,
disagreed, or needed clarification.

The operational pain today is on the configuration side. An administrator opening **Feedback Fields
Management** sees five text inputs labelled Section Name, Section Description, Field Name, Field Description,
and Parent Field Description, with no help text. Four of the five are technical identifiers that must match
strings living in FreeMarker templates and a Java enum. A wrong value produces **no error at any layer** —
the comment icon simply never renders. This makes the screen effectively unusable without reading the source.
The same is true of **Feedback Permissions Management**, where a `NULL` Cluster Type means "all clusters" in
one code path and "cluster-agnostic only" in another.

## 3. In-Scope Requirements

### 3.1 Configuration — Feedback Fields Management

- **DOMAIN-FEEDBACK-001-FN-001** — The screen at `{crp}/feedbackManagement` MUST list every
  `feedback_qa_commentable_fields` row whose `global_unit_id` is the current global unit and `is_active = 1`,
  as an accordion of editable blocks.
- **DOMAIN-FEEDBACK-001-FN-002** — `sectionName` MUST hold a section slug that is simultaneously (a) a value of
  `ProjectSectionsEnum.getStatus()` and (b) the value of the target page's `#sectionNameToFeedback` hidden input.
- **DOMAIN-FEEDBACK-001-FN-003** — `sectionDescription` MUST hold the human-readable section name used in
  reports and in the admin block title. It has no runtime behaviour.
- **DOMAIN-FEEDBACK-001-FN-004** — `fieldName` MUST hold the human-readable field label. It is served to the
  browser as JSON key `description` and rendered as the comment popup title (`Comment on <label>`) and inside
  tracking emails.
- **DOMAIN-FEEDBACK-001-FN-005** — `fieldDescription` MUST hold the exact `name` attribute (OGNL expression) of
  the instrumented form control, e.g. `deliverable.deliverableInfo.title`. It is the join key between
  configuration and DOM: it is served as JSON key `fieldName` and used as the selector
  `img.qaComment[name="…"]`, and it is echoed as `frontName` on comment payloads.
- **DOMAIN-FEEDBACK-001-FN-006** — `parentFieldDescription` MUST hold the `name` attribute of an input whose
  **value** labels the parent record. The runtime reads `$('input[name="<value>"]').val()` and submits it as the
  `parentFieldDescription` request parameter on comment save and in tracking emails.
- **DOMAIN-FEEDBACK-001-FN-007** — `globalUnit` MUST be forced to the current global unit on save; the screen
  MUST NOT allow cross-tenant writes.
- **DOMAIN-FEEDBACK-001-FN-008** — Saving MUST upsert every row present in the submitted form and hard-delete
  every previously persisted row of the current global unit that is absent from it.
- **DOMAIN-FEEDBACK-001-FN-010 (GAP)** — Deleting a field whose `id` is referenced by `feedback_qa_comments.field_id`
  MUST be reported to the administrator as a blocked operation rather than surfacing an unhandled
  `ON DELETE RESTRICT` constraint violation.
- **DOMAIN-FEEDBACK-001-FN-011 (GAP)** — `sectionName` MUST be selected from the `ProjectSectionsEnum`-derived
  list the action already prepares (`projectSections`), not typed as free text.
  **Verified against the live data:** safe to implement — all four configured slugs
  (`deliverable`, `innovation`, `study`, `projectContributionCrp`) are valid `ProjectSectionsEnum` values, so
  no existing value would be hidden by the select.
- **DOMAIN-FEEDBACK-001-FN-012 (GAP)** — `parentFieldIdentifier` is persisted and exposed over JSON but read by
  no consumer. It MUST either be surfaced on the form with a documented purpose or be retired.
- **DOMAIN-FEEDBACK-001-FN-013 (GAP)** — The five inputs MUST carry per-field help text stating, for each one,
  whether the value is a human label or a technical identifier, and what it must match.

### 3.2 Configuration — Feedback Permissions Management

- **DOMAIN-FEEDBACK-001-FN-014** — The screen at `{crp}/feedbackRolesPermissionsManagement` MUST list every
  `feedback_roles_permissions` row for the current global unit, ordered by `feedback_permission_id`, and MUST
  offer a client-side filter by permission.
- **DOMAIN-FEEDBACK-001-FN-015** — One row MUST represent one grant of `(role, cluster type) → feedback permission`.
- **DOMAIN-FEEDBACK-001-FN-016** — The **Permission Name** dropdown MUST be populated from the global
  `feedback_permissions` catalog. That catalog is intentionally not editable from the UI: adding a permission
  requires a migration plus a `FeedbackPermissionsEnum` constant plus a `BaseAction` gate.
- **DOMAIN-FEEDBACK-001-FN-017** — The **User Role** dropdown MUST list only roles whose `crp` is the current
  global unit.
- **DOMAIN-FEEDBACK-001-FN-018** — **Cluster Type** MUST scope the grant. `NULL` MUST mean "not restricted to a
  cluster type".
- **DOMAIN-FEEDBACK-001-FN-019** — **Permission Description** is a free-text operator label with no behaviour.
- **DOMAIN-FEEDBACK-001-FN-020** — Rows created in a save MUST be flagged `recentlyCreated` for one subsequent
  render (session attribute `recentlyCreatedFRP`) so the UI can badge and scroll to them.
- **DOMAIN-FEEDBACK-001-FN-021 (GAP)** — `requiresProjectAssociation` is mapped and seeded (`true` for `PL`/`PC`)
  but never read; `canManageFeedback` hardcodes `Arrays.asList("PL", "PC")`. The gate MUST consult the column,
  and the column MUST be editable on the form.
- **DOMAIN-FEEDBACK-001-FN-022 (GAP)** — Saving a row with a null `feedbackPermission` or null `role` is
  currently accepted (both FKs were relaxed to `NULL` in 2025). Both MUST be rejected server-side.
  **Verified against the live data:** latent — zero rows currently have a null `role_id` or
  `feedback_permission_id`. The validation is preventive.

### 3.3 Permission model

- **DOMAIN-FEEDBACK-001-FN-023** — Four permissions MUST exist, keyed by `feedback_permissions.name` and mirrored
  in `FeedbackPermissionsEnum`: `can_leave_comments`, `can_approve_comments`, `can_react_comments`,
  `can_track_comments`.
- **DOMAIN-FEEDBACK-001-FN-024** — `can_leave_comments` MUST gate visibility of the comment icon and creation or
  editing of comments (`BaseAction.canLeaveComments`).
- **DOMAIN-FEEDBACK-001-FN-025** — `can_approve_comments` MUST gate approval/decline of draft comments
  (`BaseAction.canApproveComments`).
- **DOMAIN-FEEDBACK-001-FN-026** — `can_react_comments` MUST gate the agree / disagree / request-clarification
  reactions (`BaseAction.canManageFeedback`), and MUST additionally require, for holders of role acronym `PL` or
  `PC`, an active project-partner-person association on that project whose `contactType` is one of the acronyms
  returned by `findRoleAcronymsByPermissionName("can_react_comments", globalUnitId)`.
- **DOMAIN-FEEDBACK-001-FN-027** — `can_track_comments` MUST gate comment tracking/highlighting
  (`BaseAction.canTrackComments`) and is evaluated with a `null` cluster type, i.e. only cluster-agnostic grants
  confer it.
- **DOMAIN-FEEDBACK-001-FN-028** — A super admin (`canAccessSuperAdmin()`) MUST pass all four gates unconditionally.
- **DOMAIN-FEEDBACK-001-FN-029** — Grant matching MUST resolve the project's cluster type from
  `ProjectInfo.clusterType` of the actual phase. With a resolved cluster type, rows with
  `cluster_type_id IS NULL OR = <id>` match; with no resolved cluster type, only `cluster_type_id IS NULL` matches.
- **DOMAIN-FEEDBACK-001-FN-030 (GAP)** — Grant matching filters on `roles.global_unit_id` and never consults
  `feedback_roles_permissions.global_unit_id`. Because `roles.global_unit_id` is `NOT NULL` and
  `BaseAction.getRolesList()` already restricts the user's roles to the current CRP, that filter is redundant and
  the grant row's own tenant column is unenforced. A row whose `global_unit_id` disagrees with its role's global
  unit (or is `NULL`) is therefore **enforced for the role's tenant but unmanageable there**: it is absent from
  that tenant's admin listing (`getFeedbackRolesPermissionByGlobalUnitID`), survives every save because the
  delete pass iterates only rows of its own global unit, and is listed instead in the other tenant's screen,
  where an administrator can delete or reassign it. Matching MUST also constrain
  `feedback_roles_permissions.global_unit_id`. This is a data-integrity and manageability defect, not a
  cross-tenant access-control breach: no user can match a role of another tenant.
  **Verified against the live data:** latent. All 26 grant rows carry a non-null `global_unit_id`
  (25 under AICCRA=45, 1 under AICCRA_III=47) equal to their role's `global_unit_id`; the pre-flight returned
  zero mis-tenanted or orphan rows, and old-vs-new predicate divergence is zero for both global units. The fix
  is therefore provably non-observable on this data. **Applied 2026-08-25 in commit `b576db30da`**, together with
  NF-003 and NF-004 for the same file.
- **DOMAIN-FEEDBACK-001-FN-031 (GAP)** — The `*Old()` gate variants (`canManageFeedbackOld`, `canApproveCommentsOld`,
  `canLeaveCommentsOld`, `canTrackCommentsOld`) are unreachable pre-database implementations and MUST be removed.

### 3.4 Runtime commenting workflow

- **DOMAIN-FEEDBACK-001-FN-032** — The whole module MUST be gated by the `feedback_active` specificity
  (`BaseAction.feedbackModule()`): the project `Feedback` menu item, per-section comment loading in the section
  actions, and `fieldsBySectionAndParent.do`.
- **DOMAIN-FEEDBACK-001-FN-033** — A comment MUST be stored against `(phase, field, parentId)` and MUST carry the
  author, timestamp, status, an optional reply, tracking flags, the resolved deep link back to the field, and the
  captured `fieldDescription` / `parentFieldDescription` text.
- **DOMAIN-FEEDBACK-001-FN-034** — Statuses MUST be those of `feedback_statuses` / `FeedbackStatusEnum`:
  `1 Agreed`, `2 Clarification needed`, `3 Draft`, `4 Admitted`, `5 Disagreed`, `6 Dismissed`, with
  `visibility` in `{1 consolidation, 2 feedback, 3 both}`.
- **DOMAIN-FEEDBACK-001-FN-035** — `feedback_draft_active`, `feedback_clarification_needed_active`, and
  `feedback_new_comment_field_active` MUST independently enable the draft/approval workflow, the
  "Clarification needed" reaction, and the inline new-comment textarea.
- **DOMAIN-FEEDBACK-001-FN-036** — The project `{crp}/feedback` tab MUST embed the Power BI report named by the
  `crp_cluster_bi_feedback_report_name` specificity.
- **DOMAIN-FEEDBACK-001-FN-037** — Tracking a comment MUST send notification email via
  `sendFeedbackActionEmail.do` / `sendFeedbackReactionEmail.do`, populated from the `feedback_assesor_*`,
  `feedback_replay_username`, `feedback_comment_reaction`, and `feedback_response` specificities.
- **DOMAIN-FEEDBACK-001-FN-038 (GAP)** — `safeguard.ftl` publishes `sectionNameToFeedback = "safeguard"` while the
  enum constant is `SAFEGUARDS("safeguards")`. `SaveFeedbackCommentsAction` performs
  `switch (ProjectSectionsEnum.getValue(sectionName))`, which throws `NullPointerException` on an unmatched slug.
  Page, enum, and configured data MUST be reconciled and the unmatched case MUST be handled.
  **Verified against the live data:** unreachable today, because **no commentable field is configured
  for any safeguard slug at all**. The only configured sections are `deliverable` (21 fields), `innovation`
  (33), `study` (22) and `projectContributionCrp` (13). `safeguard.ftl` carries the feedback markers but the
  section has no fields, so no icon renders and no comment can be created there. The NPE needs a configured
  out-of-enum slug to fire. Priority drops to the null-guard (T11); the slug decision (T10) only matters if
  someone configures safeguard fields.
- **DOMAIN-FEEDBACK-001-FN-039 (GAP)** — `feedbackParent.do` (`FeedbackParentIdAction`) has no caller and MUST be
  retired or given a documented consumer.
- **DOMAIN-FEEDBACK-001-FN-040 (GAP — RETRACTED as a data defect; narrowed to migration drift)** —
  `V2_6_0_20210604_1444__UpdateClusterTypes.sql` sets `cluster_types` id 2 to `Flagship`, but the live database
  holds `Theme` for that id. The rename happened outside Flyway, so **the migration history no longer
  reproduces the database**: a fresh environment built from migrations gets `Flagship` where production has
  `Theme`, and the two `can_react_comments` grants that target it would land on the wrong cluster type.
  Additionally, `V2_6_0_20250618_1700` derives `cluster_type_id` by matching `cluster_types.name` inside
  `description` with `LIKE`; `'thematic'` does not contain `'theme'`, so that migration silently skips the very
  rows it looks like it covers. Both are latent traps for the next reseed, not live defects.
  A migration MUST align `cluster_types` with the live catalog, and description-based backfill MUST NOT be used
  again.
  **Verified against the live data:** the live data is correct and self-consistent. Every grant's
  resolved `cluster_types.name` matches its own `description`, including `FPL` and `FPM` `can_react_comments`
  on `Theme`. My earlier claim that they sat on `Country` was wrong — it assumed the migration history matched
  the database.
- **DOMAIN-FEEDBACK-001-FN-041 (GAP)** — Feedback is effectively unconfigured for `AICCRA_III`
  (`global_unit_id = 47`). **Verified against the live data:** that global unit has **zero**
  `feedback_qa_commentable_fields` rows and exactly **one** grant (id 39: role `CL`, `can_leave_comments`,
  cluster type `Theme`), whose `description` still reads `"PMU - can_write_comments on all clusters"` — copied
  from the AICCRA row and now describing neither the right role, nor the right permission, nor the right cluster
  scope. Its `requires_project_association` is `NULL`, unlike every migration-seeded row (`0`/`1`), because the
  admin screen does not set that column. If phase 3 is meant to use feedback, its fields and grants MUST be
  configured; if not, the stray grant SHOULD be removed. This is also the first concrete evidence that the
  free-text `description` (FN-019) drifts from the row it labels.
  **Re-checked later the same day:** grant id 39 no longer exists, so `AICCRA_III` is now at zero grants and
  zero fields, as is `Alliance` (`global_unit_id = 46`). All 25 remaining grants belong to `AICCRA` (45).
  Both empty global units are reachable through the admin menu, because `BaseAction.isAiccra()` is simply
  `getCurrentCrp().getId() >= 45`.
- **DOMAIN-FEEDBACK-001-NF-013 (GAP)** — `studiesTemplates.ftl` is a per-item macro, so the feedback markers it
  renders are emitted **once per study in the list**, producing duplicate `id` attributes in the document.
  `$('#<id>')` takes the first match and every copy carries the same value, so it works by accident. The markers
  SHOULD be emitted once per page, outside the repeated macro.

### 3.5 Non-functional

- **DOMAIN-FEEDBACK-001-NF-001** — Both admin screens MUST remain on the `crpAdminStack` interceptor stack;
  `save()` MUST remain gated on an authorization check.
- **DOMAIN-FEEDBACK-001-NF-002** — Both admin actions MUST keep binding their collections manually from the
  request (`bindFeedbackFieldsFromRequest`, `bindFeedbackRolesPermissionsFromRequest`), because Struts 6 does not
  shrink an OGNL-bound list on delete. Index continuity from the client is therefore a contract: the binder stops
  at the first fully empty index.
- **DOMAIN-FEEDBACK-001-NF-003 (GAP)** — `FeedbackQACommentableFieldsMySQLDAO.findBySectionName`,
  `findAllByGlobalUnit`, and `FeedbackRolesPermissionMySQLDAO.existsByRoleIdsAndPermissionName` interpolate
  arguments into HQL/SQL strings. All MUST be converted to bound parameters.
- **DOMAIN-FEEDBACK-001-NF-004 (GAP)** — `FeedbackRolesPermissionMySQLDAO.findObjectsByRoleIdsAndPermissionName`
  references an alias `frp` that its `FROM` clause never declares; the statement would fail if invoked. It MUST be
  fixed or removed.
- **DOMAIN-FEEDBACK-001-NF-005 (GAP)** — `FeedbackRolesPermissions.hbm.xml` maps
  `<column name="requires_project_association " …>` with a trailing space. It MUST be corrected.
- **DOMAIN-FEEDBACK-001-NF-006 (GAP)** — Neither admin screen has a `Validator` class; `validate()` is
  `if (save) { }` in both, so the FTL `required=true` markers are cosmetic. Server-side validation MUST be added
  per the constitutional save pipeline (`Action.validate()` guarded by `if (save)` → `Validator` → manager chain).
- **DOMAIN-FEEDBACK-001-NF-007 (GAP)** — Both admin JS files are copies of the SLO admin script and retain dead
  handlers (`addIndicator`, `addTargets`, `addCrossCuttingIssue`, datepicker configuration) and `console.log`
  calls; `feedbackAutoImplementation.js` logs all four permission flags on every page load. All MUST be removed.
- **DOMAIN-FEEDBACK-001-NF-008 (GAP)** — `feedbackManagement.ftl` references `js/admin/feedbackManagement.js`
  with no `?YYYYMMDD` cache-buster. One MUST be added, and bumped on every edit.
- **DOMAIN-FEEDBACK-001-NF-009** — Both admin menu entries currently render only in the `action.isAiccra()` branch
  of `menu-admin.ftl`. Any change to that visibility is a product decision, not a refactor.
- **DOMAIN-FEEDBACK-001-NF-011 (GAP)** — Several DAOs in this area return **`null` instead of an empty list**
  when a query matches nothing — the root cause of the empty-dropdown defect on Feedback Permissions
  Management. `ClusterTypeMySQLDAO.findAll` and
  `FeedbackQACommentableFieldsMySQLDAO.findAll` / `findAllByGlobalUnit` still do; `FeedbackPermissionMySQLDAO.findAll`
  and `FeedbackQACommentableFieldsMySQLDAO.findBySectionName` already return `Collections.emptyList()`, so the
  codebase is inconsistent with itself. Every list-returning DAO method in this area MUST return an empty list.
- **DOMAIN-FEEDBACK-001-NF-012 (GAP — PARTIALLY FIXED)** — Loading the roles of a global unit was done
  as `roleManager.findAll()` filtered in Java, hydrating the entire `roles` table (**382 rows across 21 global
  units**) to use the ~20 of one tenant, on every page load. `RoleManager` exposed no
  scoped accessor. A `findAllByGlobalUnit(long)` was added across `RoleDAO` / `RoleMySQLDAO` / `RoleManager` /
  `RoleManagerImpl` and both call sites were migrated. Note the HQL uses the property path `r.crp.id`, not the
  raw column name `global_unit_id` that several sibling DAOs interpolate — `Role` has no such property, and the
  property path is the only form guaranteed to parse.
- **DOMAIN-FEEDBACK-001-NF-010** — All user-facing strings MUST stay i18n-keyed under `feedbackManagement.*`,
  `feedbackPermissions.*`, and `CRPAdmin.menu.feedback*`. The runtime JS currently hardcodes English prompt text
  (`"Reason for disagreement:"`, `"Where clarification is needed:"`, …) — see FN gap tracking in `task.md`.

## 4. Out-of-Scope

- The Power BI report content itself (definition, dataset, refresh). Only the embed and its specificity are in scope.
- The QA module (`docs/specs/domain/qa/`), which is a separate review workflow.
- Email template authoring and the `SendMailS` transport.
- Extending feedback to project sections not currently instrumented.
- Migrating the module off Struts `.do` to Spring MVC.
- Any change to phase replication semantics for comments; comments are phase-scoped rows, not replicated
  entities, and this spec does not propose changing that.

## 5. Personas Affected

| Persona | How |
|---|---|
| CRP / Global Unit administrator | Owns both configuration screens. Primary victim of the label/semantics inversion and the absence of help text and validation. |
| PMU reviewer | Holds `can_leave_comments` + `can_approve_comments` + `can_track_comments`; raises and approves observations. |
| Flagship / Regional leader or manager (FPL, FPM, RPL, RPM) | Cluster-scoped mix of leave/approve/react/track; the cluster-type semantics in §3.3 decide what they actually see. |
| Project leader / coordinator (PL, PC) | Holds `can_react_comments`, additionally gated by project-partner-person association. Responds to each comment. |
| QA lead | Consumes the consolidated view via the embedded BI report. |
| Developer | Must keep `field_description` and the FTL control `name` in lockstep; has no compile-time or runtime signal when they drift. |

## 6. Acceptance Criteria

- **AC-001 (FN-005)** — Given a row whose `fieldDescription` equals the `name` of an instrumented control on the
  configured section, when a user with `can_leave_comments` opens that section, then a comment icon renders
  adjacent to that control. Given a row whose `fieldDescription` does not match any control, then no icon renders
  and no error is raised anywhere — this is the current failure mode the help text of FN-013 must prevent.
- **AC-002 (FN-004)** — Given a row with `fieldName = "Deliverable title"`, when the icon is clicked, then the
  popup title reads `Comment on Deliverable title`.
- **AC-003 (FN-006)** — Given `parentFieldDescription` naming an input whose value is `"My deliverable"`, when a
  comment is saved, then the `saveFeedbackComments.do` request carries `parentFieldDescription=My deliverable`.
- **AC-004 (FN-008)** — Given three persisted rows, when the administrator deletes the second and saves, then the
  remaining two are updated and the deleted one is removed from `feedback_qa_commentable_fields`.
- **AC-006 (FN-010)** — Given a field with at least one `feedback_qa_comments` row, when the administrator
  deletes it and saves, then an actionable message names the blocked field. *(Currently fails: unhandled
  constraint violation.)*
- **AC-007 (FN-026/FN-029)** — Given a `can_react_comments` grant for role `FPL` and cluster type `Theme`, when an
  `FPL` user opens a project whose phase `ProjectInfo.clusterType` is `Theme`, then reaction controls are enabled;
  when the project's cluster type is `Country`, then they are not.
- **AC-008 (FN-027)** — Given a `can_track_comments` grant with `cluster_type_id` set to any non-null value, when
  that user opens any project, then tracking is unavailable — because the gate passes `null`. Given the same grant
  with `cluster_type_id IS NULL`, then tracking is available.
- **AC-009 (FN-026)** — Given a user with role `PL` and a `can_react_comments` grant, when the user is **not** an
  active project-partner person with contact type `PL`/`PC` on that project, then reaction controls are disabled.
- **AC-010 (FN-028)** — Given a super admin, when opening any instrumented section of any project, then all four
  capabilities are available regardless of `feedback_roles_permissions` content.
- **AC-011 (FN-030)** — Given a grant row whose `global_unit_id` is A and whose `role_id` references a role of
  global unit B, when a user of B is evaluated, then the grant does not apply, and the row is listed and
  deletable in exactly one tenant's admin screen. *(Cannot fail on current data: zero such rows exist in
  the live data. Retained as a regression guard; re-run the cross-tenant grant pre-flight before any reseed.)*
- **AC-012 (FN-032)** — Given `feedback_active` false for the global unit, when a user opens a project, then the
  `Feedback` menu item is absent, no comment icons render, and `fieldsBySectionAndParent.do` returns an empty
  `fieldsMap`.
- **AC-013 (FN-038)** — Given the Safeguard section, when a user with `can_leave_comments` saves a comment, then
  the comment persists and the stored deep link resolves back to the safeguard field. *(Currently at risk: slug
  mismatch and `switch` on a `null` enum.)*
- **AC-014 (FN-022)** — Given a permission block submitted with no Permission Name or no User Role, when saved,
  then the row is rejected with a field-level message. *(Currently fails: it is persisted.)*
- **AC-015 (NF-002)** — Given rows at indexes 0..4, when index 2 is removed client-side, then the submitted
  parameters are contiguous 0..3 and all four rows persist.
- **AC-016 (FN-040)** — Given a database built from the migration history alone, when `cluster_types` is
  compared against the live catalog, then the two agree. *(Currently fails: migrations produce `Flagship` for
  id 2 where the live database has `Theme`. The live grant data itself is consistent — verified 2026-08-25 —
  so this criterion is about environment reproducibility, not about production correctness.)*
- **AC-017 (FN-041)** — Given `AICCRA_III` (`global_unit_id = 47`), when an administrator opens Feedback
  Permissions Management, then every listed grant's `description` matches its role, permission, and cluster
  scope. *(Currently fails: grant id 39 is `CL` / `can_leave_comments` / `Theme` but described as
  `"PMU - can_write_comments on all clusters"`.)*

## 7. Constitutional Compliance Checklist

| Rule | Status |
|---|---|
| 1. Phased data forward-only | **Not applicable.** Comments are phase-scoped rows created in the actual phase; there is no replication path. Configuration tables are phase-agnostic. |
| 2. Save pipeline `validate()` → `Validator` → manager chain | **Deviation, pre-existing.** Both admin actions have empty `validate()` and no `Validator`. Recorded as NF-006 and scheduled in `task.md`; not introduced by this spec. |
| 3. Spring MVC owns `/api/*` | **Honored.** All feedback endpoints are pre-existing `*.do` Struts JSON actions in the same module; no new `.json` paths. |
| 4. Specificities via `parameters` + `custom_parameters`, constants in both `APConstants.java` | **Honored.** All six keys are present in `marlo-data` and `marlo-web` `APConstants.java` with values equal to `parameters.key`. |
| 5. Schema changes as Flyway migrations with the mandated naming | **Honored.** All ~40 feedback migrations follow `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`. |
| 6. GPL header on new Java files | **Honored** in existing files; mandatory for any file added under this spec. |
| 7. Code style / Checkstyle gate | **Honored.** No feedback file exceeds the 3500-line cap (`feedbackAutoImplementation.js` is JS, ~1780 lines). |
| 8. English only in code; i18n for user-facing strings | **Deviation, pre-existing.** `feedbackAutoImplementation.js` hardcodes English UI prompts. Recorded as NF-010. |
| 9. Branching from `staging` | **Honored** for any work derived from this spec. |
| 10. Java 17 run scripts | **Honored.** `scripts/run-marlo-java17.sh`. |
| 11. Dependency baseline | **Not applicable.** No dependency change. |
| 12. No credential files committed | **Not applicable.** |

## 8. Open Questions

1. **OQ-001** — Should the feedback admin screens remain AICCRA-only (`action.isAiccra()`), or be gated on the
   `feedback_active` specificity so any global unit that enables the module can configure it?
2. **OQ-002** — Is the `field_name` / `field_description` inversion (label says one thing, JSON says another, the
   2022 migration comment says a third) to be fixed by renaming the columns, by relabelling the UI, or left
   as-is with documentation only? Renaming touches JSON keys consumed by `feedbackAutoImplementation.js`.
3. **OQ-003** — Is `can_track_comments` intended to be cluster-agnostic by design (FN-027 / AC-008), or is passing
   `null` a bug that should pass the project's cluster type like the other three gates?
4. **OQ-004** — Should `requiresProjectAssociation` (FN-021) generalize the `PL`/`PC` special case for all four
   permissions, or only for `can_react_comments`?
5. **OQ-005** — Is `parentFieldIdentifier` (FN-012) and `feedbackParent.do` (FN-039) recoverable design intent for
   a future section, or safe to retire?
6. **OQ-006** — What is the correct Safeguard slug (FN-038): change the FTL to `safeguards`, add a
   `SAFEGUARD("safeguard")` enum constant, or migrate the configured data?
7. **OQ-007** — For FN-040, is the intended fix to insert a `Theme` cluster type (which changes a catalog shared
   with `projects_info.type_id` and every other cluster-type consumer), or to repoint the `FPL`/`FPM`
   `can_react_comments` grants at the existing `Flagship` cluster type? The 2022 seed used
   `cluster_type_id = 2` (`Flagship`) for a row it labelled "Theme Clusters", which suggests `Flagship` was the
   original intent.

## 9. Decision Log

- 2026-08-25 — Create `docs/specs/domain/feedback/` as a domain spec rather than an enhancement or bugfix spec —
  the module is a standing MARLO domain area with its own tables, admin screens, and permission model, and the
  identified defects are better tracked against a documented baseline than as isolated bugfix specs.
- 2026-08-25 — Document as-built behaviour and gaps in one requirements file rather than splitting into a
  descriptive doc plus remediation specs — the gaps are only intelligible next to the behaviour that produces
  them, and every gap here was found by reading the code, not by a reported incident.
- 2026-08-25 — Record the empty `validate()` methods (NF-006) as a deviation from constitutional rule 2 rather
  than silently accepting them, per the CLAUDE.md requirement that deviations carry an explicit Decision Log entry.
- 2026-08-25 — Keep `field_description` as the DOM join key in the documentation and treat the 2022 migration
  column comments as stale, since `CommentableFieldsBySectionNameAndParents` and
  `feedbackAutoImplementation.js` are the authoritative runtime behaviour.
- 2026-08-25 — Downgrade FN-030 from "cross-tenant privilege escalation" to a data-integrity and manageability
  defect, after confirming `roles.global_unit_id` is `NOT NULL` and that `BaseAction.getRolesList()` already
  scopes the user's roles to the current CRP. A user can never match a role of another tenant, so the missing
  `frp.global_unit_id` predicate cannot grant access across tenants; it only lets a mis-tenanted grant row become
  invisible and undeletable in the tenant it actually affects. The fix stays in scope; the severity label does not.
- 2026-08-25 — Verified the whole spec against `aiccradb1` (local dev copy, MySQL 8.0.43). Results:
  FN-030 latent (0 mis-tenanted rows of 26; old-vs-new predicate divergence 0), FN-022 latent (0 incomplete
  rows), FN-038 unreachable (no safeguard fields configured), FN-011 safe (all 4 configured slugs are valid
  enum values). Module is in heavy use: 6992 comments, 5272 replies, 89 active commentable fields — which
  raises the stakes on FN-010, since most fields now have comments and any deletion attempt will hit the
  `ON DELETE RESTRICT` FK. **Caveat: this is a development database; `feedback_active` reads `false` for both
  global units here, so per-tenant specificity values must be re-checked against production.**
- 2026-08-25 — Retract FN-040 as a data defect. `cluster_types` id 2 is `Theme` in the live database, not
  `Flagship` as `V2_6_0_20210604_1444` sets it, so the grants that target it are correct and the two
  "thematic clusters" rows resolve to `Theme` as intended. The original finding was derived from migration
  history alone and was wrong. FN-040 is narrowed to the real residue: the migration history no longer
  reproduces the database, and description-based `LIKE` backfill silently skips `'thematic'`.
- 2026-08-25 — Add `RoleManager.findAllByGlobalUnit` (NF-012) rather than keep `findAll()` + in-memory filter.
  The repo idiom was the filter (`CrpUsersAction` did the same), but `roles` holds 382 rows across 21 global
  units and the screen needs 20, so the scoped query is worth the four-layer addition. Wrote the HQL as
  `r.crp.id = :globalUnitId`: several sibling DAOs interpolate the raw column name `global_unit_id`, which is not
  a property of `Role`, and a query that fails to parse would have silently re-broken the very dropdown the
  NPE fix had just restored.
- 2026-08-25 — Apply the FN-030 / NF-003 / NF-004 fix to `FeedbackRolesPermissionMySQLDAO` ahead of the T02
  decision gate, on the grounds that the pre-flight proved it non-observable. Chose
  `AND r.global_unit_id = frp.global_unit_id` over a second `:globalUnitID` binding: transitively equivalent,
  keeps the parameter to a single occurrence (no repeated-named-parameter reliance, for which the repo has no
  precedent), and states the intent directly — the grant belongs to this tenant, and the role to the same tenant
  as the grant.
