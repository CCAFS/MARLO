# Homepage Schedule Card — Design

**Spec ID:** ENH-HOMEPAGE-SCHEDULE-001
**Status:** Implemented
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-19
**Implements requirements:** ENH-SCHED-FN-001 … FN-013, ENH-SCHED-NF-001 … NF-007
**Touches modules:** marlo-web

---

## 1. Architecture Summary

The card is split by **what the server can know**. The server knows dates; it cannot know
pixels-per-day, because that depends on the measured width of the track and on the selected zoom
stop. So every date-only concern is server-rendered and every scale-dependent concern is drawn by
JavaScript from a single JSON payload.

```
DashboardAction.prepare()
  └─ loadScheduleActivities()  → TimelineManager.findAllByGlobalUnit(loggedCrp.id)
                                 null-guarded, sorted by (order nullsLast, id)
                                        │
dashboard.ftl                           ▼
  ├─ derives phase status from `editable` + dates; keeps open + not-started only
  ├─ renders the shell: header, legend, zoom control, section headers,
  │  footer templates
  └─ emits ONE payload:  data-schedule="{today, months[], activities[]}"
                                        │
schedule.js (ES5 IIFE, DOMContentLoaded)▼
  ├─ measures trackWidth = scroll.clientWidth − var(--sched-label)
  ├─ pxPerDay = trackWidth / (weeks × 7)     ← the only scale variable
  ├─ packLanes(): greedy first-fit → 3 lanes + overflow chips
  └─ paints axis ticks, gridlines, today line, bars, pills, chips, counts, footer
```

Consequence: all 35 user-facing strings stay in `global.properties`. JavaScript receives only
`{0}`-style templates through `data-*` attributes, the idiom already used at `dashboard.ftl` →
`dashboard.js`.

## 2. Module Footprint

### marlo-web

- **New:** `webapp/crp/js/home/schedule.js`
- **Modified:** `webapp/WEB-INF/crp/views/home/dashboard.ftl` — the `homepage_timeline_active` block
  replaced (was the month-percentage Gantt); the dead hardcoded 2016/2017 `[#assign timeline = [...]]`
  sequence deleted; `schedule.js` registered in `customJS`; cache-bust query strings bumped.
- **Modified:** `webapp/crp/css/home/dashboard.css` — the `.reportTimeline` block replaced by
  `.scheduleCard`.
- **Modified:** `webapp/global/css/marlo-redesign.css` — four tokens added to the single `:root`.
- **Modified:** `java/org/cgiar/ccafs/marlo/action/home/DashboardAction.java` — `TimelineManager`
  injected; `loadScheduleActivities()`; `getScheduleActivities()`.
- **Modified:** `webapp/crp/js/home/dashboard.js` — `initReportTimeline()` and its call removed.
- **Modified:** `resources/global.properties` — `dashboard.reportingTimeline.*` (17 keys) replaced by
  `dashboard.schedule.*` (35 keys); `timelineManagement.help` reworded.

### marlo-data

No change. `Timeline`, `TimelineManager` and `TimelineMySQLDAO` are used as they are.

## 3. Data Model Changes

**Not applicable — no schema change and no Flyway migration.** The card reads two existing shapes:

| Entity | Fields used | Notes |
|---|---|---|
| `Phase` | `composedName` (derived, `name + " - " + year`), `startDate`, `endDate`, `editable` | `startDate`, `endDate` and `editable` are all nullable; the template guards each. |
| `Timeline` | `id`, `description`, `startDate`, `endDate`, `order`, `globalUnit` | `description` is the label — there is no name/title column. `globalUnit` is the only relation, so "platform-wide" means "tied to no project and no phase", not "shared across CRPs". |

Two pre-existing hazards are recorded in `requirements.md` §8 rather than fixed here: rows with
`global_unit_id IS NULL` are invisible to the finder, and the hbm/DDL nullability of the date columns
disagree.

## 4. API / Action Surface

**No new action, no new endpoint.** Phases already reach the view as `${phases}` via
`BaseAction.getPhases()`. Activities reach it as `${scheduleActivities}` from a new getter on
`DashboardAction`.

Two caller-less legacy JSON endpoints (`getTimelineInformation.do`,
`getTimelineWeeksParameter.do`) were deliberately **not** revived: a value-stack getter gives a
synchronous first paint with no loading state, and CLAUDE.md rule 3 discourages new `*.json` paths.

## 5. Frontend Composition

### DOM contract

One scroll container holds a `max-content`-width canvas of flex rows. Each row is a sticky label cell
plus a track cell whose width JavaScript sets to `totalDays × pxPerDay`.

```
.scheduleCard[data-schedule]
├─ __head               title, subtitle (date + activity count), 4-item legend
└─ __layout             flex row; becomes display:block at 1250px and below
   ├─ __main            the whole timeline. No border or padding of its own —
   │  │                 __frame is already a bordered box. Also the positioning
   │  │                 host for __popover, so the popover cannot drift over the
   │  │                 panel beside it.
   │  ├─ __controls     4 zoom buttons (aria-pressed) + Today
   │  ├─ __frame        1px border, radius, overflow hidden, edge masks
   │  │  │              (masks inset by --sched-hbar / --sched-vbar so they
   │  │  │               never veil a scrollbar; schedule.js measures both)
   │  │  └─ __scroll    overflow auto, max-height 388px, tabindex=0, aria-label
   │  │     └─ __canvas position relative, width max-content
   │  │        ├─ __gridLayer  z 0   absolute, left var(--sched-label)
   │  │        ├─ __nowLayer   z 2   the 2px today hairline
   │  │        ├─ __row--axis  z 4   sticky top; ticks + TODAY badge
   │  │        ├─ __row--section     TIMELINE ACTIVITIES + count
   │  │        ├─ __row--lane   ×3   36px; label col + pill track
   │  │        └─ __row--overflow    32px; OVERFLOW + chip track
   │  ├─ __foot         window/span (left), placed/hint (right)
   │  └─ __popover      z 20, appended by schedule.js
   └─ __next            300px aside, margin-top 44px so its top edge meets
                        __frame's rather than the controls row's; next activity,
                        else next phase, else absent
```

**Stacking contract.** `.scheduleCard__canvas` is the only stacking context. Order: gridlines 0,
section-header tints (positioned, `z-index: auto`, later in tree order), bars/pills 1, today line 2,
sticky axis row 4, edge masks 6, popover 20. **No row may carry a `z-index`** — a row with one becomes
a stacking context and stops the sticky axis row winning over it. This is commented in the stylesheet
at the top of the section.

There is no label column any more (`--sched-label: 0px`), so the track starts at the frame's left
edge. The axis row is still `position: sticky; top: 0`.

### Height budget

`3 × 36` lane + `32` overflow = **140px**, which is the number ENH-SCHED-NF-001 holds. Adding the 32px
axis and one ~28px section header gives ~200px, comfortably inside the 388px `max-height`, so the
scroll container no longer scrolls vertically at all — the cap is now only a backstop. Measured card
height: 375px side-by-side. (Was 272px / 535px while the three phase lanes existed.)

### Zoom and scale

`pxPerDay = trackWidth / (weeks × 7)` is the only scale variable; bars, pills, ticks, the today line
and the packing all derive from it. `trackWidth` is measured, never assumed — see the Decision Record
below. The rendered range spans every phase and activity plus a week either side, snapped to Monday,
clamped to today ±550 days so one outlier date cannot flatten the track.

### Packing

Greedy first-fit over three lanes, iterating activities sorted by `(startDate, order, id)`. Activities
are the only thing plotted; the rendered span is derived from them alone, seeded with today so an
empty list still produces a valid axis. The only
absolute lengths are an 8px gutter, a 6px minimum width, 178px for a milestone capsule (shape only —
`--milestone` must never set a colour, or it outranks the status rules that follow it) with 170px of
reserved label room, and a 190px chip-merge threshold. Because those are pixel constants and the date
axis compresses with zoom, overflow grows at wider windows — expected, and reported in the footer.

Overflow chips live in their own row. This is structural, not cosmetic: a chip's x-position comes from
the displaced activity's start, which is by definition inside the span of the pill that displaced it,
so a chip drawn in a packed lane would always cover a pill.

A lane carries no meaning, so an activity can change lane as the window changes. That is the accepted
price of a fixed-height container.

### FTL → JS boundary

One JSON payload in a **double-quoted** `data-schedule` attribute. Every string passes
`?json_string` for the JSON string layer (it also escapes `<` as `\u003C`, so no `<script` substring
ever reaches the HTML parser). The attribute layer is FreeMarker's own auto-escaping, which is on for
every template Struts renders — `?html` is a **parse error** under that policy, see ADR-7.
Numbers use `?c`.
Dates serialize as `yyyy-MM-dd` and are parsed in JS with an explicit `new Date(y, m-1, d)` from a
split — `new Date("2026-08-19")` is read as UTC and lands a day early west of Greenwich. Per-item
display date ranges are formatted server-side; only zoom-dependent axis labels are assembled in JS,
from a server-provided `months[]` array. Activity text reaches the DOM only via `textContent`.

## 6. Persistence & Phase Replication Plan

**Not applicable.** The card is read-only. It performs no save and no delete, so there is nothing to
replicate forward and no past-phase immutability concern.

## 7. Validation & Save Pipeline

**Not applicable.** No `Action.validate()`, no `Validator`, no manager save chain. Activities continue
to be validated on write by `TimelineManagementValidator`, unchanged.

## 8. Permissions & Edit Gates

No new gate. The card is visible to any user who can reach the homepage; it exposes only phase dates
and platform-wide activity descriptions, neither of which is per-user or per-project data. The card
has no interactive affordance that reaches outside itself.

## 9. Specificity / Feature-Flag Strategy

Reuses the existing `homepage_timeline_active` specificity — no new `parameters` row and no new
`custom_parameters` migration. The gate stays exactly where it was, as the outermost
`[#if action.hasSpecificities('homepage_timeline_active') ]` in the block. `hasSpecificities` is a
session-only boolean parse that returns false on any miss, so a global unit without the parameter
simply does not get the card.

## 10. Integration Points

No CLARISA, CGSpace, BI, AI-services, S3 or Pusher interaction. One cross-component contract remains
— the `#allPhasesToggle` dependency went with the "Browse closed phases" button:

1. **jQuery UI's tooltip widget owns every `title` on the page.** `global.js` initialises it inside
   `$(document).ready`, so the widget element is `document` and every descendant carrying a `title`
   — including this card's phase bars and activity pills — is a *delegated* target. For delegated
   targets the widget tears an open tooltip down from a `remove` handler on the target, reached only
   through jQuery UI's `$.cleanData` override, which jQuery calls from `.empty()`/`.remove()` and
   never from native `removeChild`. Any component that rebuilds DOM carrying `title` attributes must
   therefore remove the old nodes **through jQuery**, or leave an orphaned `.ui-tooltip` div stuck in
   `<body>`. `clear()` in `schedule.js` does this, falling back to the native loop when jQuery is
   absent. This is a repo-wide hazard, not specific to this card.

## 11. Observability

One log line: `loadScheduleActivities()` logs at ERROR through the existing `LOG` if the manager call
throws, and degrades to an empty list so the homepage still renders. There are no new metrics; the
card writes nothing, so there is no audit trail to extend.

## 12. Performance & Scalability

One extra query per homepage render: `findAllByGlobalUnit`, an unindexed scan of a table currently in
the tens of rows. Sorting is in Java on that same small list.

Client cost is bounded by the packing, which is `O(n × 3)` over activities plus an `O(n log n)` sort,
and by the DOM, which is bounded by what fits: at 400 activities only 44–108 nodes are created
because everything else merges into chips. Height and node count were measured at 20, 40 and 400
activities; the lane region stayed 272px in all three. Reflow is debounced at 120ms behind a
`ResizeObserver`, falling back to `window.resize`.

## 13. Security Considerations

The one real surface is the activity `description`: free text from a Timeline Management textarea,
stored raw (client-side `sanitizeInputs()` only does NFKD normalization and strips non-BMP). The
predecessor week grid injected the description straight into `innerHTML` and a `title=` attribute.

FreeMarker auto-escaping **is** on: `APFreemarkerManager.createConfiguration` delegates to
`super.createConfiguration`, and Struts 6.8.0's `FreemarkerManager` ends that method with
`setAutoEscapingPolicy(ENABLE_IF_DEFAULT)` + `setOutputFormat(HTMLOutputFormat.INSTANCE)`,
unconditionally and with no setting to opt out. Every `${...}` in every `.ftl` is therefore escaped
for HTML, which is also why no view needs `[#escape]`.

Mitigation is layered and both layers are load-bearing: `?json_string` plus that auto-escaping on the
way out, and `textContent` on the way into the DOM. Verified by rendering an activity described as
`<script>window.__pwned=1;</script>` — nothing executed, zero `<script>` or `<b>` elements were
created inside the card, and the raw attribute contained no literal double quote and no `<script`
substring.

## 14. Backwards Compatibility & Rollout

No migration, so no forward or backward data step. Rollout is the existing specificity: the card
appears only for global units with `homepage_timeline_active` true. Rollback is a revert of the seven
files; nothing outside them is touched, and no other view referenced `.reportTimeline`,
`initReportTimeline` or `dashboard.reportingTimeline.*`.

The 17 retired i18n keys are safe to remove because no `custom/*.properties` overrides any of them.

## 15. Decision Records

- **ADR-1 — Scale-dependent rendering moves to the client.** Zoom must recompute the packing, not
  stretch the DOM (ENH-SCHED-FN-007), and packing depends on `pxPerDay`. The server cannot know the
  measured track width, so the lane geometry cannot stay in FreeMarker as it was in the interim
  Gantt. Everything date-only stayed on the server, which keeps i18n out of JavaScript.
- **ADR-2 — The track width is measured, not fixed.** `global.css:85` forces
  `.container:not(.loginPage)` to `width: 95% !important` below 1300px. The design's 1058px track plus
  a 276px label column is exact only at a 1440px viewport with overlay scrollbars; hardcoding it
  overflows the card below roughly 1400px. `measure()` reads `scroll.clientWidth` and
  `--sched-label` via `getComputedStyle`, so the two languages never disagree about the column width.
- **ADR-3 — Phase status uses only the fields `Phase` has.** See `requirements.md` Decision Log. Two
  reachable states for phases (`In progress`, `Not started`), three for activities (`Completed`,
  `In progress`, `Not started`); every legend swatch keeps a referent and no figure is invented.
- **ADR-4 — Overflow chips get their own row.** Structural, per §5.
- **ADR-5 — One JSON payload in a `data-*` attribute.** The repo has a working precedent
  (`innovationTemplates.ftl` → `projectInnovations.js`), but it uses a single-quoted attribute, which
  leaves an apostrophe-injection hole. This uses double quotes plus auto-escaping, which covers `'`
  as well as `"`. Rejected: reviving
  `async: false` AJAX (would reintroduce a loading state the value stack makes unnecessary), a hidden
  `<span>` of JSON (interpolates raw), and building a JS literal in an inline `<script>` (no
  precedent in the repo for anything but scalars).
- **ADR-6 — Five new tokens rather than literal colours.** `--marlo-info-solid: #15719B` and
  `--marlo-success-solid: #2E7D32` exist because the light brand `--marlo-info` / `--marlo-success`
  fail AA behind white text; `--marlo-warning-ink: #4A3000` is the dark ink for the amber fill;
  `--marlo-today-line: #C7401C` completes the set. The light hues stay for status dots and legend
  swatches. `--marlo-outline-dashed` was also added but is **gone again**: its only three uses were the
  `Upcoming` swatch, the `Upcoming` bar and the milestone's own border colour, all of which were
  removed, so keeping the token would leave a declaration with no referent.
- **ADR-7 — The attribute layer is auto-escaping, not `?html`.** Struts 6.8.0's `FreemarkerManager`
  finishes `createConfiguration` with `setAutoEscapingPolicy(ENABLE_IF_DEFAULT)` and
  `setOutputFormat(HTMLOutputFormat.INSTANCE)`, unconditionally; `APFreemarkerManager` inherits that
  by calling `super`. Under an HTML output format FreeMarker rejects `?html` at **parse** time
  ("legacy escaping is not allowed ... to avoid double-escaping mistakes"), so the template never
  compiles and the page returns a 500. `${...}` already escapes `<`, `>`, `&`, `"` and `'`, which is
  a superset of what `?html` did, so the fix is to delete the built-in and keep the payload otherwise
  identical. Rejected: `?no_esc` (would emit the raw payload and reopen the injection hole) and
  `?esc` (equivalent, but redundant noise under an already-escaping policy).

## 16. Open Risks

1. **Rows invisible to the finder.** `timeline.global_unit_id` was added nullable with no backfill; the
   card cannot show a row the manager will not return. Needs the DB check in `requirements.md` §8.
2. **Lane instability across zoom.** Accepted, but users who change zoom will see activities move
   between lanes. Mitigated only by the footer stating that lanes carry no meaning implicitly through
   the placement count; if it confuses users in QA, the mitigation is a lane label change, not a
   different algorithm.
3. **Print.** A `@media print` block unclamps the scroll and hides the controls, but with no print
   stylesheet anywhere in MARLO the surrounding page remains unstyled for print.
4. **`?json_string` is new to this repo.** It is used zero other times, so there is no in-repo
   precedent to fall back on if a FreeMarker upgrade changes its behaviour. FreeMarker is pinned at
   2.3.32 in `marlo-parent/pom.xml`.
5. **30 pre-existing `?html` uses in 20 other templates.** Out of scope for this spec, but they are
   the same parse error ADR-7 describes, latent since the Struts 6 upgrade (`a9a2ed2e77`,
   2025-10-27). Any page rendering one of them returns a 500. None of them is in the dashboard's
   include chain.
