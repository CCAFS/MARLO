# Homepage Schedule Card — Requirements

**Spec ID:** ENH-HOMEPAGE-SCHEDULE-001
**Status:** Implemented
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-19
**Related PRD sections:** docs/prd.md — homepage / reporting cycle visibility
**Related System Design sections:** docs/system-design/design.md — homepage information architecture, layout patterns, accessibility commitments
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (data model), §5 (frontend composition)
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md

---

## 1. Overview

The homepage card that shows the reporting calendar is replaced by a new **Schedule** card. The card
answers, in under two seconds: which reporting phases are open, their exact start and end dates,
where today sits, how much time remains per open phase, and which platform-wide timeline activities
are running in the visible period. Vertical compactness is a primary success criterion — the card
must not grow as data grows.

This spec exists now because the branch `A2-2398-US1-Re-design-MARLO-home-page` replaced the legacy
full-width week-grid calendar with an interim month-percentage Gantt that shows phases only. The
Gantt dropped timeline activities entirely, had no zoom, and rendered closed-phase lanes that
duplicate the phase selector's history popover.

## 2. Problem Statement

Users open MARLO during a reporting window and need to know what is due and when. The legacy week
grid was full-width, grew with the number of activities, and rotated activities through three lanes
by array index rather than by date, so overlapping work could be drawn on top of itself. The interim
Gantt is compact but shows no activities at all, and its "Show closed phases (N)" toggle repeats
information the phase selector already owns. Neither view makes a per-phase deadline legible when
several phases are open at once — which is the normal state, not the exception.

## 3. In-Scope Requirements

### Functional

- **ENH-SCHED-FN-001** — *Withdrawn 2026-08-20.* The timeline renders **activities only**; there is no
  reporting-phases section, no phase lane and no phase bar. Phase dates reach the user through the
  phase selector above the card and through the "what's next" panel's fallback (FN-012).
- **ENH-SCHED-FN-002** — *Withdrawn 2026-08-20* with FN-001.
- **ENH-SCHED-FN-003** — *Withdrawn 2026-08-20* with FN-001, except that `Opens in N days` /
  `Opens tomorrow` survive in the "what's next" panel when it falls back to a phase.
- **ENH-SCHED-FN-004** — Platform-wide timeline activities (`Timeline`, scoped to the current global
  unit) MUST be packed into exactly **three** reserved lanes by greedy first-fit, ordered by start
  date with the `order` field as tiebreaker.
- **ENH-SCHED-FN-005** — Activities that do not fit MUST appear as `+N more` chips in a dedicated
  overflow strip, never inside the three packed lanes. Clicking a chip MUST open a popover listing
  those activities with name, date range and status.
- **ENH-SCHED-FN-006** — An activity whose start equals its end MUST render as a single-day milestone
  capsule wide enough to read its label, not a zero-width bar. Shape and status are **independent**:
  the capsule MUST still carry its status colour and status dot.
- **ENH-SCHED-FN-007** — Zoom MUST offer 2 / 4 / 8 / 16 visible weeks, defaulting to 8, and MUST
  recompute the lane packing — not stretch the DOM. Modifier + wheel MUST step the zoom.
- **ENH-SCHED-FN-008** — The zoom choice MUST persist per browser via `localStorage`.
- **ENH-SCHED-FN-009** — A single vertical today marker MUST span all lanes, with a `TODAY` badge on
  the axis row aligned to the same offset.
- **ENH-SCHED-FN-010** — The `Today` button MUST re-centre the track on today. A zoom change MUST
  **preserve** the user's position instead: the buttons keep the day at the centre of the viewport,
  and modifier + wheel keeps the day under the pointer. (Amended 2026-08-19; this originally required
  every zoom change to re-centre on today, which discards the user's position each time they change
  scale. See the Decision Log.)
- **ENH-SCHED-FN-011** — The footer MUST report the packing honestly: the window size, the span of
  the rendered range, and how many of the total activities were placed.
- **ENH-SCHED-FN-012** — The card MUST always use the two-column layout: the timeline (controls,
  frame, footer, overflow popover) in the main column and a "what's next" panel beside it. The panel
  MUST name the soonest activity that has not started yet, falling back to the soonest phase still to
  open, and MUST be omitted when neither exists. (Amended 2026-08-20; this originally specified a
  separate zero-state that *replaced* the timeline when no phase was open. See the Decision Log.)
- **ENH-SCHED-FN-014** — The "what's next" panel MUST stack below the timeline at **1250px** and
  below, because the panel is a fixed 300px and every pixel it takes comes off the track, which packs
  fewer activities into the three lanes. (Set to 1300px, briefly moved to 1024px, settled at 1250px on
  2026-08-20.) The panel is 300px wide and
  offset 44px from the top of the column so it aligns with the timeline frame, not with the zoom
  controls; the offset MUST be reset when stacked.
- **ENH-SCHED-FN-013** — The card MUST remain behind the `homepage_timeline_active` specificity.

### Non-functional

- **ENH-SCHED-NF-001** — The lane region MUST stay **140px** (`3 × 36` activity lanes + `32` overflow)
  and MUST NOT grow with activity count. Verified at 20, 40 and 400 activities. (Was ~272px while the
  three phase lanes existed; FN-001 withdrew them.)
- **ENH-SCHED-NF-002** — The visible track width MUST be measured at runtime. No fixed pixel track
  width is permitted, because `global.css` forces `.container` to `95% !important` below a 1300px
  viewport.
- **ENH-SCHED-NF-003** — The label column MUST stay 276px at every viewport; it is a content minimum
  (`Progress - 2026` plus its `Opens in 44 days` pill must not clip).
- **ENH-SCHED-NF-004** — WCAG 2.1 AA throughout. No white text on the light brand blue or green. No
  status conveyed by colour alone. 12px is the type floor.
- **ENH-SCHED-NF-005** — Activity descriptions are free text typed by users. The FTL→JS payload MUST
  be escaped for both the JSON and the HTML-attribute layer, and JavaScript MUST write activity text
  with `textContent` only. The attribute layer is covered by FreeMarker's own auto-escaping, which
  Struts 6.8.0 enables unconditionally; the legacy `?html` built-in MUST NOT be used, because it is a
  parse error under that policy.
- **ENH-SCHED-NF-006** — JavaScript MUST be ES5 (no `let`/`const`, arrow functions or template
  literals), matching the other homepage-redesign files. There is no transpiler for webapp JS.
- **ENH-SCHED-NF-007** — No new schema, no new Struts action, no new JSON endpoint.

## 4. Out-of-Scope

- Per-phase completion percentages. The `Phase` entity carries no progress data; see the Decision Log.
- A notification subscription ("notify me when a phase opens"). No such entity, table or endpoint exists.
- Editing activities from the card. Activities stay managed in Admin → Timeline Management.
- The center homepage. `WEB-INF/center/views/home/dashboard.ftl` has not existed since 2018.
- Backfilling `timeline.global_unit_id` rows left NULL by `V2_6_0_20250808_1134__UpdateTimelineTable.sql`.
- Print layout beyond an unclamped fallback; MARLO has no print stylesheet.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Cluster leader / coordinator | Sees which phases are open and how many days remain, per phase, on landing. |
| PMU / PMC | Sees the platform-wide activity calendar restored, packed and legible at four zoom levels. |
| CRP admin | Timeline Management help text now describes date-driven placement; the `order` field is a tiebreaker. |
| QA reviewer | Reads the same dates as everyone else; no closed-phase noise in this card. |

## 6. Acceptance Criteria

- **AC-001** (FN-001, FN-002) — Given two open phases with overlapping ranges, when the homepage
  renders, then each occupies its own lane with its own countdown pill and neither bar obscures the other.
- **AC-002** (FN-003) — Given a phase ending today, then the pill reads `Last day` in the accent
  treatment; given an editable phase whose end date has passed, then it reads `Past its end date`.
- **AC-003** (FN-004, FN-005) — Given N activities, then the sum of the three lane counts plus the
  overflow count equals N at every zoom stop, and no chip is drawn inside lanes 1–3.
- **AC-004b** (FN-006) — Given a single-day activity whose date has passed, then it renders in the
  `Completed` colour with a visible `Completed` dot, and still with the dashed single-day edge.
- **AC-004** (FN-006) — Given an activity with start equal to end, then it renders as a dashed capsule
  approximately 178px wide with its label legible.
- **AC-005** (FN-007, FN-010) — Given any zoom stop, when it is selected, then the packing is
  recomputed, no pill escapes the track, and no label clips mid-word.
- **AC-005b** (FN-010) — Given the track is scrolled to a date away from today, when the zoom changes
  by button, then the date at the centre of the viewport is unchanged; when it changes by modifier +
  wheel, then the date under the pointer is unchanged. Except where the timeline's end clamps
  `scrollLeft`, which is permitted.
- **AC-006** (FN-008) — Given a user picks 16 weeks and reloads, then the card renders at 16 weeks.
- **AC-007** (FN-009) — Given today falls inside the rendered range, then the today line's offset
  equals the `TODAY` badge's offset exactly.
- **AC-008** (NF-001) — Given 20, 40 and 400 activities, then the lane region height is identical.
- **AC-009** (NF-002) — Given a 1024px viewport where `.container` is 95% wide, then nothing overflows
  the card and the page body does not scroll horizontally.
- **AC-010** (NF-005) — Given an activity described as `<script>…</script>`, then no script executes,
  no element is created from the markup, and the text is shown literally.
- **AC-011** (FN-012) — Given no open phase, then the timeline still renders and the panel names what
  is next; the reporting-phases section is omitted only when there is no lane at all to draw.
- **AC-011b** (FN-012) — Given a future activity and a future phase both exist, then the panel names
  the activity. Given only a future phase, it names the phase. Given neither, it is absent.
- **AC-011c** (FN-014) — Given a viewport of 1250px or less, then the panel is full-width below the
  timeline and the track keeps the width it would have had without the panel. At 1251px and above the
  layout stays side by side.
- **AC-012** (FN-013) — Given `homepage_timeline_active` is false, then the card is absent entirely.

## 7. Constitutional Compliance Checklist

| Rule | Status |
|---|---|
| 1. Phased data is forward-only | Not applicable — read-only card, no writes. |
| 2. Save pipeline pattern | Not applicable — no save path. |
| 3. Spring MVC owns `/api/*` | Honored — no new `*.json` Struts path; data travels on the value stack. |
| 4. Specificities via `parameters` + `custom_parameters` | Honored — reuses the existing `homepage_timeline_active` key; no new specificity. |
| 5. Schema changes ship as Flyway migrations | Not applicable — no schema change. |
| 6. GPL header on every new Java file | Not applicable — no new Java file; `DashboardAction.java` already carries it. |
| 7. Code style, Checkstyle gate | Honored on the rules — Checkstyle 8.18 with `configuration/marlo-checkstyle.xml` reports zero violations on the changed Java file. **The `mvn checkstyle:check` goal is broken repo-wide** (plugin 2.9.1 vs checkstyle 8.18, `NoSuchMethodError`), pre-existing and unrelated to this change — see `task.md` §1. |
| 8. English only; user-facing strings i18n-keyed | Honored — 38 keys under `dashboard.schedule.*`. |
| 9. Branching | Honored — feature branch `A2-2398-US1-Re-design-MARLO-home-page`; `staging` is the integration branch. |
| 10. Run scripts / Java 17 | Honored — built and verified with Zulu 17. |
| 11. Dependency baseline | Honored — no dependency change. |
| 12. No credential files committed | Honored. |

## 8. Open Questions

1. **Data hygiene, needs a DBA check.** `V2_6_0_20250808_1134__UpdateTimelineTable.sql` added
   `timeline.global_unit_id` as nullable with no backfill, while `Timeline.hbm.xml` declares it
   not-null. Rows created before 2025-08-08 hold NULL and are invisible to `findAllByGlobalUnit`, so
   the card silently omits them. Run `SELECT COUNT(*) FROM timeline WHERE global_unit_id IS NULL;`
   and backfill under a separate bugfix spec if the count is non-zero.
2. **Mapping vs DDL drift.** `V2_6_0_20230221_0842__UpdateTimelineTable.sql` made `start_date` and
   `end_date` nullable but the hbm still says `not-null="true"`. The card null-guards on read; the
   write path should be reconciled separately.
3. Should a future spec derive a real per-phase completion figure from `SectionStatusManager`?
4. **The Checkstyle gate does not run.** `marlo-parent/pom.xml:827-834` pins
   `maven-checkstyle-plugin` 2.9.1 with a `checkstyle` 8.18 override; 8.x removed the
   `Checker.setClassloader` method 2.9.1 calls. AGENTS.md documents `mvn checkstyle:check` as a
   gate, so the gate is currently vacuous on every module. Fixing it means bumping the plugin
   (3.x) and belongs in its own bugfix spec, not here.

## 9. Decision Log

- 2026-08-19 — **Zoom state lives in `localStorage`, not the database.** — The legacy
  `crp_timeline_week_parameter_visualization` parameter is on a 1/2/4/8 scale and its seeded default
  is `'423'`, which `GlobalUnitCreationManagerImpl` itself comments is invalid. Reusing it would
  require a remap plus a repair migration for existing global units, to control a purely visual
  preference no code has ever written back. `marlo.clusterBanner.collapsed` is the precedent.
- 2026-08-19 — **New `dashboard.schedule.*` namespace; the 17 `dashboard.reportingTimeline.*` keys are
  retired.** — No `custom/*.properties` overrides any of them (checked across all 20 tenant files),
  and roughly seven describe removed features (`showClosed`, `hideClosed`, `view`, `legend.closed`).
- 2026-08-19 — **The "Notify me when <phase> opens" control is dropped.** — No notification
  subscription exists anywhere in MARLO. Shipping a dead or disabled control would promise something
  the backend cannot do.
- 2026-08-19 — **Packing sorts by start date, with `order` as the tiebreaker.** —
  `timelineManagement.help` previously promised admins that dates do not determine order; it has been
  reworded. Packing strictly in `order` sequence would strand a late activity in lane 1 above an
  earlier one, which reads as a bug.
- 2026-08-20 — **The reporting-phases section is removed from the timeline.** — Product asked for the
  timeline to carry activities only. Removed: the section header and its badge, the phase lanes and
  bars, the countdown pills that lived in their label column, the `phases` array in the
  `data-schedule` payload, and `paintPhases()` with its track lookups in `schedule.js`. The rendered
  span is now derived from activities alone (still seeded with today, so an empty list renders).
  Consequences: the lane region drops from 272px to **140px** and the card from 535px to 375px; six
  i18n keys are retired (`phases.title`, `phases.open`, `phases.oneOpen`, `daysLeft`, `lastDay`,
  `overdue`), leaving 38; sixteen CSS rules for phase rows and bars are deleted. `opensIn` /
  `opensTomorrow` stay because the "what's next" panel still falls back to a phase. The subtitle's
  `N phases open` was **kept** — it is header context, not part of the timeline — which is worth a
  product check now that no phase is plotted.
- 2026-08-20 — **The two-column layout becomes the default; the zero-state is gone.** — Product
  preferred the composition that only appeared when every phase was closed, so it is now the card's
  normal state: `__layout > __main` holds the whole timeline and `__next` sits beside it. Consequences
  taken deliberately: (a) the "Nothing due right now" heading, its paragraph and the
  "Browse closed phases (N)" button are **removed** (5 i18n keys retired, 6 added, net 44), which also
  removes the card's only dependency on the phase selector's `#allPhasesToggle`; (b) the
  reporting-phases section is omitted when there is no lane, rather than showing a header above
  nothing; (c) the panel prefers the next *activity* and falls back to the next *phase*, with distinct
  labels so the two are never confused; (d) `__main` carries no border or padding, because the frame
  inside it is already a bordered box. Measured before first choosing a 1300px breakpoint, later moved
  to 1024px at product's request (40 activities):
  a 340px side panel costs 28% → 30% overflow at 1600px but 40% → 60% at 1000px, where the track falls
  to 200px. Stacked at 1200px the track is 790px and overflow is back to 30%.
- 2026-08-20 — **The panel is 300px and drops 44px to meet the frame.** — Product asked for both. The
  44px is not a round guess: it is the controls row measured exactly — 34px of button plus its 10px
  `margin-bottom` — so `panelTop === frameTop` to the pixel. It is a coupled constant, so the comment
  in the stylesheet says what it tracks; the stacked media query resets it to 12px, verified so the
  offset cannot leak into the stacked layout. Narrowing 340 → 300 returned 40px to the track (812 →
  852px at 1600px).
- 2026-08-19 — **A single-day activity keeps its status; `--milestone` now styles shape only.** — QA
  spotted "MARLO AICCRA opens for reporting" rendering white although its date had passed. Two faults
  stacked. In `schedule.js` the class was chosen by ternary — `item.milestone ? 'milestone' :
  item.status` — so a milestone got the shape class *instead of* the status class and had no fill, no
  border colour and no status dot at all (the dot computed to `rgba(0, 0, 0, 0)`). In the stylesheet
  `--milestone` also set `background`, `border-color` and `color`, and being later in source with equal
  specificity it would have outranked every status rule anyway. The pill now carries **both** classes
  and `--milestone` keeps only `border-style: dashed`. The status calculation itself was always
  correct, and the overflow popover had always shown the right dot — which is why the same activity
  looked finished in the popover and blank on the track. Contrast re-checked: 5.87:1 completed,
  5.52:1 not started.
- 2026-08-19 — **`Upcoming` is removed; a future phase is `Not started`.** — The two categories were
  computed by the same test — `startDate > today` — one applied to phases (`upcoming`) and one to
  activities (`notStarted`), so the legend carried five swatches for four real states. A future phase
  now takes the `Not started` treatment that future activities already had, which also gives
  `.scheduleCard__bar--notStarted` a referent; it previously had none, since phases could never reach
  that status. Contrast re-checked: 6.05:1 on the amber bar, 5.52:1 on the pill. `Opens in N days` is
  unaffected — it is timing, not status. **Not touched:** the phase selector (`timeline-phases.ftl`)
  keeps its own independent `upcoming` vocabulary and `phaseSelector.summary` still reads
  `N open · N closed · N upcoming`; aligning it is a separate change to a separate component.
- 2026-08-19 — **Phase bars render only `In progress` or `Not started`; no completion percentage.** — The
  design mockup showed a phase as "Not started · 24%" while its date range contained today, so phase
  status there was neither date-derived nor backed by data. `Phase` carries only `startDate`,
  `endDate`, `editable`, `visible`, `year`, `name`, `description`, `upkeep` and `next`. `Not started`
  and `Completed` are earned by activities, whose status *is* date-derived, so every legend swatch
  still has a referent. A fabricated percentage on a reporting deadline is the kind of number people
  plan around.
- 2026-08-19 — **The track width is measured at runtime rather than fixed at the design's 1058px.** —
  `global.css:85` forces `.container:not(.loginPage)` to `width: 95% !important` below a 1300px
  viewport, leaving ~1163px of card interior. A hardcoded 1058px track plus the 276px label column
  overflows its own card at any viewport below roughly 1400px. 1058 + 276 = 1334 is exact only at a
  1440px viewport with overlay scrollbars.
- 2026-08-19 — **The zoom buttons' accessible name repeats the visible abbreviation.** — An
  adversarial review found the visible `8 wks` was not contained in the accessible name
  `Show 8 weeks at a time`, failing WCAG 2.5.3 Label in Name (Level A) for speech input. The visible
  abbreviation is kept for design fidelity, so the accessible name now leads with it.
- 2026-08-19 — **Axis tick labels are `aria-hidden`; the `TODAY` badge is not.** — The ticks are a
  purely visual scale and every bar and pill already states its own dates, so exposing up to ~158 bare
  date fragments only obstructed screen-reader users. The badge stays exposed because it is the only
  accessible representation of the today marker.
- 2026-08-19 — **The label column stays 276px at all viewports.** — An earlier revision narrowed it to
  232px below 1100px, which ellipsized "Progress - 2026". The track absorbs the loss instead; it
  scrolls either way.
- 2026-08-19 — **The edge masks are inset by the measured scrollbar thickness.** — The masks are
  positioned on `__frame`, whose padding box extends under the scroll container's scrollbars, so a
  `bottom: 0` mask painted a white notch across the horizontal bar where it crossed the label-column
  edge. Thickness is platform-dependent (0 for overlay bars, 11-15px for classic ones), so
  `schedule.js` measures both axes into `--sched-hbar` / `--sched-vbar`. A second `ResizeObserver`
  watches the **canvas**, not the scroll container: the container's height is capped by `max-height`,
  so it never reports content growth (a web font swapping in) that could make a vertical bar appear
  after boot. That observer only re-measures the bars, which affects no layout, so it cannot feed
  itself. **Correction:** this observer was originally justified by an apparently stale `--sched-vbar`
  on the four-phase fixture. That reading was a harness artifact — the verification browser pane does
  not deliver `ResizeObserver` or `requestAnimationFrame` callbacks, so the DOM was simply stale
  relative to the viewport. The observer is kept as defence for the font-swap case, which is real, but
  no such defect was ever observed.
- 2026-08-19 — **DOM is cleared through jQuery so jQuery UI releases its tooltip.** — QA reported the
  `ui-tooltip ui-corner-all ui-widget-shadow ui-widget ui-widget-content` div sticking to the screen
  after zooming with `Cmd` + scroll over a card. Cause: `clear()` used native `removeChild`, which
  bypasses jQuery UI's `$.cleanData` override and therefore the `remove` handler the widget relies on
  to close a delegated tooltip. Zoom-by-wheel guarantees the cursor is over a bar, so the hovered
  element was destroyed under an open tooltip. Rejected `$(document).tooltip('close')` in `render()`:
  it treats the symptom, assumes the widget stays bound to `document`, and would silently stop working
  if `global.js` changed. Rejected dropping the `title` attributes: they are how a truncated pill
  label is read. See `design.md` §10 — this hazard applies to any dynamically rebuilt DOM in MARLO.
- 2026-08-19 — **Changing the zoom keeps the user's position instead of jumping to today.** — Every
  zoom change called `centreOnToday()`, so a user reading, say, early 2025 was thrown back to today
  and had to scroll out again after each step. The buttons now pin the day at the centre of the
  viewport, and `Ctrl`/`Cmd` + wheel pins the day under the pointer, which is what a scale control is
  expected to do. Returning to today stays available and explicit through the `Today` button. The
  resize path already worked this way, so both now share `dayAtTrackOffset` / `renderAnchored`; note
  that `renderAnchored` must apply the **post**-render half-width when re-centring, because a resize
  changes `trackWidth` while a zoom does not. Position is preserved exactly except where the timeline
  end clamps `scrollLeft`, which is unavoidable.
- 2026-08-19 — **The scroll container's `:focus` ring was removed.** — Requested; the browser default
  ring still marks keyboard focus on `tabindex="0"`, and MARLO's only universal focus reset is scoped
  to `.loginForm` (`customLogin.css:293`), so the indicator survives for WCAG 2.4.7.
- 2026-08-19 — **`?html` is removed from the payload; auto-escaping covers the attribute layer.** —
  Running the card in Tomcat produced a FreeMarker `ParseException` at `dashboard.ftl:181` and a blank
  dashboard. The premise recorded earlier in this spec, that the app has no auto-escaping, was wrong:
  Struts 6.8.0 turns it on inside `super.createConfiguration`, which `APFreemarkerManager` calls. See
  `design.md` ADR-7. The verification harness had configured FreeMarker without an output format,
  which is why it could not reproduce the failure; it now mirrors the Struts configuration and a
  control run with `?html` reinstated reproduces the exact error.
