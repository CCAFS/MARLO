# AI Services (AI-CCRA section) — Agent Context

Read this before changing anything in the MARLO **AI section** (the `AI-CCRA` main-menu entry, route `{crp}/ai`).
This is a compact, as-built operational guide. Inspect the target source files after reading it.

**This folder also holds a full spec set — `requirements.md`, `design.md`, `task.md` (spec ID
`DOMAIN-AI-SERVICES-001`, Jira A2-2433).** Read them for anything beyond a routine tweak: `design.md` §15 carries ADRs
that constrain the DAO contract and the template, and `task.md` §9 lists the open risks on the as-built code. Trust
the directory listing over this paragraph — it has been wrong about which spec files exist.

## The One Thing To Know First

**The section is data driven and scoped per Global Unit. Every AI tool card rendered on the dashboard is one row of
the `ai_report_configuration` table owned by the logged Global Unit.** The row supplies the card title, the card
description, the button label and the button URL. Nothing about the cards is hardcoded in `AiAction.java`, in
`aiDashboard.ftl`, or in `global.properties`.

Consequences, and they are the most common source of confusion in this module:

- To **add, rename, re-describe, re-link or retire a tool**, ship a Flyway migration that writes to
  `ai_report_configuration`, always setting `global_unit_id`. Do **not** add markup to the FTL and do **not** add i18n
  keys for card text.
- A row belongs to exactly one Global Unit. To offer the same tool to two programs, insert one row per Global Unit.
- The card copy is **not translatable**. It is stored as raw text (emoji included) in the table and printed verbatim.
- If the **logged Global Unit** has no active row, the dashboard renders a "no tools configured" fallback block
  instead of cards. Another Global Unit's rows are never rendered.
- The `userIdea.reportGeneratorNarrative`, `userIdea.ChatbotNarrative` and `userIdea.innovationGenerator` keys in
  `global.properties` are **stale leftovers** from before the table existed. They duplicate the current card text but
  are no longer read by any template. Do not edit them expecting the UI to change.

## What The Module Is

A single read-only landing page that lists the external AI tools available to the program (today for AICCRA: Report
Generator, Chatbot, Innovation Metadata Extractor) and links out to each one, passing the logged user's email and name
as query parameters. Below the cards it renders a disclaimer, an invitation message, and a free-text box that is a
leftover of the original "user idea" feedback form (see *Legacy UserIdea Half* below).

The page does **not** consume the AI services itself: it is a launcher. The tools live outside MARLO (PRMS web,
AWS Lambda function URLs).

## Primary Files

- Route: `marlo-web/src/main/resources/struts-projects.xml` (`{crp}/ai`, package `ai` namespace `/ai`)
- Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/ai/AiAction.java`
- View: `marlo-web/src/main/webapp/WEB-INF/crp/views/ai/aiDashboard.ftl`
- CSS: `marlo-web/src/main/webapp/crp/css/ai/aiDashboard.css`
- Menu entry: `marlo-web/src/main/webapp/WEB-INF/global/pages/main-menu.ftl` (slug `ai`)
- Interceptor: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/interceptor/project/EditAiInterceptor.java`
- Entity: `marlo-data/.../data/model/AiReportConfiguration.java`
- HBM: `marlo-data/src/main/resources/xmls/AiReportConfigurations.hbm.xml`
- DAO: `marlo-data/.../data/dao/mysql/AiReportConfigurationMySQLDAO.java`
- Manager: `marlo-data/.../data/manager/impl/AiReportConfigurationManagerImpl.java`
- Legacy entity: `AiReportConfiguration`'s sibling `UserIdea` (`UserIdeas.hbm.xml`, `UserIdeaMySQLDAO`)
- i18n: `global.properties` keys `menu.ai`, `breadCrumb.menu.userIdea`, `userIdea.*`

## Route And Gates

```
/ai/{crp}/ai.do  ->  AiAction  ->  interceptor stack: editAiStack  ->  /WEB-INF/crp/views/ai/aiDashboard.ftl
```

- `editAiStack` (`struts.xml`) = `i18nFile`, `validCrp`, `requireUser`, `validSessionCrp`, `canEditAi`,
  `keepRedirectMessages`, `accessibleStage`, `trimInputs`, `defaultStack`.
- `canEditAi` → `EditAiInterceptor`. It reuses the **project** permission (`Permission.PROJECT__PERMISSION`) and grants
  edit to super admin / CRP admin / `PC` / `PL`. Since the page is effectively read-only, the edit flags only control
  the leftover textarea and the Save button.
- **Menu visibility is a specificity:** `action.hasSpecificities('ai_section_active')` in `main-menu.ftl`. The Struts
  route still resolves for a global unit that has the flag off — only the menu link disappears.

### Specificity history

| Migration | What it did |
|---|---|
| `V2_6_0_20250905_1607__CreateSpecifiyToAIModule.sql` | Seeded the flag for global unit types 1, 3, 4 as `user_idea_section_active`, default `false`. |
| `V2_6_0_20260826_1000__RenameUserIdeaSectionActiveParameter.sql` | Renamed key/description to `ai_section_active` / `Activate the AI section`. |

### Schema history

| Migration | What it did |
|---|---|
| `V2_6_0_20251107_1400__CreateReportsTableConfiguration.sql` | Created `ai_report_configuration`. |
| `V2_6_0_20251113_1400__UpdateAITable.sql` | Seeded the three AICCRA tools. |
| `V2_6_0_20260827_0747__AddGlobalUnitToAiReportConfiguration.sql` | **A2-2433:** added `global_unit_id` (`NOT NULL`, FK, indexed) and backfilled the existing rows to Global Unit 45 (AICCRA). |

Constant: `APConstants.AI_SECTION_ACTIVE` in **both** `marlo-data` and `marlo-web` (value must equal `parameters.key`).
Per-global-unit values live in `custom_parameters` and are keyed by `parameter_id`, so the rename preserved them.
The flag reaches the session through `ValidSessionCrpInterceptor` / `InternationalitazionFileInterceptor`, which put
every `parameters.key` into the session map; `BaseAction.hasSpecificities(key)` just reads that map.

## `ai_report_configuration` — What Each Column Means

One row = one card, owned by one Global Unit. Seeded by
`V2_6_0_20251107_1400__CreateReportsTableConfiguration.sql` (DDL) and `V2_6_0_20251113_1400__UpdateAITable.sql` (the
three AICCRA tools); made per-tenant by `V2_6_0_20260827_0747__AddGlobalUnitToAiReportConfiguration.sql` (A2-2433).

| Column | Entity property | Mapped in HBM | What it does |
|---|---|---|---|
| `id` | `id` | yes | PK. |
| `global_unit_id` | `globalUnit` | yes | **Owner of the card.** `NOT NULL`, FK to `global_units`, indexed. The read is filtered by it, so a card is only ever rendered for its own Global Unit. |
| `report_title` | `reportTitle` | yes | Card heading, printed verbatim (leading emoji is part of the stored value). Rendered only when non-empty. |
| `report_description` | `reportDescription` | yes | Card body text, printed verbatim. Rendered only when non-empty. |
| `button_label` | `buttonLabel` | yes | Button caption. Falls back to `reportTitle` when empty. |
| `button_link` | `buttonLink` | yes | Target URL. **No button is rendered at all when this is empty.** See link handling below. |
| `is_active` | `active` (inherited) | yes | Soft-delete flag. Filters the query (`arc.active = TRUE`). |
| `active_since` | `activeSince` (inherited) | yes | Set by `AuditColumnHibernateListener` on insert; `update="false"`. |
| `created_by` | `createdBy` (inherited) | yes | `NOT NULL` + FK to `users`. Set by `AuditColumnHibernateListener` on insert; `update="false"`. |
| `modified_by` | `modifiedBy` (inherited) | yes | Set by `AuditColumnHibernateListener` on update. |
| `modification_justification` | inherited | yes | |

### Tenancy: the content is per Global Unit

Each row belongs to exactly one Global Unit through `global_unit_id`, and
`AiReportConfigurationDAO.findAllByGlobalUnit(long)` is the only read path:

```sql
SELECT arc FROM AiReportConfiguration arc
WHERE arc.globalUnit.id = :globalUnitId AND arc.active = TRUE ORDER BY arc.id
```

`AiAction.prepare()` passes `this.getCurrentCrp().getId()`, so a Global Unit never sees another one's tools. Two rules
follow:

- **There is deliberately no unscoped `findAll()`** on the DAO or the manager. It was removed in A2-2433 precisely so
  the tenancy filter cannot be forgotten. Do not reintroduce one.
- **`global_unit_id` is `NOT NULL` on purpose.** A row with no owner would be unreachable by the query — invisible to
  everyone, with no error and no log. `NULL` is *not* a "shared across all Global Units" marker; to offer one tool to
  two programs, insert one row per Global Unit.

The section *visibility* is a separate, older mechanism: the `ai_section_active` specificity in `custom_parameters`
(see *Route And Gates*). Visibility and content are both per Global Unit now, but they are configured independently —
a Global Unit can have the flag on and no rows, which renders the empty state.

### Persistence: what the A2-2433 mapping fixed

The audit columns (`is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) exist in the
table but used to be **absent from the HBM**. `AuditColumnHibernateListener` populates them by looking the property up
in the entity metamodel (`ArrayUtils.indexOf(propertyNames, "createdBy")`), so an unmapped property meant the listener
logged `Field 'createdBy' not found on entity …` and wrote nothing. Consequences, now resolved:

- `deleteAiReportConfiguration()` calls `setActive(false)` and updates. With `active` unmapped the generated `UPDATE`
  touched only the four text columns — **the soft delete was a no-op.** It now really writes `is_active = 0`.
- `saveAiReportConfiguration()` on a new row emitted an `INSERT` without `created_by`, which is `NOT NULL` with an FK to
  `users` and no default, so the insert failed. The listener now fills `created_by`, `active_since` and `is_active`.
- `saveAiReportConfiguration()` carried **no `@Transactional`**, so any write would have been rolled back by the pool.
  It has one now.
- Entities loaded from this table used to report `isActive() == true` in memory whatever the DB said. They no longer do.

**Rows are still managed by migrations / DBA action.** There is no admin screen for this table (explicitly out of scope
in A2-2433), and `AiReportConfigurationManager` has exactly one caller — `AiAction`. The write path merely works now if
something ever needs it; a caller creating a row must set `globalUnit` itself.

### The read returns an empty list, never `null`

`findAllByGlobalUnit` returns an empty list when the Global Unit has no active rows — the pre-A2-2433 `findAll()`
returned `null` instead. `AiAction.prepare()` also falls back to an empty list when there is no Global Unit in session
(logging `"No Global Unit in session…"`) or when the query throws (logging `"Error loading AI report
configurations"`). The view still guards with `[#if reportConfigurations?? && reportConfigurations?has_content]`; keep
that guard if you touch the template. If the cards vanish in an environment, grep the logs for those two lines before
suspecting the specificity flag.

## How A Card Link Is Built

`aiDashboard.ftl` post-processes `buttonLink` before rendering the anchor:

1. Trim; if the value has no `scheme://` prefix, it becomes protocol-relative (`//host/path`).
2. Append the logged user's identity as query parameters: `?user_email=<email>` and `&user=<full name>`
   (from `AiAction.getUserEmail()` / `getUsername()`, both derived from `getCurrentUser()`).
3. If the URL already contains a `?`, the appended block's `?` is rewritten to `&`.
4. The anchor is `target="_blank" rel="noopener noreferrer"`.

So the external tools receive MARLO user identity in the URL. Keep that in mind for any new destination: **do not point
`button_link` at a host that should not receive user emails.**

## Legacy UserIdea Half

The page still carries the original "share your idea" form: a `userIdea.answer` textarea plus an inline send button
whose click handler simply clicks the standard Save button from `buttons-projects.ftl`. Treat it as **non-functional**:

- `AiAction.prepare()` has `userIdeas = userIdeaManager.findAll();` **commented out**, so `userIdea` is always a fresh
  empty `UserIdea`.
- `AiAction.save()` calls `userIdeaManager.saveUserIdea(...)`, but `UserIdeaManagerImpl.saveUserIdea` has **no
  `@Transactional`**, and the bound entity has no `createdBy` while `user_ideas.created_by` is `NOT NULL` with an FK.
- `AiAction.validate()` has an empty `if (save) { }` body — no validator, no `invalidFields` population.

Table `user_ideas` (`V2_6_0_20250905_1550__CreateUserIdeaTable.sql`): `question`, `answer`, standard audit columns.
Its HBM *does* map `active` / `activeSince` / `createdBy` / `modifiedBy`, unlike the AI table.

If the ask is "make the comment box work", that is a real piece of work (transactional manager, `createdBy`, a
validator, a read-back list), not a tweak — and the naming should move away from `userIdea` at the same time.

## i18n Keys

| Key | Used by the view | Notes |
|---|---|---|
| `menu.ai` | yes (menu label) | `AI-CCRA`. Also in `custom/test.properties`. |
| `userIdea.description` | yes (intro paragraph) | |
| `userIdea.disclaimer` | yes (warning box) | |
| `userIdea.question.default` | yes (info box) | |
| `userIdea.answer` | yes (textarea label) | |
| `userIdea.noReportsConfigured` / `.noReportsConfiguredDescription` | yes (empty-state) | Both added to `global.properties` in A2-2433, so the `default=` literals in the template are no longer what renders. Not in any `custom/*.properties` — the global fallback covers every program. |
| `breadCrumb.menu.ai` | yes (breadcrumb) | Reads `AI-CCRA`. The breadcrumb is a **single** entry: A2-2433 dropped the stale second one (`breadCrumb.menu.userIdea`, `AICHAT BOT`) that duplicated a link to this same page. |
| `breadCrumb.menu.userIdea` | **no** | Dead since A2-2433. Still present in `global.properties` and `custom/test.properties`; removing it belongs to the `userIdea.*` rename cleanup. |
| `userIdea.reportGeneratorNarrative`, `userIdea.ChatbotNarrative`, `userIdea.innovationGenerator` (+ `.readText`) | **no** | Stale duplicates of the DB card text. Dead. |
| `userIdea.title`, `userIdea.Description`, `userIdea.question*` | **no** | Dead. |

Note the whole key family is still prefixed `userIdea.` even though the section is now the AI dashboard. Renaming the
keys is a separate, mechanical cleanup (touch `global.properties`, every `custom/*.properties`, and the FTL together).

**The section label is still AICCRA-specific and is not per Global Unit** — see *Known Gaps And Traps* #7, which is
the only record of it. There is no Jira issue.

## Known Gaps And Traps

1. **`struts-ai.xml` is dead.** It declares package `ai` on namespace `/ai2` and is **not** included from `struts.xml`.
   The live mapping is in `struts-projects.xml`. Edit the wrong file and nothing happens.
   (`docs/trd/trd.md` §Routing still lists `struts-ai.xml` among the active mapping files.)
2. The view is loaded through the `input` result, and `success` redirects back to `{crp}/ai` with `edit=true`.
3. `aiDashboard.ftl` pulls two **CDN stylesheets** (select2) directly. That conflicts with the offline/self-contained
   posture of the rest of the app; the page also declares `pageLibs = ["select2","flag-icon-css"]` without using them.
4. The template carries a lot of inline `style="…"` and an inline `<script>` block. If you restyle it, prefer
   `crp/css/ai/aiDashboard.css` and remember the **cache-busting `?YYYYMMDD` bump** on the asset reference.
5. `currentSectionString` in the template is built as `project-…-phase-…` — this page is not a project section; the
   value exists only to satisfy shared includes.
6. Emoji live inside the DB values (`🧾`, `💬`, `🧠`); the table is `utf8mb4`. Keep any new migration `utf8mb4`-safe.
7. **The section label is AICCRA-specific while the content is per Global Unit.** A2-2433 made the cards per tenant but
   deliberately left the label alone, so a non-AICCRA program now sees *its own* cards under an `AI-CCRA` heading. Four
   places hold the literal:
   - `global.properties` / `custom/test.properties` → `menu.ai=AI-CCRA` (main-menu entry)
   - `global.properties` / `custom/test.properties` → `breadCrumb.menu.ai=AI-CCRA` (breadcrumb)
   - `aiDashboard.ftl` → `[#assign title = "AI-CCRA" /]`
   - `aiDashboard.ftl` → the page `<h4>AI-CCRA</h4>`

   The fix, when someone picks it up: make `menu.ai` and `breadCrumb.menu.ai` generic in `global.properties`, add an
   `AI-CCRA` override in `custom/aicrra.properties` and `custom/aiccra3.properties`, and replace both FTL literals with
   `[@s.text name="menu.ai" /]` so AICCRA keeps rendering exactly as it does today. **No Jira issue was raised — this
   entry is the record.**

## Change Recipes

**Add / change / retire an AI tool** → Flyway migration only. `global_unit_id` decides which program sees the card, so
it is never optional:

```sql
-- add (global_unit_id is NOT NULL: name the owner explicitly)
INSERT INTO ai_report_configuration
  (global_unit_id, report_title, report_description, button_label, button_link, created_by)
VALUES (45, '🧠 New Tool', 'What it does…', 'Go to New Tool', 'https://…', 1);

-- change copy or destination
UPDATE ai_report_configuration SET report_description = '…', button_link = '…' WHERE id = ?;

-- give another program the same tool: one row per Global Unit, never a shared row
INSERT INTO ai_report_configuration
  (global_unit_id, report_title, report_description, button_label, button_link, created_by)
SELECT <other_global_unit_id>, report_title, report_description, button_label, button_link, 1
FROM ai_report_configuration WHERE id = ?;

-- retire
UPDATE ai_report_configuration SET is_active = 0 WHERE id = ?;
```

**Onboard a new program's AI tools** → two independent steps, both required: set `ai_section_active` to `true` for the
Global Unit (menu visibility) *and* insert its `ai_report_configuration` rows (content). Only the first one is
reversible from the Super Admin UI.

**Enable the section for a global unit** → set the `ai_section_active` custom parameter to `true` for that global unit
(Super Admin → CRP parameters, or a `custom_parameters` migration). Users must re-login for the session map to pick it
up.

**Change the intro / disclaimer / invitation text** → `global.properties` keys `userIdea.description`,
`userIdea.disclaimer`, `userIdea.question.default` (plus the program's `custom/*.properties` when it overrides them).

**Change a card's text** → the table, never the properties file.

## Boundaries — What This Spec Does *Not* Cover

- `summary_ai_report_tab_active` and `action/summaries/ai/*` (`AIReportService`, `AIIndicatorReport`,
  `AIReportSummaryAction`) — a different feature: AI-generated content inside the summaries/report tab.
- The TIP module (`tip_section_active`), the Feedback module, and the BI/Power BI reports.
- The external AI services themselves (PRMS web, the Lambda-hosted chatbot and extractor). MARLO only links to them.
