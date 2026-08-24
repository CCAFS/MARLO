# HellDots Comment Overlay — Design

**Spec ID:** ENH-HELLDOTS-OVERLAY-001
**Status:** Draft
**Owner:** Kevin Collazos — IBD Team
**Last Updated:** 2026-08-24
**Implements requirements:** ENH-HELLDOTS-FN-001..009, ENH-HELLDOTS-NF-001..008
**Touches modules:** marlo-web, marlo-data

---

## 1. Architecture Summary

A version-pinned UMD build of HellDots is served from MARLO's own static assets and initialised by a small
adapter script. The adapter reads the signed-in identity from `data-*` attributes rendered by FTL, wires the
library's callbacks to five Spring MVC endpoints, and does nothing else. All widget UI lives inside the
library's Shadow DOM, so MARLO's CSS and the widget's cannot reach each other.

```
footer.ftl  [#if currentUser?? && !config.production]
   |
   +-- <div id="helldots-config" data-user-id data-user-name data-base-url>
   +-- helldots-0.7.0.umd.js        (self-hosted, 180 KB / 52 KB gzip)
   +-- helldots-init.js             (adapter)
            |
            |  onReady / onCommentRequested   -> GET  /api/helldots/comments...
            |  onChange (origin !== "host")   -> POST /api/helldots/events
            |  transformScreenshot            -> POST /api/helldots/screenshots
            v
   HelldotsController (Spring MVC, org.cgiar.ccafs.marlo.rest)
            |
            +-- HelldotsCommentManager -> DAO -> helldots_comments   (columns + json payload)
            +-- FileManager.copyFile   -> disk  -> helldots_screenshots (file registry)
```

Data flows one way per page load: the server renders identity, the widget loads that page's comments, and every
subsequent mutation is pushed as a single event. Nothing is polled and nothing is pushed back to the browser.

## 2. Module Footprint

### marlo-web

- New: `webapp/global/js/vendor/helldots-0.7.0.umd.js` — the published UMD bundle, unmodified.
- New: `webapp/global/js/helldots-init.js` — adapter: reads config, builds the overlay, wires callbacks.
- New: `java/org/cgiar/ccafs/marlo/rest/controller/helldots/HelldotsController.java`
- New: `java/org/cgiar/ccafs/marlo/rest/dto/helldots/HelldotsCommentDTO.java`
- New: `java/org/cgiar/ccafs/marlo/rest/dto/helldots/HelldotsEventDTO.java`
- New: `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__CreateHelldotsTables.sql`
- Modified: `webapp/WEB-INF/global/pages/footer.ftl` — the guarded mount block.
- Modified: `webapp/WEB-INF/global/pages/header.ftl` — add `crossorigin` to the Google Fonts stylesheet link.
- Modified: `java/org/cgiar/ccafs/marlo/WebAppInitializer.java` — `dispatcher.addMapping(REST_API_REQUESTS)`.

### marlo-data

- New: `data/model/HelldotsComment.java`
- New: `data/model/HelldotsScreenshot.java`
- New: `data/dao/HelldotsCommentDAO.java`, `data/dao/mysql/HelldotsCommentMySQLDAO.java`
- New: `data/dao/HelldotsScreenshotDAO.java`, `data/dao/mysql/HelldotsScreenshotMySQLDAO.java`
- New: `data/manager/HelldotsCommentManager.java`, `data/manager/impl/HelldotsCommentManagerImpl.java`
- New: `data/manager/HelldotsScreenshotManager.java`, `data/manager/impl/HelldotsScreenshotManagerImpl.java`
- New: `resources/xmls/HelldotsComments.hbm.xml`, `resources/xmls/HelldotsScreenshots.hbm.xml`

No `pom.xml` in any module is touched: the widget is a static asset, not a Maven dependency.

## 3. Data Model Changes

Two new tables. MySQL 8 (`utf8mb4_0900_ai_ci`) is confirmed in existing migrations, so the native `json`
column type is available.

```sql
CREATE TABLE helldots_comments (
  id bigint NOT NULL AUTO_INCREMENT,
  comment_id varchar(64) NOT NULL,
  page varchar(500) NOT NULL,
  page_query varchar(1000) DEFAULT NULL,
  author_user_id bigint DEFAULT NULL,
  author_name varchar(255) DEFAULT NULL,
  status varchar(20) NOT NULL,
  type varchar(20) DEFAULT NULL,
  priority varchar(10) DEFAULT NULL,
  created_at datetime NOT NULL,
  edited_at datetime DEFAULT NULL,
  resolved_at datetime DEFAULT NULL,
  schema_version int DEFAULT NULL,
  global_unit_id bigint DEFAULT NULL,
  payload json NOT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  modified_by bigint DEFAULT NULL,
  modification_justification text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  PRIMARY KEY (id),
  UNIQUE KEY helldots_comments_UN (comment_id),
  KEY helldots_comments_page_IDX (page),
  KEY helldots_comments_status_IDX (status),
  KEY helldots_comments_users_FK (author_user_id),
  KEY helldots_comments_users_FK_1 (created_by),
  KEY helldots_comments_users_FK_2 (modified_by),
  KEY helldots_comments_global_units_FK (global_unit_id),
  CONSTRAINT helldots_comments_users_FK   FOREIGN KEY (author_user_id) REFERENCES users (id)         ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT helldots_comments_users_FK_1 FOREIGN KEY (created_by)     REFERENCES users (id)         ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT helldots_comments_users_FK_2 FOREIGN KEY (modified_by)    REFERENCES users (id)         ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT helldots_comments_global_units_FK FOREIGN KEY (global_unit_id) REFERENCES global_units (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE helldots_screenshots (
  id bigint NOT NULL AUTO_INCREMENT,
  comment_id varchar(64) DEFAULT NULL,
  kind varchar(20) NOT NULL,
  file_name varchar(255) NOT NULL,
  relative_path varchar(500) NOT NULL,
  content_type varchar(50) NOT NULL,
  byte_size bigint DEFAULT NULL,
  is_active tinyint(1) NOT NULL DEFAULT '1',
  active_since timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY helldots_screenshots_comment_IDX (comment_id),
  KEY helldots_screenshots_users_FK (created_by),
  CONSTRAINT helldots_screenshots_users_FK FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**Column split rationale.** The columns above are the ones worth filtering, sorting or joining on. Everything
else the library serialises — `anchor`, `context`, `replies[]`, `reactions{}`, `history[]`, `tags[]`,
`screenshots[]`, `contextScreenshot` — lives in `payload`. `schema_version` mirrors the library's
`schemaVersion` field, so a future release that adds fields lands without a migration.

**`comment_id`** is the library's own nanoid and is the natural key for upserts. `id` exists because MARLO's
Hibernate mappings and audit FKs assume a numeric surrogate.

**`page_query`** is a MARLO addition, not a library field: the library records `location.pathname` only, and
MARLO carries entity state in the query string. It is stored for diagnosis; grouping stays on `page`
(see ENH-HELLDOTS-OQ-002).

**No `id_phase` column.** These records annotate the interface, not phased reporting content, so the
forward-only replication rule does not apply to them.

**Screenshot rows exist for sweeping, not for serving.** `comment_id` is nullable because the library uploads
a reply attachment when the file is picked, not when the reply is sent — so a blob can be created that no
record ever references. A row with `comment_id IS NULL` older than the retention window is a sweep candidate.

**Migration:** single Flyway file, `V2_6_0_<YYYYMMDD>_<HHMM>__CreateHelldotsTables.sql`, additive only. It
ships to every environment including production, where the tables stay empty because the widget never mounts.

## 4. API / Action Surface

Spring MVC only. No Struts action, no new `*.json` path.

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/helldots/comments?page={pathname}` | Comments for one page, screenshots as URLs. Feeds `onReady`. |
| `GET` | `/api/helldots/comments?all=true` | Whole corpus, for the inbox's cross-page view. |
| `GET` | `/api/helldots/comments/{commentId}` | One comment. Feeds `onCommentRequested`. |
| `POST` | `/api/helldots/events` | One `ChangeEvent`. Upsert by `comment_id`; `comment:deleted` soft-deletes. |
| `POST` | `/api/helldots/screenshots` | Multipart image upload. Returns `{ url }`. Feeds `transformScreenshot`. |

`POST /events` handles all ten event types with one `switch`. Every type except `comment:deleted` carries the
full comment, so the handler is: resolve or create the row by `comment_id`, overwrite the projected columns,
replace `payload`, stamp `modified_by`. `comment:deleted` sets `is_active = 0`.

**Routing prerequisite.** [`WebAppInitializer.java:142`](../../../marlo-web/src/main/java/org/cgiar/ccafs/marlo/WebAppInitializer.java)
currently calls `dispatcher.addMapping(REST_SWAGGER_REQUESTS)` and nothing else, so `/api/*` is excluded from
Struts by `struts.xml` and served by no servlet. Adding `dispatcher.addMapping(REST_API_REQUESTS)` inside the
same `!production` block is a precondition for every endpoint above. It also activates `ExceptionHandlerFilter`
and `AddSessionToRestRequestFilter`, both already mapped to `/api/*`.

## 5. Frontend Composition

**Mount point** — `footer.ftl`, after the existing script block, guarded exactly as the debug panel is:

```ftl
[#if (currentUser??)!false && !config.production]
  <div id="helldots-config"
       data-user-id="${currentUser.id?c}"
       data-user-name="${(currentUser.composedName)!'Unknown'}"
       data-base-url="${baseUrl}"></div>
  <script defer src="${baseUrlCdn}/global/js/vendor/helldots-0.7.0.umd.js"></script>
  <script defer src="${baseUrlCdn}/global/js/helldots-init.js?20260824"></script>
[/#if]
```

Identity travels in `data-*` attributes rather than an interpolated `<script>` body. Under Struts 6.8
FreeMarker auto-escaping, `${...}` inside JavaScript emits HTML entities, so a name like `O'Brien` would
produce `O&#39;Brien` and break the string literal. In an attribute the escaping is correct and the browser
reverses it on `dataset` access. `?html` is not an alternative — it is a parse error in this version.

`baseUrlCdn` resolves through `BaseAction.getBaseUrlCdn()`, which falls back to `baseUrl` when `cdn.url` is
empty (`APConfig.getBaseUrlCdn()` returns `null` for the empty string). Locally that yields
`http://localhost:8080/marlo-web/`.

**Adapter** — `helldots-init.js`, roughly:

```js
(function () {
  var el = document.getElementById("helldots-config");
  if (!el || !window.HellDots) return;
  var api = el.dataset.baseUrl.replace(/\/+$/, "") + "/api/helldots";

  var overlay = HellDots.createCommentOverlay({
    user: { name: el.dataset.userName, id: el.dataset.userId },
    locale: "en",
    embedCrossOriginFonts: false,          // header.ftl gains crossorigin instead

    transformScreenshot: function (dataUrl, info) { /* POST -> { url } */ },

    onReady: function (o) { /* GET ?page= -> o.loadComments(...) */ },
    onCommentRequested: function (id) { /* GET /{id} -> overlay.loadComments([...]) */ },
    onChange: function (event) {
      if (event.origin === "host") return;  // echo-loop guard
      /* POST /events */
    },
    onError: function (err, ctx) { console.warn("[helldots]", ctx, err); }
  });

  window.marloHelldots = overlay;           // for notifyNavigation() from AJAX sections
})();
```

**Fonts.** [`header.ftl:41`](../../../marlo-web/src/main/webapp/WEB-INF/global/pages/header.ftl) loads Figtree
from `fonts.googleapis.com` without `crossorigin`. Reading `cssRules` on that stylesheet throws `SecurityError`,
so its `@font-face` never reaches the capture: text renders in a fallback face whose metrics differ, and a
drag-crop tight around a few letters can return the wrong ones. Adding `crossorigin` to that one link fixes it
at no runtime cost and keeps `embedCrossOriginFonts` off, so the widget makes no third-party request on a
user's behalf.

**Navigation.** MARLO navigates by full page load, so `notifyNavigation()` is not needed for routing. It *is*
needed after an expandable block or other AJAX section rebuilds its DOM — otherwise comments anchored inside
that subtree orphan. `window.marloHelldots` is exposed for exactly that call. See
`EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`.

**Shadow DOM.** The widget renders in a shadow root, so it cannot collide with MARLO's global CSS, nor with the
document-delegated jQuery UI tooltips.

## 6. Persistence & Phase Replication Plan

**Not applicable in the usual sense.** HellDots comments are not phased data. They annotate the interface,
carry no `id_phase`, and are never replicated to future phases. The forward-only rule (constitutional rule 1)
governs reporting content, not UI annotations, and applying it here would be meaningless — a comment about a
button is not "true in phase 4 onwards".

Deletion is a soft delete (`is_active = 0`), matching MARLO convention, so a removed comment stays auditable.

## 7. Validation & Save Pipeline

**Not applicable.** The save pipeline pattern (`Action.validate()` guarded by `if (save)` → `Validator` →
manager chain) governs Struts-based critical reporting sections. This feature has no Struts Action and no
form submission. Validation lives in the controller: payload size cap, known `event.type`, `status`/`type`/
`priority` against the library's enumerations, and the identity overwrite described in §13.

Malformed input is rejected with a 4xx. The widget treats a failed `transformScreenshot` as fail-open — it
keeps the data URL and reports `onError(err, "transform")` — so a rejected upload degrades the record rather
than losing the comment.

## 8. Permissions & Edit Gates

No MARLO interceptor or `canEdit*` gate applies; this is not a reporting section. The gates are:

1. **Mount gate** (FTL) — session user present and non-production.
2. **Transport gate** — `AddSessionToRestRequestFilter` on `/api/*` supplies session values for authorization.
3. **Write gate** (controller) — session identity overwrites payload identity on create; edit and delete
   require the session user to be the comment's author or to hold a MARLO admin role. Note the controller is
   Spring MVC, not a Struts `BaseAction`, so `canAccessSuperAdmin()` is not available to it: the session user
   is resolved from `APConstants.SESSION_USER` (the attribute `AddSessionToRestRequestFilter` already works
   with) and the role predicate is evaluated from that user's roles.
4. **Environment gate** (controller) — reject when the active profile is production.

## 9. Specificity / Feature-Flag Strategy

**Not applicable.** Gating is environmental, not per global unit, so no `parameters` / `custom_parameters` row
and no `APConstants` entry in either module. Should the tool later need per-CRP activation, that is a separate
spec: it would add the constant to both `APConstants.java` files with the value equal to the `parameters.key`,
per constitutional rule 4.

## 10. Integration Points

- **Disk storage** — written under `config.getUploadsBaseFolder()` and served at `config.getDownloadURL()`,
  the same pair used by project highlights and case studies.
- **Shiro** — session identity for every write.
- **Google Fonts** — indirectly, via the `crossorigin` fix in `header.ftl`.
- **S3** — deliberately not integrated. `transformScreenshot` is the seam if that changes.
- CLARISA, CGSpace, BI, AI services, Pusher — untouched.

## 11. Observability

- MARLO's audit columns (`created_by`, `modified_by`, `active_since`, `is_active`) on both tables.
- The library's own `history[]` inside `payload` — an append-only log of creation, edits, status moves and
  reclassification. Note it is *attributive, not evidential*: it records what the client asserted, and its
  timestamps come from the client's clock. The server-side audit columns are the trustworthy record.
- `onError` is logged to the browser console with its context (`capture`, `storage`, `load`, `link`,
  `transform`). Forwarding these to the server is deliberately out of scope for the first delivery.
- Controller logs each event type and comment id at debug level.

## 12. Performance & Scalability

Load is a handful of QA users in non-production. The shapes that matter:

- **Per page load:** one indexed `SELECT ... WHERE page = ? AND is_active = 1`. Screenshots are URLs, so a
  page's payload is on the order of 1–2 KB per comment rather than 30–100 KB.
- **Corpus view:** one unindexed scan of an intentionally small table.
- **Bundle:** 180 KB raw / 52 KB gzip, `defer`, self-hosted and cacheable, emitted on non-production pages only.
- **Screenshot capture** costs a moment on comment creation; the library documents `autoScreenshot: false` as
  the escape hatch if that becomes annoying.

Keeping base64 out of the row is what makes the corpus view viable at all; the `page` index is what keeps the
common path constant-cost as the table grows.

## 13. Security Considerations

**The library authenticates nobody.** It stamps whatever `user` the host declared into records and history
entries. Every trust decision is the backend's:

- `author_user_id` / `author_name` are written from the Shiro session and **overwrite** any `authorId` or
  `author` in the payload.
- Edit and delete verify session user == author, or an admin role on the session user (resolved from
  `APConstants.SESSION_USER`, not from `BaseAction`, which a Spring MVC controller cannot reach).
- Uploads: content type restricted to `image/jpeg` and `image/png`, size capped at 5 MB by
  `HelldotsUploadValidator.MAX_SCREENSHOT_BYTES`, **filename generated server-side** — a client-supplied name
  is never used in a path.
- `page`, `text`, `tags` and the payload are user-controlled: parameterised queries only, and a payload size cap.
- Endpoints refuse when the active profile is production, independently of the DispatcherServlet not being
  registered there.
- The stored `history[]` is a client assertion. Where a trustworthy record matters, the audit columns are it.

## 14. Backwards Compatibility & Rollout

Purely additive: two new tables, two new static assets, one guarded FTL block, one attribute on a `<link>`,
one line in `WebAppInitializer`. No existing table, action, view or endpoint changes shape.

**Rollout:** local environment against the local MySQL database (`mysql.host=localhost`,
`mysql.database=aiccradb1`) first, exercised end to end, before the branch is proposed for `staging`.

**Rollback:** revert the commits; the two tables can be dropped or left empty and inert. Removing the
`footer.ftl` block alone disables the whole feature without touching data.

**Local prerequisite — `file.uploads.baseFolder`.** The local `marlo-dev.properties` sets it to
`C:/xampp/htdocs/marlo`, a Windows path on a macOS machine. Left as is, uploads land in a literal `C:`
directory at the repo root (already anticipated by `.gitignore:27`) and are not reachable at
`file.downloads=http://localhost:8080/marlo-web/data`. It must be pointed at a real local directory that the
downloads URL maps to before screenshot upload can work. The file is gitignored, so this is a local-only fix.

## 15. Decision Records

- **DR-001 — Self-host the UMD build rather than load from unpkg.** MARLO already loads Vue and PrimeVue from
  unpkg, so precedent exists, but a version-pinned local copy survives the CGIAR firewall, adds no third-party
  request per page, and cannot change under the app without a commit.
- **DR-002 — Hybrid schema over full normalisation or a single blob.** See requirements Decision Log (c).
- **DR-003 — One event endpoint over granular REST.** See requirements Decision Log (e).
- **DR-004 — Disk storage over S3 or a blob column.** See requirements Decision Log (d).
- **DR-005 — `data-*` attributes over `<script>` interpolation.** See requirements Decision Log (f).
- **DR-006 — Fix the fonts link rather than enable `embedCrossOriginFonts`.** One attribute, no runtime cost,
  and the widget never fetches third-party stylesheets on a user's behalf.
- **DR-007 — `helldots_` table prefix.** MARLO already owns the word "feedback" for a different system
  (`feedback_qa_comments`, anchored to form fields, with its own role/permission matrix). The prefix keeps the
  two apart in schema, code and conversation.

## 16. Open Risks

- **R-001 — Mapping `/api/*` widens the reachable surface.** The whole `org.cgiar.ccafs.marlo.rest` tree becomes
  addressable there in non-production. Blocked on ENH-HELLDOTS-OQ-001.
- **R-002 — `page` is pathname-only.** Comments from every project pile onto one `page` value. `page_query`
  records the difference but grouping does not use it. ENH-HELLDOTS-OQ-002.
- **R-003 — Orphaned screenshot blobs.** The library uploads a reply attachment at file-pick time, so blobs can
  outlive any record. The registry table makes sweeping possible; the sweep itself is not in this delivery.
- **R-004 — Anchors drift as the UI changes.** MARLO's markup is under active redesign; comments will orphan
  when an element disappears. The library marks them orphaned rather than dropping them, which is the intended
  behaviour, but the inbox will accumulate them.
- **R-005 — No retention policy.** ENH-HELLDOTS-OQ-003.
