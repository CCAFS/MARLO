# Homepage Schedule Card — Tasks

**Spec ID:** ENH-HOMEPAGE-SCHEDULE-001
**Status:** Done
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-19
**Implements design:** docs/specs/enhancement/homepage-schedule/design.md
**Branching:** feature branch `A2-2398-US1-Re-design-MARLO-home-page` (continues the homepage redesign)
**Target merge:** staging (then promoted per release process)

---

## 1. Execution Context

- Java 17 (Zulu 17.54.21). `marlo-parent/pom.xml` declares `<java.version>17</java.version>`.
- Local run: `scripts/run-marlo-java17.sh`. Spring profile `dev`, config from
  `marlo-web/src/main/resources/config/marlo-dev.properties`.
- Build gate used: `mvn -pl marlo-web -am compile` (BUILD SUCCESS).
- Checkstyle: `mvn checkstyle:check` **cannot run in this repository** — `marlo-parent/pom.xml:827-834` pins `maven-checkstyle-plugin` 2.9.1 against `checkstyle` 8.18, and 8.x removed `Checker.setClassloader(ClassLoader)` which 2.9.1 calls, so the goal dies with a `NoSuchMethodError` before parsing any source. This is pre-existing and repo-wide: it fails identically on `marlo-utils` and `marlo-data`, which this change does not touch. The style rules were therefore verified by invoking `checkstyle-8.18.jar` directly with `configuration/marlo-checkstyle.xml` against `DashboardAction.java` — clean, zero violations.
- No database access was available in the implementing session, so the DB checks in
  `requirements.md` §8 remain outstanding and are listed under Operational Steps.
- Browser verification ran against a static harness that loads the **real** `dashboard.css` and the
  **real** `schedule.js` with fixture payloads, plus a FreeMarker 2.3.32 renderer driving the **real**
  template block. See Testing Plan.
- **The first run in Tomcat failed** with a FreeMarker `ParseException` on the `data-schedule`
  interpolation, blanking the dashboard. Cause: the payload used `?html`, which Struts 6.8.0's
  auto-escaping policy rejects at parse time (`design.md` ADR-7). The harness had not caught it
  because it left FreeMarker's output format undefined. It now applies the same settings Struts does
  — `setAutoEscapingPolicy(ENABLE_IF_DEFAULT)`, `setOutputFormat(HTMLOutputFormat.INSTANCE)`,
  `setNewBuiltinClassResolver(SAFER_RESOLVER)`, `VERSION_2_3_28`, no `numberFormat` override — and a
  control run with `?html` reinstated reproduces the production error verbatim, down to the column.

## 2. Pre-flight Checklist

- [x] `requirements.md` and `design.md` written and self-reviewed.
- [x] Branch already exists and continues the homepage redesign work (`A2-2398-…`).
- [x] Confirmed the block being replaced and its exact bounds before editing.
- [x] Confirmed no `custom/*.properties` overrides the i18n keys being retired.
- [x] Confirmed `TimelineManager.findAllByGlobalUnit` returns `null` (not an empty list) when a global
      unit has no activities.

## 3. Task List

| ID | Task | Depends on | Acceptance | Verification |
|---|---|---|---|---|
| ENH-HOMEPAGE-SCHEDULE-001-T01 | Replace the 17 `dashboard.reportingTimeline.*` keys with 44 `dashboard.schedule.*` keys | — | New section header comment follows the `# <Name> (<view path>)` convention | `grep -rn reportingTimeline marlo-web/src` returns nothing |
| …-T02 | Reword `timelineManagement.help` to describe date-driven placement and the `order` tiebreaker | T01 | Old text about dates not determining order is gone; the pre-existing "if its filled" typo is gone | Rendered raw in `timelineManagement.ftl`, so the `<em>` still works |
| …-T03 | Add five tokens to the single `:root` in `marlo-redesign.css` | — | `--marlo-info-solid`, `--marlo-success-solid`, `--marlo-warning-ink`, `--marlo-outline-dashed`, `--marlo-today-line` | `getComputedStyle` resolves `--marlo-today-line` to `#C7401C` |
| …-T04 | Replace the `.reportTimeline` CSS block with `.scheduleCard` | T03 | Stacking contract commented; card shell recipe reused verbatim from the existing pattern | Brace balance unchanged; zero `.reportTimeline` selectors remain |
| …-T05 | Write `webapp/crp/js/home/schedule.js` (ES5 IIFE) | T04 | Measures the track, packs three lanes, paints axis/bars/pills/chips, owns zoom + popover | `node --check` passes; no `let`/`const`/arrow/template literals |
| …-T06 | Replace the gated card block in `dashboard.ftl` and emit the `data-schedule` payload | T01, T05 | `homepage_timeline_active` gate kept; open + upcoming phases only | Renders through FreeMarker 2.3.32 with valid JSON and no missing keys |
| …-T07 | Delete the dead hardcoded 2016/2017 `[#assign timeline = [...]]` sequence | T06 | Nothing lists or dereferences it | `grep` for `#list timeline` / `timeline as` returns nothing |
| …-T08 | Register `schedule.js` in `customJS` and bump cache-bust query strings | T05, T06 | Loaded from `baseUrlMedia`, after `utils.js`/`global.js` | Present in the `customJS` assign at the top of `dashboard.ftl` |
| …-T09 | Remove `initReportTimeline()` and its call from `dashboard.js` | T06 | No dangling reference | `node --check` passes; `grep initReportTimeline` returns nothing |
| …-T10 | Inject `TimelineManager` into `DashboardAction`, add `loadScheduleActivities()` + getter | — | Null-guarded, sorted `(order nullsLast, id)`, exception-safe | `mvn compile` passes; Checkstyle run directly (the Maven goal is broken repo-wide) reports zero violations |
| …-T11 | Hold the label column at 276px at every viewport | T04 | No phase name ellipsized at any zoom or viewport | Measured at 1024px: zero clipped labels (was 1 at 232px) |
| …-T12 | Build the verification harness and run the full matrix | T05, T06 | Every DoD item measured, not asserted | See Testing Plan |
| …-T13 | Write this spec folder | T12 | Three files, mandated section order | — |

## 4. Dependency Graph

```
T01 ─┬─ T02
     └─ T06 ─┬─ T07
             ├─ T08
             └─ T09
T03 ── T04 ─┬─ T05 ── T06
            └─ T11
T10 (independent)
T05, T06, T10, T11 ──> T12 ──> T13
```

## 5. Testing Plan

### Server-side template (FreeMarker 2.3.32, real block, stubbed `s:` namespace)

Configured exactly as Struts 6.8.0 configures it, auto-escaping and HTML output format included; a
harness that skips this cannot reproduce ADR-7's parse error.

| Scenario | Result |
|---|---|
| control: `?html` reinstated | fails to parse with the exact production message at the same column — the harness reproduces the defect it previously missed |
| reference (2 open, 1 upcoming, 2 closed, 1 null-date phase) | 3 lanes; closed and null-date phases excluded; valid JSON |
| all i18n keys | zero `MISSING_KEY` across every scenario |
| hostile descriptions (`<script>`, `"`, `'`, `<b>`, `&`, `\`, newline, tab, unicode) | valid JSON; raw attribute contains no literal `"` and no `<script` |
| blank description / null dates | skipped, not rendered as empty pills |
| `order` as `2.0` and `6.5` | serialized as `2` and `6.5`; JSON-parseable |
| zero open phases | zero-state renders; `emptyNext` present, `browseClosed` present, no frame |
| zero open **and** zero upcoming | zero-state renders **without** the next-phase panel |
| countdown edges | `10 days left` URGENT · `42 days left` neutral · `Last day` URGENT · `Past its end date` URGENT · `Opens tomorrow` · `Opens in 43 days` |
| "notify" anywhere in output | 0 occurrences |

### Client-side (real CSS + real JS, Chrome, 1440 / 1280 / 1024 viewports)

| Check | Result |
|---|---|
| Lane region height, all 4 zoom stops | 272px at every stop |
| Lane region height at 20 / 40 / 400 activities | 272px in all three; card 535px, frame 373px |
| `placed + overflow == total`, all stops | 20/20, 40/40, 400/400 |
| Any bar/pill/chip escaping the track | none; worst overshoot negative at every stop |
| Negative offsets | none |
| Today line vs `TODAY` badge offset | identical (0px delta) at every stop |
| Minimum chip gap vs the 190px merge threshold | ≥ 198px in every scenario |
| Milestones | dashed capsules, 178px, labels legible; 20/20 packed as 7/7/6 |
| Labels clipped mid-word, all stops | none, after T11 |
| Page body horizontal scroll | never |
| 4 phase lanes | clamps at `max-height: 388px` and scrolls vertically |
| Sticky axis (scrolled down) / sticky label column (scrolled right) | both hold; label column opaque |
| z-index stack | rows `auto`, grid 0, today 2, label 3, axis 4 — as specified |
| Zoom persistence | 16 stored, restored after reload, footer and axis label follow |
| Modifier + wheel | 8 → 4 → 2 in, clamps at 2; 3 steps out → 16, clamps at 16 |
| Plain wheel | not `preventDefault`ed; page scrolling preserved |
| Jump to today | restores the centre offset exactly |
| Overflow popover | opens, `role="dialog"`, lists name + dates + status, escapes the frame's clipping, closes on Escape and resets `aria-expanded` |
| Focus ring | computes to `solid 2px rgb(199, 64, 28)` at `-2px` offset |
| Script injection | `window.__pwned` undefined; 0 `<script>` and 0 `<b>` elements inside the card |
| WCAG 2.5.3 Label in Name on the zoom buttons | all four pass — the visible `8 wks` is a substring of the accessible name |
| Axis ticks in the accessibility tree | 0 exposed at every stop (25 ticks at 2 wks, 5 at 8 wks); the `TODAY` badge stays exposed |
| Accessible text per item | 19 `.sr-only` spans (3 phase bars + 16 pills) carry full name, dates and status |
| 1280px (95% container active) | container 1216px, track 866px, no overflow |
| 1024px | label column 276px, zero clipped labels, no overflow |

### Adversarial review

An adversarial review ran over the diff: six independent dimensions, each finding then put to two
skeptics instructed to refute it and to default to refuted when uncertain.

**Two findings survived and were fixed:**

1. *WCAG 2.5.3 Label in Name (Level A) on the zoom buttons.* The visible label was `8 wks` while the
   `aria-label` read `Show 8 weeks at a time`, so the visible string was not contained in the
   accessible name and a speech-input user saying "click 8 wks" would not match it.
   `dashboard.schedule.zoom.accessibleName` is now `{0} wks, show {0} weeks at a time`.
2. *Axis tick labels flooded the accessibility tree.* `#scheduleGrid` and `#scheduleNow` were
   `aria-hidden` but the axis was not, so `paintAxis` exposed one bare date fragment per week — up to
   ~158 at the widest range — between the view label and the first phase row. The tick spans are now
   individually `aria-hidden`; the `TODAY` badge is deliberately left exposed, because it is the only
   accessible representation of the today marker.

**Review coverage was incomplete.** The run hit the session's usage limit with 15 of 46 agents
finished. The `ftl` and `integration` dimensions never produced a result, and the verification pass
for the `js-logic`, `css` and `java` dimensions was cut short. The checks those two dimensions were
scripted to perform were then run by hand instead, and all passed:

- The bundle loads through `java.util.Properties`: 5897 keys, 44 under `dashboard.schedule.*`, and the
  `\u2013` / `\u2318` / `\u00b7` escapes decode to `–`, `⌘` and `·`.
- 44 keys defined, 44 referenced — no unused key and no undefined reference.
- Placeholder arity matches the `[@s.param]` count at every call site.
- Repo-wide grep for `reportTimeline`, `reportingTimeline` and `initReportTimeline`: no survivors.
- `timelineManagement.help` is rendered with `[@s.text /]` inside a `<p>`, unescaped, so its `<em>`
  still italicises.
- `schedule.js` returns immediately when `#scheduleCard` is absent, contains no `let`/`const`/arrow/
  template literal, and does not touch jQuery.

A residual risk follows from the truncated run: the JS packing logic, the CSS cascade and the Java
loader were reviewed but their findings were not fully adversarially verified, so a defect could have
been dismissed by an incomplete verification pass rather than by evidence.

### Regression

- `mvn -pl marlo-web -am compile` — BUILD SUCCESS.
- Checkstyle rules — clean on `DashboardAction.java` via `checkstyle-8.18.jar` and
  `configuration/marlo-checkstyle.xml`. The `mvn checkstyle:check` goal itself is broken
  repo-wide and pre-existing; see Execution Context.
- `node --check` on both `schedule.js` and `dashboard.js`.
- Repo-wide `grep` for `reportTimeline`, `reportingTimeline`, `initReportTimeline` — no survivors.

### Not covered

- No live-application run: no database was reachable in the implementing session, so the card has not
  been rendered against real `phases` and `timeline` rows inside Tomcat. This is the main residual gap
  and the first item under Operational Steps.
- No automated test was added. MARLO has no JS test harness and no FreeMarker rendering test
  infrastructure; the harness built here lives outside the repo.

## 6. Operational Steps

1. **Run the app locally and confirm against real data** (`scripts/run-marlo-java17.sh`), for a global
   unit with `homepage_timeline_active` enabled. This is the one verification the implementing session
   could not perform.
2. **Run the DB checks** from `requirements.md` §8:
   - `SELECT COUNT(*) FROM timeline WHERE global_unit_id IS NULL;` — non-zero means rows are invisible
     to the card; raise a bugfix spec to backfill.
   - `SELECT id, description, start_date, end_date, order_index FROM timeline ORDER BY start_date;`
     — confirm the real row count and how often `start_date = end_date`.
   - Confirm which global units have `homepage_timeline_active` set.
3. No migration to deploy, no configuration or env-var change, no BI/AI coordination.
4. Static assets carry `?20260819` cache-bust query strings; confirm the CDN picks them up if
   `cdn.url` is set for the target environment.

## 7. Rollback Plan

Revert the seven touched files. There is no migration, no schema change and no written data, so revert
is complete and immediate:

```
marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/home/DashboardAction.java
marlo-web/src/main/resources/global.properties
marlo-web/src/main/webapp/WEB-INF/crp/views/home/dashboard.ftl
marlo-web/src/main/webapp/crp/css/home/dashboard.css
marlo-web/src/main/webapp/crp/js/home/dashboard.js
marlo-web/src/main/webapp/crp/js/home/schedule.js        (delete)
marlo-web/src/main/webapp/global/css/marlo-redesign.css
```

A faster partial mitigation, if the card misbehaves in production but the rest of the homepage is
fine: set `homepage_timeline_active` to `false` for the affected global unit in `custom_parameters`.
The card disappears and nothing else changes.

## 8. Definition of Done

- [x] Card body holds ~272px with three phase lanes, three activity lanes and the overflow strip.
- [x] Adding activities changes chip counts, never the card height — measured at 40 and 400.
- [x] At every zoom stop: no pill escapes the track, no label clips mid-word, no chip overlaps a pill
      (structurally impossible — chips have their own row), today line aligned with the axis badge.
- [x] Two simultaneously open phases with overlapping ranges are legible on separate lanes.
- [x] Zero-open-phases state renders as a designed state.
- [x] No closed-phase lanes and no "show closed phases" control anywhere in the card.
- [x] `mvn compile` passes; Checkstyle rules clean on the changed Java file (the Maven goal is broken repo-wide, pre-existing).
- [x] User-entered activity text cannot inject markup or script.
- [ ] Verified in a running MARLO instance against real data (Operational Steps 1–2).
