# Feedback — Agent Context

Read this before changing anything in the MARLO Feedback (QA comments) module. This is a compact, as-built
operational guide. Inspect the target source files after reading it; open `requirements.md` / `design.md` /
`task.md` in this folder only for broad, architectural, or formally tracked work.

## What The Module Is

Feedback lets reviewer roles (PMU, flagship/regional leaders and managers) attach threaded comments to
**individual form fields** of a project item (deliverable, innovation, study, outcome, safeguard), and lets the
project side (PL/PC) react to each comment (agree / disagree / ask for clarification). Comments are stored per
**phase** and per **parent record**, and are surfaced back through a Power BI report on the project `Feedback` tab.

Two admin screens configure it:

| Screen | Route | Purpose |
|---|---|---|
| Feedback Fields Management | `{crp}/feedbackManagement` | Declares **which form fields are commentable**, per section. Rows of `feedback_qa_commentable_fields`. |
| Feedback Permissions Management | `{crp}/feedbackRolesPermissionsManagement` | Maps **role + cluster type → feedback permission**. Rows of `feedback_roles_permissions`. |

Both menu entries are rendered **only inside the `action.isAiccra()` branch** of
`crp/views/admin/menu-admin.ftl`. Non-AICCRA global units have no menu link (the Struts routes still resolve).

## Primary Files

- Admin routes: `marlo-web/src/main/resources/struts-admin.xml` (`feedbackManagement`, `feedbackRolesPermissionsManagement`)
- Project route: `marlo-web/src/main/resources/struts-projects.xml` (`{crp}/feedback` → `FeedbackStatusAction`)
- JSON routes: `marlo-web/src/main/resources/struts-json.xml` (see table below)
- Admin actions: `action/crp/admin/FeedbackManagementAction.java`, `action/crp/admin/FeedbackRolesPermissionsManagementAction.java`
- Permission logic: `action/BaseAction.java` — `canLeaveComments`, `canApproveComments`, `canManageFeedback`, `canTrackComments`, `feedbackModule`
- Permission query: `marlo-data/.../dao/mysql/FeedbackRolesPermissionMySQLDAO.java#existsByRoleIdsAndPermissionName`
- Runtime UI engine: `marlo-web/src/main/webapp/crp/js/feedback/feedbackAutoImplementation.js` (~1.8k lines)
- Comment icon markup: `marlo-web/src/main/webapp/WEB-INF/global/macros/forms.ftl` (`qaComment` img in `input`, `textArea`, `select`, …; `qaPopUpMultiple` macro)
- Entities: `FeedbackQACommentableFields`, `FeedbackQAComment`, `FeedbackQAReply`, `FeedbackPermission`, `FeedbackRolesPermission`, `FeedbackStatus`
- HBM mappings: `marlo-data/src/main/resources/xmls/Feedback*.hbm.xml`
- i18n: `global.properties` keys `feedbackManagement.*`, `feedbackPermissions.*`, `CRPAdmin.menu.feedback*`

## Feedback Fields Management — What Each Field Means

One row = one commentable form field. Table `feedback_qa_commentable_fields`.

| Admin label (i18n) | Entity property | Column | What it actually does |
|---|---|---|---|
| Section Name (`feedbackManagement.sectionName`) | `sectionName` | `section_name` | Section slug. Must match the page's `<input id="sectionNameToFeedback">` value **and** a `ProjectSectionsEnum.getStatus()` value. Used to filter fields in `fieldsBySectionAndParent.do` and to build the comment deep link in `SaveFeedbackCommentsAction`. |
| Section Description (`…sectionDescription`) | `sectionDescription` | `section_description` | Human-readable section name for reports and for the admin block title. Not read by the runtime JS. |
| Field Name (`…fieldName`) | `fieldName` | `field_name` | **Human-readable label.** Served to the browser as JSON key `description`; rendered as the popup title (`Comment on <label>`) and used in notification emails. |
| Field Description (`…fieldDescription`) | `fieldDescription` | `field_Description` | **The technical DOM name — the join key.** Must be the exact `name` attribute (OGNL expression) of the instrumented form control, e.g. `deliverable.deliverableInfo.title`. Served as JSON key `fieldName`; used as the selector `img.qaComment[name="…"]` to attach the icon, and echoed as `frontName` in comment payloads. |
| Parent Field Description (`…parentFieldDescription`) | `parentFieldDescription` | `parent_field_description` | `name` attribute of an input whose **value** labels the parent record (e.g. the deliverable title). JS reads `$('input[name="<value>"]').val()` and sends it as `parentFieldDescription` on save and in emails. |
| *(not on the form)* | `parentFieldIdentifier` | `parent_field_identifier` | Legacy. Exposed as `identifierField` / `parentField` by `fieldsBySectionAndParent.do` and `feedbackParent.do`, but **no consumer reads it today**. Preserved on save only because the binder skips nulls. |
| *(implicit)* | `globalUnit` | `global_unit_id` | Tenant scope. Forced to the current global unit on save. |
| *(implicit)* | `active` | `is_active` | Queries filter `is_active = 1`, but the entity's `isActive()` returns a hardcoded `true`. |

**The `fieldName` / `fieldDescription` naming is inverted twice.** `CommentableFieldsBySectionNameAndParents`
maps `field_description → JSON "fieldName"` and `field_name → JSON "description"`, and the 2022 migration's
column comments describe the reverse of current behaviour. Trust this table and the JS, not the column comments.

## Feedback Permissions Management — What Each Field Means

One row = one grant. Table `feedback_roles_permissions`.

| Admin label | Property | Column | Meaning |
|---|---|---|---|
| Permission Description | `description` | `description` | Free-text label; also the admin block title. No behaviour. |
| Permission Name | `feedbackPermission` | `feedback_permission_id` | FK to `feedback_permissions`. Catalog is global (not per global unit) and is **not editable from the UI**. |
| User Role | `role` | `role_id` | FK to `roles`. The dropdown lists only roles whose `crp` is the current global unit. |
| Cluster Type | `clusterType` | `cluster_type_id` | Scopes the grant to a cluster type. `NULL` = all cluster types. |
| *(implicit)* | `globalUnit` | `global_unit_id` | Tenant scope. Forced to the current global unit on save. |
| *(not on the form)* | `requiresProjectAssociation` | `requires_project_association` | Mapped and seeded (`true` for PL/PC) but **never read** — `canManageFeedback` still hardcodes `Arrays.asList("PL", "PC")`. |

### The four permissions

`FeedbackPermissionsEnum` ↔ `feedback_permissions.name`:

| Enum constant | `name` value | BaseAction gate | Effect |
|---|---|---|---|
| `CAN_LEAVE_COMMENTS` | `can_leave_comments` | `canLeaveComments(projectID)` | See the comment icon and create/edit comments. |
| `CAN_APPROVE_COMMENTS` | `can_approve_comments` | `canApproveComments(projectID)` | Approve/decline draft comments (promote `Draft` → visible). |
| `CAN_MANAGE_FEEDBACK` | `can_react_comments` | `canManageFeedback(projectID)` | React to a comment: agree / disagree / request clarification. |
| `CAN_TRACK_COMMENTS` | `can_track_comments` | `canTrackComments()` | Highlight/track a comment and receive tracking emails. |

Note the constant name and the value disagree for `CAN_MANAGE_FEEDBACK` (`can_react_comments`).

### How a grant is evaluated

`existsByRoleIdsAndPermissionName(roleIds, permissionName, globalUnitId, clusterTypeId)`:

- Super admin (`canAccessSuperAdmin()`) short-circuits to `true` in all four gates.
- Joins `feedback_roles_permissions → feedback_permissions → roles`, filters `roles.global_unit_id`
  (**not** `feedback_roles_permissions.global_unit_id`).
- `clusterTypeId` comes from `getClusterTypeIDFromProject(projectID)` → `ProjectInfo.clusterType` of the actual phase.
- `clusterTypeId == null` → matches **only** rows with `cluster_type_id IS NULL`.
  `clusterTypeId != null` → matches `cluster_type_id IS NULL OR = clusterTypeId`.
- `canTrackComments()` always passes `null`, so only cluster-agnostic rows grant tracking.
- `canManageFeedback` additionally requires, for users holding role acronym `PL` or `PC`, that the user be an
  active project partner person on that project whose `contactType` is one of the acronyms returned by
  `findRoleAcronymsByPermissionName("can_react_comments", globalUnitId)`.
- `*Old()` variants (`canManageFeedbackOld`, `canApproveCommentsOld`, `canLeaveCommentsOld`, `canTrackCommentsOld`)
  are the pre-database, acronym-hardcoded implementations. Dead but retained; do not extend them.

## Runtime Wiring

A page opts into feedback with these hidden markers (see `projectDeliverable.ftl` for the canonical set):

```
<input type="hidden" id="sectionNameToFeedback" value="deliverable" />
<span id="parentID">${deliverableID!}</span>   <span id="projectID">…</span>
<span id="phaseID">…</span>                    <span id="userID">…</span>
<span id="userCanManageFeedback">${(action.canManageFeedback(projectID!-1)?c)!"false"}</span>
<span id="userCanLeaveComments">${(action.canLeaveComments(projectID!)?c)!"false"}</span>
<span id="userCanApproveFeedback">${(action.canApproveComments(projectID!-1)?c)!"false"}</span>
<span id="canTrackComments">${(action.canTrackComments()?c)!"false"}</span>
```

plus `feedbackAutoImplementation.js` in `customJS` and `[@customForm.qaPopUpMultiple …]` for the popup templates.

Sections currently instrumented: `projectDeliverable.ftl`, `projectInnovation.ftl`, `projectStudy.ftl`
(via `studiesTemplates.ftl`), `projectContributionCrp.ftl`, `safeguard.ftl`.

### JSON endpoints (`struts-json.xml`)

| Path | Action | Role |
|---|---|---|
| `fieldsBySectionAndParent.do` | `CommentableFieldsBySectionNameAndParents` | Commentable fields for a section. Gated by `hasSpecificities(FEEDBACK_ACTIVE)`. |
| `feedbackComments.do` / `feedbackComments2.do` | `FeedbackQACommentsAction` / `…MultipleAction` | Load comment threads. |
| `feedbackReplies.do` | `FeedbackQARepliesAction` | Load replies. |
| `getCommentStatus.do` | `FeedbackQANumberCommentsAction` | Comment counts / bubble state. |
| `saveFeedbackComments.do` | `SaveFeedbackCommentsAction` | Create/update a comment; also builds the deep link. |
| `saveFeedbackReply.do` | `SaveFeedbackReplyAction` | Create a reply. |
| `saveCommentStatus.do` / `saveReplyStatus.do` | `SaveCommentStatusAction` / `SaveReplyStatusAction` | Set status. |
| `saveTrackingStatus.do` | `SaveTrackingStatusAction` | Toggle tracking. |
| `deleteFeedbackComments.do` / `deleteFeedbackReplies.do` | `DeleteFeedbackCommentsAction` / `…Replies` | Delete. |
| `sendFeedbackActionEmail.do` / `sendFeedbackReactionEmail.do` | `EmailTrackingCommentAction` / `EmailTrackingReactionCommentAction` | Tracking notifications. |
| `feedbackParent.do` | `FeedbackParentIdAction` | Returns `parentFieldIdentifier` / `parentFieldDescription`. **No caller.** |

### Statuses

`feedback_statuses` / `FeedbackStatusEnum`: `1 Agreed`, `2 Clarification needed`, `3 Draft`, `4 Admitted`
(a.k.a. "Pending"), `5 Disagreed`, `6 Dismissed`. `visibility` is `1 consolidation`, `2 feedback`, `3 both`.
The JS reaction codes are `0 → Disagreed`, `1 → Agreed`, `2 → Clarification needed`, `6 → Dismissed`;
`SaveCommentStatusAction` maps both `0` and `5` to `Disagreed`.

## Specificities (`parameters` / `custom_parameters`)

Constants live in **both** `APConstants.java` files. `BaseAction.feedbackModule()` returns `FEEDBACK_ACTIVE`.

| Key | Constant | Effect |
|---|---|---|
| `feedback_active` | `FEEDBACK_ACTIVE` | Master switch: project `Feedback` menu item, per-section comment loading, `fieldsBySectionAndParent.do`. |
| `feedback_clarification_needed_active` | `FEEDBACK_CLARIFICATION_NEEDED_ACTIVE` | Enables the "Clarification needed" reaction. |
| `feedback_draft_active` | `FEEDBACK_DRAFT_ACTIVE` | Enables the draft → approval workflow. |
| `feedback_new_comment_field_active` | `FEEDBACK_NEW_COMMENT_FIELD_ACTIVE` | Enables the inline "New comment" textarea. |
| `crp_cluster_bi_feedback_report_name` | `CRP_CLUSTER_BI_FEEDBACK_REPORT_NAME` | Name of the Power BI report embedded on the project `Feedback` tab. |
| `feedback_assesor_*`, `feedback_replay_username`, `feedback_comment_reaction`, `feedback_response` | matching constants | Email template placeholders. |

## Non-Negotiable Rules

- `field_description` **is** the DOM `name` attribute. Change it only together with the FTL control it targets;
  a typo silently removes the comment icon with no error anywhere.
- `section_name` must stay in `ProjectSectionsEnum`. `SaveFeedbackCommentsAction` does
  `switch (ProjectSectionsEnum.getValue(sectionName))`, and `getValue` returns `null` for unknown slugs → NPE.
- Both admin actions bind their lists **manually** from `HttpServletRequest` (`bindFeedbackFieldsFromRequest`,
  `bindFeedbackRolesPermissionsFromRequest`) because Struts 6 will not shrink an OGNL-bound list on delete.
  The binder walks `name[0]`, `name[1]`, … and **stops at the first fully empty index** — index continuity is
  required, which is why the JS `updateIndexes()` reindexing on remove is load-bearing.
- Both admin `save()` are gated on `hasPermission("*")` with no `basePermission` set, i.e. the
  `crpAdminStack` (`accessibleAdmin` + `canEditCrpAdmin`) is the real gate. Keep that stack.
- Deletes here are **hard** deletes, not `is_active = 0`. `feedback_qa_comments.field_id` is
  `ON DELETE RESTRICT`, so removing a field that already has comments raises a constraint violation.
- Adding a new permission requires an insert into `feedback_permissions` (there is no UI for the catalog)
  **and** a new `FeedbackPermissionsEnum` constant **and** a gate method in `BaseAction`.
- After editing `feedbackAutoImplementation.js` or either admin JS, bump the `?YYYYMMDD` cache-buster in every
  FTL that references it. `feedbackManagement.ftl` currently has **no** cache-buster on its `customJS` entry.

## Known Defects / Traps (as-built)

1. `FeedbackManagementAction.save()` nests the delete loop inside `if (feedbackFields != null && !feedbackFields.isEmpty())`,
   so removing **all** rows and saving deletes nothing. `FeedbackRolesPermissionsManagementAction` does not have
   this bug (its delete loop is outside the guard).
2. `safeguard.ftl` publishes `sectionNameToFeedback = "safeguard"`, but the enum constant is `SAFEGUARDS("safeguards")`.
   Rows configured as `safeguard` return no fields from the enum-based paths; rows configured as `safeguards`
   never match the page. Verify the actual `section_name` data before touching this section.
3. `FeedbackQACommentableFieldsMySQLDAO.findBySectionName` / `findAllByGlobalUnit` interpolate arguments into HQL
   strings. Do not extend that pattern. (`FeedbackRolesPermissionMySQLDAO` was converted to bound parameters on
   2026-08-25.)
4. ~~`existsByRoleIdsAndPermissionName` ignored `feedback_roles_permissions.global_unit_id`.~~ **Fixed
   2026-08-25.** All three live queries now require `frp.global_unit_id = :globalUnitID` plus
   `r.global_unit_id = frp.global_unit_id`. Verified non-observable first: 0 of 26 grants were mis-tenanted.
5. ~~`findObjectsByRoleIdsAndPermissionName` emitted `AND frp.cluster_type_id …` without ever aliasing~~
   **Fixed 2026-08-25** (alias declared; still has no caller). Original text: it emitted the predicate without aliasing
   `feedback_roles_permissions AS frp` — it would fail if called. No caller today.
6. `FeedbackRolesPermissions.hbm.xml` maps `<column name="requires_project_association " …>` with a trailing
   space. Cosmetic in practice — MySQL tolerates the trailing whitespace in the generated SQL, and the admin
   screen loads these rows fine. Still worth fixing.
7. `FeedbackManagementAction.prepare()` builds `projectSections` from `ProjectSectionsEnum`, but
   `feedbackManagement.ftl` renders Section Name as a free-text `input` and never uses the list.
8. `validate()` in both admin actions is `if (save) { }` — empty. `required=true` in the FTL is cosmetic;
   there is no server-side validation and no `Validator` class for either screen.
9. Both admin JS files are copies of the SLO admin script and carry dead handlers (`addIndicator`, `addTargets`,
   `addCrossCuttingIssue`, datepicker config) plus `console.log` calls.
10. `feedbackAutoImplementation.js` ships `console.log` of every permission flag on page load.
11. **The migration history does not reproduce the database for `cluster_types`.**
    `V2_6_0_20210604_1444` sets id 2 = `Flagship`; the live database has id 2 = `Theme` (renamed outside
    Flyway). Live grant data is correct and self-consistent — `FPL`/`FPM` `can_react_comments` do sit on
    `Theme` — but a fresh environment built from migrations gets `Flagship` there and those grants land wrong.
    Also: `V2_6_0_20250618_1700` backfills `cluster_type_id` by `LIKE`-matching `cluster_types.name` inside
    `description`, and `'thematic'` does not contain `'theme'` — so it silently skips the rows it appears to
    cover. Never backfill by description again.
12. **`AICCRA_III` (global unit 47) has feedback effectively unconfigured:** zero commentable fields and a
    single grant (id 39, `CL` / `can_leave_comments` / `Theme`) whose description still says
    `"PMU - can_write_comments on all clusters"`. Check which global unit you are actually working in before
    concluding the module is broken.
13. ~~Feedback Permissions Management could not create its first grant in an empty global unit.~~ **Fixed
    2026-08-25.** `getFeedbackRolesPermissionByGlobalUnitID` returned `null` on no rows, `prepare()` streamed it
    unguarded, and the resulting NPE was swallowed before the three dropdown catalogs loaded. If you see empty
    selects on this screen again, check that `prepare()` still loads the catalogs in try blocks separate from
    the grant list.
14. **`ClusterTypeMySQLDAO.findAll` and `FeedbackQACommentableFieldsMySQLDAO.findAll` / `findAllByGlobalUnit`
    still return `null` when empty.** Always null-check them; that pattern caused item 13.
15. ~~The study section was missing two of the four capability markers.~~ **Fixed 2026-08-25.**
    `studiesTemplates.ftl` now renders all four. Note the JS guards compare against the **string** `'false'`, so
    an absent marker reads as `undefined` and the guard never fires — always default these spans to `"false"`.
16. **No safeguard fields are configured.** `safeguard.ftl` carries the feedback markers, but the only
    configured sections are `deliverable`, `innovation`, `study` and `projectContributionCrp`. The slug
    mismatch in item 2 is therefore latent, not live.

## Verification Shortlist

- `mvn -q checkstyle:check` (gate).
- Admin round-trip on both screens: add, edit, reorder-by-delete, save, reload — confirm indexes and persisted rows.
- On a project section: confirm the comment icon appears next to the control whose `name` equals `field_description`,
  and that the popup title equals `field_name`.
- Exercise each of the four gates with a role granted / not granted, and with a project whose cluster type
  matches / does not match the grant row.
- Toggle `feedback_active` off and confirm the project `Feedback` tab and all icons disappear.

## Open Only When Relevant

- UI composition: `reports/ai-context/frontend-composition-map.md`
- Save/validation: `reports/ai-context/save-validation-matrix.md`
- Interceptors: `reports/ai-context/interceptor-validator-playbook.md`
- Routing: `reports/ai-context/struts-critical-routing-catalog.md`
- Manager save/delete: `reports/ai-context/persistence-replication-managerimpl.md`
- Accordion/expandable list bugs (both admin screens use that pattern):
  `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`
- Broad or architectural Feedback work: `requirements.md`, `design.md`, `task.md` in this folder.
