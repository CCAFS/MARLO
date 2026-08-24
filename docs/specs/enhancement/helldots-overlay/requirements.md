# HellDots Comment Overlay — Requirements

**Spec ID:** ENH-HELLDOTS-OVERLAY-001
**Status:** Draft
**Owner:** Kevin Collazos — IBD Team
**Reviewers:** Tech lead, QA lead
**Last Updated:** 2026-08-24
**Related PRD sections:** docs/prd.md — QA and internal feedback loops
**Related System Design sections:** docs/system-design/design.md — global chrome, component inventory
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (data model), §5 (persistence), §6 (security)
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md, reports/ai-context/struts-critical-routing-catalog.md

---

## 1. Overview

MARLO has no way for a tester to report *where* on a page something is wrong. Findings travel as prose in
Jira or chat — "the button in the Schedule card does nothing" — and whoever picks it up has to reconstruct
which page, which viewport, which global unit, which element.

This spec integrates **HellDots** (`npm: helldots`, v0.7.0, MIT, authored in-house), a drop-in comment
overlay that anchors a comment to the exact DOM element a person clicked and captures the screenshot,
the CSS selector, the DOM path and the browser environment alongside it.

Scope is deliberately narrow: an **internal QA and development tool**, mounted only for authenticated users
and only outside production. It does not replace, extend, or interact with MARLO's existing Feedback QA
module (`feedback_qa_comments`), which anchors comments to *form fields* under a role/permission matrix.
The two are separate systems that happen to share the English word "feedback".

## 2. Problem Statement

QA findings against MARLO lose their context between the browser and the tracker. The reporter knows the
page state; the implementer receives a sentence. The gap costs a round-trip per finding — "which project?",
"what screen size?", "can you screenshot it?" — and some findings are never reproduced at all.

The widget already exists and is published. What is missing is the MARLO-side integration: mounting it for
the right people, giving it the signed-in identity, and persisting what it produces somewhere the team can
read.

## 3. In-Scope Requirements

### Functional

- **ENH-HELLDOTS-FN-001** — The overlay MUST mount if and only if both conditions hold: a user is present in
  session (`currentUser??`) **and** the active environment is not production (`!config.production`).
- **ENH-HELLDOTS-FN-002** — The overlay MUST receive the signed-in user's display name and numeric id from the
  MARLO session, passed as `user: { name, id }`.
- **ENH-HELLDOTS-FN-003** — On mount, the overlay MUST load the comments belonging to the current
  `location.pathname` via `onReady` → `loadComments()`.
- **ENH-HELLDOTS-FN-004** — A "Copy link" URL pointing at a comment outside the current page's set MUST resolve:
  `onCommentRequested(id)` fetches that single comment and loads it.
- **ENH-HELLDOTS-FN-005** — The inbox MUST be able to list the full corpus across pages, not only the current page.
- **ENH-HELLDOTS-FN-006** — Every mutation reported by `onChange` MUST be persisted to the MARLO backend, and
  events carrying `origin === "host"` MUST NOT be forwarded (echo-loop guard).
- **ENH-HELLDOTS-FN-007** — Screenshots MUST be uploaded via `transformScreenshot` and stored as files on disk;
  the persisted comment record holds a URL, never a base64 data URL.
- **ENH-HELLDOTS-FN-008** — Comment deletion MUST be a soft delete (`is_active = 0`), consistent with MARLO
  convention; no row is removed.
- **ENH-HELLDOTS-FN-009** — The record MUST retain the global unit the reporter was working in at creation time.

### Non-functional

- **ENH-HELLDOTS-NF-001** — The widget bundle MUST be self-hosted from MARLO's own static assets, version-pinned
  in the filename. No third-party CDN request at page load.
- **ENH-HELLDOTS-NF-002** — The backend MUST NOT trust author identity from the client payload. `author_user_id`
  and `author_name` are taken from the Shiro session on write and overwrite whatever the payload asserts.
- **ENH-HELLDOTS-NF-003** — Edit and delete MUST verify that the session user is the comment's author, or holds a
  MARLO admin role. The controller is Spring MVC and therefore has no `BaseAction`, so `canAccessSuperAdmin()` is
  not reachable; the predicate is evaluated from the session user resolved via `APConstants.SESSION_USER`.
  The exact role predicate is fixed in task T06.
- **ENH-HELLDOTS-NF-004** — Screenshot upload MUST validate MIME type against `{image/jpeg, image/png}`, enforce
  a 5 MB per-image cap, and generate the stored filename server-side. The cap is a HellDots-specific constant,
  not `file.maxSizeAllowed.bytes`: no Java in this repository reads that property, so there is no getter to
  call, and MARLO's general document cap is orders of magnitude larger than any capture the widget produces.
- **ENH-HELLDOTS-NF-005** — The endpoints MUST refuse requests when the active Spring profile is production, as
  defence in depth beyond the DispatcherServlet not being registered there.
- **ENH-HELLDOTS-NF-006** — User-supplied values MUST NOT be interpolated inside a `<script>` block in FTL.
  Auto-escaping is active under Struts 6.8, so an apostrophe in a display name would corrupt the JS literal.
- **ENH-HELLDOTS-NF-007** — All new Java files carry the GPL header; code passes `mvn checkstyle:check`
  (2-space indent, 120 columns, braces on same line).
- **ENH-HELLDOTS-NF-008** — Schema changes ship as a Flyway migration under
  `marlo-web/src/main/resources/database/migrations/` with the mandated naming.

## 4. Out-of-Scope

- Any change to the existing Feedback QA module (`feedback_qa_comments`, `feedback_roles_permissions`,
  `feedbackManagement.ftl`). No migration between the two systems.
- Exposing the overlay in production, under any flag.
- A MARLO-native admin screen for HellDots comments. The widget's own inbox is the interface.
- Object storage (S3). MARLO has no AWS SDK and no bucket; disk storage via `FileManager` is the target.
  The `transformScreenshot` contract makes a later swap a change to one endpoint body.
- Real-time multi-user sync (websockets). Comments refresh on page load.
- Role- or global-unit-based gating of the widget. Non-production plus authenticated is the whole gate.
- Localisation of the widget UI beyond the `en` locale it ships with.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| **IBD developer** | Receives findings with selector, DOM path, screenshot and environment attached; stops round-tripping for reproduction details. |
| **QA reviewer** | Reports a finding by clicking the thing that is wrong, from inside the app, without leaving the page or writing a description of where it is. |
| **PMU / cluster coordinator** | Unaffected — never sees the widget, which does not mount in production. |
| **External API consumer** | Indirectly affected: mapping the DispatcherServlet to `/api/*` makes the existing `org.cgiar.ccafs.marlo.rest` tree reachable there in non-production environments. See ENH-HELLDOTS-OQ-001. |

## 6. Acceptance Criteria

- **AC-001 (FN-001)** — *Given* `marlo.production=false` and a signed-in user, *when* any MARLO page renders,
  *then* the HellDots toolbar is present. *Given* no session user, *then* no HellDots script tag is emitted at all.
- **AC-002 (FN-001)** — *Given* `marlo.production=true`, *when* a page renders, *then* no HellDots asset is
  requested and no `#helldots-config` element exists in the DOM.
- **AC-003 (FN-002, NF-006)** — *Given* a user whose display name contains an apostrophe, *when* the page renders,
  *then* the name reaches the widget intact and no JavaScript error is thrown.
- **AC-004 (FN-003)** — *Given* three comments stored for `/dashboard.do`, *when* that page loads, *then* three
  markers are anchored and `loadComments` reports `orphaned: 0`.
- **AC-005 (FN-004)** — *Given* a copied link to a comment created on a different page, *when* it is opened,
  *then* the inbox opens on that comment rather than reporting it missing.
- **AC-006 (FN-006)** — *Given* the host calls `setCommentStatus()` in code, *when* the resulting event fires,
  *then* no HTTP request is issued (`origin === "host"`).
- **AC-007 (FN-007)** — *Given* a comment is created with auto-capture on, *when* it is persisted, *then* the
  stored `payload` contains an image URL and no `data:` string, and the file exists on disk.
- **AC-008 (NF-002)** — *Given* a forged request whose payload claims a different `authorId`, *when* it is
  processed, *then* the stored `author_user_id` is the session user's id.
- **AC-009 (NF-003)** — *Given* user A's comment, *when* user B (non-admin) attempts to edit or delete it,
  *then* the request is rejected and the record is unchanged.
- **AC-010 (NF-004)** — *Given* an upload whose content type is not JPEG or PNG, or which exceeds the 5 MB cap,
  *when* it is posted, *then* it is rejected; the widget falls back to the data URL and reports
  `onError(err, "transform")` without losing the comment.
- **AC-011 (FN-008)** — *Given* a deleted comment, *when* the table is inspected, *then* the row is present with
  `is_active = 0`.
- **AC-012 (NF-008)** — *Given* a clean local database, *when* the app starts, *then* the Flyway migration applies
  and both tables exist.

## 7. Constitutional Compliance Checklist

| Rule | Status |
|---|---|
| 1. Phased data is forward-only | **Not applicable.** HellDots comments are not phased data; they annotate the UI, not reporting content. No `id_phase` column. |
| 2. Save pipeline pattern for critical sections | **Not applicable.** This is not a critical reporting section; there is no Struts Action, no `validate()`, no Validator. Persistence is a Spring MVC controller over a Manager. |
| 3. Spring MVC owns `/api/*` | **Honored, and repaired.** No new Struts `*.json` path. The spec adds the missing `dispatcher.addMapping("/api/*")` so the rule becomes true in practice — see Decision Log 2026-08-24 (b). |
| 4. Specificities via `parameters` + `custom_parameters` | **Not applicable.** Gating is environment-based, not per global unit. No new specificity constant. |
| 5. Flyway migrations | **Honored.** ENH-HELLDOTS-NF-008. |
| 6. GPL header on new Java files | **Honored.** ENH-HELLDOTS-NF-007. |
| 7. Code style / Checkstyle gate | **Honored.** ENH-HELLDOTS-NF-007. |
| 8. English only in code; i18n for user-facing strings | **Honored.** Widget strings belong to the library, not to `global.properties`; the widget is never seen by end users. |
| 9. Branch from `staging`, never commit to `main` | **Honored.** See `task.md` front matter. |
| 10. Java 17 run scripts | **Honored.** `scripts/run-marlo-java17.sh`. |
| 11. Dependency baseline | **Honored.** No `pom.xml` change. The widget is a static asset, not a Maven dependency. |
| 12. No credential files committed | **Honored.** Local path fixes go in the gitignored `marlo-dev.properties`. |

## 8. Open Questions

- **ENH-HELLDOTS-OQ-001** — Mapping the DispatcherServlet to `/api/*` makes the whole
  `org.cgiar.ccafs.marlo.rest` controller tree reachable under `/api/*` in addition to `/swagger/*`, in
  non-production environments. Confirm with the tech lead that this is the intended state and not something
  deliberately disabled during the Struts 6 / SpringDoc migration.
- **ENH-HELLDOTS-OQ-002** — `page` is `location.pathname` only. MARLO carries state in the query string
  (`?projectID=123&phaseID=4`), so every project shares one `page` value. Is per-pathname grouping acceptable
  for a QA tool, or does the inbox need query-aware grouping?
- **ENH-HELLDOTS-OQ-003** — Retention. Nothing prunes comments or orphaned screenshot blobs today. Decide a
  policy before the corpus is large enough for it to matter.

## 9. Decision Log

- **2026-08-24 (a)** — Scope limited to an internal QA/development tool, gated on non-production plus an
  authenticated session. *Rationale:* the smallest gate that delivers the value, with no exposure to end users,
  no moderation surface, and no permission matrix to design.
- **2026-08-24 (b)** — Add `dispatcher.addMapping("/api/*")` in `WebAppInitializer`. *Rationale:* CLAUDE.md rule 3
  states Spring MVC owns `/api/*`, and `struts.xml` already excludes the prefix, but the DispatcherServlet is
  mapped only to `/swagger/*` on both this branch and `staging` — so `/api/*` currently resolves to nothing.
  The alternative, a Struts `*.json` action, would deviate from rule 3 and require its own deviation entry.
  Change is confined to the existing `!production` block.
- **2026-08-24 (c)** — Hybrid schema: indexable columns plus a `json` payload column. *Rationale:* keeps SQL
  reporting possible while letting `schemaVersion`-stamped additions in future HellDots releases land without a
  migration. Full normalisation would need a migration per library release; a single opaque blob would make the
  data unqueryable.
- **2026-08-24 (d)** — Screenshots on disk via `FileManager`, not in the database and not in S3. *Rationale:*
  reuses the storage path MARLO already uses for deliverables and highlights. MARLO has no AWS SDK, no bucket
  and no credential plumbing; adding them for a non-production tool is disproportionate. `transformScreenshot`
  isolates the choice to one endpoint body, so S3 remains a later swap.
- **2026-08-24 (e)** — One event endpoint with upsert semantics rather than granular REST. *Rationale:* replies,
  reactions and history live inside the JSON payload regardless, so six endpoints would all write the same
  column. One `switch (event.type)` covers all ten event types.
- **2026-08-24 (f)** — User identity passed through `data-*` attributes, not interpolated into a `<script>` block.
  *Rationale:* FreeMarker auto-escaping is active under Struts 6.8; `${...}` inside JS emits HTML entities and
  corrupts any name containing an apostrophe. Attribute escaping is undone by the browser on `dataset` read.
  `?html` is not an option — it is a parse error in this version.
- **2026-08-24 (g)** — First delivery targets the local environment and the local MySQL database
  (`mysql.host=localhost`, `mysql.database=aiccradb1`) for testing, before any shared environment.
