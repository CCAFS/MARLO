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
- Local reproduction requires an empty `bi_reports` table. Use a throwaway local database and
  `DELETE FROM bi_reports;` — never against a shared environment.

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
200). The BI route sits behind `requireUser`, so rendering it needs an authenticated session. The agent doing this
work does not enter credentials, so the remaining criteria need a human-driven login. The empty-state path
additionally needs an instance whose `bi_reports` is empty; `marlo-web/tomcat/context.xml` points at the shared
`aiccradb_icipe_test` RDS, and emptying that table there was ruled out (§1 Execution Context).

**Residual risk after this pass:** low for the FTL and JS logic, which now have direct evidence. Unmeasured: the
placeholder's visual rendering and the browser-level regression of the four tab behaviours.
