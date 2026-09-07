# BI (Power BI Embedded) - Agent Context

Read this before editing the BI module. This is a compact context file for low-token sessions.
Use it to orient quickly, then inspect only the target files you will change.

## Scope

This context covers the BI module: the embedded Power BI dashboard, the BI widget bootstrap, and the two
configuration tables behind them (`bi_reports`, `bi_parameters`). It also covers the project feedback status
screen, which lives in the Projects module but embeds a BI report and reads the same two tables.

It does not cover the Power BI reports themselves, the external widget served from `bi.prms.cgiar.org`, the
feedback QA module that surrounds the feedback screen, or TIP (Deliverable Dissemination), which is a separate
module - see the note under High-Risk Areas about its leftover dependency on the BI managers.

## Primary Files

- Routes: marlo-web/src/main/resources/struts-bi.xml
- Feedback route: marlo-web/src/main/resources/struts-projects.xml (`{crp}/feedback`)
- Dashboard action: marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/bi/BiReportsAction.java
- Feedback action: marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/FeedbackStatusAction.java
- Dashboard view: marlo-web/src/main/webapp/WEB-INF/crp/views/bi/biDashboard.ftl
- Feedback view: marlo-web/src/main/webapp/WEB-INF/crp/views/projects/feedbackStatus.ftl
- JS: marlo-web/src/main/webapp/crp/js/bi/biDashboard.js
- CSS: marlo-web/src/main/webapp/crp/css/bi/biDashboard.css
- Models: marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/BiReports.java, BiParameters.java
- Mappings: marlo-data/src/main/resources/xmls/BiReports.hbm.xml, BiParameters.hbm.xml
- Managers: marlo-data/.../manager/impl/BiReportsManagerImpl.java, BiParametersManagerImpl.java
- DAOs: marlo-data/.../dao/mysql/BiReportsMySQLDAO.java, BiParametersMySQLDAO.java

## Route Map

- /bi/{crp}/bi -> BiReportsAction -> homeStack -> biDashboard.ftl
- /projects/{crp}/feedback -> FeedbackStatusAction -> editProjectsStack -> feedbackStatus.ftl

Both are Struts `.do` flows with FreeMarker views. There is no Spring MVC `/api/*` surface for BI.

## Data Model

Two tables, both scoped by global unit:

- `bi_reports` - one row per dashboard tab. Columns: `report_name`, `report_title`, `report_description`,
  `embed_report`, `is_active`, `has_filters`, `report_order`, `has_rls_security`, `has_role_authorization`,
  `global_unit_id`. Mapped property for the global unit is **`crp`**.
- `bi_parameters` - key/value configuration. Columns: `parameter_name`, `parameter_value`, `global_unit_id`.
  Mapped property for the global unit is **`globalUnit`**.

The naming asymmetry (`crp` vs `globalUnit`) is deliberate but unfinished: `bi_parameters` was renamed to
`globalUnit`, `bi_reports` was left as `crp`. Both map the same `global_unit_id` column. Do not "fix" one side
without updating its HQL, which references the property name, not the column.

Only one parameter is live today: `bi_widget_url`, the script URL for the embedded BI widget.

## Non-Negotiable Rules

- Every read of `bi_reports` or `bi_parameters` MUST be scoped by global unit. Both managers expose only
  `findAll(long globalUnitId)`; the unscoped `findAll()` was removed on purpose so the leak cannot reappear.
- Get the global unit from the session, never from a request parameter: `this.getCurrentCrp()` in a BaseAction,
  or `(GlobalUnit) this.getSession().get(APConstants.SESSION_CRP)`.
- All schema changes ship as Flyway migrations. Never alter these tables from application code.
- Never commit Power BI credentials. The 2020 migration committed a client secret in plain text; the rows were
  deleted in 2024 but the value is still in git history.
- Keep user-facing strings i18n-keyed.

## Scoping Contract (Current)

`bi_parameters` supports a platform-wide default:

- `BiParametersMySQLDAO.findAll(globalUnitId)` returns rows where `globalUnit.id = :globalUnitId` **or**
  `globalUnit is null`.
- `BiParametersManagerImpl.findAll(globalUnitId)` then collapses that list to one row per `parameter_name`, with
  the global-unit row winning over the `NULL` default.
- Result: at most one `BiParameters` per name, which is what makes the `[0]` index in the FTLs safe.

`bi_reports` has no fallback. `BiReportsMySQLDAO.findAll(globalUnitId)` filters strictly on
`crp.id = :globalUnitId and is_active=1`. A report with `global_unit_id NULL` is invisible to everyone.

## High-Risk Areas

- **The `[0]` index in the FTLs.** Both views do
  `biParameters?filter(param -> param.parameterName = "bi_widget_url")` and then take `[0]`. It is only safe
  because the manager guarantees uniqueness per name. If you bypass the manager, or insert two rows for the same
  global unit and name, the page picks whichever row Hibernate returns first.
- **Global unit 45 (AICCRA) is hardcoded in the backfill migration and is not created by any migration.** The
  highest `global_units` id the migrations seed is 28. Any data migration that writes `global_unit_id = 45` must
  be guarded with `AND EXISTS (SELECT 1 FROM global_units WHERE id = 45)`, otherwise it fails with
  `ERROR 1452` on a database built from scratch and aborts Flyway.
- **On a clean database `bi_widget_url` keeps `global_unit_id NULL`**, so the `NULL` fallback is what makes BI
  work in local dev. Do not remove the fallback without seeding a per-global-unit row.
- **TIP is not part of BI, but `TIPEmbeddedAction` still injects `BiReportsManager` and `BiParametersManager`.**
  It calls both on every request and `tipEmbedded.ftl` uses neither, so the queries feed nothing. This matters in
  one direction only: changing a BI manager signature breaks that file's compilation even though TIP owns no BI
  behaviour. The dependency is vestigial and safe to delete - verified that `tipEmbedded.ftl` is the only TIP view
  and that no FTL or JS outside biDashboard.ftl and feedbackStatus.ftl reads either collection.
- `FeedbackStatusAction` picks its report by name through `BaseAction.getFeedbackBIReportName()`, which reads
  `APConstants.CRP_CLUSTER_BI_FEEDBACK_REPORT_NAME` from the session. If that session key is absent the filter
  matches nothing and the section renders empty.

## Practical Guardrails

- Adding a parameter: insert a row per global unit that needs it, or one row with `global_unit_id NULL` as the
  platform default. Do not duplicate the same value across every global unit.
- Adding a report: `global_unit_id` is mandatory in practice. A `NULL` leaves the report orphaned and hidden.
- Changing the widget URL for one program only: add a row for that `global_unit_id`; it overrides the default
  automatically, no code change.
- HQL in these DAOs references **mapped property names** (`globalUnit.id`, `crp.id`), not column names. Renaming a
  property without updating the HQL compiles fine and fails at runtime.
- After editing biDashboard.js or biDashboard.css, bump the `?YYYYMMDD` cache-busting param in biDashboard.ftl.

## Quick Debug Checklist

- Dashboard empty for a program: check `SELECT id, report_name, is_active, global_unit_id FROM bi_reports` for
  that global unit. A `NULL` global unit or `is_active=0` hides the report.
- Widget never loads: check that a `bi_parameters` row named `bi_widget_url` resolves for the global unit, either
  its own row or the `NULL` default. The FTLs guard the `<script>` tag with `[#if BiAppURL?has_content]`, so a
  missing row renders a blank section instead of an error.
- Reports of another program showing up: something is calling a DAO directly or reintroduced an unscoped query.
- Confirm the session has a global unit; both actions no-op or render empty without it.
- Rebuild with: mvn -pl marlo-data,marlo-web -am -DskipTests compile

## Checkstyle Note

`mvn checkstyle:check` does not run in this checkout: the maven-checkstyle-plugin 2.9.1 pinned in
marlo-parent/pom.xml fails to start under Java 17 with a `PluginContainerException`. To verify these files, run
Checkstyle 8.18 directly against `configuration/marlo-checkstyle.xml`.

## Related Context (Open Only If Needed)

- reports/ai-context/struts-critical-routing-catalog.md
- reports/ai-context/frontend-composition-map.md
- reports/ai-context/interceptor-validator-playbook.md
- AGENTS.md - Database Migrations

## Token-Saving Usage for Claude

Use this sequence:

1. Read this file first.
2. Read only the one action or DAO tied to the exact problem.
3. Read the exact FTL section tied to the failing element.
4. Check the two tables in the database before assuming a code defect; most BI issues are data scoping.
5. Run a targeted compile only for marlo-data and marlo-web.
