# BI Module — Blank Page and JS TypeError When No Dashboards Are Configured — Tasks

**Spec ID:** BUG-BI-EMPTY-001
**Status:** In Progress
**Owner:** IBD Team — Kevin Collazos
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-24
**Implements design:** docs/specs/bugfix/a2-2416-bi-empty-state/design.md
**Branching:** `A2-2416-BI-module-fails-to-render-content-JS-TypeError-in-biDashboard.js-production-AICCRA-FSRP-IV`, from `staging`
**Target merge:** staging (then promoted to main per release process)
**Jira:** A2-2416, due 2026-08-28

---

## 1. Execution Context

- Java 17, run script `scripts/run-marlo-java17.sh`.
- Java level verified in `marlo-parent/pom.xml`.
- Spring profile: local `marlo-<profile>.properties` bootstrapped from `marlo-test.properties`.
- Build for verification: `mvn -o -pl marlo-web -am -DskipTests compile`.
- `mvn checkstyle:check` is known-broken in this checkout (`maven-checkstyle-plugin:2.9.1` →
  `NoSuchMethodError: Checker.setClassloader` under Java 17, on untouched modules too). Not a blocker here: this spec
  touches no Java. Style verified manually.
- Local reproduction requires an empty `bi_reports` table. The local run connects to a **private local MySQL**
  (`localhost:3306/aiccradb1`, per `marlo-web/src/main/resources/config/marlo-dev.properties`; the RDS entries in
  that file are commented out). `marlo-web/tomcat/context.xml` is *not* the datasource for this run — that file
  belongs to the tomcat7 plugin used by `run-marlo-java8.sh`, while `run-marlo-java17.sh` runs cargo/tomcat9x.
  So `DELETE FROM bi_reports` is a local, private, reversible operation. Back it up first anyway:
  no table references `bi_reports` by foreign key, so the delete neither cascades nor blocks.

## 2. Pre-flight Checklist

- [ ] `requirements.md` reviewed and approved.
- [ ] `design.md` reviewed and approved.
- [ ] Working branch is at `origin/staging` (verified 2026-08-24: `0 0` divergence).
- [ ] Local instance runs and the BI menu item is reachable before any edit.
- [ ] Baseline captured: reproduce the `TypeError` locally with `bi_reports` empty, so the fix is provably the cause
      of the change and not an environment difference.

## 3. Task List

### BUG-BI-EMPTY-001-T01 — Reproduce the failure locally

- **Depends on:** —
- **Change:** none; baseline only.
- **Acceptance:** with `bi_reports` empty, the BI page shows a blank content area and the console shows
  `TypeError: Cannot read properties of undefined (reading 'replace')` at `biDashboard.js:76`.
- **Verification:** browser devtools console; confirm the stack points at `executePetition`.

### BUG-BI-EMPTY-001-T02 — Add the i18n keys

- **Depends on:** —
- **Change:** add `biDashboard.comingSoon.title` and `biDashboard.comingSoon.description` to
  `global.properties` next to the existing `biDashboard.menu.title` (line ~6203), and mirror both into
  `custom/aiccra3.properties` and `custom/aicrra.properties`.
- **Acceptance:** both keys resolve; no duplicate key added; `global.comingSoon` left untouched.
- **Verification:** `grep -n "biDashboard.comingSoon" ` across the three files returns exactly two hits each.

### BUG-BI-EMPTY-001-T03 — Add the placeholder branch to biDashboard.ftl

- **Depends on:** T02
- **Change:** add the `[#else]` arm to the `[#if biReports?has_content]` at `biDashboard.ftl:33`, rendering
  `<div class="simpleBox emptyMessage text-center">` with the two keys from T02.
- **Acceptance:** AC-001, AC-007. Placeholder renders with `bi_reports` empty; no `?html` anywhere in the added
  markup; `biDashboard.css` untouched.
- **Verification:** reload the BI page; the placeholder is visible and centred.

### BUG-BI-EMPTY-001-T04 — Guard the widget script tag

- **Depends on:** —
- **Change:** at `biDashboard.ftl:89-92`, wrap the `<script>` in `[#if BiAppURL?has_content]` and change the assign to
  `(biParameters)![]?filter(...)`.
- **Acceptance:** AC-004. With the `bi_widget_url` row removed the page returns 200 with the placeholder and no
  widget `<script>`; previously a FreeMarker 500.
- **Verification:** temporarily delete the `bi_widget_url` row locally, reload, confirm 200 and view-source shows no
  widget script; restore the row.

### BUG-BI-EMPTY-001-T05 — Guard biDashboard.js

- **Depends on:** T01
- **Change:** in `addEvents()` resolve `$('.reportSection').children().first()` once and call `executePetition()` only
  when both `class` and `id` are present; add an early return on empty `urlReport` at the top of `executePetition()`.
- **Acceptance:** AC-002. No uncaught exception with `bi_reports` empty, and the handlers after the
  `executePetition()` call are registered.
- **Verification:** console clean; confirm handler registration rather than assuming it — the whole point of the guard
  is that the throw used to abort the rest of `addEvents()`.

### BUG-BI-EMPTY-001-T06 — Bump the JS cache-busting parameter

- **Depends on:** T05
- **Change:** `biDashboard.ftl:4`, `biDashboard.js?20240727` → `?20260824`. CSS version unchanged (no CSS edit).
- **Acceptance:** AC-006.
- **Verification:** devtools Network tab shows the request for the new version string.

### BUG-BI-EMPTY-001-T07 — Regression pass with reports configured

- **Depends on:** T03, T04, T05, T06
- **Change:** none; verification only.
- **Acceptance:** AC-003. With two or more reports configured, tab switching, report title, full screen, and iframe
  auto-height all behave as before.
- **Verification:** manual walkthrough of each of the four behaviours, plus a clean console. This is the task that
  protects AICCRA and CCAFS from the change; do not skip it because the guards "look" inert.

### BUG-BI-EMPTY-001-T08 — Direct-URL check with the module disabled

- **Depends on:** T03
- **Change:** none; verification only.
- **Acceptance:** AC-005. With `crp_bi_module_active=false`, requesting `/bi/{crp}/bi` renders the placeholder.
- **Verification:** set the flag false locally, log out and back in (session-cached — `BaseAction.java:6581`), confirm
  the menu item is gone, then hit the URL directly.

### BUG-BI-EMPTY-001-T09 — Confirm no ai-context doc needs updating

- **Depends on:** T07
- **Change:** none expected. Verified 2026-08-24 that neither `reports/ai-context/frontend-composition-map.md` nor
  `reports/ai-context/struts-critical-routing-catalog.md` mentions the BI module, `biDashboard`, or the `/bi`
  namespace, so this change alters no documented contract and CLAUDE.md step 10 is satisfied without an edit.
- **Acceptance:** the two greps below still return nothing; if either doc has gained a BI section by implementation
  time, add the empty-state branch and the widget-script guard to it.
- **Verification:** `grep -rin "biDashboard\|/bi\b" reports/ai-context/frontend-composition-map.md
  reports/ai-context/struts-critical-routing-catalog.md`
- **Note:** documenting the BI view's composition in `frontend-composition-map.md` is a worthwhile follow-up, but it
  is new documentation rather than a change this fix forces. Out of scope here.

### BUG-BI-EMPTY-001-T10 — Open the PR and hand off to QA

- **Depends on:** T07, T08, T09
- **Change:** PR from the working branch into `staging`, linked to A2-2416.
- **Acceptance:** PR describes the root cause, the five changed files, and the operational steps of §6.
- **Verification:** PR opened; A2-2416 updated with the verification notes.

## 4. Dependency Graph

```
T01 ──> T05 ──> T06 ──┐
T02 ──> T03 ──┬───────┼──> T07 ──┐
T04 ──────────┘       │          ├──> T09 ──> T10
                      └> T08 ────┘
```

## 5. Testing Plan

- **Unit:** none. No Java touched, and MARLO has no FTL or JS unit-test infrastructure. Stated as a gap, not a pass.
- **Integration:** none available for this layer.
- **Manual — empty instance:** T03, T04, T05, T08.
- **Manual — configured instance:** T07, the regression gate.
- **Regression risk concentrated in T07.** The guards are inert when reports exist, but that claim is exactly what
  T07 exists to prove.
- **QA hand-off:** re-run A2-2407-TC07 on FSRP after deployment *and* after the flag flip of §6, otherwise the test
  case still sees a hidden menu item and cannot verify AC-001.

## 6. Operational Steps

1. Deploy the branch through the normal staging → production path. No migration to run.
2. Set `crp_bi_module_active = true` for the AICCRA FSRP global unit (`custom_parameters`). **Owner: Kenji / Laura** —
   re-enabling a module in production is their call, not the PR's.
3. Tell FSRP users with an open session to log out and log back in. `hasSpecificities()` reads the flag from the
   session, so the change is invisible until re-authentication — the same caveat Kenji documented when disabling it.
4. Confirm with QA that the browser cache picked up `biDashboard.js?20260824`.

## 7. Rollback Plan

- Revert the single commit (five files: `biDashboard.ftl`, `biDashboard.js`, `global.properties`,
  `custom/aiccra3.properties`, `custom/aicrra.properties`). No schema or data state to unwind.
- If only the flag flip proves premature, set `crp_bi_module_active=false` again; the code fix is harmless on its own
  and can stay deployed.
- No coordination with the BI or AI services needed — this change only reduces contact with the PowerBI widget.

## 8. Definition of Done

- [ ] AC-001 … AC-007 verified, each with the evidence named in its task.
- [ ] Console clean on an instance with no reports; no behavioural change on an instance with reports.
- [ ] PR merged into `staging` and linked to A2-2416.
- [ ] Operational steps of §6 communicated on the ticket, with the flag owner named.
- [ ] QA re-ran A2-2407-TC07 on FSRP after the flag flip and passed it.
- [ ] Out-of-scope items of `requirements.md` §4 filed as their own tickets — in particular the unfiltered
      `findAll()` in `BiReportsAction`.

---

## 9. Verification Log

### 2026-08-24 — offline verification (no browser session required)

Harnesses written for this pass live in the session scratchpad (`FtlParseCheck.java`, `GuardEval.java`,
`OldVsNew.java`, `guardTest.js`). Each carries a negative control, because a check that cannot fail proves nothing.

| # | Check | Result |
|---|---|---|
| 1 | `biDashboard.ftl` parses under FreeMarker 2.3.32 (the version `marlo-parent/pom.xml` declares) | PASS — and a deliberately broken copy *does* fail, so the check is real |
| 2 | `((biParameters)![])?filter(...)` across 5 states: absent, null, empty, no match, match | PASS in all 5 — emits the script only on a match |
| 3 | Old vs new expression with `bi_widget_url` absent | Old raises `InvalidReferenceException` (= HTTP 500); new renders empty. **The latent 500 was real.** |
| 4 | `node --check biDashboard.js` | PASS |
| 5 | JS control flow, no reports configured | Before: **0** handlers + `TypeError: Cannot read properties of undefined (reading 'replace')` — the exact error A2-2416 reports. After: **4** handlers, no error, `pbiwidget.init` not called |
| 6 | JS control flow, one report configured | Before and after identical: 4 handlers, `pbiwidget.init` called once |
| 7 | i18n keys reach the build output | 2 matches each in `target/classes/global.properties`, `custom/aiccra3.properties`, `custom/aicrra.properties` |
| 8 | Deployed FTL matches source | Identical in `target/marlo-web` and the cargo webapp |
| 9 | Patched JS served over HTTP at the new version string | `GET /crp/js/bi/biDashboard.js?20260824` → 200, contains both guards |

Check 5 supersedes the inference recorded in `design.md` §1 with measurement: the throw did abort handler
registration, 0 versus 4.

**Status against the acceptance criteria**

| AC | State |
|---|---|
| AC-002 (no uncaught exception, handlers registered) | **Verified** — checks 5, 6 |
| AC-004 (missing `bi_widget_url` → no 500) | **Verified** — checks 2, 3 |
| AC-003 (no regression with reports) | **Partly** — JS control flow verified (check 6); tab switching, full screen and iframe auto-height still need a browser |
| AC-006 (cache-busted script served) | **Partly** — the asset is served at `?20260824` (check 9); that the page requests it needs a rendered page |
| AC-001 (placeholder renders with the right copy) | **Not verified** |
| AC-005 (direct URL with the module disabled) | **Not verified** |
| AC-007 (same copy for every role) | **Not verified** — true by construction, no role branching exists |

**Why the rest is still open.** MARLO runs locally (Tomcat 9.0.80, `http://localhost:8080/marlo-web/`, login page
200). The BI route sits behind `requireUser`, so rendering it needs an authenticated session, and the agent doing
this work does not enter credentials. The empty-state path additionally needs `bi_reports` empty — see §1 for the
datasource correction: the run uses a private local MySQL, so that is a safe local operation, but the agent's
sandbox blocks destructive database writes, so the delete/restore has to be run by a developer.

**Residual risk after this pass:** low for the FTL and JS logic, which now have direct evidence. Unmeasured: the
placeholder's visual rendering and the browser-level regression of the four tab behaviours.

### 2026-08-24 — in-browser verification (authenticated session, local instance)

Local instance: Tomcat 9.0.80 embedded, `http://localhost:8080/marlo-web/`, against the **local** database
`localhost:3306/aiccradb1`. Global unit AICCRA, phase AR-2026 (`AICCRA-bi-phase-431`). Login performed by the
developer; the agent does not enter credentials.

> Correction (2026-08-24): an earlier revision of this log named the shared `aiccradb_icipe_test` RDS as the
> datasource, read from `marlo-web/tomcat/context.xml`. That was the wrong file — it configures the tomcat7 plugin,
> not the cargo/tomcat9x run. The live JVM holds its connections to `127.0.0.1:3306`. Nothing here was ever run
> against a shared environment.

**This database has four BI reports configured, so this session exercised the regression scenario (AC-003), not the
empty state.**

| Observation | Value |
|---|---|
| `/bi/AICCRA/bi.do` response | Renders, title `MARLO BI` — **no FreeMarker 500** |
| `biDashboard.js` requested | `?20260824` → 200 (not the cached `?20240727`) |
| `biDashboard.css` requested | `?20251112` → 200, unchanged as designed |
| Report tabs rendered | 4 |
| Widget script emitted by the guard | `https://bi.prms.cgiar.org/widget/main.js`, `pbiwidget` defined |
| Initial report load | `BIreport-7-contentOptions` gained `loaded`; `pbiwidget.init` requested `…/bi/aiccra-bi-module?reportName=aiccra-bi-module` — the `replace("BIreport-", "")` path end to end |
| `setReportTitle()` on load | "Business Intelligence module" |
| Tab switch to "QA process for PMC" | `current` moved to `BIreport-9`; second container lazily loaded; only it visible; second report requested (`aiccra_feedback_consolidation`); title updated |
| Handlers bound | `.reportSection` click:1, `window` message:1 |
| Unresolved i18n keys in the DOM | 0 |
| Console errors | One 404 from a third-party asset (tawk.to / clarity / recaptcha / unpkg group). Count did not increase after the tab switch, and no BI request failed. Not attributable to this change. |

**AC-003 verified in the browser.** The guard is transparent when reports exist: initial load, lazy tab loading,
title updates, show/hide, and the widget script all behave as before.

**AC-006 verified in the browser.** The page requests the bumped version string.

**Still not verified:** AC-005, AC-007. AC-001 needs `bi_reports` empty and AC-005 needs
`crp_bi_module_active=false`. Both are safe locally (see the correction above), but the agent's sandbox blocks
destructive database writes, so a developer has to run the delete/restore and the flag flip. Note also that
`BiReportsAction.prepare()` calls `findAll()` without a Global Unit filter (`requirements.md` §4), so *every*
global unit in this database sees the same four reports — there is no already-empty global unit to test against.
AC-007 needs no database change: no role branching exists in the view, so it holds by construction.

**Pre-existing defect noticed, not in scope:** `$('.setFullScreen')` in `addEvents()` matches no element on the
rendered page, so that binding is dead. The visible "Full Screen" button is wired elsewhere. The selector is
untouched by this change; worth its own ticket.

### 2026-08-24 — AC-001 verified in the browser (empty `bi_reports`)

A developer emptied `bi_reports` on the local `aiccradb1` and the page was reloaded.

| Observation | Value |
|---|---|
| Placeholder markup | `<div class="simpleBox emptyMessage text-center"><h4>BI dashboards coming soon</h4><p>Business Intelligence dashboards are not available for this instance yet. They will appear here once they are published.</p></div>` |
| i18n keys resolved | Yes — no `biDashboard.comingSoon` literal anywhere in the rendered text |
| `.reportSection` elements | 0 |
| Uncaught exceptions | None |
| `window` `message` handlers | 1 — **registered even with no reports**, which is exactly what the `TypeError` prevented before |
| Widget `<script>` | Still emitted, correctly: the guard keys off the missing `bi_widget_url` parameter, not off empty reports |

**AC-001 verified.** With AC-002, AC-003, AC-004 and AC-006, the only criteria left are AC-005 (needs the module
flag flipped plus a logout/login cycle) and AC-007 (holds by construction — no role branching exists).

**Incident during this step — data loss and full recovery.** The backup table was never created; only the
`DELETE` ran, so four rows were deleted unprotected. Root cause in process, not in code: the instructions put
`CREATE TABLE ... AS SELECT` and `DELETE` in one block with no confirmation step between them. Recovery was
complete because the server runs `binlog_format=ROW` with `binlog_row_image=FULL`, so the `Delete_rows` event
(`binlog.000016`, end_log_pos 61825) held the full before-image of every row. All four were restored exactly and
the page was re-verified: four tabs in the original order (ids 7, 9, 8, 10 by `report_order` 5, 7, 10, 15), titles
and flags matching the binlog, initial report loaded.

A partial reconstruction assembled from prior queries and the DOM had been prepared as a fallback. Comparing it to
the binlog afterwards, it would have been **wrong** in `has_filters` (0 vs the real 1), `report_order` (invented
1/2/3/4 vs the real 5/7/10/15), `has_role_authorization` on id 9 (0 vs the real 1), and it lacked `embed_report`
for id 10 entirely. Recorded here as a caution: reconstructed-from-observation data should never be treated as a
restore.

**Process fix for any future destructive step in this repo:** never issue a backup and a delete as one block.
Require the backup row count to be checked against the source count before the delete runs.

### 2026-08-26 — verification after merging aiccra-fsrp-improvements (A2-1664 global-unit scoping)

Merging Kenji's branch changed how BI data is loaded: `findAll()` became `findAll(globalUnitId)`, and a new
migration moved `bi_reports` and `bi_parameters` to global unit 45 (AICCRA). Everything below was re-verified on a
local instance after the merge, because the earlier browser pass ran against the unscoped `findAll()`.

| Check | Result |
|---|---|
| Flyway on startup | Applied **only** `AddGlobalUnitToBiParametersAndReports`; no older migration replayed |
| `bi_reports.global_unit_id` | 1 → **45** on all four rows |
| `bi_parameters` | `global_unit_id` column added, all three rows backfilled to 45 |
| bi.do with data | Four dashboards in the original order, titles resolved, no `undefined` |
| bi.do tab switch | `current` → `BIreport-9`, title updated, lazy load, only the active container visible |
| A2-2428 live control | With one class appended after `current`, the old selector matches **0** elements and would paint `"undefined"`; the new one resolves the title |
| feedback.do with data | Its filtered dashboard (id 8) loads, no error page, no `undefined` |
| AC-001 empty state | **Verified** on bi.do under global unit AICCRA_III, which has no BI configuration — no database mutation needed |
| AC-002 handlers | `window` `message` handler registered with zero reports |
| AC-004 no 500 | **Verified in the browser for the first time.** Under AICCRA_III the `bi_widget_url` parameter belongs to global unit 45, so it does not resolve; the guard suppressed the `<script>` (no widget request). Before the fix this exact state was a FreeMarker 500 on `BiAppURL[0]`. The global-unit scoping turned a hypothetical case into a real one. |
| A2-2428 structural | In the empty state the `headTitle` element is not rendered at all, so the literal `undefined` cannot appear there by any path |

**AC-010 (feedback.do placeholder) is verified pre-merge only.** Reproducing it post-merge needs an authenticated
session for a global unit with no BI configuration, and the session could not be established in the automation
browser. The reasoning chain was closed by direct inspection instead, each link observed rather than assumed:
Kenji's action normalizes null to an empty list; an empty list makes `biReports?has_content` false; that `[#if]`
(feedbackStatus.ftl:49) is the one carrying this spec's `[#else]` (line 98, closing at 104); the `[#else]` holds the
placeholder with keys that are known to resolve; and the template parses after the merge. The same mechanism, with
the same keys and classes, was observed working on bi.do post-merge. Residual risk is low but it is inference, not
measurement.

**Hand to QA:** confirm AC-005 (module disabled plus direct URL) and AC-010 post-merge. Every other criterion is
measured in a browser.

### Local environment incidents during this pass (not defects in the change)

Recorded so the next person does not mistake them for regressions:

- **Flyway version collision.** A migration `V2_6_0_20260824_1000__CreateHelldotsTables.sql` had been applied to
  the local database but exists in no branch, and it collides with Kenji's migration on version
  `2.6.0.20260824.1000`. Flyway 4.0.1's `repair()` realigns checksums only, not descriptions, so validation failed
  and the context would not start. **Whoever owns the helldots work must renumber their migration**, or the
  collision will surface in a shared environment.
- **Flyway history lost.** Clearing the phantom row went wrong and emptied `schema_version` (~1579 rows). Schema and
  data were untouched — only the metadata. Recovered by re-baselining at `2.6.0.20260724.1433`, the last legitimately
  applied migration, so Flyway skips the older ones and applies only what is newer. The audit trail of when each
  migration ran is gone; restoring it faithfully from the ROW binlog remains possible.
- **Root cause of both database mishaps was multi-line SQL.** Statements handed over across two lines were executed
  partially by the SQL client, running a `DELETE` without its `WHERE`. This happened twice. Any destructive
  statement must be given as a single line, one statement at a time, with a count check in between.
