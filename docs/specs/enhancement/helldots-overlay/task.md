# HellDots Comment Overlay — Tasks

**Spec ID:** ENH-HELLDOTS-OVERLAY-001
**Status:** Draft
**Owner:** Kevin Collazos — IBD Team
**Last Updated:** 2026-08-24
**Implements design:** docs/specs/enhancement/helldots-overlay/design.md
**Branching:** feature branch from `staging`, named `helldots-overlay`. See Pre-flight PF-02 — the current
checkout is on `feedback-overlay`, which already carries the A2-2398 homepage redesign.
**Target merge:** `staging` (then promoted per release process).

---

## 1. Execution Context

| Item | Value |
|---|---|
| Environment | Local development only for this delivery |
| Run script | `scripts/run-marlo-java17.sh` — HTTP on `localhost:8080`, `cargo:run` |
| Java | 17 (`marlo-parent/pom.xml` → `java.version=17`). `run-marlo-java8.sh` fails with `invalid flag: --release` on this branch. |
| Database | **Local MySQL** — `mysql.host=localhost`, `mysql.database=aiccradb1`, port 3306 |
| Config file | `marlo-web/src/main/resources/config/marlo-dev.properties` (gitignored — never commit) |
| Env flags | `marlo.production=false`, `marlo.debug=true` → the mount gate is open locally |
| Base URL | `http://localhost:8080/marlo-web/` |
| Flyway | `MarloFlywayConfiguration` runs `repair()` before every `migrate()` on startup |
| Checkstyle | `mvn checkstyle:check` must pass before any commit |

Everything below is executed and verified against the local database. No shared environment is touched by
this delivery.

## 2. Pre-flight Checklist

- **PF-01** — `requirements.md` and `design.md` reviewed and approved.
- **PF-02** — Decide the branch. `feedback-overlay` currently holds ~20 commits of unrelated A2-2398 homepage
  work; a fresh `helldots-overlay` branch cut from `staging` keeps the two reviewable apart. Confirm before T01.
- **PF-03** — Confirm the local MySQL `aiccradb1` is reachable and `marlo-web/tomcat/context.xml` exists.
- **PF-04** — **Fix `file.uploads.baseFolder`.** It is `C:/xampp/htdocs/marlo` — a Windows path on macOS.
  Point it at a real local directory that `file.downloads=http://localhost:8080/marlo-web/data` maps to, and
  confirm an existing upload (e.g. a project highlight image) round-trips. Screenshot upload cannot work until
  this is true. Local-only change to a gitignored file.
- **PF-05** — Baseline run: `scripts/run-marlo-java17.sh`, sign in, confirm the app is healthy before changes.
- **PF-06** — Resolve or accept ENH-HELLDOTS-OQ-001 (widening `/api/*`) with the tech lead.

## 3. Task List

### ENH-HELLDOTS-OVERLAY-001-T01 — Vendor the UMD bundle
**Depends on:** PF-02
**Do:** Add `marlo-web/src/main/webapp/global/js/vendor/helldots-0.7.0.umd.js`, unmodified, from
`registry.npmjs.org/helldots/-/helldots-0.7.0.tgz` (`dist/helldots.umd.js`, 180 KB).
**Acceptance:** File present, version in the filename, contents byte-identical to the published artifact.
**Verify:** `shasum` against the tarball entry; the file defines the `HellDots` global.

### ENH-HELLDOTS-OVERLAY-001-T02 — Flyway migration for both tables
**Depends on:** PF-03
**Do:** `V2_6_0_<YYYYMMDD>_<HHMM>__CreateHelldotsTables.sql` with `helldots_comments` and
`helldots_screenshots` exactly as in design §3.
**Acceptance:** Naming matches the mandated pattern; additive only; FKs resolve against `users` and `global_units`.
**Verify:** Start the app; confirm both tables exist in `aiccradb1` and the row in `flyway_schema_history` is
`success = 1`.

### ENH-HELLDOTS-OVERLAY-001-T03 — marlo-data layer
**Depends on:** T02
**Do:** `HelldotsComment` and `HelldotsScreenshot` models, `.hbm.xml` mappings, DAO + MySQL DAO, Manager +
ManagerImpl for both, per module convention.
**Acceptance:** GPL header on every file; 2-space indent; 120 columns; Hibernate maps `payload` as a string-backed
JSON column.
**Verify:** `mvn clean install -DskipTests -pl marlo-data -am` green; `mvn checkstyle:check` green.

### ENH-HELLDOTS-OVERLAY-001-T04 — Map the DispatcherServlet to `/api/*`
**Depends on:** PF-06
**Do:** Add `dispatcher.addMapping(REST_API_REQUESTS);` beside the existing `REST_SWAGGER_REQUESTS` mapping in
`WebAppInitializer.java`, inside the same `!production` block.
**Acceptance:** One line; no other behaviour changed; still inside the non-production guard.
**Verify:** After restart, an existing v2 controller responds under `/api/...`; `/swagger/...` still responds;
Struts pages (`*.do`) are unaffected.
**Note:** This is the change behind risk R-001. Regression-check a few `.do` pages explicitly.

### ENH-HELLDOTS-OVERLAY-001-T05 — Read endpoints
**Depends on:** T03, T04
**Do:** `HelldotsController` with `GET /api/helldots/comments?page=`, `?all=true`, and `/{commentId}`, plus the
DTO that rebuilds the library's `SerializedComment` shape from columns + payload.
**Acceptance:** Response deserialises into `loadComments()` without transformation; production profile is rejected.
**Verify:** `curl` each endpoint with an authenticated session cookie; assert JSON shape against `index.d.ts`.

### ENH-HELLDOTS-OVERLAY-001-T06 — Event endpoint
**Depends on:** T05
**Do:** `POST /api/helldots/events`. `switch (event.type)`: upsert by `comment_id` for the nine mutating types,
`is_active = 0` for `comment:deleted`. Project the indexed columns out of the payload; stamp audit columns.
**Acceptance:** Identity is taken from the Shiro session and **overwrites** payload `authorId`/`author`
(NF-002). Edit and delete require author or an admin role (NF-003) — resolve the session user from
`APConstants.SESSION_USER` and fix the concrete role predicate here, since `BaseAction.canAccessSuperAdmin()`
is not reachable from a Spring MVC controller. Payload size capped. Unknown `event.type` → 4xx.
**Verify:** Unit-test the switch across all ten types. Manually post a forged `authorId` and assert the stored
row carries the session user (AC-008). Attempt a cross-user edit and assert rejection (AC-009).

### ENH-HELLDOTS-OVERLAY-001-T07 — Screenshot upload endpoint
**Depends on:** T03, T04, PF-04
**Do:** `POST /api/helldots/screenshots`. Validate MIME against `{image/jpeg, image/png}` and size against
`file.maxSizeAllowed.bytes`; generate the filename server-side; write with `FileManager.copyFile` under
`config.getUploadsBaseFolder()`; register a `helldots_screenshots` row; return `{ url }` built from
`config.getDownloadURL()`.
**Acceptance:** No client-supplied string reaches a filesystem path. Rejections return 4xx without a stack trace.
**Verify:** Upload a JPEG and fetch the returned URL — it renders. Upload a PDF renamed `.jpg` and a
20 MB file; both rejected (AC-010).

### ENH-HELLDOTS-OVERLAY-001-T08 — Adapter script
**Depends on:** T05, T06, T07
**Do:** `marlo-web/src/main/webapp/global/js/helldots-init.js` per design §5: read `#helldots-config` dataset,
build the overlay, wire `onReady`, `onCommentRequested`, `onChange` (with the `origin === "host"` guard),
`transformScreenshot`, `onError`. Expose `window.marloHelldots`.
**Acceptance:** No-ops cleanly when `HellDots` or the config element is absent. No `origin === "host"` event is
ever forwarded (AC-006).
**Verify:** In the console, call `marloHelldots.setCommentStatus(id, "resolved")` and confirm the Network tab
shows no POST.

### ENH-HELLDOTS-OVERLAY-001-T09 — Mount block in footer.ftl
**Depends on:** T01, T08
**Do:** Add the guarded block from design §5 to `footer.ftl`, using `data-*` attributes for identity.
**Acceptance:** Guard is `(currentUser??)!false && !config.production`. No `${...}` inside any `<script>` body.
No `?html` anywhere.
**Verify:** Signed out → no script tag in `view-source` (AC-001). Signed in → toolbar appears. Temporarily set
`marlo.production=true`, restart, confirm nothing is emitted (AC-002), then restore. Sign in as a user whose
display name contains an apostrophe and confirm no JS error (AC-003).

### ENH-HELLDOTS-OVERLAY-001-T10 — Fonts crossorigin fix
**Depends on:** T09
**Do:** Add `crossorigin` to the Google Fonts stylesheet `<link>` in `header.ftl:41`.
**Acceptance:** Figtree still loads and renders identically across the app.
**Verify:** Capture a screenshot through the widget and confirm the text is in Figtree, not a fallback face;
drag-select a few words and confirm the crop holds the right glyphs.

### ENH-HELLDOTS-OVERLAY-001-T11 — End-to-end pass against acceptance criteria
**Depends on:** T09, T10
**Do:** Walk AC-001 through AC-012 in the running local app.
**Acceptance:** All twelve pass, each with the observation that proves it.
**Verify:** Record the result per criterion in this file under Testing Plan.

### ENH-HELLDOTS-OVERLAY-001-T12 — Anchor durability spot-check
**Depends on:** T11
**Do:** Leave comments on a page with an expandable block; expand and collapse it; reload.
**Acceptance:** Comments inside a rebuilt subtree re-anchor after `marloHelldots.notifyNavigation()`, and are
reported orphaned rather than dropped when their element is genuinely gone.
**Verify:** `loadComments()` return value (`{ anchored, orphaned, inactive }`) matches what is on screen.

### ENH-HELLDOTS-OVERLAY-001-T13 — Regression sweep
**Depends on:** T11
**Do:** Exercise the areas the shared-infra changes could reach: several `.do` pages, an existing file upload,
`/swagger/*`, and a page that already uses `${baseUrlCdn}` assets.
**Acceptance:** No behaviour change outside the feature.
**Verify:** Manual pass; note anything unexpected against R-001.

### ENH-HELLDOTS-OVERLAY-001-T14 — Gates and commit
**Depends on:** T12, T13
**Do:** `mvn checkstyle:check`; confirm GPL headers; confirm no credential file staged; commit with the
gitmoji/semantic convention.
**Acceptance:** Checkstyle green. `git status` shows no `marlo-dev.properties`, no `context.xml`, no `C:` directory.
**Verify:** `git diff --stat` reviewed file by file before committing.

## 4. Dependency Graph

```
PF-02 ─┬─ T01 ──────────────┐
       │                     ├─ T09 ─┬─ T10 ─┐
PF-03 ─┴─ T02 ─ T03 ─┬─ T05 ─┴─ T08 ─┘       ├─ T11 ─┬─ T12 ─┐
                     │                        │       │       ├─ T14
PF-06 ─── T04 ───────┼─ T06 ─────────────────┘       └─ T13 ─┘
                     │
PF-04 ───────────────┴─ T07
```

T04 gates every endpoint: without the `/api/*` mapping, T05–T07 return 404 no matter how correct they are.
PF-04 gates T07 for the same reason on the storage side.

## 5. Testing Plan

**Unit (marlo-web).** The `switch (event.type)` across all ten event types, including the identity overwrite
and the unknown-type rejection. Upload validation: accepted MIME types, rejected types, size boundary.

**Integration (local MySQL).** Migration applies on a clean database. Upsert by `comment_id` updates rather than
duplicates. Soft delete leaves the row with `is_active = 0`. Page query uses the `page` index.

**Manual (browser).** AC-001..AC-012, recorded here as they pass:

| Criterion | Result |
|---|---|
| AC-001 mount gate, signed in / out | pending |
| AC-002 production gate | pending |
| AC-003 apostrophe in display name | pending |
| AC-004 per-page load, 0 orphaned | pending |
| AC-005 cross-page deep link | pending |
| AC-006 host-origin echo guard | pending |
| AC-007 screenshot stored as URL | pending |
| AC-008 forged authorId overwritten | pending |
| AC-009 cross-user edit rejected | pending |
| AC-010 bad upload rejected, fail-open | pending |
| AC-011 soft delete | pending |
| AC-012 migration on clean DB | pending |

**Regression.** T13. Particular attention to anything reachable through the newly mapped `/api/*`.

## 6. Operational Steps

Local only for this delivery. One additive Flyway migration, applied automatically on startup; `repair()` runs
first, so a failed attempt is cleared and retried. No BI, AI-service or external coordination. No new
environment variable in any committed file — the `file.uploads.baseFolder` correction is local and gitignored.

Before any shared environment: confirm its `file.uploads.baseFolder` is a real path there too, and re-confirm
ENH-HELLDOTS-OQ-001 for that environment's exposure.

## 7. Rollback Plan

1. **Disable without data loss** — remove the `footer.ftl` block. The widget stops mounting everywhere; tables
   and endpoints stay inert.
2. **Full revert** — revert the branch's commits. `WebAppInitializer` returns to `/swagger/*` only; the two
   tables remain, empty and unreferenced, or are dropped in a follow-up migration.
3. **Local storage** — delete the uploaded files under `file.uploads.baseFolder`; nothing else references them.

No production impact is possible: the widget does not mount there and the DispatcherServlet is not registered
there.

## 8. Definition of Done

- All twelve acceptance criteria pass locally against `aiccradb1`, recorded in §5.
- `mvn checkstyle:check` green; GPL headers on every new Java file.
- No credential or environment file staged; no `C:` directory in the working tree.
- T13 regression sweep clean, with any `/api/*` exposure finding recorded against R-001.
- ENH-HELLDOTS-OQ-001 resolved, or explicitly accepted by the tech lead with the acceptance noted in the
  requirements Decision Log.
- Commits follow the gitmoji/semantic convention; branch is ready to propose against `staging`.
