# BI Module — Blank Page and JS TypeError When No Dashboards Are Configured — Requirements

**Spec ID:** BUG-BI-EMPTY-001
**Status:** Draft
**Owner:** IBD Team — Kevin Collazos
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-24
**Jira:** A2-2416 (subtask of A2-2407), due 2026-08-28
**Related PRD sections:** docs/prd.md — BI / reporting access
**Related System Design sections:** docs/system-design/design.md — main menu, empty states
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §4 (frontend composition)
**Companion ai-context docs:** none. Verified 2026-08-24 that neither `frontend-composition-map.md` nor
`struts-critical-routing-catalog.md` covers the BI module, so this change alters no documented contract.

---

## 1. Overview

The BI module renders a blank content area and throws an uncaught JavaScript exception on any MARLO instance whose
`bi_reports` table is empty. QA found it in production on AICCRA-FSRP IV (A2-2416). The reported trigger — "no BI
dashboard is configured for FSRP" — is accurate but it is not the defect. The defect is that `biDashboard.js`
assumes at least one report is always rendered, so the absence of data becomes a crash instead of an empty state.
This spec closes the crash and gives the module a deliberate "Coming soon" placeholder.

## 2. Problem Statement

A SuperAdmin on https://aiccrafsrp.marlo.cgiar.org opens the BI menu item and gets a completely blank content area
with no explanation. The console shows a reproducible `TypeError: Cannot read properties of undefined (reading
'replace')` at `biDashboard.js:76`. Reproduced twice independently (manual Chrome session and Playwright with a
fresh context), so it is not a caching or session artifact.

The chain is:

1. `BiReportsAction.prepare()` calls `biReportsManager.findAll()`. On FSRP the table is empty, so `biReports` is empty.
2. `biDashboard.ftl:33` wraps the whole view in `[#if biReports?has_content]` with no `[#else]`, so nothing renders
   and no `.reportSection` element exists.
3. `biDashboard.js:11-14` reads the first `.reportSection` child unconditionally and passes `undefined` into
   `executePetition()`.
4. `biDashboard.js:76` calls `.replace()` on that `undefined` and throws. Execution stops there, so the remaining
   handlers in `addEvents()` (`selectBIReport`, `setFullScreen`, the `message` listener that sizes the iframe) are
   never registered.

Two latent failures sit in the same view and are in scope because they are one edit away:

- `biDashboard.ftl:89-92` indexes `BiAppURL[0]` with no guard. An instance missing the `bi_widget_url` BI parameter
  gets a FreeMarker error (HTTP 500), not a blank page.
- Hiding the menu item does not close the route. `struts-bi.xml` uses `homeStack`
  (`i18nFile`, `validCrp`, `requireUser`, `validSessionCrp`) with no module-permission check, so any logged-in user
  reaching `/bi/{crp}/bi` by direct URL or a saved link still hits the crash even while the module is "disabled".

## 3. In-Scope Requirements

### Functional

- **BUG-BI-EMPTY-F-001** — When `biReports` is empty, the BI page MUST render a "Coming soon" placeholder instead of
  an empty content area.
- **BUG-BI-EMPTY-F-002** — `biDashboard.js` MUST NOT call `executePetition()` when no `.reportSection` element
  exists, and MUST register the remaining handlers regardless.
- **BUG-BI-EMPTY-F-003** — `executePetition()` MUST return early when `urlReport` is empty, because the click
  handler is a second call site.
- **BUG-BI-EMPTY-F-004** — The BI widget `<script>` tag MUST only be emitted when the `bi_widget_url` BI parameter
  resolves, and MUST tolerate a null `biParameters` list.
- **BUG-BI-EMPTY-F-005** — The placeholder MUST show the same message to every user, with no role branching.
- **BUG-BI-EMPTY-F-006** — Reaching `/bi/{crp}/bi` by direct URL while the module is disabled MUST show the
  placeholder, not a blank page.

### Non-Functional

- **BUG-BI-EMPTY-N-001** — Instances with at least one configured report MUST see no behavioural or visual change.
- **BUG-BI-EMPTY-N-002** — The `customJS` cache-busting parameter for `biDashboard.js` MUST be bumped, otherwise
  browsers keep serving the cached broken script and the fix is invisible in production.
- **BUG-BI-EMPTY-N-003** — No new CSS rules. The placeholder MUST reuse the existing `.simpleBox` / `.emptyMessage`
  classes already in `global.css`.

### Data

- **BUG-BI-EMPTY-D-001** — No schema change and no Flyway migration. The placeholder is driven by the absence of
  `bi_reports` rows, not by a new flag.

### UI

- **BUG-BI-EMPTY-U-001** — The placeholder MUST follow the established MARLO empty-state pattern
  (`<div class="simpleBox emptyMessage text-center">`), as used in `projectBudgetByFlagships.ftl:122`.
- **BUG-BI-EMPTY-U-002** — Copy MUST come from new `biDashboard.comingSoon.*` i18n keys, never hardcoded strings.

### Security

Not applicable — no change to permissions, interceptor stacks, or data exposure. The placeholder deliberately
carries no configuration detail, so it is safe for end users.

## 4. Out-of-Scope

- **`BiReportsAction.prepare()` does not filter by Global Unit.** `BiReports` has a `crp` relation
  (`BiReports.java:48`) but the action calls `findAll()`. Harmless while each instance has its own database; it would
  expose another global unit's dashboards on a shared database. Separate ticket.
- **Adding a module-permission check to the `/bi` route.** `homeStack` has no `crp_bi_module_active` gate. The
  placeholder makes the route harmless, so hardening the interceptor stack is a separate, higher-risk change.
- **Configuring actual FSRP dashboards.** Content work owned by Kenji / Laura, not this spec.
- **The commented-out `canLeaveComments` permission check** (`biDashboard.ftl:30`, `biDashboard.js:38-40`), disabled
  in commit `89ecd42d53`. Untouched here.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| End user (AICCRA FSRP) | Stops seeing a blank page; gets an explicit "coming soon" message |
| SuperAdmin / PMU | Same placeholder; can re-enable the BI menu without shipping a broken page |
| QA reviewer | Gains a verifiable observable outcome; console is clean |
| Developer | The BI page stops being a latent crash for every new instance |

## 6. Acceptance Criteria

- **AC-001** (F-001, U-001, U-002) — Given an instance with zero `bi_reports` rows, when a logged-in user opens the
  BI page, then the placeholder renders with the `biDashboard.comingSoon.*` copy and no blank area.
- **AC-002** (F-002, F-003) — Given the same instance, when the page loads, then the browser console shows no
  uncaught exception, and `$._data(document, 'events')` confirms the `addEvents()` handlers were registered.
- **AC-003** (N-001) — Given an instance with two or more configured reports, when a user opens the BI page, then
  tab switching, the report title, full screen, and iframe auto-height behave exactly as before this change.
- **AC-004** (F-004) — Given an instance with no `bi_widget_url` BI parameter, when a user opens the BI page, then
  the page returns HTTP 200 with the placeholder and emits no widget `<script>` tag, instead of a FreeMarker 500.
- **AC-005** (F-006) — Given the BI module disabled via `crp_bi_module_active=false`, when a logged-in user requests
  `/bi/{crp}/bi` directly, then the placeholder renders.
- **AC-006** (N-002) — Given a browser holding `biDashboard.js?20240727` in cache, when the fix is deployed, then
  the page requests the new version string and executes the patched script.
- **AC-007** (F-005) — Given a SuperAdmin and a regular user on the same instance, when both open the BI page, then
  both see identical placeholder copy.

## 7. Constitutional Compliance Checklist

| Rule | Status |
|---|---|
| 1. Phased data forward-only | Not applicable — read-only view, no save |
| 2. Save pipeline pattern | Not applicable — no save path |
| 3. Spring MVC owns `/api/*` | Honored — no new Struts `*.json` path; existing `/bi` mapping untouched |
| 4. Specificities via `parameters` + `custom_parameters` | Not applicable — no new flag; behaviour keys off absent data |
| 5. Flyway migrations for schema changes | Not applicable — no schema change |
| 6. GPL header on new Java files | Not applicable — no new Java file |
| 7. Code style / Checkstyle | Honored — 2-space indent, 120-char limit; no Java touched |
| 8. English only, i18n-keyed user strings | Honored — new `biDashboard.comingSoon.*` keys |
| 9. Branch from staging, never commit to main | Honored — branch `A2-2416-…-AICCRA-FSRP-IV`, verified at `origin/staging` |
| 10. Java 17 run script | Honored — `scripts/run-marlo-java17.sh` for verification |
| 11. Dependency baseline | Not applicable — no dependency change |
| 12. No credential files committed | Honored |

## 8. Open Questions

- **Q-001 — Which custom properties file does AICCRA FSRP use?** Driven by the `crp_custom_file` custom parameter in
  the FSRP database; not readable from the repository. Resolved as non-blocking: `global.properties` is registered as
  a default resource bundle before the instance file (`InternationalitazionFileInterceptor.java:96` then `:117-118`),
  so keys present only in `global.properties` still resolve. The keys are added to the AICCRA-family custom files
  anyway, so the fix does not depend on that ordering.
- **Q-002 — Who re-enables `crp_bi_module_active` on FSRP, and when?** The code fix is invisible on FSRP until the
  flag returns to `true`, because the menu item stays hidden. Needs Kenji / Laura to action it after deployment.
  Tracked as an operational step in `task.md`, not a code change.

## 9. Decision Log

- 2026-08-24 — Treat this as a code defect, not a configuration gap — the reported cause ("no dashboard configured")
  is the trigger; the defect is an unguarded assumption in `biDashboard.js` that affects every instance.
- 2026-08-24 — Single "Coming soon" message for all roles, no role branching — matches the request in A2-2416 and
  keeps configuration detail out of the end-user view.
- 2026-08-24 — New `biDashboard.comingSoon.*` keys instead of reusing the orphaned `global.comingSoon` — a
  full-page state needs a title plus a line of context, and instance-level copy override stays possible.
- 2026-08-24 — Reuse `.simpleBox` / `.emptyMessage` instead of a new card style — consistency with existing MARLO
  empty states and zero new CSS.
- 2026-08-24 — Include the `BiAppURL[0]` guard in the same PR — the 500 is a worse failure than the blank page and
  the fix is two lines in the file already being edited.
- 2026-08-24 — Do not add a `crp_bi_module_active` check to the interceptor stack — the placeholder makes the route
  harmless; changing `homeStack` is disproportionate risk for this ticket.
- 2026-08-24 — Include re-enabling the FSRP flag as an operational step — without it the ticket ships a placeholder
  nobody can reach through the menu, and QA has nothing to verify.
