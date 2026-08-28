# Homepage Banner — Requirements

**Spec ID:** ENH-HOMEPAGE-BANNER-001
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-28
**Related PRD sections:** docs/prd.md — homepage, administration of program-specific content
**Related System Design sections:** docs/system-design/design.md — homepage information architecture, component inventory, accessibility commitments
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (data model), §5 (frontend composition)
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md, reports/ai-context/struts-critical-routing-catalog.md

---

## 1. Overview

The homepage opens with an explanatory banner. Today that banner is hardcoded: its title, its body
text and its illustration are AICCRA-specific content baked into `dashboard.ftl` and
`global.properties`, and the illustration is an interactive map of Africa whose twelve hotspots
carry hardcoded project ids and coordinates. This spec replaces the hardcoded banner with a
banner whose title, description and image are entered by an administrator from the `/admin`
module, one banner per Global Unit, and which disappears entirely when no content has been
entered. It also retires the "cluster" nomenclature from the component: the banner is a generic
homepage banner, not a cluster explainer.

This spec exists now because the component is currently unusable by any Global Unit other than
AICCRA. A new program landing on MARLO either sees AICCRA's cluster definition and a map of
Africa, or has to have a developer edit an FTL template and ship a release.

## 2. Problem Statement

Three distinct pains, all traceable to the same cause — homepage banner content lives in code:

1. **Program-specific content in shared code.** A Global Unit cannot state what its own homepage
   banner says. The copy in `global.properties` describes AICCRA clusters, and every Global Unit
   that renders the homepage renders that copy.
2. **No way to turn it off short of a code change.** The only lever today is the
   `homepage_hide_section_map` specificity, which an administrator cannot reach from the UI.
3. **Domain nomenclature leaking into a generic component.** The CSS classes, the JavaScript
   identifiers, the localStorage key and the i18n keys all say `cluster`, which misleads any
   developer reading the homepage and blocks reuse.

## 3. In-Scope Requirements

### Functional

- ENH-HOMEPAGE-BANNER-001-FN-001 — The system MUST provide a section in the `/admin` module where
  an administrator can enter and save a homepage banner title, description and image for the
  current Global Unit.
- ENH-HOMEPAGE-BANNER-001-FN-002 — The system MUST persist exactly one homepage banner per Global
  Unit; saving again MUST update that banner rather than create a second one.
- ENH-HOMEPAGE-BANNER-001-FN-003 — The homepage MUST render the banner using the stored title,
  description and image instead of any hardcoded content.
- ENH-HOMEPAGE-BANNER-001-FN-004 — The homepage MUST NOT render the banner at all when the stored
  banner has no title, no description and no image.
- ENH-HOMEPAGE-BANNER-001-FN-005 — The homepage MUST render the banner when at least one of the
  three fields has content, and MUST omit each individual field that has no content.
- ENH-HOMEPAGE-BANNER-001-FN-006 — The homepage MUST continue to hide the banner when the
  `homepage_hide_section_map` specificity is active, regardless of stored content.
- ENH-HOMEPAGE-BANNER-001-FN-007 — The administrator MUST be able to remove a previously uploaded
  image without clearing the title or description.
- ENH-HOMEPAGE-BANNER-001-FN-008 — The banner MUST keep its existing collapse/expand behaviour,
  including persistence of the collapsed state across page loads.
- ENH-HOMEPAGE-BANNER-001-FN-009 — The interactive map hotspots MUST be removed from the homepage.
  The banner image is a static illustration with no clickable regions.

### Non-Functional

- ENH-HOMEPAGE-BANNER-001-NF-001 — Loading the homepage MUST issue at most one additional database
  query for the banner.
- ENH-HOMEPAGE-BANNER-001-NF-002 — Rendering the banner MUST NOT depend on any JavaScript request:
  title, description and image URL MUST be present in the server-rendered HTML.
- ENH-HOMEPAGE-BANNER-001-NF-003 — Nomenclature: no identifier, CSS class, JavaScript symbol,
  storage key or i18n key belonging to the banner component may contain the word `cluster`. The Struts
  `/clusters` namespace of the projects module is out of scope: it is a live URL namespace, and renaming it
  is a separate change.

### Data

- ENH-HOMEPAGE-BANNER-001-DA-001 — A new table `homepage_banners` MUST be created via a Flyway
  migration, holding title, description, image file name and a foreign key to `global_units`.
- ENH-HOMEPAGE-BANNER-001-DA-002 — The table MUST enforce one banner per Global Unit with a unique
  constraint on `global_unit_id`.
- ENH-HOMEPAGE-BANNER-001-DA-003 — All three content columns MUST be nullable: an empty banner is a
  valid state and is how an administrator hides the banner.
- ENH-HOMEPAGE-BANNER-001-DA-004 — The banner MUST NOT be phase-scoped. It carries no phase
  reference and takes part in no forward replication.
- ENH-HOMEPAGE-BANNER-001-DA-005 — The entity MUST integrate with the MARLO audit trail
  (`MarloAuditableEntity` / `IAuditLog`) so banner edits are attributable.
- ENH-HOMEPAGE-BANNER-001-MIG-001 — The migration MUST seed the AICCRA banner with the title and
  description currently held in `global.properties` (`dashboard.cluster.title`,
  `dashboard.cluster.description`), resolving the Global Unit by acronym rather than by id, so that
  deploying this change does not blank the AICCRA homepage.
- ENH-HOMEPAGE-BANNER-001-MIG-002 — The migration MUST leave the seeded `image_file_name` NULL. The
  current illustration is a CDN asset, not an uploaded file; an administrator uploads it once after
  deploy.

### UI

- ENH-HOMEPAGE-BANNER-001-UI-001 — The admin section MUST appear in the `/admin` secondary menu for
  both AICCRA and non-AICCRA Global Units.
- ENH-HOMEPAGE-BANNER-001-UI-002 — The admin section MUST show the currently stored image, so the
  administrator can see what the homepage is rendering before replacing it.
- ENH-HOMEPAGE-BANNER-001-UI-003 — The banner image MUST use the banner title as its `alt` text
  when a title exists, and an empty `alt` when it does not.
- ENH-HOMEPAGE-BANNER-001-UI-004 — All labels, buttons, help text and error messages in the admin
  section MUST come from i18n keys. Banner title and description are data, not i18n strings.
- ENH-HOMEPAGE-BANNER-001-UI-005 — The homepage MUST NOT produce horizontal page scroll at any
  viewport width as a result of this change.

### Security

- ENH-HOMEPAGE-BANNER-001-SEC-001 — The admin section MUST be reachable only through the
  `crpAdminStack` interceptor stack, and its save path MUST additionally check the `*` permission,
  matching every other section of the `/admin` module.
- ENH-HOMEPAGE-BANNER-001-SEC-002 — Image upload MUST accept only PNG, JPEG and SVG, verified
  against file content and not only against the file name or the browser-supplied content type.
- ENH-HOMEPAGE-BANNER-001-SEC-003 — Uploaded images MUST be written under a fixed, derived path
  inside the configured uploads folder. No part of the stored path may come from the uploaded file
  name.
- ENH-HOMEPAGE-BANNER-001-SEC-004 — Image upload MUST reject files larger than 2 MB.
- ENH-HOMEPAGE-BANNER-001-SEC-005 — The image-serving route MUST be readable without
  authentication, matching the existing Global Unit logo route, and MUST expose nothing but the
  banner image of the requested Global Unit. When there is no image to serve it MUST NOT return a
  placeholder image; following the `struts-data.xml` convention, it redirects to the 404 page.

### Operations

- ENH-HOMEPAGE-BANNER-001-OPS-001 — When the uploads folder is unconfigured, missing or not
  writable, the admin section MUST surface an explicit i18n error message to the administrator and
  MUST log the failure. Silent failure is not acceptable.
- ENH-HOMEPAGE-BANNER-001-OPS-002 — Banner images are filesystem state, not database state.
  Environment promotion procedures MUST note that a database restore does not carry banner images.

## 4. Out-of-Scope

- Configurable hotspots. The banner image is static; per the design decision recorded below, the
  interactive map is removed rather than made configurable.
- Per-phase banner content. The banner is Global Unit scoped only.
- Multilingual banner content. One title and one description per Global Unit; MARLO's homepage is
  English-only today.
- Rich text in the description. Plain text only.
- A public REST endpoint for banner content.
- Exposing the `homepage_hide_section_map` specificity in the admin UI.
- Renaming the `homepage_hide_section_map` specificity key itself. The key stays; only the
  component's own nomenclature changes.

## 5. Personas Affected

- **Global Unit administrator (primary).** Gains the ability to write the homepage banner without a
  developer or a release.
- **Any homepage visitor.** Sees Global-Unit-appropriate banner content, or no banner, instead of
  AICCRA cluster copy.
- **PMU.** Can commission homepage messaging changes as content work rather than as a code change.
- **Developer / IBD team.** Reads a homepage component whose names describe what it is.

## 6. Acceptance Criteria

**AC for FN-001, FN-002:**
- Given an authenticated administrator of Global Unit X with the `*` permission,
- When they open the homepage banner section under `/admin`, enter a title, a description and an
  image, and save,
- Then exactly one `homepage_banners` row MUST exist for X holding those values,
- And re-opening the section MUST show the saved values and the stored image,
- And saving a second time MUST update the same row.

**AC for FN-003, FN-005, UI-003:**
- Given a stored banner for X with a title and a description but no image,
- When a user loads the homepage for X,
- Then the banner MUST render with that title and that description,
- And no `<img>` element MUST be emitted,
- And no hardcoded banner copy MUST appear anywhere in the response.

**AC for FN-004:**
- Given a stored banner for X whose title, description and image are all empty, or no row at all,
- When a user loads the homepage for X,
- Then no banner element MUST be present in the response.

**AC for FN-006:**
- Given a stored banner for X with all three fields filled,
- And the `homepage_hide_section_map` specificity active for X,
- When a user loads the homepage for X,
- Then no banner element MUST be present in the response.

**AC for FN-007:**
- Given a stored banner for X with a title, a description and an image,
- When the administrator checks the remove-image control and saves,
- Then `image_file_name` MUST be NULL,
- And the previously uploaded file MUST be deleted from the uploads folder,
- And the title and the description MUST be unchanged,
- And the homepage MUST render the banner without an image.

**AC for FN-008:**
- Given a rendered banner,
- When the user collapses it and reloads the page,
- Then the banner MUST render collapsed.

**AC for FN-009, NF-003:**
- Given the implemented change,
- When the homepage response, `dashboard.ftl`, `dashboard.css` and `dashboard.js` are inspected,
- Then no map hotspot markup, no hardcoded project id and no coordinate table MUST remain,
- And no banner-related identifier, CSS class, JavaScript symbol, storage key or i18n key MUST contain
  `cluster`,
- And the only surviving occurrences of `cluster` in those artefacts MUST be references to the pre-existing
  Struts `/clusters` namespace of the projects module, which this spec does not rename.

**AC for SEC-002, SEC-004:**
- Given the administrator uploads a file that is not a PNG, JPEG or SVG, or is larger than 2 MB,
- When they save,
- Then the section MUST re-render with an i18n error message,
- And no file MUST be written to the uploads folder,
- And the stored `image_file_name` MUST be unchanged.

**AC for OPS-001:**
- Given the configured uploads folder does not exist or is not writable,
- When the administrator uploads an image and saves,
- Then the section MUST re-render with an explicit i18n error naming the problem,
- And the failure MUST be logged at ERROR level,
- And the title and description MUST still save.

**AC for MIG-001:**
- Given a database at the revision immediately before this migration, containing the AICCRA Global
  Unit,
- When the migration runs,
- Then a `homepage_banners` row MUST exist for AICCRA carrying the previous
  `dashboard.cluster.title` and `dashboard.cluster.description` text,
- And the AICCRA homepage MUST render the same title and description as before the deploy.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: **not applicable** — DA-004 makes the banner Global Unit scoped, with no
      phase column and no replication. Recorded as a deliberate, justified deviation in the
      Decision Log.
- [x] Save validation: `HomepageBannerManagementAction.validate()` guarded by `if (save)` plus
      `HomepageBannerManagementValidator`; interceptor stack identified in `design.md`.
- [x] Permissions: the new admin action declares `crpAdminStack`; the new image route is a public
      `data` package route, matching `globalUnitLogo`.
- [x] Specificity: no new specificity. The existing `homepage_hide_section_map` is retained as a
      hard override (FN-006); no `parameters` / `custom_parameters` change.
- [x] Migrations: the new table and its seed ship as one Flyway migration following the
      `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql` naming.
- [x] i18n: UI-004 keeps every label keyed; banner content is data. Retired keys are removed, not
      left dangling.
- [x] License header: every new Java file carries the GPL header from `AGENTS.md`.
- [x] Code style: `mvn checkstyle:check` is a gate; 2-space indent, 120-char lines.
- [x] REST: **not applicable** — no `/api/*` endpoint. The image route is a Struts `stream` result
      following the pre-existing `globalUnitLogo` pattern, not a new `*.json` path.
- [x] Audit: DA-005 puts the entity on `MarloAuditableEntity` / `IAuditLog`.
- [x] Dependency floors: no dependency change.
- [x] Branching: feature branch from `staging`, merge target `staging`.

## 8. Open Questions

- Should the admin section be restricted further than the `*` permission that gates every other
  `/admin` section? Assumed no: banner content is public-facing chrome, and the `/admin` module
  already carries a uniform gate.
- Does any deployment environment serve the homepage from a cache long enough that a banner edit
  would not be visible promptly? Assumed no; to be confirmed with the deploy owner before release.

## 9. Decision Log

- 2026-08-28 — The uploaded image replaces the interactive map; hotspots are removed rather than
  made configurable — Rationale: the twelve hotspots encode AICCRA project ids and coordinates
  measured against one specific SVG. Keeping them contradicts the goal of a generic component, and
  making them configurable would add a child table plus a coordinate-picking UI for a feature no
  Global Unit has asked for.
- 2026-08-28 — The banner is Global Unit scoped, not phase scoped — Rationale: the banner is
  homepage chrome, not reportable cycle data. Phase scoping would pull it into forward-only
  replication and require a fallback rule for phases with no content, for no user benefit. This is
  a deliberate deviation from the phased-data default and is recorded here as required by
  `CLAUDE.md`.
- 2026-08-28 — The `homepage_hide_section_map` specificity is retained as a hard override —
  Rationale: an administrator can then hide the banner without destroying stored content, and no
  Global Unit that currently has the flag active sees an unexpected banner appear.
- 2026-08-28 — Nomenclature is `homepageBanner` — Rationale: it names what the component is and
  where it lives without committing to what the content says, and it lines up with the existing
  `homepage_hide_section_map` key. `infoBanner` was rejected as vague, `welcomeBanner` as biasing
  the content toward a greeting when the real example is a definition.
- 2026-08-28 — Images are stored on the filesystem and served by a Struts `stream` action, not as
  database BLOBs and not as an admin-entered URL — Rationale: `UploadGlobalUnitLogoAction` and
  `DownloadGlobalUnitLogoAction` already establish this pattern in MARLO; BLOBs would introduce a
  pattern with no precedent here, and a URL field would not satisfy the requirement that the
  administrator upload an image. Accepted cost: banner images do not travel with database dumps
  (OPS-002).
- 2026-08-28 — SVG is an accepted upload format — Rationale: the illustration in production use is
  `Map_africa.svg`; rasterising it to PNG the way the logo pipeline does would visibly degrade it.
  SVG is served only as an `<img>` source, where embedded scripts do not execute.
