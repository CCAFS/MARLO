# AI Services (AI-CCRA section) — Agent Context

Read this before changing anything in the MARLO **AI section** (the `AI-CCRA` main-menu entry, route `{crp}/ai`).
This is a compact, as-built operational guide. Inspect the target source files after reading it. There is no
`requirements.md` / `design.md` / `task.md` in this folder yet — create the full spec set only when the work is broad,
architectural, or needs formal traceability.

## The One Thing To Know First

**The section is data driven. Every AI tool card rendered on the dashboard is one row of the `ai_report_configuration`
table.** The row supplies the card title, the card description, the button label and the button URL. Nothing about the
cards is hardcoded in `AiAction.java`, in `aiDashboard.ftl`, or in `global.properties`.

Consequences, and they are the most common source of confusion in this module:

- To **add, rename, re-describe, re-link or retire a tool**, ship a Flyway migration that writes to
  `ai_report_configuration`. Do **not** add markup to the FTL and do **not** add i18n keys for card text.
- The card copy is **not translatable**. It is stored as raw text (emoji included) in the table and printed verbatim.
- If the table has no active row, the dashboard renders a "no tools configured" fallback block instead of cards.
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

Constant: `APConstants.AI_SECTION_ACTIVE` in **both** `marlo-data` and `marlo-web` (value must equal `parameters.key`).
Per-global-unit values live in `custom_parameters` and are keyed by `parameter_id`, so the rename preserved them.
The flag reaches the session through `ValidSessionCrpInterceptor` / `InternationalitazionFileInterceptor`, which put
every `parameters.key` into the session map; `BaseAction.hasSpecificities(key)` just reads that map.

## `ai_report_configuration` — What Each Column Means

One row = one card. Seeded by `V2_6_0_20251107_1400__CreateReportsTableConfiguration.sql` (DDL) and
`V2_6_0_20251113_1400__UpdateAITable.sql` (the three AICCRA tools).

| Column | Entity property | Mapped in HBM | What it does |
|---|---|---|---|
| `id` | `id` | yes | PK. |
| `report_title` | `reportTitle` | yes | Card heading, printed verbatim (leading emoji is part of the stored value). Rendered only when non-empty. |
| `report_description` | `reportDescription` | yes | Card body text, printed verbatim. Rendered only when non-empty. |
| `button_label` | `buttonLabel` | yes | Button caption. Falls back to `reportTitle` when empty. |
| `button_link` | `buttonLink` | yes | Target URL. **No button is rendered at all when this is empty.** See link handling below. |
| `is_active` | `active` (inherited) | **no** | Filters the query (`where is_active=1`), but is never loaded into the entity. |
| `active_since` | `activeSince` (inherited) | **no** | DB default only. |
| `created_by` | `createdBy` (inherited) | **no** | `NOT NULL` + FK to `users`. Not written by the entity. |
| `modified_by`, `modification_justification` | inherited | **no** | Not written by the entity. |

**The tenant caveat:** the table has **no `global_unit_id`**. Every global unit with the flag on sees the *same* cards.
The current rows are AICCRA-specific. Making the section multi-tenant requires a schema change, not a config change.

### Why the `is_active` filter works even though it is unmapped

`AiReportConfigurationMySQLDAO.findAll()` runs the HQL `from AiReportConfiguration where is_active=1`. `is_active` is
not a mapped property, but Hibernate 5.6 passes an unresolved identifier in the `WHERE` clause through to SQL verbatim,
so the emitted statement really is `... from ai_report_configuration aireportco0_ where is_active=1`. The filter is
correct. (Verified by compiling the query plan against this checkout's mappings.) The same idiom appears in ~250 MARLO
DAOs, so do not "fix" it in isolation.

What does **not** work as a consequence of the unmapped columns:

- `AiReportConfigurationMySQLDAO.deleteAiReportConfiguration()` calls `setActive(false)` and updates. Because `active`
  is unmapped, the generated `UPDATE` touches only the four text columns — **the soft delete is a no-op.** Deactivate a
  card with SQL (`UPDATE ai_report_configuration SET is_active = 0 WHERE id = ?`) in a migration.
- `saveAiReportConfiguration()` on a new row would emit an `INSERT` without `created_by`, which is `NOT NULL` with an FK
  to `users` and no default — the insert fails. Combined with the point below, creating rows through the manager is not
  a viable path today.
- `AiReportConfigurationManagerImpl.saveAiReportConfiguration()` carries **no `@Transactional`** (only
  `deleteAiReportConfiguration` does), so any write through it would be rolled back by the pool anyway.
- Entities loaded from this table always report `isActive() == true` in memory, whatever the DB says.

**Net effect: rows are managed by migrations / DBA action, not from the application.** There is no admin screen for
this table, and `AiReportConfigurationManager` has exactly one caller — `AiAction`.

### `findAll()` returns `null`, not an empty list

`AiAction.prepare()` assigns the result straight to `reportConfigurations`, so the field is `null` when no active row
exists. The view guards with `[#if reportConfigurations?? && reportConfigurations?has_content]`. Keep that guard if you
touch the template. `prepare()` also wraps the call in `try/catch` and logs `"Error loading AI report configurations"`
— if the cards vanish in an environment, grep the logs for that line before suspecting the flag.

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
| `userIdea.noReportsConfigured` / `.noReportsConfiguredDescription` | yes (empty-state) | **Missing from `global.properties`** — the template supplies an English `default=` literal. Add the keys if the empty state must be translatable. |
| `breadCrumb.menu.userIdea` | yes (breadcrumb) | Reads `AICHAT BOT`. |
| `userIdea.reportGeneratorNarrative`, `userIdea.ChatbotNarrative`, `userIdea.innovationGenerator` (+ `.readText`) | **no** | Stale duplicates of the DB card text. Dead. |
| `userIdea.title`, `userIdea.Description`, `userIdea.question*` | **no** | Dead. |

Note the whole key family is still prefixed `userIdea.` even though the section is now the AI dashboard. Renaming the
keys is a separate, mechanical cleanup (touch `global.properties`, every `custom/*.properties`, and the FTL together).

## Known Gaps And Traps

1. **`struts-ai.xml` is dead.** It declares package `ai` on namespace `/ai2` and is **not** included from `struts.xml`.
   The live mapping is in `struts-projects.xml`. Edit the wrong file and nothing happens.
   (`docs/detailed-design/detailed-design.md` §Routing still lists `struts-ai.xml` among the active mapping files.)
2. The view is loaded through the `input` result, and `success` redirects back to `{crp}/ai` with `edit=true`.
3. `aiDashboard.ftl` pulls two **CDN stylesheets** (select2) directly. That conflicts with the offline/self-contained
   posture of the rest of the app; the page also declares `pageLibs = ["select2","flag-icon-css"]` without using them.
4. The template carries a lot of inline `style="…"` and an inline `<script>` block. If you restyle it, prefer
   `crp/css/ai/aiDashboard.css` and remember the **cache-busting `?YYYYMMDD` bump** on the asset reference.
5. `currentSectionString` in the template is built as `project-…-phase-…` — this page is not a project section; the
   value exists only to satisfy shared includes.
6. Emoji live inside the DB values (`🧾`, `💬`, `🧠`); the table is `utf8mb4`. Keep any new migration `utf8mb4`-safe.

## Change Recipes

**Add / change / retire an AI tool** → Flyway migration only:

```sql
-- add
INSERT INTO ai_report_configuration (report_title, report_description, button_label, button_link, created_by)
VALUES ('🧠 New Tool', 'What it does…', 'Go to New Tool', 'https://…', 1);

-- change copy or destination
UPDATE ai_report_configuration SET report_description = '…', button_link = '…' WHERE id = ?;

-- retire (the DAO soft delete does not work — see above)
UPDATE ai_report_configuration SET is_active = 0 WHERE id = ?;
```

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
