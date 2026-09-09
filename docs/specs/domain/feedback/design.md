# Feedback — Design

**Spec ID:** DOMAIN-FEEDBACK-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-09-03
**Implements requirements:** DOMAIN-FEEDBACK-001-FN-001 … FN-040, NF-001 … NF-010
**Touches modules:** marlo-web, marlo-data

> This is an **as-built design record** plus the remediation design for the requirements marked **(GAP)**.
> Sections 1–13 describe what exists today. Section 14 and the Decision Records cover the changes proposed.

---

## 1. Architecture Summary

Feedback is a configuration-driven overlay on existing project forms. Administrators declare, per section, the
list of form controls that accept comments; at page load a JavaScript engine fetches that list, matches each
entry to a live DOM control by its `name` attribute, and injects a comment icon plus a popup. All comment
CRUD happens through Struts JSON `.do` endpoints. Capability is resolved server-side into four boolean flags
rendered into hidden spans, which the engine reads to decide which controls to show.

```
                        ┌──────────────────────── CRP Admin ────────────────────────┐
                        │  {crp}/feedbackManagement                                │
                        │    FeedbackManagementAction ──► feedback_qa_commentable_fields
                        │  {crp}/feedbackRolesPermissionsManagement                 │
                        │    FeedbackRolesPermissionsManagementAction ──► feedback_roles_permissions
                        └──────────────────────────┬───────────────────────────────┘
                                                   │ configuration
                                                   ▼
  Project section page (e.g. projectDeliverable.ftl)
    ├─ hidden markers: #sectionNameToFeedback, #parentID, #projectID, #phaseID, #userID
    ├─ capability spans: #userCanLeaveComments #userCanApproveFeedback
    │                    #userCanManageFeedback #canTrackComments
    │        ▲ rendered from BaseAction.canLeaveComments / canApproveComments /
    │          canManageFeedback / canTrackComments
    │              └─► FeedbackRolesPermissionMySQLDAO.existsByRoleIdsAndPermissionName
    │                     (roleIds, permissionName, globalUnitId, clusterTypeId)
    ├─ forms.ftl macros emit  <img class="qaComment" name="<OGNL name>" fieldID="" description="">
    └─ feedbackAutoImplementation.js
         1. GET  fieldsBySectionAndParent.do?sectionName=<slug>
              → [{ fieldID, fieldName (=DB field_description), description (=DB field_name),
                   sectionName, identifierField, parentFieldDescription }]
         2. match  img.qaComment[name = <fieldName>]  →  stamp fieldID + description
         3. GET  feedbackComments2.do / feedbackReplies.do / getCommentStatus.do
         4. POST saveFeedbackComments.do / saveFeedbackReply.do /
                 saveCommentStatus.do / saveReplyStatus.do / saveTrackingStatus.do /
                 deleteFeedbackComments.do / deleteFeedbackReplies.do
         5. POST sendFeedbackActionEmail.do / sendFeedbackReactionEmail.do  (when tracking)

  Project "Feedback" tab: {crp}/feedback → FeedbackStatusAction → feedbackStatus.ftl
    embeds the Power BI report named by specificity crp_cluster_bi_feedback_report_name
```

The **join key** is a string, matched at runtime in the browser with no schema, no compile-time check, and no
error path: `feedback_qa_commentable_fields.field_Description` must equal the `name` attribute of the target
control. Everything fragile about this module follows from that.

## 2. Module Footprint

### marlo-web (as-built)

Routes
- `resources/struts-admin.xml` — `{crp}/feedbackManagement`, `{crp}/feedbackRolesPermissionsManagement`, both on `crpAdminStack`
- `resources/struts-projects.xml` — `{crp}/feedback` on `editProjectsStack`
- `resources/struts-json.xml` — the 14 feedback JSON actions listed in §4

Actions
- `action/crp/admin/FeedbackManagementAction.java`
- `action/crp/admin/FeedbackRolesPermissionsManagementAction.java`
- `action/projects/FeedbackStatusAction.java`
- `action/json/project/` — `CommentableFieldsBySectionNameAndParents`, `FeedbackQACommentsAction`,
  `FeedbackQACommentsMultipleAction`, `FeedbackQARepliesAction`, `FeedbackQANumberCommentsAction`,
  `FeedbackParentIdAction`, `SaveFeedbackCommentsAction`, `SaveFeedbackReplyAction`, `SaveCommentStatusAction`,
  `SaveReplyStatusAction`, `SaveTrackingStatusAction`, `DeleteFeedbackCommentsAction`, `DeleteFeedbackRepliesAction`
- `action/json/email/EmailTrackingCommentAction.java`, `EmailTrackingReactionCommentAction.java`
- `action/BaseAction.java` — `canLeaveComments`, `canApproveComments`, `canManageFeedback`, `canTrackComments`,
  `getClusterTypeIDFromProject`, `isUserAssociatedWithProjectForFeedback`, `feedbackModule`,
  `getFeedbackBIReportName`, plus the four `*Old()` variants
- `validation/projects/FeedbackStatusValidator.java`
- `config/APConstants.java` — six feedback specificity keys

Views / assets
- `webapp/WEB-INF/crp/views/admin/feedbackManagement.ftl`, `feedbackRolesPermissionsManagement.ftl`
- `webapp/WEB-INF/crp/views/admin/menu-admin.ftl` (both entries, AICCRA branch only)
- `webapp/WEB-INF/crp/views/projects/feedbackStatus.ftl`
- `webapp/WEB-INF/global/macros/forms.ftl` — `qaComment` icon in `input`, `textArea`, `select`, and the
  `qaPopUpMultiple` / `qaCommentReplyBlock` macros
- `webapp/WEB-INF/global/macros/studiesTemplates.ftl`, `innovationTemplates.ftl`
- Instrumented sections: `projects/projectDeliverable.ftl`, `projectInnovation.ftl`, `projectStudy.ftl`,
  `projectContributionCrp.ftl`, `safeguard.ftl`
- `webapp/crp/js/feedback/feedbackAutoImplementation.js`
- `webapp/crp/js/admin/feedbackManagement.js`, `webapp/crp/js/admin/feedbackRolesPermissionsManagement.js`
- `webapp/crp/js/projects/feedbackStatus.js`, `webapp/crp/css/projects/feedbackStatus.css`,
  `webapp/crp/css/admin/feedbackRolesPermissionsManagement.css`
- `resources/global.properties` — `feedbackManagement.*`, `feedbackPermissions.*`, `CRPAdmin.menu.feedback*`,
  `form.buttons.addFeedbackField`, `form.buttons.addFeedbackPermission`

### marlo-data (as-built)

- Models: `FeedbackQACommentableFields`, `FeedbackQAComment`, `FeedbackQAReply`, `FeedbackPermission`,
  `FeedbackRolesPermission`, `FeedbackStatus`, `FeedbackPermissionsEnum`, `FeedbackStatusEnum`
- DAO interfaces + MySQL implementations for each of the six tables
- Managers + `ManagerImpl` for each
- HBM: `xmls/FeedbackQACommentableFields.hbm.xml`, `FeedbackQAComments.hbm.xml`, `FeedbackQAReplies.hbm.xml`,
  `FeedbackPermissions.hbm.xml`, `FeedbackRolesPermissions.hbm.xml`, `FeedbackStatuses.hbm.xml`
- `config/APConstants.java` — the same six specificity keys

### Remediation footprint (proposed)

#### marlo-web
- Modified: `action/crp/admin/FeedbackManagementAction.java` (FN-010)
- Modified: `action/crp/admin/FeedbackRolesPermissionsManagementAction.java` (FN-021, FN-022)
- New: `validation/crp/admin/FeedbackManagementValidator.java` (NF-006)
- New: `validation/crp/admin/FeedbackRolesPermissionsManagementValidator.java` (NF-006)
- Modified: `action/BaseAction.java` (FN-021, FN-030, FN-031)
- Modified: `action/json/project/SaveFeedbackCommentsAction.java` (null-safe section resolution; `SAFEGUARDS`
  link case)
- Modified: `webapp/WEB-INF/crp/views/admin/feedbackManagement.ftl` (FN-013)
- Modified: `webapp/WEB-INF/crp/views/admin/feedbackRolesPermissionsManagement.ftl` (FN-013, FN-021)
- Modified: `webapp/crp/js/admin/feedbackRolesPermissionsManagement.js` (duplicated permission filter)
- Modified: `webapp/crp/js/feedback/feedbackAutoImplementation.js` (NF-010)
- Modified: `resources/global.properties` (help keys, validation messages, JS prompt keys)
- Deleted: `action/json/project/FeedbackParentIdAction.java` + its `struts-json.xml` mapping (FN-039, pending OQ-005)

#### marlo-data
- Modified: `data/dao/mysql/FeedbackQACommentableFieldsMySQLDAO.java` (NF-003)
- Modified: `data/dao/mysql/FeedbackRolesPermissionMySQLDAO.java` (NF-003, NF-004, FN-030)

#### marlo-core / marlo-utils
- Not applicable.

## 3. Data Model Changes

### As-built schema

`feedback_qa_commentable_fields` — the commentable-field catalog (one row per commentable form control)

| Column | Type | Meaning |
|---|---|---|
| `id` | bigint PK | |
| `section_name` | text NOT NULL | Section slug; must be a `ProjectSectionsEnum` value and the page's `#sectionNameToFeedback`. |
| `section_description` | text | Human-readable section name (reports, admin block title). |
| `parent_field_description` | text | `name` of an input whose **value** labels the parent record. |
| `parent_field_identifier` | text | Legacy; name of the field holding the parent id. No consumer. |
| `field_name` | text | **Human-readable field label** → JSON `description` → popup title, emails. |
| `field_Description` | text | **DOM `name` attribute — the join key** → JSON `fieldName` → `img.qaComment[name=…]`, `frontName`. |
| `is_active` | boolean NOT NULL | Filtered in queries; entity `isActive()` returns hardcoded `true`. |
| `global_unit_id` | bigint FK NOT NULL (hbm) | Tenant scope. Added 2025-06-04. |

`feedback_qa_comments` — one comment; `field_id` → commentable field (`ON DELETE RESTRICT`), `parent_id` → the
section record id, `id_phase` → phase, `status_id` → `feedback_statuses`, `reply_id` → accepted reply, plus
`project_id`, `comment`, `field_value`, `link`, `field_description`, `parent_field_description`, `user_id`,
`user_approval_id`, `user_editor_id`, `draft_action_user_id`, `responsible_user_id`, `comment_date`,
`approval_date`, `edition_date`, `draft_action_date`, `is_tracking`, `start_track_date`, `end_track_date`.

`feedback_qa_replies` — one reply; `comment_id` → comment (`cascade="all-delete-orphan"` from the parent set),
`status_id`, `id_phase`, `user_id`, `user_approval_id`, `comment`, `comment_date`, `approval_date`.

`feedback_permissions` — global catalog: `id`, `name`, `description`. Seeded with the four permission names.
Not editable from the UI.

`feedback_roles_permissions` — one grant

| Column | Type | Meaning |
|---|---|---|
| `id` | bigint PK | |
| `role_id` | bigint FK NULL | Role receiving the grant. Relaxed to NULL 2025-06-17. |
| `feedback_permission_id` | bigint FK NULL | Permission granted. Relaxed to NULL 2025-06-17. |
| `cluster_type_id` | bigint FK NULL | Cluster-type scope; NULL = unrestricted. |
| `description` | text | Operator label. |
| `global_unit_id` | bigint FK NULL | Tenant scope. Added 2025-06-05. **Not used by the matching query.** |
| `requires_project_association` | tinyint(1) NULL | Seeded `true` for PL/PC. **Never read.** |

`feedback_statuses` — `id`, `status_name`, `status_description`, `status_consolidation_name`,
`visibility int(1)` (`1 consolidation`, `2 feedback`, `3 both`). Seeded:

| id | `status_name` | `status_consolidation_name` | visibility |
|---|---|---|---|
| 1 | Agreed | Agreed | 2 |
| 2 | Clarification needed | Clarification needed | 2 |
| 3 | Draft | Draft | 1 |
| 4 | Pending | Admitted | 3 |
| 5 | Disagreed | Disagreed | 2 |
| 6 | Dismissed | Dismissed | 1 |

`FeedbackStatusEnum` uses the ids above with labels `Agreed`, `ClarificatioNeeded`, `Draft`, `Admitted`,
`Disagreed`, `Dismissed`.

### Migration inventory (chronological, abridged)

| Migration | Effect |
|---|---|
| `V2_6_0_20220411_0823__CreateInternalQATableCommentableFields.sql` | Original `internal_qa_commentable_fields`. |
| `V2_6_0_20220420_1040__CreateFeedbackCommentsTable.sql` | `feedback_comments` (later renamed to replies). |
| `V2_6_0_20220420_1200__CreateQAFeedbackCommentsTable.sql` | `feedback_qa_comments`; `field_id` FK `ON DELETE RESTRICT`. |
| `V2_6_0_20220426_1400/1420`, `20220427_0912`, `20220428_1543` | Comment table columns and date type fixes. |
| `V2_6_0_20220428_1616__RenameCommentableFieldsTable.sql` | Rename to `feedback_qa_commentable_fields`; `feedback_comments` → `feedback_qa_replies`. |
| `V2_6_0_20220503_1038__AddFieldsToCommentableTable.sql` | `parent_name`→`parent_field_description`, `parent_id`→`parent_field_identifier`, `front_name`→`field_Description`, adds `section_description`. **Its column comments describe the reverse of current runtime usage.** |
| `V2_6_0_20220513_1440`, `20220726_0935`, `20221003_1130` | Specificities `feedback_active`, `feedback_clarification_needed_active`, `feedback_draft_active` for global unit types 1, 3, 4. |
| `V2_6_0_20220901_1600` … `20220922_1530` | `feedback_statuses` table, seeds, `is_public`→`visibility`, `status_consolidation_name`. |
| `V2_6_0_20220905_0750/0900`, `20220907_1200` | Comment edition-tracking columns. |
| `V2_6_0_20220915_1450/1500/1510/1514` | `feedback_permissions`, `feedback_roles_permissions`, first seeds (3 permissions, 9 grants). |
| `V2_6_0_20230504_1415`, `20230525_1026/1027` | Adds `can_track_comments` (id 4) and its grants. |
| `V2_6_0_20250211_1540__FeedbackReportParameter.sql` | Specificity `crp_cluster_bi_feedback_report_name`. |
| `V2_6_0_20250530_0815` | Specificity `feedback_new_comment_field_active`. |
| `V2_6_0_20250603_1120`, `20250603_1655` | Replies table restructure + data migration. |
| `V2_6_0_20250604_1540`, `20250605_1540`, `20250605_1550` | `global_unit_id` on both configuration tables + backfill. |
| `V2_6_0_20250616_1420`, `20250616_1500` | Truncate and reseed all grants for global unit 45 (AICCRA). |
| `V2_6_0_20250617_1700` | `role_id` and `feedback_permission_id` relaxed to NULL. |
| `V2_6_0_20250618_1700`, `20251106_1500` | Cluster-type backfill by description matching; FPM `can_react_comments` → Theme. **Both partially inert — see FN-040: no `Theme` row exists in `cluster_types`.** |
| `V2_6_0_20250625_1320`, `20250625_1340` | Adds and seeds `requires_project_association`. |

### Proposed migrations

None. The safeguard slug was reconciled in the FTL, not in the data — no row was ever configured with either
spelling, so there was nothing to normalise.

FN-021 needs no migration — `requires_project_association` already exists. FN-030 and NF-003..NF-004 are
code-only. FN-022 is validated in the application layer; the NOT NULL constraints are deliberately not
reinstated because existing rows may violate them.

## 4. API / Action Surface

No new endpoints. Existing surface, all pre-existing Struts `.do` (constitutional rule 3 respected — nothing
new under `/api/*` and no new `*.json` paths):

### Admin (`struts-admin.xml`, `crpAdminStack`)

| Route | Action | Results |
|---|---|---|
| `{crp}/feedbackManagement` | `FeedbackManagementAction` | `input` → `admin/feedbackManagement.ftl`; `success` → redirect to self with `edit=true` |
| `{crp}/feedbackRolesPermissionsManagement` | `FeedbackRolesPermissionsManagementAction` | `input` → `admin/feedbackRolesPermissionsManagement.ftl`; `success` → redirect to self with `edit=true` |

### Project (`struts-projects.xml`, `editProjectsStack`)

| Route | Action | View |
|---|---|---|
| `{crp}/feedback` | `FeedbackStatusAction` | `projects/feedbackStatus.ftl` |

### JSON (`struts-json.xml`)

| Path | Action | Contract |
|---|---|---|
| `fieldsBySectionAndParent.do` | `CommentableFieldsBySectionNameAndParents` | In: `sectionName`. Out: `fieldsMap[]` of `{fieldID, fieldName, description, sectionName, identifierField, parentFieldDescription}`. Gated on `hasSpecificities(FEEDBACK_ACTIVE)`; falls back from `findAllByGlobalUnit` to `findAll` on exception. |
| `feedbackComments.do` | `FeedbackQACommentsAction` | Threads for one field. |
| `feedbackComments2.do` | `FeedbackQACommentsMultipleAction` | In: `sectionName`, `parentID`, `phaseID`. All threads for a record. |
| `feedbackReplies.do` | `FeedbackQARepliesAction` | Replies for a comment. |
| `getCommentStatus.do` | `FeedbackQANumberCommentsAction` | In: `sectionName`, `parentID`, `phaseID`, `fieldDescription`. Counts for the bubble. |
| `saveFeedbackComments.do` | `SaveFeedbackCommentsAction` | In: `sectionName`, `parentID`, `comment`, `phaseID`, `fieldID`, `userID`, `projectID`, `parentFieldDescription`, optional `commentID`. Resolves the deep link via `switch (ProjectSectionsEnum.getValue(sectionName))`. |
| `saveFeedbackReply.do` | `SaveFeedbackReplyAction` | Create a reply. |
| `saveCommentStatus.do` | `SaveCommentStatusAction` | In: `status`, `commentID`, `userID`. Maps `0,5→Disagreed`, `1→Agreed`, `2→Clarification needed`, `3→Draft`, `4→Admitted`, `6→Dismissed`. |
| `saveReplyStatus.do` | `SaveReplyStatusAction` | Reply status. |
| `saveTrackingStatus.do` | `SaveTrackingStatusAction` | Toggle `is_tracking` + track dates. |
| `deleteFeedbackComments.do` | `DeleteFeedbackCommentsAction` | Delete a comment. |
| `deleteFeedbackReplies.do` | `DeleteFeedbackRepliesAction` | Delete a reply. |
| `sendFeedbackActionEmail.do` | `EmailTrackingCommentAction` | Tracking email on a new comment. |
| `sendFeedbackReactionEmail.do` | `EmailTrackingReactionCommentAction` | Tracking email on a reaction. |
| `feedbackParent.do` | `FeedbackParentIdAction` | Returns `{parentField, parentDescription}`. **No caller** (FN-039). |

## 5. Frontend Composition

### Admin screens

Both use the same shape: `menu-admin.ftl` in a 3-column, an `[@s.form]` wrapping an accordion of
`borderBox` blocks rendered by a local macro, an `.addSlo bigAddButton` to clone a hidden `#srfSlo-template`
block, and a `.remove-element` per block. `updateIndexes()` reindexes `name="collection[i].prop"` after every
add/remove — this is the contract the server-side manual binder depends on (NF-002). Both are instances of the
accordion list pattern in `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`.

`feedbackManagement.ftl` — block title `Feedback Field <n>: <sectionDescription> - <fieldName>`; body is a
hand-written `<select class="sectionName">` for `sectionName` plus four `[@customForm.input]` controls
(`sectionDescription`, `fieldName`, `fieldDescription`, `parentFieldDescription`) and a hidden `id`.
`parentFieldIdentifier` is not rendered. `pageLibs = ["select2"]`,
`customJS = ["js/admin/feedbackManagement.js?YYYYMMDD"]`.

The section select is written out in the template rather than through `[@customForm.select]`, which hands the
current value to `[@s.select]` as an OGNL expression that never matches a string slug. Its options are
`getProjectSections()` (every `ProjectSectionsEnum.getStatus()`), rendered as
`<option value="<slug>"><label> (<slug>)</option>`, where the label is
`FeedbackManagementAction.getProjectSectionLabel(slug)` → `getText("feedbackManagement.section." + slug, slug)`.
The label is presentational only; the posted and persisted value stays the bare slug, so a tenant can rename a
section in `custom/*.properties` without touching data. A stored slug outside the enum gets its own extra
option flagged `feedbackManagement.sectionName.unknown`, so it survives the save; the placeholder posts `-1`,
which the binder normalises to an empty string.

`feedbackRolesPermissionsManagement.ftl` — block title `Permission <n>: <description>` with a `New` badge when
`recentlyCreated`; body is `description` input plus three `[@customForm.select]` controls
(`feedbackPermission.id` from `feedbackPermissionsList`, `role.id` from `roleList` by `displayLabel`,
`clusterType.id` from `clusterTypeList` by `name`). A `#feedbackPermissionFilter` select2 filters blocks
client-side by `data-permission-id`. `customJS` carries `?20250619a`; `customCSS` pulls select2 CSS from
`cdnjs.cloudflare.com`.

### Runtime overlay

`forms.ftl` emits, for `input`, `textArea`, `select` and the deliverable-participants variant:

```
<div class="commentNumberContainer">
  <div class="numberOfCommentsBubble"><p></p></div>
  <img src="…/comment.png" class="qaComment" name="${name}" fieldID="" description="">
</div>
```

`[@customForm.qaPopUpMultiple fields=… name=feedback.fieldDescription index=… canLeaveComments=…]` renders the
popup and the `#qaTemplate` reply-block template. Note the macro is passed `feedback.fieldDescription`,
confirming that column as the DOM name.

A section opts in with these hidden markers plus the four capability spans and the JS include; the canonical
set is in `projectDeliverable.ftl` lines ~96–118. Sections instrumented today: deliverable, innovation,
study (via `studiesTemplates.ftl`), outcome (`projectContributionCrp.ftl`), safeguard.

`feedbackAutoImplementation.js` responsibilities, in order: read markers and capability flags; deep-link
handling (`window.location.hash` → open the owning tab, position and show the popup, scroll to centre);
`loadQACommentsIcons` → `addfeedbackFlexItemsClass` (DOM reflow so the icon sits inline with the label) →
`showQAComments` (stamp `fieldID`/`description`/`id` on the icon and index every button inside each
`qaCommentReply-<name>[i]` block); `hideShowOptionButtons(block, status)` — the single place where status
decides which reaction buttons and which reply-label text are visible; comment/reply/status/tracking AJAX.

## 6. Persistence & Phase Replication Plan

- Configuration tables (`feedback_qa_commentable_fields`, `feedback_roles_permissions`, `feedback_permissions`)
  are **phase-agnostic**. There is nothing to replicate.
- `feedback_qa_comments` and `feedback_qa_replies` are **phase-scoped rows**, written with the actual phase and
  read with `getFeedbackQACommentsByPhaseAndParentId(phaseId, parentId)`. They are deliberately **not**
  replicated forward: a comment belongs to the review round in which it was raised. Constitutional rule 1
  (forward-only phased data) is satisfied trivially — past phases are never rewritten.
- Both admin `save()` implement the same two-pass shape: upsert every submitted row, then hard-delete every
  pre-save row of the current global unit whose id is absent from the submitted ids. The pre-save snapshot is
  captured **before** the upsert pass so newly inserted rows cannot be deleted by their own save.
- `FeedbackRolesPermissionsManagementAction` wraps each upsert and each delete in `try/catch` and logs;
  `FeedbackManagementAction` does not, so a single failure aborts the whole save (FN-010).
- Deletes are hard `session.delete`, not `is_active = 0`. `FeedbackQAReply` is reachable by
  `cascade="all-delete-orphan"` from `FeedbackQAComment.feedbackReplies`, so deleting a comment removes its replies.
- All writes go through `*Manager` → `*ManagerImpl` → `*DAO` → `*MySQLDAO`; no action touches a session directly.

## 7. Validation & Save Pipeline

As-built, both admin screens **deviate** from the constitutional pattern:

```
Action.validate()  →  if (save) { }        // empty in both actions
Validator          →  does not exist for either screen
Manager chain      →  present and correct
```

`required=true` on the FTL controls renders the red asterisk but enforces nothing. `bindFeedbackFieldsFromRequest`
and `bindFeedbackRolesPermissionsFromRequest` swallow every parse failure (`catch (NumberFormatException) { /* silent */ }`)
and treat a fully empty index as end-of-list.

Proposed (NF-006, FN-022):

```
FeedbackManagementAction.validate()
  if (save) → FeedbackManagementValidator.validate(action, feedbackFields)
      per row: sectionName ∈ ProjectSectionsEnum   → "feedbackManagement.sectionName.required"
               fieldName not blank                  → "…fieldName.required"
               fieldDescription not blank           → "…fieldDescription.required"
               (sectionName, fieldDescription) unique within the submitted set

FeedbackRolesPermissionsManagementAction.validate()
  if (save) → FeedbackRolesPermissionsManagementValidator.validate(action, feedbackRolesPermissions)
      per row: feedbackPermission.id present        → "feedbackPermissions.permission.required"
               role.id present                       → "feedbackPermissions.role.required"
               (role, permission, clusterType) unique within the submitted set
```

Error surface: `addMissingField` / `addMessage` on the action, rendered by `generalMessages.ftl`, matching every
other CRP-admin screen.

## 8. Permissions & Edit Gates

- Both admin routes sit on `crpAdminStack`: `i18nFile`, `validCrp`, `requireUser`, `validSessionCrp`,
  `accessibleAdmin`, `canEditCrpAdmin`, `keepRedirectMessages`, `accessibleStage`, `trimInputs`, `defaultStack`.
  `accessibleAdmin` + `canEditCrpAdmin` are the effective authorization.
- Both `save()` additionally test `hasPermission("*")`. Because neither action sets `basePermission`,
  `BaseAction.hasPermission` delegates straight to `securityContext.hasPermission("*")` — i.e. a wildcard
  Shiro permission, satisfied only by an admin-level grant. Keep the stack; do not treat this string as a
  no-op.
- Menu visibility: both entries exist only in the `action.isAiccra()` branch of `menu-admin.ftl` (NF-009).
  The routes themselves are reachable by URL for any global unit.
- `{crp}/feedback` sits on `editProjectsStack`; `FeedbackStatusAction` sets
  `basePermission = Permission.PROJECT_DESCRIPTION_BASE_PERMISSION`.
- Runtime capability resolution (FN-023..FN-030), in `BaseAction`:

```
can<X>(projectID):
  if canAccessSuperAdmin()                       → true
  roles      = getRolesList()                    ; empty → false
  clusterId  = getClusterTypeIDFromProject(projectID)      // ProjectInfo.clusterType of actualPhase
                                                           // canTrackComments() passes null instead
  granted    = existsByRoleIdsAndPermissionName(roleIds, <name>, currentGlobalUnit.id, clusterId)
  // canManageFeedback only:
  if granted and roles contain "PL" or "PC"
       → isUserAssociatedWithProjectForFeedback(user, projectID, "can_react_comments", globalUnitId)
```

`existsByRoleIdsAndPermissionName` SQL shape:

```sql
SELECT COUNT(frp.id) FROM feedback_roles_permissions frp
  JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id
  JOIN roles r                ON frp.role_id = r.id
WHERE frp.role_id IN (<roleIds>) AND fp.name = '<name>' AND r.global_unit_id = <gu>
  AND (clusterTypeId IS NULL ? "frp.cluster_type_id IS NULL"
                             : "(frp.cluster_type_id IS NULL OR frp.cluster_type_id = <ct>)")
```

Two design consequences worth stating explicitly, both requirements in their own right:
tenant scoping uses `roles.global_unit_id`, never `frp.global_unit_id` (FN-030); and because
`canTrackComments()` passes `null`, a tracking grant carrying any `cluster_type_id` can never match (AC-008).

`isUserAssociatedWithProjectForFeedback` walks
`getProjectPartnersForProjectWithActiveProjectPhasePartnerPersons(projectId, actualPhase.id)` →
`findAllActiveForProjectPartner(partner.id)` and accepts the user when their `contactType` is in
`findRoleAcronymsByPermissionName(permissionName, globalUnitId)`.

## 9. Specificity / Feature-Flag Strategy

All six keys exist as `parameters` rows for global unit types 1, 3, 4 with `format='1'` (boolean) except the
BI report name (`format='4'`, category 3), defaults `false` / `'QA process for PMC'`. Per-tenant values live in
`custom_parameters`. Constants are declared in **both** `APConstants.java` files with values equal to
`parameters.key` (constitutional rule 4).

| Key | Constant | Read at |
|---|---|---|
| `feedback_active` | `FEEDBACK_ACTIVE` | `BaseAction.feedbackModule()`; `menu-projects.ftl`; `messages-projects.ftl`; each instrumented section action (e.g. `DeliverableAction` ~2430); `CommentableFieldsBySectionNameAndParents.execute()` |
| `feedback_clarification_needed_active` | `FEEDBACK_CLARIFICATION_NEEDED_ACTIVE` | Clarification reaction availability |
| `feedback_draft_active` | `FEEDBACK_DRAFT_ACTIVE` | Draft → approval workflow |
| `feedback_new_comment_field_active` | `FEEDBACK_NEW_COMMENT_FIELD_ACTIVE` | `#isFeedbackNewCommentFieldActive` → inline new-comment textarea |
| `crp_cluster_bi_feedback_report_name` | `CRP_CLUSTER_BI_FEEDBACK_REPORT_NAME` | `BaseAction.getFeedbackBIReportName()` → `FeedbackStatusAction.prepare()` |
| `feedback_assesor_input` / `_name` / `_email`, `feedback_replay_username`, `feedback_comment_reaction`, `feedback_response` | matching constants | Email placeholder substitution |

No new specificity is proposed. Admin-screen visibility is currently `isAiccra()`, not specificity-driven —
OQ-001 asks whether to switch it to `feedback_active`.

## 10. Integration Points

- **Power BI** — `FeedbackStatusAction` selects `BiReports` rows whose `reportName` equals the specificity
  value, and `feedbackStatus.ftl` loads the embed widget from the `bi_widget_url` BI parameter. The consolidated
  feedback view is therefore a BI artefact, not a MARLO page. See `docs/specs/domain/bi/`.
- **Email** — `EmailTrackingCommentAction` / `EmailTrackingReactionCommentAction` via `utils/SendMailS`, fired
  from the browser only when the comment carries `isTracking = true`.
- **CLARISA / CGSpace / S3 / AI services / Pusher** — Not applicable.

## 11. Observability

- Actions log through SLF4J. `FeedbackRolesPermissionsManagementAction` logs per-row save and delete failures;
  `FeedbackManagementAction` has no logger at all and its binder swallows exceptions silently — the main
  observability gap on the admin side.
- Audit columns on `feedback_qa_comments` are rich and are the real audit trail: `user_id`, `user_editor_id`,
  `user_approval_id`, `draft_action_user_id`, `responsible_user_id`, `comment_date`, `edition_date`,
  `approval_date`, `draft_action_date`, `start_track_date`, `end_track_date`.
- `FeedbackQACommentableFields` implements `IAuditLog` but `getModifiedBy()` returns a hardcoded
  `new User(3L)` and `getModificationJustification()` returns `""` — the audit-log integration for the
  configuration table is effectively inert.
- No metric or dashboard covers comment volume or reaction latency; the BI report is the only aggregate view.

## 12. Performance & Scalability

- `fieldsBySectionAndParent.do` calls `findAllByGlobalUnit` and filters by section **in Java**, not in the
  query, once per page load. Row counts are small (tens per global unit), so this is acceptable, but note the
  `async: false` XHR in `loadQACommentsIcons` blocks the main thread.
- Each instrumented section action loads all commentable fields for the global unit and all comments for
  `(phase, parentId)` in two queries, then joins them in memory — one pass per field. Linear in
  fields × comments for a single record; fine at current scale.
- `existsByRoleIdsAndPermissionName` runs once per capability check. The FTLs call the four `can*` methods
  independently (and `projectDeliverable.ftl` calls `canLeaveComments` a third time inside the popup macro), so
  a section render issues several of these queries plus, for PL/PC, a partner-person walk. Caching the four
  flags per request would be a cheap win but is out of scope here.
- Indices: PKs plus a redundant unique index on `feedback_qa_comments.id`. There is **no** index on
  `feedback_qa_comments (id_phase, parent_id)` or on `feedback_roles_permissions (role_id, feedback_permission_id)`;
  both are the hot lookup shapes and are candidates if comment volume grows.

## 13. Security Considerations

- Authentication and CRP-admin authorization come from the `crpAdminStack` interceptors; capability checks are
  server-side and the hidden spans are rendered from them, so a client-side edit of a span cannot grant a
  capability — every mutating JSON endpoint re-derives permission server-side.
- **SQL/HQL string interpolation (NF-003).** `FeedbackQACommentableFieldsMySQLDAO.findBySectionName` and
  `findAllByGlobalUnit` concatenate arguments into HQL; `FeedbackRolesPermissionMySQLDAO.existsByRoleIdsAndPermissionName`
  concatenates `roleIds`, `globalUnitID` and `clusterTypeId` into native SQL and only escapes `permissionName`
  (single-quote doubling). All inputs on these paths are currently server-derived (ids from the session,
  permission names from an enum), so there is no reachable injection today — but the pattern is one careless
  caller away from being one, and must be converted to bound parameters.
- **Unenforced tenant column on grants (FN-030).** Matching filters `roles.global_unit_id` and never reads
  `frp.global_unit_id`. Since `roles.global_unit_id` is `NOT NULL` and `getRolesList()` already restricts the
  user's roles to the current CRP, no user can match a role of another tenant — so this is **not** a cross-tenant
  privilege escalation. What it is: the grant row's own tenant column is decorative, so a row whose
  `global_unit_id` disagrees with its role's global unit is enforced for the role's tenant while being invisible
  and undeletable in that tenant's admin screen, and manageable only from the other tenant's screen. Not
  reachable through the UI — both the role dropdown and the assigned `globalUnit` are scoped to the current
  tenant — so the only origin is a migration or manual SQL. Severity: data integrity and manageability, not
  access control. **Verified latent and fixed 2026-08-25:** `aiccradb1` holds 26 grants, none mis-tenanted;
  the three live queries now bind their parameters and require `frp.global_unit_id = :globalUnitID` plus
  `r.global_unit_id = frp.global_unit_id`.
- Admin routes are reachable by direct URL even where the menu entry is hidden (NF-009); the interceptor stack,
  not menu visibility, is the control.
- `feedbackRolesPermissionsManagement.ftl` loads select2 CSS from `cdnjs.cloudflare.com` — a third-party
  runtime dependency on an authenticated admin page.
- Comment bodies are user-authored HTML fragments rendered back into the popup; sanitisation is inherited from
  the shared comment rendering path and is not re-verified here.

## 14. Backwards Compatibility & Rollout

- Everything in §1–13 is already in production for AICCRA; documenting it changes nothing.
- The remediation items split cleanly by risk:
  - **Already shipped:** FN-030 (tenant predicate), NF-003 and NF-004 for
    `FeedbackRolesPermissionMySQLDAO` — verified non-observable by a pre-flight first.
  - **Behaviour-preserving, ship any time:** NF-003 for `FeedbackQACommentableFieldsMySQLDAO`,
    FN-031 (`*Old()` removal), FN-013 (help text).
  - **Behaviour-changing, needs QA sign-off:** FN-010 (delete semantics), NF-006 and FN-022
    (validation can now reject saves that previously succeeded).
  - **Behaviour-changing, needs a product decision first:** FN-030 (tenant scoping — will revoke mis-tenanted
    grants that currently work), FN-021 (`requiresProjectAssociation`),
    FN-040 (cluster-type reconciliation), FN-012 / FN-039 (retirements), OQ-001 (admin visibility),
    OQ-003 (tracking cluster semantics).
- Rollback: every code item is revertible by commit. The one candidate migration
  (`NormalizeFeedbackSafeguardSectionName`) is reversible with the inverse `UPDATE`; capture the pre-change
  `section_name` distribution before running it.
- FN-030 should be rolled out with a pre-flight report of grant rows whose `global_unit_id` differs from their
  role's `crp`, so affected tenants are known before capabilities change.

## 15. Decision Records

- **ADR-FB-001 — Configuration by DOM `name` string, not by a typed field registry.** Chosen (2022) because it
  let any existing FTL control become commentable with no Java change. Consequence: the binding is unverifiable
  and fails silently. Documented rather than redesigned; a typed registry would be a rewrite of every
  instrumented section.
- **ADR-FB-002 — Manual request binding in both admin actions.** Struts 6 does not shrink an OGNL-bound list
  when the client removes an element, so deletes were lost. Both actions read `collection[i].prop` directly from
  `HttpServletRequest` and stop at the first empty index. Keep this; it makes client-side index continuity a
  contract (NF-002).
- **ADR-FB-003 — Two-pass save with a pre-save snapshot.** The delete pass compares against a snapshot taken
  before the upsert pass, so newly created rows are never deleted by their own save. Preserve this ordering in
  any refactor.
- **ADR-FB-004 — `field_description` is the DOM name; the 2022 migration comments are stale.** The migration
  comment says `field_Description` is the label to show in the modal, but
  `CommentableFieldsBySectionNameAndParents` serves it as JSON `fieldName` and
  `feedbackAutoImplementation.js` uses it as `img.qaComment[name=…]`. Runtime behaviour wins; documentation
  follows the code, and OQ-002 decides whether to rename.
- **ADR-FB-005 — Permissions in the database, not in `Permission` constants.** Feedback capability is data
  (`feedback_roles_permissions`), unlike the rest of MARLO's Shiro-string permissions, so administrators can
  retune reviewer scopes per cluster type without a release. Cost: four bespoke `BaseAction` gates and a
  bespoke matching query outside the security framework.
- **ADR-FB-006 — Comments are not phase-replicated.** A comment is an artefact of one review round.
  Forward-replicating it would resurrect resolved observations in the next phase.
- **ADR-FB-007 — Empty `validate()` accepted at delivery, remediated under this spec.** Recorded as a
  deliberate deviation from constitutional rule 2 rather than left implicit (see `requirements.md` §9).

## 16. Open Risks

- **R-01** — *Closed.* The cross-tenant grant pre-flight returned zero mis-tenanted grants and zero old-vs-new
  predicate divergence, so the FN-030 fix revokes nothing. Re-run that pre-flight before any reseed of
  `feedback_roles_permissions`, and once against production — the measurement is from a dev database.
- **R-07** — *Reframed 2026-08-25.* The live grant data is correct; `cluster_types` id 2 is `Theme`. The
  residual risk is environment reproducibility: a database built from migrations alone gets `Flagship` for id 2,
  so the two `can_react_comments` grants land on the wrong cluster type there. Fix the catalog migration, and
  never backfill `cluster_type_id` from `description` again — `'thematic'` does not contain `'theme'`, which is
  why `V2_6_0_20250618_1700` silently skipped those rows.
- **R-08** — The module carries real volume: 6992 comments and 5272 replies over 89 active fields. Most fields
  therefore have comments, so FN-010 is not a corner case — nearly any attempt to delete a commentable field
  will hit the `ON DELETE RESTRICT` FK. Treat T12 as the highest-value remediation task.
- **R-02** — *Closed.* All four configured slugs (`deliverable`, `innovation`, `study`,
  `projectContributionCrp`) are valid `ProjectSectionsEnum` values, and `safeguard.ftl` now publishes
  `safeguards`, so the section select hides nothing.
- **R-03** — Adding validation (NF-006, FN-022) can block a save that administrators have been completing for
  years with partially empty rows. Audit existing rows for null `role_id` / `feedback_permission_id` first.
- **R-04** — `feedbackAutoImplementation.js` is ~1780 lines of DOM-index-coupled jQuery with no tests. Any edit
  risks silent breakage of a status/button combination in `hideShowOptionButtons`; manual regression across all
  six statuses × four capabilities is the only available net.
- **R-05** — Renaming the JSON keys (OQ-002) would break the engine's field matching. If OQ-002 chooses a
  rename, the DAO/JSON/JS change must land atomically with a cache-buster bump.
- **R-06** — The BI report is the only consolidated view; a report-name drift in `custom_parameters` silently
  empties the project `Feedback` tab with no error.
