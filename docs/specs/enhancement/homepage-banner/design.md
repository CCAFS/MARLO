# Homepage Banner — Design

**Spec ID:** ENH-HOMEPAGE-BANNER-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-28
**Implements requirements:** FN-001..FN-009, NF-001..NF-003, DA-001..DA-005, MIG-001, MIG-002, UI-001..UI-005, SEC-001..SEC-005, OPS-001, OPS-002
**Touches modules:** marlo-web, marlo-data

---

## 1. Architecture Summary

One row per Global Unit holds the banner's title, description and image file name. The homepage
action reads that row and hands the view a banner object, or `null` when the row carries no
content; the view renders nothing when it is `null`. A new `/admin` section writes the row and
writes the image file to the uploads folder. A public `stream` route serves the image back.

The whole flow reuses two patterns already present in the repository: the `/admin` section shape of
`TimelineManagementAction`, and the upload/serve pair of `UploadGlobalUnitLogoAction` /
`DownloadGlobalUnitLogoAction`.

```
ADMIN WRITE PATH
  /admin/{crp}/homepageBannerManagement  (crpAdminStack)
        │
        ├─ prepare()  ── HomepageBannerManager.findByGlobalUnit(currentGlobalUnit)
        │                 └─ null → empty HomepageBanner for the form
        │
        └─ save()     ── validate() [if (save)] → HomepageBannerManagementValidator
                          ├─ image present?  → BannerImageStore.store(globalUnit, file)
                          │                     └─ uploads/homepageBanners/<ACRONYM>.<ext>
                          ├─ removeImage?    → BannerImageStore.delete(globalUnit)
                          └─ HomepageBannerManager.saveHomepageBanner(banner)   [upsert]

HOMEPAGE READ PATH
  /home/dashboard  ── DashboardAction.prepare()
                       └─ HomepageBannerManager.findByGlobalUnit(...)
                            └─ all three fields blank → getHomepageBanner() returns null
                                 │
                       dashboard.ftl
                         [#if !hasSpecificities('homepage_hide_section_map') && homepageBanner??]
                            title? description? image? each rendered only when present
                                 │
                            <img src="/data/homepageBannerImage.do?acronym=X">
                                 └─ DownloadHomepageBannerImageAction (stream, dynamic contentType)
```

## 2. Module Footprint

### marlo-web

- New: `action/crp/admin/HomepageBannerManagementAction.java`
- New: `validation/superadmin/HomepageBannerManagementValidator.java`
- New: `action/downloads/DownloadHomepageBannerImageAction.java`
- New: `utils/HomepageBannerImageStore.java` — the filesystem side: path derivation, format and
  size checks, write, delete. Kept out of the action so the action stays readable and the rules are
  testable on their own.
- New: `webapp/WEB-INF/crp/views/admin/homepageBannerManagement.ftl`
- New: `resources/database/migrations/V2_6_0_20260828_0900__CreateHomepageBannerTable.sql`
- Modified: `action/home/DashboardAction.java` — inject `HomepageBannerManager`, expose
  `getHomepageBanner()`.
- Modified: `resources/struts-admin.xml` — new `{crp}/homepageBannerManagement` action.
- Modified: `resources/struts-data.xml` — new `homepageBannerImage` stream action.
- Modified: `webapp/WEB-INF/crp/views/admin/menu-admin.ftl` — new menu entry in both the AICCRA and
  the non-AICCRA item lists.
- Modified: `webapp/WEB-INF/crp/views/home/dashboard.ftl` — banner rewritten, hotspots removed.
- Modified: `webapp/crp/js/home/dashboard.js` — `initClusterBanner` → `initHomepageBanner` and
  identifiers renamed.
- Modified: `webapp/crp/css/home/dashboard.css` — `.clusterBanner*` → `.homepageBanner*`,
  `.clusterMap*` rules deleted.
- Modified: `resources/global.properties` — retire `dashboard.cluster.*`, add `dashboard.banner.*`
  and `homepageBannerManagement.*`.

### marlo-data

- New: `data/model/HomepageBanner.java`
- New: `resources/xmls/HomepageBanner.hbm.xml`
- New: `data/dao/HomepageBannerDAO.java`
- New: `data/dao/mysql/HomepageBannerMySQLDAO.java`
- New: `data/manager/HomepageBannerManager.java`
- New: `data/manager/impl/HomepageBannerManagerImpl.java`
- Modified: `resources/hibernate.cfg.xml` — register `xmls/HomepageBanner.hbm.xml`.

### marlo-core / marlo-utils

- Not applicable. No shared utility or core change is required.

### APConstants

- Not applicable. No new specificity key, so neither `APConstants.java` changes.

## 3. Data Model Changes

### Migration

File: `marlo-web/src/main/resources/database/migrations/V2_6_0_20260828_0900__CreateHomepageBannerTable.sql`

```sql
CREATE TABLE `homepage_banners` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `global_unit_id` bigint(20) NOT NULL,
  `title` varchar(500) DEFAULT NULL,
  `description` text,
  `image_file_name` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_homepage_banners_global_unit` (`global_unit_id`),
  CONSTRAINT `fk_homepage_banners_global_unit` FOREIGN KEY (`global_unit_id`)
    REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_homepage_banners_created_by` FOREIGN KEY (`created_by`)
    REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_homepage_banners_modified_by` FOREIGN KEY (`modified_by`)
    REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- Seed the banner that is currently hardcoded in global.properties, so the
-- AICCRA homepage reads the same after this deploy as before it. Resolved by
-- acronym: global unit ids are not stable across environments. INSERT ...
-- SELECT with a WHERE on the acronym is also the no-op when the Global Unit is
-- absent, which is the case on a fresh database.
INSERT INTO `homepage_banners`
  (`global_unit_id`, `title`, `description`, `image_file_name`, `modification_justification`)
SELECT gu.`id`,
       'What is a Cluster?',
       'A cluster is defined as the group of AICCRA main activities led by each AICCRA Country Leader (Ghana, Mali, Senegal, Ethiopia, Kenya and Zambia), AICCRA Regional Leaders (Western Africa and Eastern & Southern Africa), and AICCRA Thematic leaders (Theme 1, Theme 2, Theme 3, and Theme 4). In each cluster, participants are involved as leaders, coordinators and collaborators with specific budget allocations for each AICCRA main activity with a set of deliverables and contributions towards our performance indicators.',
       NULL,
       'Seeded from global.properties by ENH-HOMEPAGE-BANNER-001'
FROM `global_units` gu
WHERE gu.`acronym` = 'AICCRA';
```

### Entity

`HomepageBanner extends MarloAuditableEntity implements java.io.Serializable, IAuditLog`, mirroring
`Timeline`. Fields: `id`, `globalUnit` (many-to-one), `title`, `description`, `imageFileName`, plus
the auditable fields the base class provides.

The entity also carries one derived, non-persistent helper used by both the action and the view:

```java
public boolean isEmpty() {
  return StringUtils.isBlank(title) && StringUtils.isBlank(description)
    && StringUtils.isBlank(imageFileName);
}
```

`HomepageBanner.hbm.xml` maps the audit columns the way `CrpsSitesIntegration.hbm.xml` does
(`modificationJustification`, `activeSince` with `update="false"`, `createdBy` / `modifiedBy` as
`many-to-one` to `User`, `active` → `is_active`), plus the three content columns and the
`global_unit_id` many-to-one. Registered in `hibernate.cfg.xml` beside the other mappings.

### Indices, FKs, enums

- Unique key `uk_homepage_banners_global_unit` — enforces DA-002 at the database level, so a race
  between two administrators cannot produce two banners.
- FKs to `global_units` and to `users` (twice), matching the audit pattern.
- No new enums.

### Backfill / data migration

The seed above is the only data movement. No other table is touched, and nothing is deleted.

## 4. API / Action Surface

### Struts actions (.do)

| Route | Action class | Stack | View result |
|---|---|---|---|
| `/admin/{crp}/homepageBannerManagement` | `HomepageBannerManagementAction` | `crpAdminStack` | `/WEB-INF/crp/views/admin/homepageBannerManagement.ftl` |
| `/data/homepageBannerImage` | `DownloadHomepageBannerImageAction` | none (public `data` package) | `stream` |

`struts-admin.xml`, mirroring the `timelineManagement` entry exactly:

```xml
<action name="{crp}/homepageBannerManagement"
  class="org.cgiar.ccafs.marlo.action.crp.admin.HomepageBannerManagementAction">
  <interceptor-ref name="crpAdminStack" />
  <result name="input">/WEB-INF/crp/views/admin/homepageBannerManagement.ftl</result>
  <result name="success" type="redirectAction">
    <param name="actionName">${crpSession}/homepageBannerManagement</param>
    <param name="edit">true</param>
  </result>
</action>
```

`struts-data.xml`, mirroring `globalUnitLogo` but with a dynamic content type, because the stored
file may be PNG, JPEG or SVG:

```xml
<action name="homepageBannerImage"
  class="org.cgiar.ccafs.marlo.action.downloads.DownloadHomepageBannerImageAction">
  <result name="success" type="stream">
    <param name="contentType">${contentType}</param>
    <param name="inputName">fileInputStream</param>
    <param name="bufferSize">1024</param>
  </result>
  <result name="error" type="redirectAction">
    /WEB-INF/global/pages/error/404.ftl
  </result>
</action>
```

Unlike the logo route there is **no default image fallback**: a Global Unit with no banner image
must not produce a placeholder, because the view only emits the `<img>` when `imageFileName` is set.
The `error` result redirects to the 404 page, so the observable response is a `302` to that page
rather than a bare `404` status — that is how every `error` result in `struts-data.xml` already
behaves, `globalUnitLogo` included.

### Spring MVC REST

- Not applicable. No `/api/*` surface.

### Existing JSON endpoints

- Not applicable. The image upload rides the section's own multipart POST rather than a new
  `*.json` path, per `AGENTS.md`.

## 5. Frontend Composition

### `dashboard.ftl`

The `[#assign clusterHotspots = [...]]` block and the entire `div.clusterBanner__map` are deleted.
What replaces the section:

```html
[#if !action.hasSpecificities('homepage_hide_section_map') && homepageBanner??]
  <section class="homepageBanner" id="homepageBanner">
    <div class="homepageBanner__body">
      <div class="homepageBanner__head">
        <svg class="homepageBanner__icon" ...></svg>
        [#if homepageBanner.title?has_content]
          <h2 class="homepageBanner__title">${homepageBanner.title}</h2>
        [/#if]
        [#if homepageBanner.description?has_content]
          <button type="button" class="homepageBanner__toggle" id="homepageBannerToggle"
            aria-expanded="true" aria-controls="homepageBannerContent"
            data-label-hide="[@s.text name="dashboard.banner.hide" /]"
            data-label-show="[@s.text name="dashboard.banner.show" /]"> ... </button>
        [/#if]
      </div>
      [#if homepageBanner.description?has_content]
        <div class="homepageBanner__content" id="homepageBannerContent">
          <p class="homepageBanner__text">${homepageBanner.description}</p>
        </div>
      [/#if]
    </div>
    [#if homepageBanner.imageFileName?has_content]
      <div class="homepageBanner__image">
        <img src="${baseUrl}/data/homepageBannerImage.do?acronym=${crpSession}"
          alt="[#if homepageBanner.title?has_content]${homepageBanner.title}[/#if]">
      </div>
    [/#if]
  </section>
[/#if]
```

Three composition notes. The collapse toggle is emitted only when there is a description, because
with no description there is nothing to collapse. No `?html` appears anywhere: FreeMarker
auto-escaping is on under Struts 6.8 in this codebase, so `${homepageBanner.title}` is escaped
already and adding `?html` is a parse error that 500s the page. And the image URL is the idiom
already used for the Global Unit logo in `header.ftl:213` —
`${baseUrl}/data/globalUnitLogo.do?acronym=${crpSession}` — where `crpSession` is the Global Unit
acronym; the store and the download action both normalise it (trim, uppercase) exactly as
`UploadGlobalUnitLogoAction` does, so its casing in the URL is irrelevant.

The image panel carries no `id`: nothing addresses it from JavaScript, and the collapsed state is
reached through `.homepageBanner--collapsed .homepageBanner__image` in CSS.

### `dashboard.js`

`initClusterBanner` → `initHomepageBanner`; element ids `clusterBannerToggle` / `clusterBanner` →
`homepageBannerToggle` / `homepageBanner`; the collapsed class
`clusterBanner--collapsed` → `homepageBanner--collapsed`; the storage key
`marlo.clusterBanner.collapsed` → `marlo.homepageBanner.collapsed`. The function's guard already
returns early when the elements are absent, so a hidden banner needs no further handling. The
docblock stops naming the AICCRA cluster copy.

Changing the storage key resets each user's collapsed preference once. Accepted: the alternative is
carrying a `cluster` string forward forever, and the cost is one extra click for users who had
collapsed the banner.

### `dashboard.css`

`.clusterBanner*` → `.homepageBanner*` throughout, including inside the two media-query blocks, and
`.clusterBanner__map` → `.homepageBanner__image`. Every `.clusterMap`, `.clusterMap__spot`,
`.clusterMap__tip` and `.clusterMap__spot--tipEnd` rule is deleted — roughly lines 192-262 plus
their media-query overrides — along with the section comment that describes the twelve hotspots.

The tooltip rules are what forced the `--tipEnd` fix for horizontal page scroll below 1457px. With
the tooltips gone the cause is gone, but UI-005 still requires a check at that width, because the
image panel keeps the same flex sizing.

### `homepageBannerManagement.ftl`

Built on the `timelineManagement.ftl` skeleton: the same front-matter assigns, breadcrumb,
`generalMessages.ftl`, `menu-admin.ftl` in the left column, and an info alert carrying
`homepageBannerManagement.help`. The form is `[@s.form action=actionName
enctype="multipart/form-data"]` and holds:

- a text input bound to `homepageBanner.title`,
- a textarea bound to `homepageBanner.description`,
- the current image, when one exists, rendered through the same `/data/homepageBannerImage.do`
  route the homepage uses, so the administrator sees exactly what visitors see (UI-002),
- a file input bound to `image`, with `accept="image/png,image/jpeg,image/svg+xml"`,
- a checkbox bound to `removeImage`, disabled when no image is stored,
- the standard save button block.

Help text states plainly that clearing all three fields hides the banner from the homepage — that
behaviour is discoverable only if it is written down (FN-004).

### i18n

Removed from `global.properties`: `dashboard.cluster.title`, `dashboard.cluster.description`,
`dashboard.cluster.mapAlt` (all three are now data), plus `dashboard.cluster.browse` and
`dashboard.cluster.glossary`, which are referenced by no FTL and no JS in the repository.

Added: `dashboard.banner.hide`, `dashboard.banner.show`, `menu.superadmin.homepageBannerManagement`,
`homepageBannerManagement.title`, `.help`, `.field.title`, `.field.description`, `.field.image`,
`.field.image.current`, `.field.image.remove`, `.field.image.hint`, and the error keys
`.error.titleTooLong`, `.error.invalidImageFormat`, `.error.imageTooLarge`,
`.error.uploadsNotConfigured`, `.error.uploadsNotWritable`, `.error.imageWriteFailed`,
`.success.saved`.

Property-file changes require a server restart to take effect; that is a deploy step, not a code
concern, and it is called out in `task.md`.

## 6. Persistence & Phase Replication Plan

**Not applicable by design, deliberately.** The banner has no phase column and takes part in no
forward replication. `HomepageBannerManagerImpl.saveHomepageBanner` persists exactly one row and
returns.

This is a recorded deviation from the phased-data default (`CLAUDE.md` hard rule 1), justified in
the `requirements.md` Decision Log: the banner is homepage chrome and not reportable cycle data, so
there is no past-phase immutability question and nothing to replicate forward. `Timeline`, the
closest existing analogue and also Global-Unit-wide, is modelled the same way.

Delete flow: there is no delete. Clearing the three fields leaves the row present and empty, which
is what makes the banner disappear. The row is deleted only if its Global Unit is deleted, and the
FK is `ON DELETE RESTRICT`, consistent with the rest of the schema.

## 7. Validation & Save Pipeline

- Interceptor stack: `crpAdminStack`, declared on the action in `struts-admin.xml`.
- `Action.validate()` guarded with `if (save) { ... }`, delegating to
  `HomepageBannerManagementValidator extends BaseValidator`.
- Validator rules, all non-blocking on emptiness because an empty banner is valid:
  - title length ≤ 500 characters → `homepageBannerManagement.error.titleTooLong`;
  - when a file was uploaded, format in {PNG, JPEG, SVG} verified against content →
    `.error.invalidImageFormat`;
  - when a file was uploaded, size ≤ 2 MB → `.error.imageTooLarge`.
- On invalid: the action populates `invalidFields` and action errors and the view re-renders with
  field-level and page-level messages. No file is written and no row is updated (SEC-002 AC).
- On valid: text fields are written first, then the image side effect runs. If the image write
  fails, the action adds the corresponding action error and still saves the text — OPS-001's
  acceptance criterion requires exactly that, so a broken uploads folder does not cost the
  administrator their typing.

Ordering detail worth stating because it is easy to get backwards: `removeImage` is honoured
*before* a new upload is considered, so an administrator who ticks remove *and* picks a file ends
up with the new file rather than with nothing.

## 8. Permissions & Edit Gates

- The admin section sits behind `crpAdminStack`, the same stack every other `/admin` section uses;
  `save()` additionally checks `this.hasPermission("*")`, matching `TimelineManagementAction`.
- The image route is public and unauthenticated, in the `data` package next to `globalUnitLogo`.
  This is intentional: the banner image is rendered on the homepage, which is itself reachable by
  any authenticated user, and the route exposes nothing but that one image per Global Unit. The
  action takes an acronym and derives the path from it; it accepts no path fragment from the caller
  (SEC-003, SEC-005).

## 9. Specificity / Feature-Flag Strategy

No new specificity. The existing `homepage_hide_section_map` key is retained unchanged as a hard
override: the banner renders only when the flag is inactive *and* content exists (FN-006). Neither
`parameters` nor `custom_parameters` is touched, and neither `APConstants.java` changes.

The key's name still says "map" while the component no longer holds a map. Renaming it was
considered and rejected in the Decision Log: the rename would require a `parameters` migration plus
per-Global-Unit `custom_parameters` data movement for no functional gain. The mismatch is documented
here and in the `dashboard.ftl` comment at the conditional.

## 10. Integration Points

- Not applicable. No CLARISA, CGSpace, BI, AI-services, S3 or Pusher interaction. The BI pipeline
  does not read homepage chrome.

## 11. Observability

- `HomepageBannerImageStore` logs at ERROR when the uploads base folder is unconfigured, cannot be
  created, is not writable, or a write fails — each with the resolved absolute path, which is the
  single most useful fact when this breaks in an environment.
- Warns at WARN when a rejected upload is discarded, with the rejection reason and the Global Unit
  acronym.
- `DownloadHomepageBannerImageAction` warns when a row names a file that is not on disk: that
  combination means database and filesystem have drifted, which is the predictable failure mode of
  the storage decision recorded in the Decision Log.
- Audit columns on `homepage_banners` record who last edited the banner, via
  `HibernateAuditLogListener`.

## 12. Performance & Scalability

One indexed single-row lookup per homepage render, on a unique key (NF-001). The table holds one
row per Global Unit — tens of rows, forever.

The image is a separate HTTP request served from disk by the container, cached by the browser like
any other image. No caching headers are added beyond the container defaults, which means an
administrator replacing an image may need a hard reload to see it; the file name is stable across
replacements for a given format, and that trade-off buys a URL the view can build without a
database round trip for a cache-busting token. If it proves annoying in practice, appending the
banner's `active_since` as a query parameter is the follow-up.

## 13. Security Considerations

- Upload format is verified against content, not against the file name or the browser's content
  type: `ImageIO.read` must return non-null for PNG and JPEG, and the SVG path must parse as XML
  with an `svg` root element (SEC-002).
- The stored path is fully derived — uploads base + fixed `homepageBanners` folder + the Global
  Unit acronym, uppercased and stripped, + an extension chosen from the whitelist, which maps
  exactly: PNG → `.png`, JPEG → `.jpg`, SVG → `.svg`. Nothing from the uploaded file name reaches
  the filesystem, so traversal via `../` in a file name is not expressible (SEC-003). The file name
  is therefore fully reconstructible from the acronym plus the extension, and `image_file_name` in
  the database stores that name so the download action knows which extension to serve.
- 2 MB cap, checked before the file is read into an image (SEC-004).
- SVG is stored verbatim and served as `image/svg+xml`. It is referenced only as an `<img>` source,
  a context in which scripts and external references in the SVG do not execute. It is nonetheless a
  file an administrator uploads and every visitor loads, so the upload gate — `*` permission behind
  `crpAdminStack` — is the control that matters here.
- The description renders through FreeMarker with auto-escaping on, so administrator-entered text
  cannot inject markup into the homepage.

## 14. Backwards Compatibility & Rollout

- The migration is purely additive: a new table plus one seeded row.
- The seed is what keeps the change invisible to AICCRA on deploy day for text. The **image is not
  seeded**, so the AICCRA homepage renders text-only until an administrator uploads the map once.
  That is a visible, intended, one-step-manual regression, and it must be on the release notes so
  it is not reported as a bug.
- Rollback: revert the code; leave the table in place. The old `dashboard.cluster.*` keys would need
  to come back with the code revert, which a plain `git revert` of the merge handles.
- No dual-running period, no feature flag beyond the retained specificity.

## 15. Decision Records

### ADR-ENH-HOMEPAGE-BANNER-001-1 — Remove the map hotspots rather than make them configurable
- Decision: the banner image is a static illustration; the twelve hotspots are deleted.
- Rationale: the hotspots encode AICCRA project ids and coordinates measured against one specific
  SVG. They cannot survive a swapped image, and making them data would mean a child table and a
  coordinate-picking UI nobody has asked for.
- Alternatives considered: keep the map as a fallback when no image is uploaded (rejected: keeps
  `cluster` nomenclature and AICCRA specifics inside the generic component); make hotspots
  configurable (rejected on scope).
- Status: Accepted.

### ADR-ENH-HOMEPAGE-BANNER-001-2 — Global Unit scope, not phase scope
- Decision: `homepage_banners` has no phase column and no replication.
- Rationale: homepage chrome, not cycle data. Phase scoping would add forward replication and a
  fallback rule for content-less phases with no user benefit. `Timeline` sets the precedent.
- Status: Accepted, as an explicit deviation from `CLAUDE.md` hard rule 1.

### ADR-ENH-HOMEPAGE-BANNER-001-3 — Filesystem storage with a stream route
- Decision: images live under `uploadsBaseFolder/homepageBanners/`; the database stores the file
  name only.
- Rationale: `UploadGlobalUnitLogoAction` and `DownloadGlobalUnitLogoAction` already establish this
  pattern; the database stays small and the container serves bytes.
- Alternatives considered: `LONGBLOB` in the row (rejected: no precedent for image BLOBs in MARLO,
  and it still needs the stream action); an admin-entered URL (rejected: the requirement is that the
  administrator uploads an image).
- Consequence, accepted: banner images do not travel with database dumps.
- Status: Accepted.

### ADR-ENH-HOMEPAGE-BANNER-001-4 — Keep `homepage_hide_section_map` as a hard override
- Decision: render only when the flag is inactive and content exists; do not rename the key.
- Rationale: an administrator can hide the banner without losing content, and no Global Unit that
  currently has the flag set sees a banner appear unexpectedly. Renaming the key would mean a
  `parameters` migration plus `custom_parameters` data movement for zero functional gain.
- Status: Accepted, with the name mismatch documented in this design and in the template.

### ADR-ENH-HOMEPAGE-BANNER-001-5 — Accept SVG uploads
- Decision: the whitelist is PNG, JPEG and SVG; SVG is stored verbatim and served with a dynamic
  content type.
- Rationale: the illustration in production use is an SVG map; rasterising it the way the logo
  pipeline does would visibly degrade it. `<img>` context does not execute SVG scripts.
- Status: Accepted.

### ADR-ENH-HOMEPAGE-BANNER-001-6 — Filesystem rules live in a separate class
- Decision: path derivation, validation, write and delete go in `HomepageBannerImageStore`, not in
  the action.
- Rationale: the security-relevant rules (SEC-002..SEC-004) are then testable without an HTTP
  request, and the action stays short enough to read in one screen.
- Status: Accepted.

## 16. Open Risks

- Risk: an environment's `uploadsBaseFolder` is misconfigured — in this checkout it points at a
  Windows path, which on macOS makes every write fail. Mitigation: OPS-001's explicit error message
  and ERROR log, plus a manual test case that runs with the folder deliberately unwritable.
- Risk: database and filesystem drift, so a row names a missing file and the homepage shows a broken
  image. Mitigation: the download action warns on the mismatch; a follow-up could have the homepage
  omit the `<img>` when the file is absent, at the cost of a stat per render. Not doing that now.
- Risk: the AICCRA homepage ships without its map until someone uploads it. Mitigation: release
  note plus a named owner for the one-time upload, tracked in `task.md`.
- Risk: an administrator pastes a very long description and the banner layout breaks. Mitigation:
  the collapse control already exists; the manual QA pass includes a long-text case.
