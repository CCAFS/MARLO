# Interceptor and Validator Playbook

## Goal
Provide a fast decision guide to trace save failures across interceptor stacks, `Action.validate()`, and validator classes.

## Save Pipeline
1. Route hits Struts action mapping in `struts-*.xml`.
2. Interceptor stack runs first (auth, session context, section access, edit permission).
3. `Action.validate()` executes when Struts invokes validation phase.
4. Most actions guard validation with `if (save) { ... }`.
5. Validator populates invalid fields/action errors.
6. Save path uses these errors to block persistence and return input.

## Verified Patterns in Critical Actions

| Action Class | validate() behavior | Validator class | Notes |
|---|---|---|---|
| `ProjectDescriptionAction` | `if (save) validator.validate(this, project, true)` | `ProjectDescriptionValidator` | Standard Projects section pattern |
| `ProjectPartnerAction` | `if (save) projectPartnersValidator.validate(...)` | `ProjectPartnersValidator` | Returns `hasErrors` and blocks save path |
| `DeliverableAction` | `if (save) deliverableValidator.validate(...)` | `DeliverableValidator` | Uses extended stack sequence (`editProjectListStack` + `editDeliverable`) |
| `FinancialPlanAction` | `if (save) validator.validate(this, powbSynthesis, true)` | `FinancialPlanValidator` | POWB synthesis save flow |
| `OutcomesAction` | `if (save) validator.validate(this, outcomes, selectedProgram, true)` | `OutcomeValidator` | Impact pathway save flow |
| `MeliaAction` | `if (save) validator.validate(this, reportSynthesis, true)` | `MeliaValidator` | Annual report synthesis flow |
| `PortfolioManagementAction` | `if (save) { }` (empty body) | none in action | Admin action with no explicit validator call |

## Interceptor Stack Intent Map
1. `editProjectsStack`: project edit permission (`canEditProject`) + stage and session constraints.
2. `editFSStack`: funding-specific edit guard (`editFunding`).
3. `editPowbStack`: synthesis-specific edit guard (`canEditPowbSynthesis`).
4. `editReportSynthesisStack`: annual report edit guard (`canEditReportSynthesis`).
5. `impactPathwayStack`: impact pathway edit guard (`canEditImpactPathway`).
6. `crpAdminStack`: admin section access (`accessibleAdmin`, `canEditCrpAdmin`).

## Debugging Checklist for Save Failures
1. Confirm action route and stack in the corresponding `struts-*.xml` file.
2. Verify request includes `save=true` when expecting validator execution.
3. Inspect action `validate()` for the exact validator call and parameters.
4. Inspect invalid fields and action messages after submit.
5. Verify permission interceptor in stack (`canEdit*`, `editFunding`, `accessibleAdmin`).
6. If validator is missing (for example `PortfolioManagementAction`), review action-side checks before changing behavior.

## Common Pitfalls
1. Assuming validator runs without `save` flag.
2. Focusing on validator first when interceptor denied access earlier.
3. Treating all stacks as equivalent; each module has different edit gate interceptor.
4. Applying JSON assumptions to `.do` actions that use standard form submit flow.

## Source References
- `marlo-web/src/main/resources/struts.xml`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/ProjectDescriptionAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/ProjectPartnerAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/DeliverableAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/powb/FinancialPlanAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/impactpathway/OutcomesAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/annualReport/MeliaAction.java`
- `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/crp/admin/PortfolioManagementAction.java`
