# BI Module — Blank Page and JS TypeError When No Dashboards Are Configured — Design

**Spec ID:** BUG-BI-EMPTY-001
**Status:** Draft
**Owner:** IBD Team — Kevin Collazos
**Last Updated:** 2026-08-24
**Implements requirements:** BUG-BI-EMPTY-F-001 … F-006, N-001 … N-003, D-001, U-001, U-002
**Touches modules:** marlo-web

---

## 1. Architecture Summary

No new component and no new flag. The view already branches on `biReports?has_content`; the fix adds the missing
`[#else]` arm and makes the two unguarded consumers of that data tolerate absence. The behaviour is keyed off absent
data, so it activates on FSRP without any instance-specific code.

```
GET /bi/{crp}/bi
  homeStack (i18nFile -> validCrp -> requireUser -> validSessionCrp -> defaultStack)
  BiReportsAction.prepare()
    biReports    = biReportsManager.findAll()        [empty on FSRP]
    biParameters = biParametersManager.findAll()
  -> biDashboard.ftl
       [#if biReports?has_content]  tabs + report containers   <- unchanged
       [#else]                      simpleBox emptyMessage     <- NEW
       [#if BiAppURL?has_content]   widget <script>            <- NEW guard
  -> biDashboard.js  (customJS, version bumped)
       addEvents()
         first .reportSection child resolved once
         executePetition() only when class AND id are present  <- NEW guard
         remaining handlers registered unconditionally         <- restored by the guard
```

The crash today happens between the last two lines: the throw at `biDashboard.js:76` aborts `addEvents()`, so every
handler after the `executePetition()` call is silently lost. Guarding the call is what restores them — the
placeholder alone would not.

## 2. Module Footprint

### marlo-web

- Modified: `src/main/webapp/WEB-INF/crp/views/bi/biDashboard.ftl`
  - line 4 — bump the `biDashboard.js` cache-busting parameter to `?20260824`
  - lines 33-86 — add the `[#else]` placeholder arm
  - lines 89-92 — guard `BiAppURL` and null-tolerate `biParameters`
- Modified: `src/main/webapp/crp/js/bi/biDashboard.js`
  - `addEvents()` — resolve the first report once, call `executePetition()` only when both attributes are present
  - `executePetition()` — early return on empty `urlReport`
- Modified: `src/main/resources/global.properties` — two new keys
- Modified: `src/main/resources/custom/aiccra3.properties`, `custom/aicrra.properties` — same two keys, so AICCRA
  instances can override the copy and the file stays a complete mirror per existing convention

### marlo-data

Not applicable — no manager, DAO, or model change.

## 3. Data Model Changes

Not applicable. No new entity, column, index, or enum, and no Flyway migration. `bi_reports` and `bi_parameters` are
read as they are.

## 4. API / Action Surface

Not applicable. `BiReportsAction` and the `{crp}/bi` mapping in `struts-bi.xml` are untouched. No new REST or JSON
endpoint.

## 5. Frontend Composition

**Placeholder** — inside the existing `<section class="container containerBI">`, as the `[#else]` of the
`biReports?has_content` test:

```
[#else]
  <div class="simpleBox emptyMessage text-center">
    <h4>[@s.text name="biDashboard.comingSoon.title" /]</h4>
    <p>[@s.text name="biDashboard.comingSoon.description" /]</p>
  </div>
[/#if]
```

`.simpleBox` and `.emptyMessage` (`global.css:3513`) already exist, so `biDashboard.css` is not touched and its
version string stays as it is. No `?html` on any interpolation — FTL auto-escaping is on under Struts 6.8 and `?html`
is a parse error in this codebase.

The `underConstruction` macro (`utils.ftl:85`) was considered and rejected: it renders a 10-20px inline icon badge for
a menu label, not a page-level state.

**Widget script guard** — replacing lines 89-92:

```
[#assign BiAppURL = (biParameters)![]?filter(param -> param.parameterName = "bi_widget_url")]
[#if BiAppURL?has_content]
  <script src="${BiAppURL[0].parameterValue}" charset="utf-8"></script>
[/#if]
```

`(biParameters)![]` covers a null list as well as a missing parameter.

**JS guards** — `addEvents()` resolves `$('.reportSection').children().first()` once and calls `executePetition()`
only when both `class` and `id` are present; `executePetition()` returns early on an empty `urlReport` because the
click handler is a second call site. The existing `?index == 0` / `current` class logic is unchanged.

Expandable-block pattern: not applicable, this view uses tab sections, not accordions.

## 6. Persistence & Phase Replication Plan

Not applicable — read-only view, no save and no delete path, so nothing replicates across phases.

## 7. Validation & Save Pipeline

Not applicable — `BiReportsAction` has no `validate()`, no Validator, and no save chain. The interceptor stack
(`homeStack`) is unchanged.

## 8. Permissions & Edit Gates

Unchanged. `homeStack` supplies `requireUser` plus CRP session validation; there is no `canEdit*` gate on this view.
The module flag `crp_bi_module_active` is evaluated only in `main-menu.ftl:28`, and this design deliberately leaves
that as the only gate — see §15 DR-004.

## 9. Specificity / Feature-Flag Strategy

No new specificity, and no entry in either `APConstants.java`. The placeholder is conditioned on absent `bi_reports`
rows, which is already instance-scoped because each instance has its own database. This is why the fix needs no
FSRP-specific branch in the code.

The pre-existing `crp_bi_module_active` flag (`APConstants.java:161`, seeded by
`V2_6_0_20200713_1626__InsertBIModulePermissions.sql`) is currently `false` on FSRP. It is an operational input to
this spec, not something the spec changes.

## 10. Integration Points

The PowerBI embed widget, loaded from the `bi_widget_url` BI parameter and driven by `pbiwidget.init()`. This design
reduces contact with it: when no reports exist the script is no longer requested and `pbiwidget.init()` is never
called. No CLARISA, CGSpace, AI services, S3, or Pusher involvement.

## 11. Observability

No new logging. The observable signal is negative and that is the point: the browser console goes from a reproducible
uncaught `TypeError` to clean. `BiReportsAction` keeps its existing `LOG` field, unused. No server-side log is emitted
for the empty state, since an unconfigured instance is a normal condition, not an error.

## 12. Performance & Scalability

Marginally better on unconfigured instances: one fewer external script request (the PowerBI widget) and no
`pbiwidget.init()` call. Configured instances are unaffected — the guards add two attribute checks per page load.
`findAll()` query behaviour is untouched.

## 13. Security Considerations

No change to authentication or authorization. The placeholder copy is deliberately free of configuration detail, so
exposing it to end users leaks nothing about the instance setup. Guarding `BiAppURL` also removes a FreeMarker stack
trace as a possible error surface on misconfigured instances.

## 14. Backwards Compatibility & Rollout

- No migration, so no schema rollback concern.
- Fully backwards compatible: every guard is a no-op when reports exist (AC-003).
- **Cache busting is the rollout risk.** Production currently serves `biDashboard.js?20240727`. Shipping the JS change
  without bumping the query parameter in `biDashboard.ftl:4` leaves browsers on the cached broken script and the fix
  looks like it failed. The bump to `?20260824` is part of the same edit.
- Branch verified: `origin/aiccra-fsrp-improvements` is fully contained in `origin/staging` (0 commits ahead) and the
  working branch sits exactly at `origin/staging`. There is no divergent FSRP deployment line, so branch → staging
  reaches FSRP through the normal release path.
- Rollback is a revert of a single commit touching five files; no data or schema state to unwind.
- **Post-deployment operational step:** set `crp_bi_module_active=true` for the FSRP global unit, otherwise the menu
  item stays hidden and the placeholder is reachable only by direct URL. Because `hasSpecificities()` reads from the
  session (`BaseAction.java:6581`), users with an open session must log out and back in — the same caveat Kenji
  documented when disabling the module.

## 15. Decision Records

- **DR-001 — Guard the JS rather than skip loading it.** Conditionally omitting `biDashboard.js` when `biReports` is
  empty would hide today's symptom while leaving the defect for any instance with partial data. Commit `9aefbf7962`
  already had to fix stray `-test` suffixes on these exact `id` attributes, which is the same failure mode with
  reports present. Guarding the call site fixes both.
- **DR-002 — Two guards, not one.** The `addEvents()` guard covers page load; the `executePetition()` early return
  covers the click handler as an independent call site. Either alone leaves a reachable path.
- **DR-003 — New i18n keys over the orphaned `global.comingSoon`.** `global.comingSoon` ("Coming soon...") exists at
  `global.properties:6` and is used nowhere. A full-page state needs a title plus one line of context, and dedicated
  keys keep per-instance override available. `global.comingSoon` is left untouched.
- **DR-004 — No interceptor change.** Adding a `crp_bi_module_active` gate to the `/bi` route would fix direct-URL
  access more strictly, but `homeStack` is shared by many packages and the placeholder already makes the route
  harmless. Out of proportion for this ticket.
- **DR-005 — Add the keys to the AICCRA custom files too.** `global.properties` is registered as a default resource
  bundle before the instance file, so global-only keys do resolve. Mirroring them into the AICCRA-family custom files
  removes the dependency on that bundle ordering and matches the existing convention that those files are complete
  mirrors.

## 16. Open Risks

- **R-001 — FSRP's custom properties file is unverified.** Which file `crp_custom_file` points to on FSRP is a
  database value. Mitigated by DR-005 plus the `global.properties` fallback; residual risk is that FSRP points at a
  file outside the AICCRA family, in which case the fallback carries it.
- **R-002 — The flag re-enable is outside the PR.** If nobody sets `crp_bi_module_active=true` on FSRP, the ticket
  closes with no user-visible change on the instance it was raised against.
- **R-003 — No automated test coverage.** MARLO has no FTL or JS test infrastructure, and the BI module has no tests.
  Verification is manual only (§ Testing Plan in `task.md`). A regression here would not be caught by CI.
- **R-004 — Unfiltered `findAll()` remains.** Deferred to a separate ticket; it becomes a real exposure only if a
  future deployment shares one database across global units.
