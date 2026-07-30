# Projects — Design

**Spec ID:** DOMAIN-PROJECTS-001
**Status:** Draft
**Owner:** IBD Team
**Last Updated:** 2026-07-29
**Implements requirements:** DOMAIN-PROJECTS-001-FN-001..FN-005, NF-001..NF-003, DA-001..DA-003, UI-001..UI-003, SEC-001..SEC-003, OPS-001..OPS-002
**Touches modules:** marlo-web, marlo-data

## 1. Architecture Summary

The Projects module uses the standard MARLO internal-flow architecture: Struts `.do` routes map to action classes,
interceptor stacks establish scope and edit rights, actions load or bind project state, validators run when `save` is
true, managers persist and replicate phased data, and FreeMarker/FTL views render the form experience.

```text
Browser
  -> /projects/{crp}/<section>.do
  -> struts-projects.xml
  -> interceptor stack
  -> action.projects.*Action
  -> validation.projects.*Validator
  -> marlo-data manager/DAO layer
  -> MySQL + phase replication
  -> WEB-INF/crp/views/projects/*.ftl
```

## 2. Module Footprint

### marlo-web

- Existing Struts config: `marlo-web/src/main/resources/struts-projects.xml`
- Existing actions: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/`
- Existing validators: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/projects/`
- Existing views: `marlo-web/src/main/webapp/WEB-INF/crp/views/projects/`
- Existing JavaScript: `marlo-web/src/main/webapp/crp/js/projects/`
- Existing i18n: `marlo-web/src/main/resources/global.properties` and `custom/*.properties`

### marlo-data

- Existing domain entities: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/`
- Existing manager layer: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/`
- Existing DAO layer: `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/`

### marlo-core / marlo-utils

- Not applicable for this baseline. Future Projects changes should touch these modules only with explicit design
  rationale.

## 3. Data Model Changes

No data model changes are introduced by this baseline spec.

Future Projects schema changes must include:

- Flyway migration under `marlo-web/src/main/resources/database/migrations/`.
- Entity mapping updates under `marlo-data/.../data/model/`.
- Manager/DAO updates where persistence behavior changes.
- Explicit phase replication analysis for phased entities.

## 4. API / Action Surface

### Struts actions (.do)

Representative current Projects routes:

| Route | Action class | Stack | View result |
|---|---|---|---|
| `/projects/{crp}/projectsList` | `ProjectListAction` | `projectsStack` | `/WEB-INF/crp/views/projects/projectsList.ftl` |
| `/projects/{crp}/description` | `ProjectDescriptionAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectDescription.ftl` |
| `/projects/{crp}/partners` | `ProjectPartnerAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectPartners.ftl` |
| `/projects/{crp}/locations` | `ProjectLocationAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectLocations.ftl` |
| `/projects/{crp}/activities` | `ProjectActivitiesAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectActivities.ftl` |
| `/projects/{crp}/deliverableList` | `DeliverableListAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/deliverableList.ftl` |
| `/projects/{crp}/deliverable` | `DeliverableAction` | `editProjectListStack` + `editDeliverable` + `defaultStack` | `/WEB-INF/crp/views/projects/projectDeliverable.ftl` |
| `/projects/{crp}/innovationsList` | `ProjectInnovationListAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectInnovationList.ftl` |
| `/projects/{crp}/innovation` | `ProjectInnovationAction` | `editProjectListStack` + `editInnovation` + `defaultStack` | `/WEB-INF/crp/views/projects/projectInnovation.ftl` |
| `/projects/{crp}/studies` | `ProjectExpectedStudiesListAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectStudies.ftl` |
| `/projects/{crp}/study` | `ProjectExpectedStudiesAction` | `editProjectListStack` + `editExpectedStudy` + `defaultStack` | `/WEB-INF/crp/views/projects/projectStudy.ftl` |
| `/projects/{crp}/policies` | `ProjectPolicyListAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectPolicies.ftl` |
| `/projects/{crp}/policy` | `ProjectPolicyAction` | `editProjectListStack` + `editPolicy` + `defaultStack` | `/WEB-INF/crp/views/projects/projectPolicy.ftl` |
| `/projects/{crp}/budgetByPartners` | `ProjectBudgetByPartnersAction` | `editProjectsBudgetStack` | `/WEB-INF/crp/views/projects/projectBudgetByPartners.ftl` |
| `/projects/{crp}/budgetByFlagship` | `ProjectBudgetByFlagshipAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectBudgetByFlagships.ftl` |
| `/projects/{crp}/submit` | `ProjectSubmissionAction` | action mapping review required before changes | `/WEB-INF/crp/views/projects/projectSubmission.ftl` |

### Spring MVC REST

Not applicable for internal Projects UI changes. `/api/*` remains Spring MVC only.

### Existing JSON endpoints

Do not introduce new Struts JSON endpoints from this baseline. Existing JSON patterns must be reviewed in
`struts-json.xml` or same-module mappings before reuse.

## 5. Frontend Composition

Projects views are FreeMarker files under `WEB-INF/crp/views/projects/`. They use global page includes, project-specific
partials, global macros, and per-section JavaScript under `crp/js/projects/`.

Primary view files include:

- `projectsList.ftl`
- `projectDescription.ftl`
- `projectPartners.ftl`
- `projectLocations.ftl`
- `projectActivities.ftl`
- `deliverableList.ftl`
- `projectDeliverable.ftl`
- `projectInnovationList.ftl`
- `projectInnovation.ftl`
- `projectStudies.ftl`
- `projectStudy.ftl`
- `projectPolicies.ftl`
- `projectPolicy.ftl`
- `projectBudgetByPartners.ftl`
- `projectBudgetByFlagships.ftl`
- `projectSubmission.ftl`
- `safeguard.ftl`

Per-section JavaScript includes matching files such as `projectDescription.js`, `projectPartners.js`,
`projectInnovations.js`, `projectStudy.js`, and `projectSubmit.js`.

## 6. Persistence & Phase Replication Plan

Pattern: standard MARLO manager-layer persistence with phase-aware replication where entities are phase-scoped.

### Save flow

1. The action binds request data to the section model.
2. If validation passes, the action calls the appropriate manager save method.
3. ManagerImpl persists the current phase entity.
4. For phased child entities, ManagerImpl applies the forward-only replication contract.
5. Duplicate prevention and section-specific skip rules must be preserved.

### Delete flow

- Delete behavior must mirror save propagation for phased child entities.
- List/detail delete flows must keep item-level edit interceptors such as `editDeliverable`, `editInnovation`,
  `editExpectedStudy`, `editPolicy`, or `editProjectOutcome` where applicable.

### Section-specific skip rules

- Deliverable publication-related flows may contain publication-specific replication exclusions.
- Future changes must inspect the target manager before assuming generic replication.

### Tests

- Add manager-layer tests when modifying phase replication.
- Add action/validator coverage where validation behavior changes.

## 7. Validation & Save Pipeline

Critical existing patterns:

| Section | Action | Validator | Stack |
|---|---|---|---|
| Description | `ProjectDescriptionAction` | `ProjectDescriptionValidator` | `editProjectsStack` |
| Partners | `ProjectPartnerAction` | `ProjectPartnersValidator` | `editProjectsStack` |
| Deliverable | `DeliverableAction` | `DeliverableValidator` | `editProjectListStack` + `editDeliverable` |
| Innovation | `ProjectInnovationAction` | `ProjectInnovationValidator` | `editProjectListStack` + `editInnovation` |
| Studies | `ProjectExpectedStudiesAction` | `ProjectExpectedStudiesValidator` | `editProjectListStack` + `editExpectedStudy` |
| Policies | `ProjectPolicyAction` | `ProjectPolicyValidator` | `editProjectListStack` + `editPolicy` |
| Activities | `ProjectActivitiesAction` | `ProjectActivitiesValidator` | `editProjectsStack` |
| Locations | `ProjectLocationAction` | `ProjectLocationValidator` | `editProjectsStack` |
| Budgets by partner | `ProjectBudgetByPartnersAction` | `ProjectBudgetsValidator` | `editProjectsBudgetStack` |
| Budgets by flagship | `ProjectBudgetByFlagshipAction` | `ProjectBudgetsFlagshipValidator` | `editProjectsStack` |

Validation changes must preserve the `if (save) { ... }` guard where the action follows the standard save pattern.

## 8. Permissions & Edit Gates

Relevant Projects interceptor stacks and edit gates:

- `projectsStack`: list/read-oriented Projects area stack.
- `projectListStack`: project add/delete/list operations and existing JSON-security-sensitive patterns.
- `editProjectsStack`: standard project section edit stack.
- `editProjectsBudgetStack`: budget-specific edit stack.
- `editProjectListStack`: base stack for item detail operations.
- Item-level edit gates: `editDeliverable`, `editInnovation`, `editExpectedStudy`, `editPolicy`, `editProjectOutcome`,
  `editHighlight`, `editCaseStudy`.

Every new mutating action must declare the correct stack explicitly in `struts-projects.xml`.

## 9. Specificity / Feature-Flag Strategy

Not applicable for this baseline. Future conditional behavior must follow the `AGENTS.md` specificity workflow:

- Add `parameters` rows for global unit types 1, 3, and 4.
- Add `custom_parameters` rollout rows only when needed.
- Add constants in both `APConstants.java` files.
- Use `BaseAction.hasSpecificities(...)` in Java and `action.hasSpecificities('<key>')` in FTL.

## 10. Integration Points

- CLARISA: institutions, locations, and taxonomy-backed values.
- CGSpace / publication metadata: deliverable and publication-adjacent workflows where applicable.
- BI / summaries: Projects data feeds summary actions and downstream dashboards.
- AI services: project data can be consumed by AI features through documented integrations.
- Pusher / notifications: used by global notification patterns, not owned by this baseline.

## 11. Observability

- Use class-level loggers for new server-side logic.
- Do not add `System.out` or `printStackTrace`.
- Preserve audit columns and auditable entity behavior.
- Update ai-context docs when routing, validation, or replication contracts change.

## 12. Performance & Scalability

- Avoid N+1 query regressions in list pages and repeated child-section loads.
- Preserve existing pagination/table patterns for lists.
- Reuse manager methods and cached/reference-list loading patterns already present in actions.
- Review indexing needs before adding fields used in filters or summary queries.

## 13. Security Considerations

- Authentication and CRP scope are enforced by Struts interceptor stacks.
- Mutating item detail routes require both project-level and item-level edit gates where applicable.
- Do not expose internal entities directly through REST or JSON.
- Do not log credentials, tokens, or sensitive user/session data.

## 14. Backwards Compatibility & Rollout

- This baseline has no runtime rollout.
- Future changes must preserve existing route names unless a migration/redirect plan is approved.
- Schema changes should be additive where possible.
- Feature-flagged behavior should default to current behavior unless rollout explicitly enables it.

## 15. Decision Records

### ADR-DOMAIN-PROJECTS-001-1 — Keep Projects on Struts + FTL

- Decision: Projects remains an internal Struts `.do` + FreeMarker/FTL module.
- Rationale: Existing routing, validation, permissions, and form binding are deeply integrated with Struts actions.
- Alternatives considered: Spring MVC rewrite, SPA rewrite.
- Status: Accepted.

### ADR-DOMAIN-PROJECTS-001-2 — Treat this as a baseline spec

- Decision: This spec documents current module contracts and does not introduce implementation changes.
- Rationale: Projects is broad; a baseline spec improves future traceability without forcing a large refactor.
- Status: Accepted.

## 16. Open Risks

- Projects has many routes and validators; this baseline lists representative routes rather than every summary/download
  action.
- Some save paths have section-specific replication rules that must be inspected before changes.
- Legacy routes and typo-preserving action names may exist in `struts-projects.xml`; do not rename without a compatibility
  plan.
