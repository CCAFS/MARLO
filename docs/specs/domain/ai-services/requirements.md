# AI Services — Per-Global-Unit Section Content — Requirements

**Spec ID:** DOMAIN-AI-SERVICES-001
**Status:** Draft
**Owner:** IBD Team
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-26
**Jira:** A2-2433 (child of A2-2055 — Enhancements 2026)
**Related PRD sections:** docs/prd.md §5 (Scope), §8 (Assumptions, Dependencies, & Constraints)
**Related System Design sections:** docs/system-design/design.md §2 (Information Architecture), §4 (Screen Inventory), §5 (Navigation Model)
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §3 (Data Model & Entities), §5 (Backend Workflows), §8 (Security & Authorization Model)
**Companion ai-context docs:** docs/specs/domain/ai-services/agent-context.md

## 1. Overview

The MARLO AI section (main-menu entry `AI-CCRA`, route `{crp}/ai`) is a launcher page that lists the external AI
tools available to a program and links out to each one. Its content is entirely data driven: every card rendered by
`aiDashboard.ftl` is one active row of the `ai_report_configuration` table.

That table has no tenant column. This spec makes the section content per Global Unit, so that a Global Unit which
enables the section sees only its own AI tools, and closes the persistence defects that currently make the table
unmanageable from the application.

## 2. Problem Statement

MARLO is multi-tenant: each Global Unit configures its own sections through `parameters` / `custom_parameters`
(see docs/prd.md §5). The AI section honours that model **only for visibility** — the `ai_section_active` specificity
is per Global Unit — but not for content.

`ai_report_configuration` has no `global_unit_id`, and `AiReportConfigurationMySQLDAO.findAll()` returns every active
row regardless of tenant. The three rows seeded today are AICCRA-specific (AICCRA Report Generator, AICCRA Chatbot,
Innovation Metadata Extractor), including AICCRA branding in the card titles and AICCRA-owned destination URLs.

Consequences:

- Any second Global Unit that enables the section is shown AICCRA's tools, with AICCRA's name in the card titles.
- The card links receive the viewer's email and full name as query parameters, so enabling the section for another
  program would send that program's user identities to AICCRA-owned endpoints.
- Because the section cannot be scoped, the specificity is effectively unusable outside AICCRA, which blocks the
  rollout of AI tooling to other Global Units.

A second, compounding problem: the entity's audit columns are not mapped in `AiReportConfigurations.hbm.xml`, so the
DAO soft delete silently does nothing and no row can be created or deactivated from the application. Any per-tenant
model that relies on managing rows is unworkable until that is fixed.

## 3. In-Scope Requirements

### Functional

- DOMAIN-AI-SERVICES-001-FN-001 — The AI section MUST render only the AI tool cards that belong to the Global Unit of
  the current session.
- DOMAIN-AI-SERVICES-001-FN-002 — When the current Global Unit has no active AI tool row, the section MUST render the
  existing empty-state block and MUST NOT render another Global Unit's cards.
- DOMAIN-AI-SERVICES-001-FN-003 — The three AI tools currently visible to AICCRA MUST remain visible to AICCRA,
  unchanged in title, description, button label and destination link, after the migration runs.
- DOMAIN-AI-SERVICES-001-FN-004 — Deactivating an AI tool row (`is_active = 0`) MUST remove its card from the section
  for that Global Unit.

### Non-Functional

- DOMAIN-AI-SERVICES-001-NF-001 — The section MUST issue at most one query for AI tool rows per page render; the
  tenant filter MUST be applied in SQL, not in Java after loading all rows.
- DOMAIN-AI-SERVICES-001-NF-002 — The change MUST NOT alter the rendered markup or styling of the section for a Global
  Unit whose rows are unchanged.

### Data

- DOMAIN-AI-SERVICES-001-DA-001 — `ai_report_configuration` MUST carry a `global_unit_id` column with a foreign key to
  `global_units(id)`.
- DOMAIN-AI-SERVICES-001-DA-002 — Existing rows MUST be backfilled to the AICCRA Global Unit so that no data is
  orphaned and FN-003 holds.
- DOMAIN-AI-SERVICES-001-DA-003 — After backfill, `global_unit_id` MUST be `NOT NULL`, so a row can never exist
  outside a tenant.
- DOMAIN-AI-SERVICES-001-DA-004 — `AiReportConfigurations.hbm.xml` MUST map `global_unit_id` and the audit columns
  (`is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) that exist in the table and
  are currently unmapped.
- DOMAIN-AI-SERVICES-001-DA-005 — The soft delete on this entity MUST actually persist `is_active = 0`.
- DOMAIN-AI-SERVICES-001-DA-006 — The write path MUST run inside a transaction and MUST populate `created_by`, which
  is `NOT NULL` with a foreign key to `users`.

### UI

- DOMAIN-AI-SERVICES-001-UI-001 — No new screen and no change to the card layout. The empty-state block, the
  disclaimer, the invitation message and the link-building rules (protocol normalisation, `user_email` / `user` query
  parameters) MUST behave exactly as today.

### Security

- DOMAIN-AI-SERVICES-001-SEC-001 — A user MUST NOT be able to reach another Global Unit's AI tool rows by changing a
  request parameter; the tenant MUST be resolved from the session (`APConstants.SESSION_CRP`), never from user input.
- DOMAIN-AI-SERVICES-001-SEC-002 — Because card links carry the viewer's email and full name, a Global Unit MUST NOT be
  able to receive cards pointing at another Global Unit's destination hosts as a side effect of this change.

### Operations

- DOMAIN-AI-SERVICES-001-OPS-001 — The failure path in `AiAction.prepare()` MUST keep logging a distinguishable
  message when the AI tool query fails, so an empty section can be diagnosed without a debugger.

### Migration

- DOMAIN-AI-SERVICES-001-MIG-001 — All schema and data changes MUST ship as Flyway migrations under
  `marlo-web/src/main/resources/database/migrations/` with the
  `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql` naming.
- DOMAIN-AI-SERVICES-001-MIG-002 — The migration MUST be safe to run against an environment where
  `ai_report_configuration` is empty (a fresh Global Unit database).

## 4. Out-of-Scope

- An admin screen to manage `ai_report_configuration` rows from MARLO. Rows stay managed through migrations; the
  persistence fixes in DA-004..006 are prerequisites for such a screen, not the screen itself.
- The legacy `UserIdea` half of the page (the free-text box, `user_ideas` table). It stays non-functional; retiring or
  repairing it is separate work.
- Renaming the `userIdea.*` i18n key family, and the stale `userIdea.reportGeneratorNarrative` /
  `userIdea.ChatbotNarrative` / `userIdea.innovationGenerator` keys.
- The summaries AI reports tab (`summary_ai_report_tab_active`, `action/summaries/ai/*`) — a different feature.
- The external AI services themselves (PRMS web, the Lambda-hosted chatbot and extractor). MARLO only links to them.
- Translating card text. Card copy stays raw text stored in the table.
- Removing the dead `struts-ai.xml` file.

## 5. Personas Affected

- **PMU / program staff of a non-AICCRA Global Unit** (primary beneficiary): can have the section enabled without
  being shown another program's tools.
- **AICCRA program staff**: no visible change; the backfill preserves their three cards.
- **Super Admin**: unchanged workflow — enables the section per Global Unit through the `ai_section_active`
  specificity on the Super Admin → Parameters screen.
- **IBD developer / DBA**: gains a working soft delete and a transactional write path, so seeding a new tenant's tools
  no longer requires reasoning about unmapped columns.

## 6. Acceptance Criteria

AC for FN-001 / FN-002 / SEC-001:
- Given a Global Unit with `ai_section_active = true` and no active `ai_report_configuration` row of its own,
- When a user of that Global Unit opens the AI section,
- Then the empty-state block MUST be rendered,
- And no card belonging to another Global Unit MUST appear,
- And the executed SQL MUST contain the tenant predicate.

AC for FN-003:
- Given the AICCRA Global Unit and the three rows seeded by `V2_6_0_20251113_1400__UpdateAITable.sql`,
- When the migration has run and a user opens the AI section,
- Then exactly three cards MUST be rendered,
- And their title, description, button label and destination URL MUST be byte-identical to the pre-migration render.

AC for FN-004 / DA-005:
- Given an active AI tool row of the current Global Unit,
- When it is deactivated through the manager,
- Then `ai_report_configuration.is_active` MUST be `0` in the database,
- And the card MUST no longer be rendered.

AC for DA-001..003:
- Given the migration has run,
- Then `ai_report_configuration.global_unit_id` MUST exist, be `NOT NULL`, and carry a foreign key to `global_units(id)`,
- And no row MUST have a null or orphaned `global_unit_id`.

AC for DA-006:
- Given a new AI tool row saved through `AiReportConfigurationManager`,
- When the transaction commits,
- Then the row MUST exist with a non-null `created_by` referencing a valid user,
- And re-reading it MUST return the persisted values.

AC for MIG-002:
- Given an environment where `ai_report_configuration` has no rows,
- When the migration runs,
- Then it MUST complete successfully and leave the `NOT NULL` constraint in place.

## 7. Constitutional Compliance Checklist

- [ ] Phase replication: **Not applicable** — `ai_report_configuration` is not phased; the section is a launcher page
      with no phase-scoped data and no `phase_id` column.
- [ ] Save validation: **Not applicable to the section itself** — the AI section has no user-facing save. The write
      path added here is a manager-level API (`Action.validate()` + `Validator` are not introduced because no form
      submits AI tool rows; that arrives with the out-of-scope admin screen).
- [ ] Permissions: the `{crp}/ai` action keeps its existing `editAiStack` declaration; no new action is added.
- [ ] Specificity: no new flag. `ai_section_active` already exists and is already per Global Unit.
- [ ] Migrations: every schema and data change ships as a Flyway migration with the mandated naming.
- [ ] i18n: no new user-facing strings. Card copy remains table data by design (see Decision Log).
- [ ] License header: no new Java file is planned; any new file added during implementation carries the GPL header.
- [ ] Code style: Checkstyle passes; 2-space indent; 120 char line limit.
- [ ] REST: **Not applicable** — no `/api/*` surface is touched.
- [ ] Audit: `AiReportConfiguration` already implements `IAuditLog`; mapping the audit columns brings it in line with
      the rest of the model.
- [ ] Dependency floors preserved: no dependency change.
- [ ] Branching: feature branch from `staging`, named `A2-2433-AiSectionPerGlobalUnit`; merge target `staging`.

## 8. Open Questions

- **OQ-1 —** Should a Global Unit be able to *share* a tool row with another (a null `global_unit_id` meaning "all
  tenants")? DA-003 says no, on the grounds that the current rows are AICCRA-branded and the links carry user
  identity. Confirm with PMU before implementation; reversing this changes DA-003 and the FK nullability.
- **OQ-2 —** Do any non-AICCRA Global Units already have `ai_section_active = true` in production? If so they are
  currently seeing AICCRA cards, and the deploy will change what they see from three cards to an empty state. Needs a
  production `custom_parameters` check before rollout (see task.md, operational steps).
- **OQ-3 —** Should the empty-state strings (`userIdea.noReportsConfigured`,
  `userIdea.noReportsConfiguredDescription`, currently missing from `global.properties` and supplied as FTL `default=`
  literals) be added as real i18n keys as part of this work, given the empty state stops being a theoretical branch?

## 9. Decision Log

- 2026-08-26 — Scope the tenant column to `ai_report_configuration` rather than introducing a join table —
  Rationale: a tool row belongs to exactly one Global Unit; a many-to-many has no current use case and would make the
  `NOT NULL` guarantee in DA-003 impossible.
- 2026-08-26 — Keep card copy as raw table text instead of moving it to i18n keys — Rationale: the copy is
  tenant-specific operational content, not product chrome; the stale `userIdea.*Narrative` keys are evidence that the
  properties-file approach was already abandoned once.
- 2026-08-26 — Fix the unmapped audit columns and the missing `@Transactional` inside this spec rather than in a
  separate bugfix spec — Rationale: DA-005 and DA-006 are prerequisites for managing per-tenant rows at all; splitting
  them would leave this spec unverifiable.
