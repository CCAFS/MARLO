# Projects — Agent Context

Read this before changing MARLO Projects flows. This is a compact agent context, not a feature implementation spec.
Use it to orient quickly, then inspect the actual source files for the target route before editing.

## Primary Files

- Routes: `marlo-web/src/main/resources/struts-projects.xml`
- Actions: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/`
- Validators: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/projects/`
- Views: `marlo-web/src/main/webapp/WEB-INF/crp/views/projects/`
- JavaScript: `marlo-web/src/main/webapp/crp/js/projects/`
- i18n: `marlo-web/src/main/resources/global.properties` and `custom/*.properties`
- Formal spec: `docs/specs/domain/projects/requirements.md`, `design.md`, `task.md`

## Non-Negotiable Rules

- Internal Projects flows stay on Struts `.do`.
- Views stay in FreeMarker/FTL; do not create JSP views.
- Preserve explicit interceptor stacks in `struts-projects.xml`.
- Preserve the `if (save) { ... }` validation pattern where present.
- Use the matching `validation/projects/*Validator.java` before manager persistence.
- Do not bypass the `marlo-data` manager layer for writes.
- Check `*ManagerImpl` phase replication before changing save or delete behavior.
- User-facing strings must be i18n-keyed.
- Use Java 17 and the Java 17 run script for local execution.

## Critical Flows

| Flow | Route | Action | Validator | View |
|---|---|---|---|---|
| Description | `{crp}/description` | `ProjectDescriptionAction` | `ProjectDescriptionValidator` | `projectDescription.ftl` |
| Partners | `{crp}/partners` | `ProjectPartnerAction` | `ProjectPartnersValidator` | `projectPartners.ftl` |
| Deliverable detail | `{crp}/deliverable` | `DeliverableAction` | `DeliverableValidator` | `projectDeliverable.ftl` |
| Innovation detail | `{crp}/innovation` | `ProjectInnovationAction` | `ProjectInnovationValidator` | `projectInnovation.ftl` |
| Study detail | `{crp}/study` | `ProjectExpectedStudiesAction` | `ProjectExpectedStudiesValidator` | `projectStudy.ftl` |
| Policy detail | `{crp}/policy` | `ProjectPolicyAction` | `ProjectPolicyValidator` | `projectPolicy.ftl` |
| Activities | `{crp}/activities` | `ProjectActivitiesAction` | `ProjectActivitiesValidator` | `projectActivities.ftl` |
| Locations | `{crp}/locations` | `ProjectLocationAction` | `ProjectLocationValidator` | `projectLocations.ftl` |

## Routing And Permissions

- Standard section edits usually use `editProjectsStack`.
- Budget partner edits use `editProjectsBudgetStack`.
- Item detail edits usually use `editProjectListStack` plus an item gate such as `editDeliverable`,
  `editInnovation`, `editExpectedStudy`, `editPolicy`, `editProjectOutcome`, `editHighlight`, or `editCaseStudy`.
- Project list/add/delete routes may use `projectListStack`.

## Open Only When Relevant

- UI composition changes: `reports/ai-context/frontend-composition-map.md`
- Save validation changes: `reports/ai-context/save-validation-matrix.md`
- Interceptor or validator debugging: `reports/ai-context/interceptor-validator-playbook.md`
- Routing changes: `reports/ai-context/struts-critical-routing-catalog.md`
- Manager save/delete changes: `reports/ai-context/persistence-replication-managerimpl.md`
- Expandable block CRUD issues: `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`
- Broad or architectural Projects work: `docs/specs/domain/projects/requirements.md`, `design.md`, `task.md`

## Verification Shortlist

- Confirm the target route/action/view in `struts-projects.xml`.
- Confirm the validator runs only when expected.
- Confirm the interceptor stack matches the edit operation.
- Confirm manager save/delete phase replication when touching phased data.
- Run targeted build/checkstyle/tests when code changes.
- Update ai-context docs if routing, validation, replication, or composition contracts change.
