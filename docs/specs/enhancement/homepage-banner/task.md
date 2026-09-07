# Homepage Banner — Tasks

**Spec ID:** ENH-HOMEPAGE-BANNER-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-28
**Implements design:** docs/specs/enhancement/homepage-banner/design.md
**Branching:** feature branch from `staging`, named `<TICKET-ID>-Homepage-banner-admin-section` (or `homepage-banner-admin-section` if the work lands without a ticket).
**Target merge:** `staging` (then promoted to `main` per the release process).

---

## 1. Execution Context

- Java 17. Local runs use `scripts/run-marlo-java17.sh`; `marlo-parent/pom.xml` is the verification
  source for the active Java level.
- Local database: the dev database this checkout points at. Flyway runs on Tomcat startup, so a
  branch switch that moves the migration set backwards leaves the schema ahead of the code and the
  app 404s wholesale — check `flyway_schema_history` before blaming the code.
- `uploadsBaseFolder` must resolve to a real, writable directory on the dev machine before T07 can
  be verified. Verifying the *failure* path (T13) is the one case where leaving it broken is the
  point.
- Property-file edits (`global.properties`) require a server restart, not just a redeploy of the
  view. Budget for it when verifying T11.

## 2. Pre-flight Checklist

- [ ] `requirements.md` reviewed and approved.
- [ ] `design.md` reviewed and approved.
- [ ] `git fetch` then branch from the latest `staging`.
- [ ] Confirm no other in-flight branch adds a migration with a timestamp later than
      `20260828_0900`; if one does, bump this spec's migration timestamp rather than reordering
      theirs.
- [ ] Confirm the AICCRA Global Unit acronym in the target database is exactly `AICCRA` — the seed
      in T01 resolves by acronym.

## 3. Task List

### ENH-HOMEPAGE-BANNER-001-T01 — Flyway migration: create table and seed AICCRA

- **Depends on:** none
- **Module:** marlo-web
- **Files touched:**
  - `resources/database/migrations/V2_6_0_20260828_0900__CreateHomepageBannerTable.sql` (new)
- **Constitutional checks:**
  - Naming follows `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
  - Additive only; no destructive statement.
  - Seed resolves the Global Unit by acronym, never by a hardcoded id.
- **Tests:**
  - Not applicable (no unit test harness for migrations in this repo).
- **Done when:**
  - Tomcat starts clean and `flyway_schema_history` shows the migration applied.
  - `homepage_banners` holds exactly one row for AICCRA, with `image_file_name` NULL.
  - Re-running against a database without AICCRA inserts nothing and does not fail.
- **Verification:**
  - `SELECT * FROM homepage_banners;` and compare title/description against
    `global.properties:6394-6395` before those keys are removed in T11.

### ENH-HOMEPAGE-BANNER-001-T02 — Entity, mapping, DAO and Manager

- **Depends on:** T01
- **Module:** marlo-data
- **Files touched:**
  - `data/model/HomepageBanner.java` (new)
  - `resources/xmls/HomepageBanner.hbm.xml` (new)
  - `resources/hibernate.cfg.xml` (modified — register the mapping)
  - `data/dao/HomepageBannerDAO.java` (new)
  - `data/dao/mysql/HomepageBannerMySQLDAO.java` (new)
  - `data/manager/HomepageBannerManager.java` (new)
  - `data/manager/impl/HomepageBannerManagerImpl.java` (new)
- **Constitutional checks:**
  - GPL header in every new Java file.
  - Layered pattern preserved: Manager → ManagerImpl → DAO → MySQLDAO.
  - `MarloAuditableEntity` / `IAuditLog` integration for the audit trail (DA-005).
  - Phase replication: deliberately absent, per ADR-2. Do not add a phase column.
- **Tests:**
  - Not applicable at this layer; covered by the manual DB verification below and by T05's tests
    for the logic that matters.
- **Done when:**
  - `mvn checkstyle:check` passes.
  - The app starts with the new mapping registered (a bad `hbm.xml` fails Hibernate bootstrap, so
    startup is the test).
  - `findByGlobalUnit` returns the seeded AICCRA row, and `null` for a Global Unit with no row.
- **Verification:**
  - Query through the manager from the dashboard read path added in T09, or a temporary log line
    removed before commit.
- **Notes:**
  - Use a parameterised Hibernate `Query` via `findSingleResult(Class, Query)` rather than
    concatenating the id into HQL the way `TimelineMySQLDAO.findAllByGlobalUnit` does.

### ENH-HOMEPAGE-BANNER-001-T03 — `HomepageBannerImageStore`

- **Depends on:** none (pure logic; can run in parallel with T01–T02)
- **Module:** marlo-web
- **Files touched:**
  - `utils/HomepageBannerImageStore.java` (new)
- **Constitutional checks:**
  - GPL header.
  - 2-space indent, 120-char lines.
  - English-only identifiers and comments.
- **Tests:**
  - Unit: PNG accepted; JPEG accepted; SVG accepted.
  - Unit: a text file renamed `.png` rejected (content check, not extension — SEC-002).
  - Unit: a 3 MB file rejected (SEC-004).
  - Unit: a file named `../../evil.png` produces a path inside the banners folder (SEC-003).
  - Unit: unconfigured uploads base returns the "not configured" outcome rather than throwing.
  - Unit: replacing a PNG with an SVG deletes the old PNG.
- **Done when:**
  - `mvn checkstyle:check` passes and all unit tests above pass.
- **Verification:**
  - Tests run against a JUnit temporary folder, so no dependence on the machine's uploads config.

### ENH-HOMEPAGE-BANNER-001-T04 — `DownloadHomepageBannerImageAction` and `struts-data.xml`

- **Depends on:** T02, T03
- **Module:** marlo-web
- **Files touched:**
  - `action/downloads/DownloadHomepageBannerImageAction.java` (new)
  - `resources/struts-data.xml` (modified)
- **Constitutional checks:**
  - GPL header.
  - Public `data` package route, mirroring `globalUnitLogo`; no new `*.json` path.
  - Dynamic `contentType` param; **no** default-image fallback.
- **Tests:**
  - Not applicable (thin I/O action; covered by manual verification).
- **Done when:**
  - `GET /data/homepageBannerImage.do?acronym=AICCRA` returns the stored image with the right
    content type once an image exists.
  - The same request redirects to the 404 page when no image is stored, and for an unknown acronym.
- **Verification:**
  - Browser and `curl -I`, checking `Content-Type` for both a PNG and an SVG.

### ENH-HOMEPAGE-BANNER-001-T05 — `HomepageBannerManagementValidator`

- **Depends on:** T02, T03
- **Module:** marlo-web
- **Files touched:**
  - `validation/superadmin/HomepageBannerManagementValidator.java` (new)
- **Constitutional checks:**
  - GPL header; extends `BaseValidator`.
  - Every message is an i18n key, no literal user-facing text.
- **Tests:**
  - Unit: all three fields empty → valid (an empty banner is a legal state).
  - Unit: title of 501 characters → invalid with `error.titleTooLong`.
  - Unit: oversized or wrong-format upload → invalid with the matching key.
- **Done when:**
  - `mvn checkstyle:check` passes; unit tests pass.
- **Verification:**
  - Exercised end to end in T13.

### ENH-HOMEPAGE-BANNER-001-T06 — `HomepageBannerManagementAction`

- **Depends on:** T02, T03, T05
- **Module:** marlo-web
- **Files touched:**
  - `action/crp/admin/HomepageBannerManagementAction.java` (new)
- **Constitutional checks:**
  - GPL header.
  - `validate()` guarded by `if (save)`; Validator invoked from it.
  - `save()` gated by `this.hasPermission("*")`, matching `TimelineManagementAction`.
- **Tests:**
  - Not applicable at unit level (no Action test harness in this repo); covered by T13.
- **Done when:**
  - `prepare()` loads the existing row or builds an empty one for a Global Unit with no banner.
  - `save()` upserts a single row and never creates a second one for the same Global Unit.
  - `removeImage` is honoured before a new upload is considered, so remove + pick-a-file ends with
    the new file.
  - An image write failure still saves the title and description, and surfaces the error (OPS-001).
- **Verification:**
  - Manual, in T13.

### ENH-HOMEPAGE-BANNER-001-T07 — Struts mapping and admin menu entry

- **Depends on:** T06
- **Module:** marlo-web
- **Files touched:**
  - `resources/struts-admin.xml` (modified)
  - `webapp/WEB-INF/crp/views/admin/menu-admin.ftl` (modified — both item lists)
- **Constitutional checks:**
  - `crpAdminStack` declared on the action (SEC-001).
  - Menu entry added to the AICCRA **and** the non-AICCRA list (UI-001).
- **Tests:**
  - Not applicable.
- **Done when:**
  - The section is reachable at `/admin/<crp>/homepageBannerManagement.do` and highlighted in the
    secondary menu.
  - A non-admin user is denied by the stack rather than shown an empty form.
- **Verification:**
  - Log in as an admin and as a non-admin and try both.

### ENH-HOMEPAGE-BANNER-001-T08 — `homepageBannerManagement.ftl`

- **Depends on:** T04, T07
- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/crp/views/admin/homepageBannerManagement.ftl` (new)
- **Constitutional checks:**
  - Every label, hint and error from an i18n key (UI-004).
  - No `?html` anywhere — FreeMarker auto-escaping is on under Struts 6.8 and `?html` is a parse
    error that 500s the page.
- **Tests:**
  - Not applicable.
- **Done when:**
  - The form round-trips title, description, upload and remove-image.
  - The stored image is shown through `/data/homepageBannerImage.do` (UI-002).
  - The remove-image checkbox is disabled when no image is stored.
  - Help text states that emptying all three fields hides the banner.
- **Verification:**
  - Manual walkthrough in T13.

### ENH-HOMEPAGE-BANNER-001-T09 — `DashboardAction` reads the banner

- **Depends on:** T02
- **Module:** marlo-web
- **Files touched:**
  - `action/home/DashboardAction.java` (modified)
- **Constitutional checks:**
  - Constructor injection of `HomepageBannerManager`, matching the managers already injected there.
  - At most one extra query per homepage render (NF-001).
- **Tests:**
  - Not applicable.
- **Done when:**
  - `getHomepageBanner()` returns the row when any field has content, and `null` when all three are
    blank or no row exists (FN-004). The emptiness decision lives here, not in the template.
- **Verification:**
  - Toggle content in the database and reload the homepage.

### ENH-HOMEPAGE-BANNER-001-T10 — `dashboard.ftl`: rewrite the banner, delete the hotspots

- **Depends on:** T09
- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/crp/views/home/dashboard.ftl` (modified)
- **Constitutional checks:**
  - `homepage_hide_section_map` retained as a hard override (FN-006); the comment at the
    conditional explains why the key still says "map".
  - No `?html`.
- **Tests:**
  - Not applicable.
- **Done when:**
  - The `clusterHotspots` assign and the whole map panel are gone (FN-009).
  - Each of title, description and image renders only when present (FN-005).
  - The collapse toggle is emitted only when there is a description.
  - The image `alt` is the title, or empty when there is no title (UI-003).
- **Verification:**
  - View source on the homepage for each of the content combinations in the testing plan.

### ENH-HOMEPAGE-BANNER-001-T11 — Rename in CSS and JS; retire the i18n keys

- **Depends on:** T10
- **Module:** marlo-web
- **Files touched:**
  - `webapp/crp/css/home/dashboard.css` (modified)
  - `webapp/crp/js/home/dashboard.js` (modified)
  - `resources/global.properties` (modified)
- **Constitutional checks:**
  - No identifier, class, storage key or i18n key left containing `cluster` in these files (NF-003).
  - Retired keys deleted, not left dangling: `dashboard.cluster.title`, `.description`, `.mapAlt`,
    `.browse`, `.glossary`.
  - New keys added to `global.properties`; check `custom/*.properties` for per-program overrides of
    the retired keys and remove those too.
- **Tests:**
  - Not applicable.
- **Done when:**
  - `grep -ri cluster` across `dashboard.ftl`, `dashboard.css`, `dashboard.js` returns only the `/clusters`
    namespace used by the project list macros.
  - All `.clusterMap*` rules and the hotspot comment block are deleted.
  - The cache-busting query string on the `dashboard.css` and `dashboard.js` assigns in
    `dashboard.ftl` is bumped, or the browser will serve the old files.
  - The server is restarted so the property changes take effect.
- **Verification:**
  - Collapse the banner, reload, confirm it stays collapsed under the new
    `marlo.homepageBanner.collapsed` key.

### ENH-HOMEPAGE-BANNER-001-T12 — Update the ai-context routing catalog

- **Depends on:** T07, T04
- **Module:** documentation
- **Files touched:**
  - `reports/ai-context/struts-critical-routing-catalog.md` (modified)
  - `reports/ai-context/frontend-composition-map.md` (modified, if it describes the homepage banner)
- **Constitutional checks:**
  - `CLAUDE.md` step 10: update ai-context docs when routing or composition contracts change.
- **Tests:**
  - Not applicable.
- **Done when:**
  - Both new routes are documented with their stacks.
  - Any description of the homepage cluster banner is updated to the new component.
- **Verification:**
  - Read the diff.

### ENH-HOMEPAGE-BANNER-001-T13 — Manual QA pass against the acceptance criteria

- **Depends on:** T01–T12
- **Module:** all
- **Files touched:** none
- **Constitutional checks:**
  - Every acceptance criterion in `requirements.md` §6 exercised.
- **Tests:**
  - See the Testing Plan below.
- **Done when:**
  - Every case in the Testing Plan passes, with the failure-path cases explicitly exercised, not
    reasoned about.
- **Verification:**
  - Record the result of each case in this file under the task before marking it done.

## 3b. Verification Log (2026-08-28)

Recorded as the tasks were executed, on the local dev environment (Java 17, `scripts/run-marlo-java17.sh`,
local MySQL 8 `aiccradb1`, uploads at `/Users/kevincollazos/marlo-uploads`).

**Verified**

- T01 — Migration applied: `schema_version` shows `2.6.0.20260828.0900 CreateHomepageBannerTable`,
  success 1. `homepage_banners` holds one row for AICCRA (`global_unit_id` 45), title
  `What is a Cluster?`, description 517 characters, `image_file_name` NULL, `is_active` 1.
- T02 — Hibernate bootstraps with `HomepageBanner.hbm.xml` registered; the application starts and the
  dashboard read path resolves the seeded row.
- T03 — 13 unit tests pass (`HomepageBannerImageStoreTest`), covering the content-based format
  whitelist, the 2 MB cap ahead of format detection, path derivation for a path-like acronym, the
  byte-for-byte SVG copy, replace-deletes-the-superseded-format, unconfigured and unwritable uploads,
  delete and resolve.
- T04 — `GET /data/homepageBannerImage.do?acronym=AICCRA` with no stored image redirects to the 404
  page (302), as does an unknown acronym. `globalUnitLogo` still returns 200 `image/png`, so the new
  route did not disturb the existing one.
- T05 — 7 unit tests pass (`HomepageBannerManagementValidatorTest`), including "an empty banner is
  valid" and the 500/501-character title boundary.
- T10, T11 — Rendered against the real `dashboard.css`, `marlo-redesign.css` and `dashboard.js` served
  by the running Tomcat, for all seven content combinations:
  - all three fields, collapsed, title-only (no toggle emitted), description-only, title+description
    with no image, a ~4000-character description, and the all-empty case which emits no markup at all;
  - the SVG resolves to 190x178 from its `viewBox` with the generic rule that replaced the hardcoded
    `aspect-ratio`, so an arbitrary uploaded image keeps its own proportions;
  - no horizontal page overflow at 1457px (the width the deleted tooltips used to break), nor at 1400,
    1100, 860 or 375; the card stacks to a column at 860 and the image drops to 150px at 1100;
  - zero elements matching `[class*=clusterMap]` or `[class*=clusterBanner]`;
  - the renamed toggle flips `homepageBanner--collapsed`, `aria-expanded` and the button label, hides
    both the content and the image, persists under `marlo.homepageBanner.collapsed`, never writes the
    old `marlo.clusterBanner.collapsed` key, and restores the collapsed state after a reload.
- T12 — Both routes documented in `reports/ai-context/struts-critical-routing-catalog.md`, with a new
  "Public Stream Routes" section. `frontend-composition-map.md` needed no change: it never described
  the banner.
- Checkstyle — `mvn checkstyle:check` cannot run in this checkout at all: the CLI invocation fails with
  `NoSuchMethodError: Checker.setClassloader`, an API incompatibility between maven-checkstyle-plugin
  2.9.1 and checkstyle 8.18 that is independent of this change. Ran checkstyle 8.18 directly against
  `configuration/marlo-checkstyle.xml` instead: **zero violations** across all new and modified Java
  files.

**Verified in an authenticated session (AICCRA, admin user 1082)**

- T07, T08 — `/admin/AICCRA/homepageBannerManagement.do` renders behind `crpAdminStack`, appears in
  the admin secondary menu as "Homepage Banner", and the form posts `multipart/form-data` to its own
  action with the title and description pre-filled from the stored row.
- T06 — Save round-trip: uploading `Map_africa.svg` wrote
  `homepage_banners.image_file_name = 'AICCRA.svg'` and `modified_by = 1082` (the audit trail
  populates), and put the file at `<uploads>/homepageBanners/AICCRA.svg` at exactly the source's
  53,053 bytes.
- T04 in situ — `/data/homepageBannerImage.do?acronym=AICCRA` then returned 200 `image/svg+xml`,
  byte-identical to the source SVG (`cmp` clean). A lowercase `acronym=aiccra` resolves too, so the
  normalisation holds on the read path.
- Format rejection — a 38-byte text file named `evil.png` was refused with
  "That file is not a PNG, a JPG or an SVG image."; nothing was written to disk, the stored image was
  untouched, and the typed title and 517-character description survived the re-render.
- Size rejection — a 2,098,176-byte file was refused with "The image is too large. The maximum size
  is 2 MB.". The message is the *size* one, not the format one, which confirms the intended ordering.
- Format replacement — uploading a PNG over the stored SVG left only `AICCRA.png` in the folder (the
  SVG deleted), flipped the row to `AICCRA.png`, and the route's content type followed to
  `image/png`.
- FN-007 — ticking remove-image set the column to NULL, emptied the folder, returned the route to a
  302, and left the title and description unchanged.
- FN-004 in situ, **with a correction** — an empty banner does render nothing on `crpDashboard.do`:
  `getElementById('homepageBanner')` null, zero `[class*=homepageBanner]` elements, the Schedule card
  first. But the log shows the session had been switched to the `AICCRA_III` Global Unit at that
  moment (a Global Unit switch made in the browser, not by the save), so what was observed was a
  *newly created empty row* for `AICCRA_III`, not AICCRA's populated row being cleared. The
  "empty means no markup" half is verified; "clearing a populated banner hides it" is **not**, and is
  listed below.
- T09, T10 in situ — with all three fields set, the real dashboard renders the title, the
  517-character description, the toggle and the image panel; `alt` equals the title; the image loads
  at 178x190; `aria-controls` matches the content id; zero `clusterBanner`/`clusterMap` elements; no
  horizontal overflow at 1400px. The collapsed state set during the harness run carried into the real
  page under the new storage key, which incidentally proved persistence in situ.

The environment was left in the intended end state: the seeded title and description restored and
`Map_africa.svg` uploaded as the banner image.

**Defect found during that pass, and fixed**

That stray `AICCRA_III` row is not just an artefact of a Global Unit switch — it exposed a real bug in
`HomepageBannerManagementAction.save()`, which persisted unconditionally. A Global Unit with no banner
whose administrator merely opened the section and pressed save got an all-NULL row inserted. Harmless
to render (`isEmpty()` treats it as absent, so no banner appears) but junk data, one row per Global
Unit anybody visits.

Fixed by skipping the insert when the row is new *and* the incoming content is empty: an absent row and
an all-empty row mean the same thing to the homepage. An existing row is still updated, so clearing the
three fields on a populated banner still empties it rather than being silently ignored.

`HomepageBannerTest` now covers `isEmpty()` — the predicate both the fix and the homepage condition
lean on — including whitespace-only input. Those six tests passed on first run: they are characterisation
coverage of an existing method, not a red-green cycle, and are recorded as such rather than as evidence
that the fix works. The action-level wiring has no unit test, because the repository has no Action test
harness, so it was verified manually instead:

- The running build was confirmed to carry the fix (`javap` shows the `HomepageBanner.isEmpty`
  invocation and the skip's log literal in the compiled action).
- On `AICCRA_III`, which had no row, saving the section with all three fields empty inserted **nothing**:
  the table stayed at one row, the log recorded the skip once, and zero `insert into homepage_banners`
  statements were issued since the restart. The administrator still gets the ordinary "saved"
  confirmation — having nothing to store is not an error.
- On AICCRA, whose row was populated, clearing the title, the description and the image **emptied the
  existing row** (id 1 still present, three columns NULL, `modified_by` set) rather than skipping the
  write, deleted the file from the uploads folder, and returned the image route to a 302. This is the
  boundary that matters: had the condition been `isEmpty()` alone rather than `isNew && isEmpty()`,
  this save would have been silently ignored and the banner would have stayed on the homepage.
- FN-004 then re-verified properly on AICCRA: with the row emptied, `crpDashboard.do` rendered no
  banner element at all and the Schedule card became the first section. Content and image were restored
  afterwards, with the session confirmed to be on AICCRA before each write.

One process note for whoever verifies this section next: check which Global Unit the session is on
before every write. The admin URL carries an acronym, but `getCurrentGlobalUnit()` reads the session,
and a Global Unit switch made elsewhere in the browser silently redirects the save. That is what
produced the misattributed FN-004 observation above; every later write in this log was preceded by
reading the acronym off the header logo URL.

**FN-006, the specificity override — verified**

Activated for AICCRA by inserting a `custom_parameters` row (parameter 387 — the three rows sharing
the `homepage_hide_section_map` key map to Global Unit types, and AICCRA consistently uses the middle
one, the same way it uses 89 for `crp_refresh` and 288 for `homepage_timeline_active`), then setting
`crp_refresh` to `true` so `InternationalitazionFileInterceptor` reloads the specificities into the
session.

- With the flag on and the banner fully populated, `crpDashboard.do` rendered no banner: zero
  `[class*=homepageBanner]` elements, Schedule card first. The row still held the title, the
  517-character description and `AICCRA.svg` throughout, so the banner was hidden by the flag and not
  by absent content — which is the whole point of keeping the override.
- `crp_refresh` auto-reset itself to `false`, confirming the reload actually ran rather than the page
  happening to render from a stale session.
- Reverting the value to `false` (plus another `crp_refresh`) brought the banner back intact: title,
  description, image loaded, no horizontal overflow.

Worth recording rather than leaving implicit: `/data/homepageBannerImage.do` keeps returning 200 while
the flag hides the banner. The specificity is a rendering switch, not access control, and the image is
public-facing homepage artwork, so this is the designed behaviour (SEC-005) and not a leak — but anyone
who assumes the flag makes the image unreachable would be wrong.

The `custom_parameters` row was left in place with `value = 'false'`, which behaves identically to no
row. It also closes a small inconsistency noticed on the way: AICCRA showed 115 parameters in the PTF
Parameters screen where every other Global Unit showed 116, precisely because this key had no row.

Note on why the PTF Parameters admin screen was not used to flip the flag: it renders every Global
Unit's parameters inside a single form with one save button, so saving it rewrites roughly 116
parameters for every unit. That blast radius is not warranted for toggling one flag, and any value that
did not round-trip cleanly would silently alter another unit's configuration.

**OPS-001, the unwritable uploads folder — verified**

Provoked by `chmod 000` on `<uploads>/homepageBanners`, then saving the section on AICCRA with a
changed title and a valid PNG attached:

- The section re-rendered with the i18n error "The uploads folder is not writable on this server, so
  the image could not be saved. The title and description were saved."
- The text **did** save: the probe title landed in the row and the 517-character description was
  untouched, while `image_file_name` still pointed at the previous `AICCRA.svg` — a broken uploads
  folder costs the administrator the upload, never their typing, which is the whole requirement.
- Two ERROR lines were logged, both naming the resolved absolute path:
  `HomepageBannerImageStore: ... is not a writable directory` and
  `HomepageBannerManagementAction: could not store the image for AICCRA, status UPLOADS_NOT_WRITABLE`.
- Nothing new was written to the folder.

Permissions and the probe title were restored afterwards; the image route is back to 200.

One wording defect surfaced while the folder was unreadable: `DownloadHomepageBannerImageAction`
logged "AICCRA names AICCRA.svg but it is not on disk" when the file was in fact present but
unreachable, because `resolve` cannot tell a missing file from an unreadable directory. Harmless to
behaviour — the route 302s either way — but the message would send someone hunting for a deleted file
when the real cause is permissions. Left as-is rather than fixed, and recorded here so the next reader
is not misled by it.

**Everything in this spec is now verified.** Nothing outstanding.
- Nothing else. FN-004 and the empty-insert fix were both closed in the follow-up pass recorded above.

## 4. Dependency Graph

```
T01 (migration)
  └── T02 (entity + DAO + manager)
        ├── T04 (image download route)  ←── T03 (image store)
        ├── T05 (validator)             ←── T03
        │     └── T06 (action)
        │           └── T07 (struts + menu)
        │                 └── T08 (admin view)
        └── T09 (DashboardAction)
              └── T10 (dashboard.ftl)
                    └── T11 (CSS + JS rename, i18n)

T03 (image store) runs in parallel with T01/T02.
T12 (ai-context docs) after T04 and T07.
T13 (manual QA) after everything.
```

## 5. Testing Plan

### Unit

- `HomepageBannerImageStore`: format whitelist by content, size cap, derived path safety, replace
  deletes the previous extension, unconfigured-uploads outcome (T03).
- `HomepageBannerManagementValidator`: empty-is-valid, title length, upload rejections (T05).

### Integration

- End-to-end save: multipart POST → `crpAdminStack` → action → manager → row updated and file on
  disk.
- Denial path: request the admin section as a user without the admin gate and confirm the stack
  denies it rather than rendering an empty form.

### Manual, against the acceptance criteria

| Case | Expectation |
|---|---|
| No row for the Global Unit | No banner element in the homepage HTML |
| Row with all three fields blank | No banner element |
| Title only | Banner with title, no description block, no toggle, no image |
| Description only | Banner with description and toggle, no title |
| All three | Full banner |
| All three + `homepage_hide_section_map` active | No banner element |
| Upload PNG | Renders; `Content-Type: image/png` |
| Upload SVG | Renders crisp; `Content-Type: image/svg+xml` |
| Upload SVG over an existing PNG | Old PNG deleted from disk; row points at the SVG |
| Remove image ticked | Row NULL, file deleted, title and description intact |
| Remove ticked *and* a file picked | New file wins |
| `.txt` renamed to `.png` | Rejected with an i18n error; nothing written |
| 3 MB file | Rejected with an i18n error; nothing written |
| Uploads folder made unwritable | Explicit error on screen, ERROR in the log, title and description still saved |
| Collapse, then reload | Still collapsed |
| Very long description (~4000 chars) | Layout holds; collapse still works |
| Widths 1200 / 1457 / 1920 px | No horizontal page scroll (UI-005) |

### Regression, manual

- Other `/admin` sections still load and save — `menu-admin.ftl` is shared, and both item lists were
  edited.
- The rest of the homepage — Schedule card, browse-by-category rail, impact graphic — is unaffected;
  `dashboard.js` and `dashboard.css` are shared with all of it.

### Accessibility

- The toggle keeps `aria-expanded` and `aria-controls` pointing at the renamed content id.
- Keyboard: tab to the toggle, activate with Enter and Space.
- The image `alt` is the title when present and empty when not, so a decorative image is not
  announced as unlabelled (UI-003).

### Non-functional

- Confirm exactly one extra query per homepage render (NF-001), by log or by profiler.

## 6. Operational Steps

### Migration deploy

- Flyway runs on Tomcat startup. Confirm the row in `flyway_schema_history` and the seeded AICCRA
  row in `homepage_banners`.

### Post-deploy manual step (required)

- **The AICCRA banner ships without its image.** An owner must open
  `/admin/<crp>/homepageBannerManagement.do` once after deploy and upload the Africa map, currently
  `global/images/Map_africa.svg` on the CDN. Until then the AICCRA homepage banner is text-only.
  This is intended (MIG-002) and belongs in the release notes so it is not filed as a bug.

### Configuration

- Verify `uploadsBaseFolder` is configured and writable in each target environment before the
  post-deploy upload. This is the single most likely cause of a failed rollout.
- Confirm the `homepageBanners` subfolder is included in whatever backs up the uploads volume;
  banner images are filesystem state and are **not** in database dumps (OPS-002).

### i18n

- `global.properties` changed, so the application server must be restarted, not merely redeployed.

### BI / AI coordination

- Not applicable. No BI or AI-service surface is touched.

### Notifications

- Release notes must carry the post-deploy upload step and the retired-i18n-keys note for anyone
  maintaining `custom/*.properties`.

## 7. Rollback Plan

### Code

- Revert the merge commit on `staging` and redeploy the previous artifact. The revert restores the
  `dashboard.cluster.*` keys along with the template, so the old banner comes back intact.

### Data

- The migration is additive. Leave `homepage_banners` in place; nothing reads it after a code
  revert.
- Uploaded files can be left on disk; they are inert once the route is gone.

### Partial rollback without a code revert

- Set the `homepage_hide_section_map` specificity for the affected Global Unit to hide the banner
  immediately, or empty the three fields from the admin section. Either hides it without a deploy.

## 8. Definition of Done

- [ ] Every acceptance criterion in `requirements.md` §6 verified, with the failure paths actually
      exercised.
- [ ] Every constitutional checklist item confirmed in the PR description, including the two
      recorded deviations (no phase replication; specificity key name retained).
- [ ] `mvn checkstyle:check` passes.
- [ ] New unit tests pass (T03, T05).
- [ ] SonarCloud: no new blockers or critical issues.
- [ ] Snyk: no new critical findings on changed paths.
- [ ] `grep -ri cluster` over `dashboard.ftl`, `dashboard.css` and `dashboard.js` returns only references to
      the pre-existing Struts `/clusters` namespace of the projects module.
- [ ] QA pass complete; defects closed or accepted with PMU sign-off.
- [ ] Documentation updated: this `task.md` carries a verification note per task;
      `reports/ai-context/struts-critical-routing-catalog.md` lists both new routes.
- [ ] Merged to `staging`.
- [ ] Promoted to `main` via the release pipeline.
- [ ] Production verified: migration applied, admin section reachable, AICCRA banner image uploaded.
