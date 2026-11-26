# Local PDF Report Engine — Spike Report

**Spec:** `changes/local-pdf-report-engine` · **Date:** 2026-11-20 · **Verdict:** **Option A is viable — proceed.**

The spike was throwaway code (scratchpad, not committed). This file keeps its evidence and the decisions it
forced, so `design.md` can cite them.

---

## Answer

Handlebars.java 4.5.8 + OpenHTMLtoPDF 1.1.108 reproduce the production PhantomJS output of all three templates
with the **same page count** and text positions within a few points, once six html-pdf/WebKit behaviours are
emulated (table below). No infrastructure change is needed.

| Template | Data | Production (PhantomJS, Linux) | Local engine | Comparison image |
|---|---|---|---|---|
| OICR | realistic sample (all sections filled) | 5 pages | 5 pages | `spike/oicr-phantomjs-vs-local.png` |
| Innovation | minimal (`title`, `id`, `timeCreation`) | 6 pages | 6 pages | `spike/innovation-phantomjs-vs-local.png` |
| Cluster / project | minimal | 7 pages | 7 pages | `spike/project-phantomjs-vs-local.png` |

In each image the top row is production, the bottom row is the local engine.

---

## How the baseline was produced

The baseline is the real production pipeline, not an approximation:

- Docker `node:23` on `linux/amd64` — the base image of `reports-microservice/Dockerfile`.
- `pdf-creator-node@2.3.5` → `html-pdf@3.0.1` → PhantomJS 2.1.1, the versions in `reports-microservice/package-lock.json`.
- Helpers copied from `reports-microservice/src/domain/api/pdf/pdf.module.ts`.
- Options copied from MARLO: A4, portrait, border 0, zoomFactor 1, header 40 mm, footer 30 mm
  (`ReportingSummaryAction.java:7825-7834`).
- Templates read from `report_configurations` id 1 in the local copy `partroute_local` (identical lengths in
  `aiccradb2`, `aiccradb3`, `aiccradb_actsave`).

A first baseline taken with PhantomJS on macOS was **discarded**: it substitutes Helvetica, while the Linux image
has only DejaVu fonts. Fidelity must be judged against the Linux render.

---

## Facts the spike established

| # | Fact | Evidence |
|---|---|---|
| F1 | Production renders text in **DejaVu Sans** (regular + bold); the embedded Montserrat woff2 is never used — PhantomJS cannot read woff2 | `pdffonts` on the Linux baseline: `DejaVuSans`, `DejaVuSans-Bold`; `fc-list` in `node:23` lists only DejaVu; `dpkg -l` in the image: `fonts-dejavu-core 2.37-6` |
| F2 | PhantomJS on Linux maps **1 CSS px ≈ 1 pt**; OpenHTMLtoPDF maps 1 px = 0.75 pt (96 dpi) | "SECTION" 68.17 pt wide in production vs 51.22 pt local at default settings (ratio 1.331) |
| F3 | Setting OpenHTMLtoPDF's dots-per-pixel to 27 (dots-per-point is 26.67) gives 1 px = 1.0125 pt — the closest integer; residual error +1.25 % | Same word 69.15 pt after the change |
| F4 | html-pdf takes `#pageHeader` / `#pageFooter` out of the flow and repeats them; `#pageHeader-1` / `#pageFooter-1` replace them on page 1. The templates' own `@page` and `position: running()` rules are ignored by PhantomJS | OICR template lines 29-52 and its `pageHeader-1` block; baseline page 1 shows the `-1` variants |
| F5 | OpenHTMLtoPDF does not collapse margins of blocks inside an inline `<span>` (WebKit does, via anonymous blocks) | Heading gaps 36.7 pt vs 29.0 pt until `span.section, span.content { display: block }` |
| F6 | OpenHTMLtoPDF renders whitespace around a `<br>` placed between blocks as an extra line | 49 pt vs 36 pt gap; fixed by removing whitespace-only text nodes adjacent to `<br>` |
| F7 | WebKit resolves `height: 100%` on the cover block against the page area; OpenHTMLtoPDF resolves it to `auto` | Cover background only a strip until `.first-page { height: 620px }` |
| F8 | OpenHTMLtoPDF computes the width of an `inline-block` with `letter-spacing` too narrow and wraps it (engine defect) | Project header "CLUSTER SUMMARY" wrapped and clipped; fixed with `white-space: nowrap` on the affected selectors |
| F9 | An auto-layout table with a percentage-width image grows the image column in OpenHTMLtoPDF; WebKit gives it zero min-content | Footer text wrapped in 4 lines vs 3 until `table-layout: fixed` on header/footer tables |
| F10 | Handlebars.java fails when a template references an unregistered helper; Handlebars.js only fails when that branch executes. `pdf.module.ts` registers 17 helpers (`grep -c registerHelper` = 17); `handlebars-helper.ts` holds an unused duplicate of `stringContentCompare` that no file imports | Innovation template failed on `mathOperator` with minimal data in Java, rendered in JS |
| F11 | Templates use helpers `safeEmpty`, `ifEquals`, `ifOr`, `or`, `parseJSON`, `mathOperator`, plus `if` / `each` / `with` / `unless`, `@index`, `@last` and Mustache-style sections | Token scan with Handlebars comments removed |
| F12 | Remote resources the templates load: hosts `marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com` and `prms-file-storage.s3.amazonaws.com` only; `ReportingSummaryAction.java:9850` builds SDG icon URLs on the first host | URL scan of the three templates and the three summary actions |
| F13 | Cold-JVM render time: OICR 3.6 s, innovation 3.5 s, project 0.9 s, most of it fetching ~20 remote images; production PhantomJS took 1.9-2.1 s under amd64 emulation. Local PDF sizes: OICR 552,077 B, innovation 772,151 B, project 50,969 B | Spike timings and `ls -l` of the outputs |
| F14 | Handlebars 4.5.8 pulls `slf4j-api` 2.0.x and `nashorn-core` (with asm 7.3.1); MARLO runs `slf4j-api` 1.7.22 with logback 1.2.13 and asm 9.7.1. PDFBox pulls `commons-logging` 1.4.0 (MARLO: 1.2). MARLO has no PDFBox today | `mvn dependency:tree` in the spike and in `marlo-web` |

---

## Decisions it forces (carried into `design.md`)

- Bundle DejaVu Sans regular + bold (Bitstream Vera licence, redistribution allowed) and map `sans-serif` and
  `Montserrat` to it.
- Dots-per-pixel 27.
- A preprocessing step that reproduces F4–F6, plus a compatibility stylesheet for F7–F9 applied by the local
  engine only — the database templates stay untouched, so the microservice keeps rendering them unchanged during
  the rollout.
- Port all 17 helpers from `pdf.module.ts`, not only the six in use (F10).
- Restrict remote fetches to the F12 hosts and cache them in memory (F13).
- Exclude `slf4j-api`, `nashorn-core` and `commons-logging` from the new dependencies (F14).
