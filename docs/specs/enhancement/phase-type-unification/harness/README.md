# Rendered-HTML capture harness (T07, NF-002)

Captures the rendered HTML of a fixed reference set of MARLO pages from a LOCAL app, normalizes only what is proven to
vary run-to-run, and diffs a capture against a stored baseline. GET-only after login, but some GETs write rows (see the coverage notes), so run it ONLY against a
throwaway schema.

## Run

```
# app must already be up on http://localhost:8080/marlo-web/ (see docs/infrastructure.md section 6)
harness/capture.sh <out-dir>              # one command -> capture dir (186 pages + _manifest.txt)
harness/diff.sh <baseline-dir> <out-dir>  # one command -> prints every differing page; silent + "IDENTICAL" when equal
RAW=1 harness/capture.sh <out-dir>        # no normalization (to prove what varies)
```

- Login: `~/.marlo-local-credentials` (`MARLO_LOCAL_USER`, `MARLO_LOCAL_PASSWORD`), sent only to a `localhost` BASE
  (the script refuses any other host). Credentials are never written to the capture.
- Captures go OUTSIDE the repo (they contain project data); never commit them.
- The page set is `pages.txt` (`name|crp|path|flags`). It targets ids of the local `zz_t07_actsave` schema:
  AICCRA phases 429 plan / 430 progress / 431 reporting, AICCRA_III 444 / 445 / 446, project 102086 (AICCRA) and
  102093 (AICCRA_III), deliverable 24753 (has a participants section), innovation 1626, RTB study 3384 (phase 262).
  On another schema, edit the ids.
- Coverage notes: AICCRA/AICCRA_III have no expected-study rows in 2026 phases, so the expected-study FORM is captured
  on RTB phase 262 (the only CRP with study data) and the study LISTS on AICCRA. AICCRA_III has one project and no
  deliverables or innovations, so the deliverable, innovation and study FORMS are captured for AICCRA only (the
  T09 list pages cover both units). The main menu and guide button are part
  of every page; the dashboards are the dedicated menu/guide captures. NOGU_* are the no-global-unit pages: the
  anonymous login page (no session) and a logged-in `glossary.do` (no CRP in the URL).
- T09 added the list-macro pages (deliverable and innovation lists) and one page per `crp/views/projects` section
  (partners, activities, locations, ccafsOutcomes, outcomes, otherContributions, highlights, outputs, budgetByFlagship,
  contributionCrp) for AICCRA and AICCRA_III in the plan, progress and reporting phases (`X_*` pages, 72 in all). contributionCrp
  needs a `projectOutcomeID` that exists in the phase (AICCRA 36447-36449 and 37599-37601; AICCRA_III 42440-42442): an
  id of another phase renders the "Page not found" page.
- The first view of a progress-phase `contributionCrp` page creates the missing milestone row (a GET side effect of the
  action, seen as `milestones[N].id` empty once, then a real id). Capture twice before taking a baseline on a fresh schema.
- The annual report and POWB pages (`Y_*`) also save a missing synthesis row on a plain GET (their `prepare()` calls
  `saveReportSynthesis` / `savePowbSynthesis`, e.g. `CrpProgressAction.java:511`, `ToCAdjustmentsAction.java:359`), so the
  first capture on a fresh schema creates `report_synthesis*` / `powb_synthesis*` rows. Capture twice before baselining.
  Never point the app at `aiccradb_actsave` or any real schema while running this harness.
- T10 added the `Y_*` pages (72): `publicationsList` for AICCRA and AICCRA_III in the plan, progress and reporting phases, and the RTB (phase 262 reporting, 260 POWB 2019) publication form, all 14 `annualReport` and 19 `annualReport2018` sections, all 11 `powb` and 6 `powb2019` sections, plus a few AICCRA/AICCRA_III annual report and POWB pages. RTB stands in where AICCRA answers an internal error (most annual report sections) or has no publication-type deliverable in 2026. Not reachable from any CRP: the "only at Reporting cycle" branch of the annual report sections and the POWB pages outside a plan phase answer the not-found page, so the `!reportingActive` true branch is never rendered. `main-menu.ftl` and `guide-button.ftl` and `javascript-constants.ftl` render on every page.
- The Global Unit switcher entries and every list are rendered in hash order, so the order differs per request.

## Normalizations (applied in `capture.sh`; nothing else is touched)

| Id | Rule | Why it is safe |
|----|------|----------------|
| N1 | `;jsessionid=...` becomes `NORM` | servlet session id, new on every login |
| N2 | `?<token>` after `.css/.js/.png/...` asset URLs becomes `?NORM` | cache-bust parameter, changes on every asset edit (not a rendering decision) |
| N3 | CSRF/struts token input values become `NORM` | random per session (no such input found on the captured pages; kept as the brief requires) |
| N4 | the Global Unit switcher `guItem` blocks are sorted | the list is a hash-ordered Set; proven: two raw runs differed only in the order of the same entries |
| N5 | `nonce="..."` becomes `nonce="NORM"` | per-request CSP nonce, proven different on every request |
| N6 | pages flagged `sorted`: lines are sorted (`LC_ALL=C sort`) | Hibernate collections are hash-ordered, so rows/options come in a different order on every request (project lists, deliverable rows, lever outcomes). A reordering is therefore NOT detected on these pages; any added, removed or changed line is. The two NOGU_* pages keep their exact order |
| N7 | on `sorted` pages: `[<n>]` becomes `[N]` and a trailing `_<n>"` in an `id` becomes `_N"` | collection indexes follow the unstable iteration order, so the same item gets a different index per request |
| N8 | `innovation.allianceLevers[i].id` hidden value and its `<p>` echo become `NORM` | the local data holds the same lever twice for one innovation and phase; the page shows whichever duplicate the Set yields first (18651 vs 18702 proven) |

Evidence: five consecutive captures (run1..run5) are byte-identical under these rules.
