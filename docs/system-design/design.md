# MARLO — System Design (UI/UX Blueprint)

**Status:** Living document. Version: 1.0 (Constitutional Baseline).
**Owner:** IBD Team — Alliance of Bioversity International and CIAT.
**Last Updated:** 2026-04-30.
**Related:** [docs/prd.md](../prd.md), [docs/detailed-design/detailed-design.md](../detailed-design/detailed-design.md), [reports/ai-context/frontend-composition-map.md](../../reports/ai-context/frontend-composition-map.md).

> This document defines MARLO's **UI/UX system**: how pages are composed, how forms behave, what visual and interaction primitives exist, and what the platform commits to in terms of consistency and accessibility. It is *not* a brand book; it is the rule set the codebase already follows and that future contributions must respect.

---

## 1. Product Experience Principles

These are the experience principles that guide every UI decision in MARLO. They are derived from the platform's role as a multi-stakeholder, evidence-driven research-management tool.

1. **Evidence over decoration.** The interface exists to capture and surface auditable data. Visual flourish must never obscure the data, the validation state, or the audit trail.
2. **Phase-aware by default.** Every screen that touches phased data MUST make the active phase explicit and MUST signal when an action will replicate forward. Users should never lose data because they did not realize they were in a different phase.
3. **Quality is part of the workflow.** QA feedback is a peer of the data, not a separate tool. Validation messages and reviewer comments live next to the field they describe.
4. **Forgiving but auditable.** Save states are explicit (`Save` vs. `Save & Continue`). Drafts are common. The audit log captures what changed — the UI captures who is about to change it.
5. **Multi-tenant clarity.** A user who works across more than one Global Unit (CRP / Platform / Center) MUST always see *which* one they are operating on, in the page chrome.
6. **Accessibility as a constraint, not a feature.** WCAG 2.1 AA is the floor; new components MUST be keyboard-navigable and announce state changes.
7. **Stable navigation.** Major menus are predictable across releases. Adding a section is a constitutional event; renaming or moving one is a notice-required change.
8. **Internationalization-first.** No string lives in a `.ftl` or `.java` file. Every label, message, tooltip, and validation hint is i18n-keyed.
9. **Progressive disclosure.** Long forms (POWB, AR, OICRs) use accordions, expandable blocks, and stepwise sections to keep cognitive load manageable.
10. **Power BI is part of the experience.** When analytical context is more useful than a transactional view, link out to or embed the relevant dashboard rather than rebuild it inside the form.

---

## 2. Information Architecture

MARLO's information architecture mirrors the lifecycle of a research program.

### 2.1 Top-level zones

1. **Home / Dashboard** — landing page per Global Unit, with active-phase summary and quick links to current cluster work.
2. **Impact Pathway** — strategic structure: SLOs, IDOs, outcomes, cross-cutting issues, country collaborations.
3. **POWB (Plan of Work and Budget)** — annual planning per cluster + program-level synthesis (Financial Plan, Management Governance, etc.).
4. **Projects / Clusters** — operational layer: Description, Partners, Locations, Activities, Deliverables, Innovations, OICRs, Studies, Highlights, Case Studies.
5. **Funding Sources** — donor and budget-line management, linked to deliverables and clusters.
6. **Quality Assurance (QA)** — feedback queue, validation states, dashboards.
7. **Annual Report (AR)** — narrative + indicator synthesis per cluster + program-wide.
8. **Synthesis / Summaries** — cross-cutting aggregations (CRP indicators, MELIA, governance, communications).
9. **Publications / Studies / TIP** — specialized output flows (publications, expected studies, technology innovation profile).
10. **Business Intelligence (BI)** — dashboard hub (`/bi/{crp}/bi`).
11. **AI** — entry points to text mining, report generator, chatbot.
12. **Admin** — Program-level configuration: portfolios, partners, roles, locations, parameters, specificities, notifications, email tracking, bulk replication, system messages.
13. **Super Admin** — System-level governance: Global Units, phases, system parameters.

### 2.2 Multi-tenant scoping

URLs are scoped by `{crp}` (the Global Unit acronym). Examples:

- `/projects/{crp}/description.do`
- `/powb/{crp}/financialPlan.do`
- `/annualReport/{crp}/melia.do`
- `/admin/{crp}/portfolioManagement.do`
- `/bi/{crp}/bi.do`
- `/api/v2/...` — Spring MVC REST (out of `.do` namespace).

`{crp}` validation is enforced by interceptors (`validCrp`, `validSessionCrp`).

### 2.3 Phase context

Every phased screen renders the **active phase chip** in the page header (e.g., `POWB 2026`, `AR 2025`, `UpKeep 2026`). The chip exposes a phase switcher when the user has rights to view past phases (read-only) or work in future phases.

---

## 3. Primary User Flows

### 3.1 Annual planning flow (Cluster Coordinator)

1. Land on Home Dashboard → see active POWB phase chip and incomplete sections list.
2. Open Project Description → fill required fields, save.
3. Open Project Partners → add partner institutions (CLARISA-backed selector).
4. Open Activities → register activities with timelines and partner ownership.
5. Open Deliverables → add deliverables with metadata, expected dissemination, and Open Access intent.
6. Open Innovations → register innovations with Scaling Readiness 0–9.
7. Open OICRs → register outcome impact case reports with maturity classification.
8. Submit cluster plan → status moves to *Awaiting QA*.

### 3.2 Annual reporting flow (Cluster Coordinator)

1. Phase chip shows AR 2025.
2. Open Project Description (Reporting view) → confirm or update fields.
3. Open Deliverable → upload the deliverable artifact / DOI / dissemination evidence.
4. Open OICR → mark maturity, attach evidence.
5. Open AR Synthesis sections (MELIA, Governance, Communications, Cross-Cutting).
6. Submit → QA reviewers receive items in their queue.

### 3.3 QA flow (QA Reviewer)

1. Open QA queue → filter by cluster + section + status.
2. Open item → review fields side-by-side with previous-phase values when available.
3. Add structured feedback comments (one comment can be field-scoped).
4. Mark item *Validated* / *Needs revision*.
5. QA dashboard refreshes (≤ 30 minutes during active periods).

### 3.4 PMU synthesis & report generation

1. Open BI hub → cluster completeness dashboard.
2. Use AI Reports Generator → produce a cluster-specific narrative grounded in MARLO data.
3. Edit, validate, export the donor-ready report.

### 3.5 Public consumption

1. Public reader visits embedded dashboards (Power BI JS API).
2. Drill-through links resolve to other public dashboards or to public-safe MARLO views.

---

## 4. Screen Inventory

This is the *constitutional* set of top-level views. Each lives under `marlo-web/src/main/webapp/WEB-INF/crp/views/<area>/` (or `WEB-INF/global/...` for shared screens). Module specs may add screens, but renaming or removing a screen here is a constitutional event.

### 4.1 Home

- `home/dashboard.ftl` — landing page with active-phase summary.

### 4.2 Impact Pathway

- `impactPathway/outcomes.ftl`
- `impactPathway/sloIndicators.ftl`
- `impactPathway/programImpacts.ftl`
- `impactPathway/crossCuttingDimensions.ftl`

### 4.3 POWB

- `powb/powb_financialPlan.ftl`
- `powb/powb_managementGovernance.ftl`
- `powb/powb_evidence.ftl`
- `powb/powb_crossCuttingDimensions.ftl`
- (legacy) `powb2019/*` — frozen, retained for historical compatibility.

### 4.4 Projects / Clusters

- `projects/projectDescription.ftl`
- `projects/projectPartners.ftl`
- `projects/projectLocations.ftl`
- `projects/projectActivities.ftl`
- `projects/projectDeliverable.ftl`
- `projects/projectExpectedStudy.ftl`
- `projects/projectInnovation.ftl`
- `projects/projectHighLights.ftl`
- `projects/projectCaseStudy.ftl`
- `projects/projectBudgetByPartners.ftl`
- `projects/projectBudgetByCoAs.ftl`
- `projects/projectOutcomes.ftl`

### 4.5 Funding Sources

- `fundingSources/fundingSource.ftl`
- `fundingSources/fundingSourcesList.ftl`
- `fundingSources/fundingSourcesSummary.ftl`

### 4.6 Quality Assurance

- `qualityAssessment/qualityAssessmentList.ftl`
- `qualityAssessment/qualityAssessmentDetail.ftl`

### 4.7 Annual Report

- `annualReport/annualReport_melia.ftl`
- `annualReport/annualReport_governance.ftl`
- `annualReport/annualReport_communications.ftl`
- `annualReport/annualReport_crossCutting.ftl`
- `annualReport/annualReport_evidence.ftl`
- (legacy) `annualReport2018/*` — frozen.

### 4.8 Synthesis & Summaries

- `synthesis/*.ftl` (CRP-level synthesis)
- `summaries/*.ftl` (predefined PDFs/exports)

### 4.9 Studies / Publications / TIP

- `studies/*.ftl`
- `publications/*.ftl`
- `tip/*.ftl`

### 4.10 BI

- `bi/biDashboard.ftl`

### 4.11 AI

- `ai/aiUserIdeas.ftl` (or analogous landing for AI services)

### 4.12 Admin

- `admin/portfoliosManagement.ftl`
- `admin/timelineManagement.ftl`
- `admin/partnersManagement.ftl`
- `admin/usersManagement.ftl`
- `admin/locationsManagement.ftl`
- `admin/parametersManagement.ftl`
- `admin/messagesManagement.ftl`
- `admin/notificationsManagement.ftl`

### 4.13 Super Admin

- `superadmin/*.ftl` — phases, Global Units, cross-program parameters.

---

## 5. Navigation Model

### 5.1 Shell layout

The page shell is composed by global FTL fragments under `WEB-INF/global/pages/`:

- `header.ftl` — brand, user chip, Global Unit selector, phase chip, language selector.
- `main-menu.ftl` — top-level zone navigation (Home, Impact Pathway, POWB, Projects, Funding Sources, QA, AR, BI, AI, Admin).
- `footer.ftl` — version, support email, license info, language switch.
- `breadcrumb.ftl` — context trail (e.g., `Admin > Portfolios > Cluster A`).
- `messages.ftl` / `generalMessages.ftl` — global success/info/error banner.

Module-specific submenus live alongside the views (e.g., `submenu-powb.ftl`, `menu-admin.ftl`).

### 5.2 Composition rules (from `frontend-composition-map.md`)

- **Includes (`[#include]`)** compose the page from layout fragments.
- **Imports (`[#import ... as alias]`)** bring in macro libraries.
- **Macros** are the canonical reusable UI primitives. New raw HTML inside a section MUST first try a macro from `forms.ftl`, `homeDashboard.ftl`, `deliverableMacros.ftl`, `innovationTemplates.ftl`, `studiesTemplates.ftl`, `ARMacros.ftl`, etc.
- **Expandable blocks** follow the *hidden template + indexed fields* pattern documented in `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`.

### 5.3 Linking conventions

- Internal navigation uses Struts action URLs (`/{namespace}/{crp}/{action}.do`).
- AJAX/JSON is reserved for explicitly approved patterns (see `AGENTS.md`).
- BI deep-links use Power BI JS API embedding.

---

## 6. Layout Patterns

### 6.1 Standard form page

```
+---------------------------------------------------------------+
| header.ftl                                                    |
+---------------------------------------------------------------+
| breadcrumb.ftl                                                |
+---------------------------------------------------------------+
| submenu-<area>.ftl  (left or top, area-dependent)             |
+---------------------------------------------------------------+
| messages-<area>.ftl                                           |
+---------------------------------------------------------------+
|                                                               |
|  Section title + active-phase chip + permission state         |
|                                                               |
|  [section content via macros + expandable blocks]             |
|                                                               |
|  buttons-<area>.ftl: [Cancel] [Save] [Save & Continue]        |
+---------------------------------------------------------------+
| footer.ftl                                                    |
+---------------------------------------------------------------+
```

### 6.2 List + detail

Used in QA queues, Funding Sources list, Projects list. Left column = filterable list (DataTables). Right column or detail page = read-only summary or edit form.

### 6.3 Expandable block list

Used for repeated entries (deliverables, innovations, partners, OICRs, comments). One hidden template block + a visible list. JS clones the template, reindexes fields (`items[i].id`, `items[i].name`), and posts a single form. Server reconciles posted IDs against persisted records (delete missing IDs, update existing, create new).

### 6.4 Wizard / step layout

Used for AR/POWB synthesis where the user navigates many sub-sections. The submenu plays the wizard role — each step is a dedicated `.ftl`, but state is shared via the underlying entity (e.g., `powbSynthesis`, `reportSynthesis`).

### 6.5 Dashboard / BI page

Embedded Power BI report inside a host FTL view (`bi/biDashboard.ftl`), parameterized via `BiParameters`.

---

## 7. Design Tokens

MARLO does not have a formal design-token system today. Tokens are de facto encoded across the global stylesheets. The constitutional baseline below documents what *exists*; module specs MUST consume these and not introduce parallel palettes.

### 7.1 Source files

- `marlo-web/src/main/webapp/global/css/global.css` — base typography, layout, brand color usage.
- `global/css/custom.bootstrap.css` — Bootstrap overrides.
- `global/css/custom-forms-min.css` — form input visuals.
- `global/css/customDataTable.css`, `customDataTable-flat.css` — tables.
- `global/css/customChosen.css`, `customInputsFlat.css`, `customLogin.css`, `customTrumbowyg.css`, `custom.pickadate.css` — third-party widget skins.
- `global/css/global-center.css` — centers (CGIAR center) layout overrides.
- `global/css/jquery-ui.custom.css`, `glossary.css`, `legalInformation.css`, `403.css` / `404.css` / `500.css` — auxiliary screens.

### 7.2 Token categories

| Category | Where it lives today | Constitutional rule |
|---|---|---|
| Brand color (primary CGIAR/AICCRA accent) | `global.css` | One primary accent per Global Unit; per-program overrides via `custom/*.css` references. |
| Neutrals (grays / borders) | `global.css` + Bootstrap defaults | Reuse existing scale; do not introduce new ad-hoc grays. |
| Typography | `global.css` (Helvetica / Arial fallback stack, base size ~14px) | Headings driven by Bootstrap heading classes; do not introduce new heading scales without a constitutional update. |
| Spacing | Bootstrap grid (`col-md-*`) + utility classes | Use Bootstrap spacing utilities; avoid inline pixel values. |
| Form controls | `custom-forms-min.css` + `forms.ftl` macros | All inputs MUST go through the `forms.ftl` macros. |
| Tables | `customDataTable*.css` + DataTables configuration | All structured tables use DataTables; raw `<table>` is for trivial layout only. |
| Status colors (success / warn / error / info) | Bootstrap alert / label classes | Reuse Bootstrap semantics; do not re-encode. |
| Iconography | Font Awesome (`bower_components/components-font-awesome`) + flag-icons | Reuse existing icon sets. |

### 7.3 Future token unification

Token unification (a single CSS-variables manifest, or a SCSS-driven token file) is an *open gap* (see §13). Until then, do not refactor tokens incrementally — propose a dedicated `enhancement` spec.

---

## 8. Component Inventory

The constitutional component inventory is defined by `forms.ftl` and the cross-cutting macros under `marlo-web/src/main/webapp/WEB-INF/global/macros/`.

### 8.1 Form macros (`forms.ftl`)

- `input` — single-line text input.
- `textArea` — multi-line text input (often with Trumbowyg rich-text).
- `select` — dropdown (Chosen / Bootstrap-Select backed).
- `checkbox` — boolean control.
- `radioButtonGroup` — grouped radio buttons.
- `datepicker` — pickadate-backed.
- (Specialized macros for indicators, milestones, geolocations are layered on top of these.)

### 8.2 Cross-cutting macros

- `homeDashboard.ftl` — dashboard widgets (deliverables list, OICR list, etc.).
- `deliverableListTemplate.ftl`, `deliverableMacros.ftl` — deliverable rendering primitives.
- `innovationTemplates.ftl` — innovation block rendering.
- `studiesTemplates.ftl` — expected-study rendering.
- `ARMacros.ftl` — Annual Report rendering primitives.
- `usersPopup.ftl`, `allInstitutionsPopup.ftl`, `fundingSourcesPopup.ftl`, `rejectInstitutionPopup.ftl`, `institutionRequestMacro.ftl` — modal pickers.
- `discardChangesPopup.ftl`, `draftMessage.ftl` — guardrails for unsaved-state UX.
- `historyDiff.ftl`, `logHistory.ftl` — audit visualization.

### 8.3 Patterns

- **Auto-save** (`global/js/autoSave.js`) — long forms periodically autosave to draft state.
- **Field validation** (`global/js/fieldsValidation.js`) — client-side validation surfaces errors before submit.
- **Sortable lists** (`global/js/sortableList.js`) — for ordered collections.
- **Expandable blocks** — see §6.3 and `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`.
- **Cytoscape graphs** — `bower_components/cytoscape*` for impact-pathway visualization.
- **Pusher** (`global/js/pusher-app.js`) — real-time notifications channel.
- **Intro.js** — guided tours.

### 8.4 Constitutional rule

New UI patterns MUST first try to extend an existing macro before introducing a new component. New components MUST be added under `WEB-INF/global/macros/` with their own file and an entry under §8 of this document in the next constitution update.

---

## 9. Responsive Behavior

MARLO targets desktop-first, with graceful degradation to tablet. Phone-sized screens are *not* a primary use case for data entry (this is an explicit product decision, given the density of MARLO forms).

### 9.1 Breakpoints

Bootstrap default breakpoints govern layout (`xs`, `sm`, `md`, `lg`). Most production pages target `md` and `lg`.

### 9.2 Rules

- Page chrome (header, menu, footer) collapses to a hamburger menu below `md`.
- Long forms keep desktop column structure; below `md`, columns stack and DataTables become horizontally scrollable.
- BI dashboards are NOT designed for `xs`; users on small screens are routed to the public Power BI URL.

---

## 10. Accessibility Expectations

WCAG 2.1 AA is the constitutional floor.

### 10.1 Mandatory rules

1. Every `input`, `textArea`, `select`, `checkbox`, and `radioButtonGroup` macro renders an associated `<label>`.
2. Color is never the sole indicator of state — use icon + text + color together (e.g., success = green check + "Validated").
3. All interactive elements are keyboard-operable; tab order follows visual order.
4. Modals trap focus and restore it on close.
5. Form-level errors are announced via an alert region (`role="alert"`) tied to the page-level message banner.
6. Field-level errors are tied to their input via `aria-describedby`.
7. Tables convey row/column relationships via `<th scope>`.
8. Non-decorative images (e.g., country flag icons in indicator lists) carry `alt` text.

### 10.2 Audit cadence

Accessibility regressions are caught during QA testing (see PRD §7). A formal automated audit (axe-core or Pa11y) is an open gap.

---

## 11. Dark Mode Behavior

MARLO does NOT ship a dark theme today. The constitutional position is: **light theme only**.

This is a deliberate constraint — adding dark mode is a non-trivial undertaking given the legacy CSS, third-party widget skins (Trumbowyg, Chosen, Pickadate, DataTables), and the tightly coupled stylesheets. Module specs MUST NOT introduce dark-only colors. A future `enhancement` spec under `docs/specs/enhancement/dark-mode/` would be required to change this.

---

## 12. Design Decisions

| Decision | Rationale | Where it shows up |
|---|---|---|
| FTL + jQuery + Bootstrap as the UI stack | Long-running platform with deep institutional muscle memory; reliability beats novelty. | All `*.ftl` views, `global/js/*.js`. |
| Forms-first information architecture | MARLO is data-collection software for research teams, not a content site. | Every operational view. |
| Phase chip in the header | Phase awareness is the most common source of "I lost my data" regressions. | `header.ftl` + per-action context. |
| Expandable blocks for repeated entries | Consistent CRUD pattern for partners, deliverables, comments, etc. | Admin + project sections; see `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`. |
| `forms.ftl` as the single source of form macros | Avoids drift across modules. | Every form view. |
| Embedded Power BI over reinventing dashboards | Leverages existing BI pipeline; respects the Bronze/Silver/Gold separation. | `bi/biDashboard.ftl`, public embed tool. |
| Per-program text via `custom/*.properties` | Programs can rename / re-tone strings without forking. | i18n bundles. |
| Per-program behavior via specificities | Feature flags scoped by Global Unit. | `parameters` + `custom_parameters` tables (see `AGENTS.md`). |
| Discard-changes popup before navigating away | Long forms cannot afford accidental loss. | `discardChangesPopup.ftl`. |
| Auto-save drafts | Reduces blast radius of session timeouts. | `global/js/autoSave.js`. |

---

## 13. Open Gaps / Open Questions

1. **Design tokens are implicit.** No single source-of-truth file declares brand color, neutrals, spacing, typography. Refactoring tokens is deferred to a future `enhancement/design-tokens/` spec.
2. **Component library has no Storybook / catalog.** Discoverability of macros depends on grep + file reading. A static catalog would help onboarding.
3. **Per-program visual identity scope** — beyond strings, what visual customization is allowed per Global Unit (logo, palette)? Constitutional answer is "logo + minor accent only," but scope needs explicit ratification.
4. **Accessibility automation.** No CI-integrated a11y testing today. Adding axe-core or Pa11y to QA is a candidate enhancement.
5. **Dark mode.** Out of scope today; revisit if/when tokens are unified (#1).
6. **Mobile-class data entry.** Out of scope today; revisit if/when MARLO grows a partner program with field-staff data entry needs.
7. **Frontend modernization (FTL → React islands, etc.).** Not committed; tracked as PRD open question §9.2.
8. **Real-time collaborative editing on the same form.** Not in scope; current architecture assumes one editor per cluster section at a time.
9. **Print / export styling consistency.** Pentaho + iText handle exports today; visual parity with the on-screen UI is approximate. A formal export style guide would close the gap.
