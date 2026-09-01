# AI Services — Per-Global-Unit Section Content — Design

**Spec ID:** DOMAIN-AI-SERVICES-001
**Status:** Draft
**Owner:** IBD Team
**Last Updated:** 2026-08-26
**Implements requirements:** FN-001..004, NF-001..002, DA-001..006, UI-001, SEC-001..002, OPS-001, MIG-001..002
**Touches modules:** marlo-web, marlo-data

## 1. Architecture Summary

The AI section stays exactly as it is architecturally — a Struts action rendering a FreeMarker launcher page — and the
change is a tenant scope pushed down the existing read path. The action already knows the current Global Unit
(`BaseAction.getCurrentCrp()`, resolved from `APConstants.SESSION_CRP` by the interceptor stack); today it simply does
not use it when loading AI tool rows.

```
Before:  AiAction.prepare() ── findAll() ────────────────► SELECT ... WHERE is_active = 1
After:   AiAction.prepare() ── findByGlobalUnit(id) ─────► SELECT ... WHERE is_active = 1
                │                                                    AND global_unit_id = :id
                └── id = getCurrentCrp().getId()   (session, never a request parameter)
```

Secondary change, in the same commit because the primary change is untestable without it: complete the Hibernate
mapping of `AiReportConfiguration` so that `is_active` and the audit columns are actually loaded and written.

## 2. Module Footprint

### marlo-web
- Modified: `java/org/cgiar/ccafs/marlo/action/ai/AiAction.java` — call the scoped finder; update the class javadoc
  note about the data source.
- Modified: `webapp/WEB-INF/crp/views/ai/aiDashboard.ftl` — update the in-template data-source note only. No markup
  change (UI-001).
- New: `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__AiReportConfigurationPerGlobalUnit.sql`
  (timestamp fixed at implementation time; must sort after `V2_6_0_20260826_1000`).

### marlo-data
- Modified: `resources/xmls/AiReportConfigurations.hbm.xml` — add the `globalUnit` many-to-one and the audit
  properties; update the in-file note.
- Modified: `java/.../data/model/AiReportConfiguration.java` — add the `globalUnit` field with getter/setter.
- Modified: `java/.../data/dao/AiReportConfigurationDAO.java` — add `findByGlobalUnit(long)`.
- Modified: `java/.../data/dao/mysql/AiReportConfigurationMySQLDAO.java` — implement it; fix `findAll()` HQL to use the
  mapped property.
- Modified: `java/.../data/manager/AiReportConfigurationManager.java` — expose the scoped finder.
- Modified: `java/.../data/manager/impl/AiReportConfigurationManagerImpl.java` — delegate; add `@Transactional` to the
  save path.

### marlo-core / marlo-utils
- Not applicable.

## 3. Data Model Changes

### Migration

File: `marlo-web/src/main/resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__AiReportConfigurationPerGlobalUnit.sql`

```sql
-- 1. Add the tenant column, nullable so the backfill can run.
ALTER TABLE ai_report_configuration ADD global_unit_id bigint(10) NULL;

ALTER TABLE ai_report_configuration
  ADD CONSTRAINT ai_report_configuration_global_units_FK
  FOREIGN KEY (global_unit_id) REFERENCES global_units(id)
  ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX idx_ai_report_configuration_global_unit ON ai_report_configuration (global_unit_id);

-- 2. Backfill: every row that exists today is AICCRA's (global unit 45).
--    Safe on an empty table: updates zero rows.
UPDATE ai_report_configuration
SET global_unit_id = 45
WHERE global_unit_id IS NULL;

-- 3. Lock it down: a tool row can never exist outside a tenant.
ALTER TABLE ai_report_configuration MODIFY COLUMN global_unit_id bigint(10) NOT NULL;
```

The shape follows the existing precedent in
`V2_6_0_20260724_1433__UpdateActivityTitlesTable.sql` (same column type, same FK naming convention
`<table>_global_units_FK`, same AICCRA id `45`, consistent with `BaseAction.isAiccra()` which treats
`getCurrentCrp().getId() >= 45` as AICCRA).

That precedent leaves the column nullable; this migration goes one step further and enforces `NOT NULL` per DA-003.

### Entity changes

`AiReportConfiguration.java`: add

```java
private GlobalUnit globalUnit;
```

with getter/setter, alongside the existing `reportTitle` / `reportDescription` / `buttonLabel` / `buttonLink`.

### Mapping changes

`AiReportConfigurations.hbm.xml` currently maps only the four text columns. Add:

```xml
<many-to-one name="globalUnit"
    class="org.cgiar.ccafs.marlo.data.model.GlobalUnit" fetch="select">
    <column name="global_unit_id" not-null="true" />
</many-to-one>
<property name="active" type="boolean">
    <column name="is_active" not-null="true" />
</property>
<property name="activeSince" type="timestamp" update="false">
    <column name="active_since" length="19" not-null="true" />
</property>
<many-to-one name="createdBy" class="org.cgiar.ccafs.marlo.data.model.User" fetch="select" update="false">
    <column name="created_by" not-null="true" />
</many-to-one>
<many-to-one name="modifiedBy" class="org.cgiar.ccafs.marlo.data.model.User" fetch="select">
    <column name="modified_by" />
</many-to-one>
<property name="modificationJustification" type="string">
    <column name="modification_justification" sql-type="TEXT" />
</property>
```

This mirrors `UserIdeas.hbm.xml`, which maps the same audit shape for the sibling table.

### Indices, FKs, enums
- New FK: `ai_report_configuration_global_units_FK`.
- New index: `idx_ai_report_configuration_global_unit`.
- No new enums.

### Backfill / data migration
- Required. Three rows today, all AICCRA. Idempotent and empty-table-safe (MIG-002).

## 4. API / Action Surface

### Struts actions (.do)

| Route | Action class | Stack | View result |
|---|---|---|---|
| /ai/{crp}/ai | AiAction | editAiStack | /WEB-INF/crp/views/ai/aiDashboard.ftl |

Unchanged — no route, stack or result is added or modified.

### Spring MVC REST
- Not applicable. No `/api/*` surface is touched.

### Existing JSON endpoints
- Not applicable. No JSON path is added (per AGENTS.md).

## 5. Frontend Composition

No markup, CSS or JS change. `aiDashboard.ftl` keeps:

- the `[#if reportConfigurations?? && reportConfigurations?has_content]` guard (the DAO contract still allows `null`,
  see §15 ADR-3);
- the empty-state block;
- the link-building rules (protocol normalisation, `user_email` / `user` query parameters, `target="_blank"`,
  `rel="noopener noreferrer"`).

Only the explanatory `[#-- … --]` note is updated to say the rows are scoped to the current Global Unit.

Because no CSS or JS asset changes, no cache-busting `?YYYYMMDD` bump is required.

## 6. Persistence & Phase Replication Plan

**Not applicable.** `ai_report_configuration` is not phased: it has no `phase_id` column, it is not reached from a
project section, and the AI page is a launcher with no phase-scoped data. The forward-only replication rule
(CLAUDE.md hard rule 1) does not engage. The `actualPhase` reference in the template is only there to satisfy shared
includes.

### Delete flow
Soft delete only, and it becomes real for the first time:
`AiReportConfigurationMySQLDAO.deleteAiReportConfiguration()` already calls `setActive(false)` + `update()`; once
`active` is mapped (§3) the emitted `UPDATE` includes `is_active`, so the row disappears from the scoped finder.

## 7. Validation & Save Pipeline

- Interceptor stack: `editAiStack` — unchanged.
- `Action.validate()`: unchanged. It keeps its empty `if (save) { }` body; the AI section has no user-facing form that
  submits AI tool rows, so no `Validator` class is introduced. This is a deliberate deviation from the standard
  pipeline, recorded in requirements.md §7 and ADR-4 below.
- The write path added here is manager-level only (`saveAiReportConfiguration`), consumed by migrations and future
  admin tooling, not by a form post.

## 8. Permissions & Edit Gates

- Unchanged: `i18nFile`, `validCrp`, `requireUser`, `validSessionCrp`, `canEditAi` (`EditAiInterceptor`),
  `keepRedirectMessages`, `accessibleStage`, `trimInputs`, `defaultStack`.
- Tenant resolution is `BaseAction.getCurrentCrp()`, which reads `APConstants.SESSION_CRP`. The Global Unit id is
  never taken from a request parameter (SEC-001). `validSessionCrp` already guarantees the session Global Unit matches
  the `{crp}` path segment.
- No role change. Menu visibility keeps using the `ai_section_active` specificity.

## 9. Specificity / Feature-Flag Strategy

No new flag. `ai_section_active` (`APConstants.AI_SECTION_ACTIVE`, seeded for global unit types 1, 3 and 4, renamed
from `user_idea_section_active` by `V2_6_0_20260826_1000__RenameUserIdeaSectionActiveParameter.sql`) already gates the
menu entry per Global Unit through `custom_parameters`.

This change ships unconditionally: it is a correctness fix to what an already-enabled Global Unit sees, and hiding it
behind a second flag would leave the leak (SEC-002) reachable.

## 10. Integration Points

- **External AI services** (PRMS web, the Lambda-hosted chatbot and Innovation Metadata Extractor): unchanged. MARLO
  only emits links to them. Note that those links carry the viewer's email and full name, which is why DA-003 forbids
  a shared/global row.
- **BI pipeline, CLARISA, CGSpace, S3, Pusher:** not touched. `ai_report_configuration` is not part of any export.

## 11. Observability

- `AiAction.prepare()` keeps its `try/catch` around the query and its `LOG.error("Error loading AI report
  configurations", e)` message (OPS-001). That line stays the diagnostic entry point for an unexpectedly empty
  section.
- The new audit columns become readable through the entity, so `IAuditLog` / `HibernateAuditLogListener` can record
  changes to AI tool rows for the first time.

## 12. Performance & Scalability

- The table holds single-digit rows per Global Unit; the scoped query is an indexed lookup on `global_unit_id`
  (NF-001). One query per page render, same as today.
- No second-level cache is declared on this entity and none is added, so a row change is visible on the next request
  without an eviction step — unlike `Parameter` / `CustomParameter`.

## 13. Security Considerations

- **Tenant isolation (SEC-001):** enforced in SQL from a session-derived id. There is no request parameter through
  which a user can widen the scope.
- **User identity leakage (SEC-002):** the dashboard appends `user_email` and `user` to every card link. Scoping rows
  per Global Unit is what stops one program's user identities from being sent to another program's endpoints. Any
  future decision to allow shared rows (OQ-1) must revisit this.
- No authentication, session or credential handling changes.

## 14. Backwards Compatibility & Rollout

- **Schema:** additive, then constrained. The `NOT NULL` step is the only non-additive part and runs after a backfill
  that covers every existing row.
- **Behaviour for AICCRA:** identical (FN-003).
- **Behaviour for any other Global Unit that already has the flag on:** changes from "AICCRA's three cards" to the
  empty state. This is the intended fix, but it is user-visible — OQ-2 requires checking production
  `custom_parameters` before deploy so affected programs can be told.
- **Order of operations:** migration and code deploy together. The mapping requires the column, and the code requires
  the mapping.
- **Rollback:** see task.md §Rollback Plan. The column can stay in place on a code revert; the old `findAll()` ignores
  it.

## 15. Decision Records

### ADR-DOMAIN-AI-SERVICES-001-1 — Tenant column on the row, not a join table
- Decision: add `global_unit_id` directly to `ai_report_configuration`.
- Rationale: a tool row belongs to exactly one Global Unit; a join table would make the `NOT NULL` isolation
  guarantee impossible and has no current use case.
- Alternatives considered: join table `ai_report_configuration_global_units`; a nullable column meaning "all tenants".
- Status: Accepted (pending OQ-1 confirmation with PMU).

### ADR-DOMAIN-AI-SERVICES-001-2 — Complete the audit mapping in the same change
- Decision: map `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification` now.
- Rationale: without `active` mapped, the soft delete is a no-op and FN-004 cannot be verified; without `created_by`
  mapped, no row can be inserted through the manager, so the per-tenant model cannot be exercised.
- Alternatives considered: a separate bugfix spec — rejected, it would leave this spec unverifiable.
- Status: Accepted.

### ADR-DOMAIN-AI-SERVICES-001-3 — Keep the `null`-when-empty DAO contract
- Decision: `findByGlobalUnit` returns `null` when there are no rows, matching `findAll()` and the wider MARLO DAO
  convention (e.g. `ActivityTitleMySQLDAO.findByGlobalUnit`).
- Rationale: the empty state is now a normal, expected outcome rather than an edge case, and the template guard
  (`reportConfigurations??`) already handles `null`. Changing the convention in one DAO would be inconsistent and
  would risk an NPE elsewhere if the contract were assumed.
- Alternatives considered: return an empty list and simplify the template — deferred to a repo-wide convention change.
- Status: Accepted. The contract is documented in the DAO javadoc and in agent-context.md.

### ADR-DOMAIN-AI-SERVICES-001-4 — No Validator for this section
- Decision: do not introduce `Action.validate()` business rules or an `AiValidator`.
- Rationale: the standard save pipeline (CLAUDE.md hard rule 2) applies to sections where a form posts domain data.
  The AI section posts nothing; its only form control is the legacy, non-functional `UserIdea` textarea, which is
  explicitly out of scope. A validator would have nothing to validate.
- Status: Accepted, recorded as a deliberate deviation.

### ADR-DOMAIN-AI-SERVICES-001-5 — Fix the `findAll()` HQL while mapping `active`
- Decision: change `where is_active=1` to the mapped property (`where active = true`) in this DAO only.
- Rationale: the raw-column form works by accident (Hibernate passes an unresolved identifier through to SQL) and is
  used by ~250 MARLO DAOs; a repo-wide change is out of scope. But once `active` is mapped here, leaving the raw form
  in the same file next to a mapped property is actively misleading.
- Status: Accepted, scoped to `AiReportConfigurationMySQLDAO`.

## 16. Open Risks

- Risk: a non-AICCRA Global Unit already has `ai_section_active = true` in production and will visibly lose its cards.
  Mitigation: OQ-2 — query production `custom_parameters` before deploy; coordinate with PMU if any are found.
- Risk: the `NOT NULL` step fails if an environment holds a row that the backfill cannot attribute (for example a row
  inserted manually after the seed migration).
  Mitigation: task.md includes a pre-flight count query; the migration fails loudly rather than silently dropping data.
- Risk: mapping `created_by` as `not-null="true"` will surface, as an immediate error, any existing code path that
  saves this entity without setting it. Today that path fails at the database anyway.
  Mitigation: the only caller is `AiAction`, which does not write; verified by grep before implementation.
- Risk: OQ-1 is answered "shared rows are needed", which invalidates DA-003 and ADR-1.
  Mitigation: resolve OQ-1 before starting T01; it changes the migration, not the rest of the plan.
