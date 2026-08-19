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

- **ENH-SCHED-FN-001** — The card MUST render one lane per **open or upcoming** reporting phase, with
  a bar positioned by real dates. Closed phases MUST NOT be rendered.
- **ENH-SCHED-FN-002** — Each phase lane MUST show the phase name, its full date range, and a
  countdown pill in the sticky label column (not at the bar's right edge, which scrolls out of view).
- **ENH-SCHED-FN-003** — The countdown MUST read `N days left` while open, `Last day` on the end
  date, `Past its end date` for a phase reopened beyond its end date, and `Opens in N days` /
  `Opens tomorrow` for an upcoming phase.
- **ENH-SCHED-FN-004** — Platform-wide timeline activities (`Timeline`, scoped to the current global
  unit) MUST be packed into exactly **three** reserved lanes by greedy first-fit, ordered by start
  date with the `order` field as tiebreaker.
- **ENH-SCHED-FN-005** — Activities that do not fit MUST appear as `+N more` chips in a dedicated
  overflow strip, never inside the three packed lanes. Clicking a chip MUST open a popover listing
  those activities with name, date range and status.
- **ENH-SCHED-FN-006** — An activity whose start equals its end MUST render as a single-day milestone
  capsule wide enough to read its label, not a zero-width bar.
- **ENH-SCHED-FN-007** — Zoom MUST offer 2 / 4 / 8 / 16 visible weeks, defaulting to 8, and MUST
  recompute the lane packing — not stretch the DOM. Modifier + wheel MUST step the zoom.
- **ENH-SCHED-FN-008** — The zoom choice MUST persist per browser via `localStorage`.
- **ENH-SCHED-FN-009** — A single vertical today marker MUST span all lanes, with a `TODAY` badge on
  the axis row aligned to the same offset.
- **ENH-SCHED-FN-010** — "Jump to today" MUST re-centre the track; any zoom change MUST also re-centre.
- **ENH-SCHED-FN-011** — The footer MUST report the packing honestly: the window size, the span of
  the rendered range, and how many of the total activities were placed.
- **ENH-SCHED-FN-012** — When no phase is open, the card MUST render a designed zero-state naming the
  next phase and its dates, not an empty or broken component.
- **ENH-SCHED-FN-013** — The card MUST remain behind the `homepage_timeline_active` specificity.

### Non-functional

- **ENH-SCHED-NF-001** — The lane region MUST stay ~272px (250–290px) with three phase lanes, three
  activity lanes and the overflow strip, and MUST NOT grow with activity count. Verified at 20, 40
  and 400 activities.
- **ENH-SCHED-NF-002** — The visible track width MUST be measured at runtime. No fixed pixel track
  width is permitted, because `global.css` forces `.container` to `95% !important` below a 1300px
  viewport.
- **ENH-SCHED-NF-003** — The label column MUST stay 276px at every viewport; it is a content minimum
  (`Progress - 2026` plus its `Opens in 44 days` pill must not clip).
- **ENH-SCHED-NF-004** — WCAG 2.1 AA throughout. No white text on the light brand blue or green. No
  status conveyed by colour alone. 12px is the type floor.
- **ENH-SCHED-NF-005** — Activity descriptions are free text typed by users and nothing in this app
  auto-escapes. The FTL→JS payload MUST be escaped for both the JSON and the HTML-attribute layer,
  and JavaScript MUST write activity text with `textContent` only.
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
- **AC-004** (FN-006) — Given an activity with start equal to end, then it renders as a dashed capsule
  approximately 178px wide with its label legible.
- **AC-005** (FN-007, FN-010) — Given any zoom stop, when it is selected, then the packing is
  recomputed, the track re-centres on today, no pill escapes the track, and no label clips mid-word.
- **AC-006** (FN-008) — Given a user picks 16 weeks and reloads, then the card renders at 16 weeks.
- **AC-007** (FN-009) — Given today falls inside the rendered range, then the today line's offset
  equals the `TODAY` badge's offset exactly.
- **AC-008** (NF-001) — Given 20, 40 and 400 activities, then the lane region height is identical.
- **AC-009** (NF-002) — Given a 1024px viewport where `.container` is 95% wide, then nothing overflows
  the card and the page body does not scroll horizontally.
- **AC-010** (NF-005) — Given an activity described as `<script>…</script>`, then no script executes,
  no element is created from the markup, and the text is shown literally.
- **AC-011** (FN-012) — Given no open phase, then the zero-state renders with the next phase named and
  a "Browse closed phases (N)" action, and no notification affordance.
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
| 8. English only; user-facing strings i18n-keyed | Honored — 44 keys under `dashboard.schedule.*`. |
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
- 2026-08-19 — **Phase bars render only `In progress` or `Upcoming`; no completion percentage.** — The
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
