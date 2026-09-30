# Save Validation Matrix

## Scope
Validation path for critical save sections:
`Action (.do)` -> `Interceptor stack` -> `Action.validate()` -> `Validator` -> save block/continue.

## Matrix (10 critical sections)

| Section | Route / Action | Struts Mapping + Stack | Action validate() | Validator call | Save-block behavior |
|---|---|---|---|---|---|
| Projects Description | `{crp}/description` -> `ProjectDescriptionAction` | `struts-projects.xml`, `editProjectsStack` | yes (`if (save)`) | `ProjectDescriptionValidator` -> `validator.validate(this, project, true)` | invalid fields/errors block save |
| Project Partners | `{crp}/partners` -> `ProjectPartnerAction` | `struts-projects.xml`, `editProjectsStack` | yes (`if (save)`) | `ProjectPartnersValidator` -> `projectPartnersValidator.validate(this, project, true)` | returns `hasErrors`; save flow guarded |
| Deliverable | `{crp}/deliverable` -> `DeliverableAction` | `struts-projects.xml`, `editProjectListStack` + `editDeliverable` + `defaultStack` | yes (`if (save)`) | `DeliverableValidator` -> `deliverableValidator.validate(this, deliverable, true)` | invalid field map and action errors block save |
| Funding Source | `{crp}/fundingSource` -> `FundingSourceAction` | `struts-fundingSources.xml`, `editFSStack` | yes (`if (save)`) | `FundingSourceValidator` -> `validator.validate(this, fundingSource, true)` | invalid data blocks save |
| POWB Financial Plan | `{crp}/financialPlan` -> `FinancialPlanAction` | `struts-powb.xml`, `editPowbStack` | yes (`if (save)`) | `FinancialPlanValidator` -> `validator.validate(this, powbSynthesis, true)` | invalid data blocks save |
| POWB Management Governance | `{crp}/managementGovernance` -> `ManagementGovernanceAction` | `struts-powb.xml`, `editPowbStack` | yes (`if (save)`) | `ManagementGovernanceValidator` -> `validator.validate(this, powbSynthesis, true)` | invalid data blocks save |
| Impact Pathway Outcomes | `{crp}/outcomes` -> `OutcomesAction` | `struts-impactPathway.xml`, `impactPathwayStack` | yes (`if (save)`) | `OutcomeValidator` -> `validator.validate(this, outcomes, selectedProgram, true)` | invalid data blocks save |
| Annual Report MELIA | `{crp}/melia` -> `MeliaAction` | `struts-annualReport.xml`, `editReportSynthesisStack` | yes (`if (save)`) | `MeliaValidator` -> `validator.validate(this, reportSynthesis, true)` | invalid data blocks save |
| Annual Report Governance | `{crp}/governance` -> `ManagementGovernanceAction` | `struts-annualReport.xml`, `editReportSynthesisStack` | yes (`if (save)`) | `GovernanceValidator` -> `validator.validate(this, reportSynthesis, true)` | invalid data blocks save |
| Admin Portfolio Management | `{crp}/portfolioManagement` -> `PortfolioManagementAction` | `struts-admin.xml`, `crpAdminStack` | validate method present but no validator call | none | currently relies on action-side checks/logic |

## Notes
1. Interceptor stack is the first gate (auth/session/edit rights).
2. `save` flag controls whether section validation is executed.
3. A non-empty invalid field set or action errors should be treated as save blockers.
4. Sections without explicit validator call (Portfolio Management) should be reviewed before complex changes.
5. **No `@Transactional` call inside `validate()` on the save path, not even a read.** On POST, `prepare()` has already
   mutated managed entities (cleared collections, nulled checkbox fields, transient instances swapped in) and the
   `ParametersInterceptor` has bound the form on top. A non-readOnly `@Transactional` manager/DAO call commits that
   half-edited state on the spot: it either fails there with a constraint error before `save()` runs, or writes it
   even if validation then rejects the save. Reuse what `prepare()` already loaded, or call a non-transactional method.
   Precedent: `f389f18e0a` made `ProjectDescriptionValidator.hasActivePrograms()` call
   `GlobalUnitManager.getGlobalUnitById` (`GlobalUnitMySQLDAO.find()` is `@Transactional`), and saving Project
   Description failed in `validate()` with a `DataIntegrityViolationException`; `b0ad9abc6c` fixed it. The
   mechanism was reproduced on 2026-09-30 against Spring 5.3.39 / Hibernate 5.6.15 with a `FlushMode.MANUAL` session:
   a transactional read commits a dirty entity immediately, a `readOnly = true` one does not. Save-path calls of this
   kind still present (not demonstrated as failing, since that depends on what each `prepare()` mutates):
   `getGlobalUnitById` in `PowbCollaborationValidator`, `PlannedCollaborationValidator`, `PlannedBudgetValidator`,
   `ProgramChangeValidator`, `ProgressOutcomesValidator`, `ToC2019Validator`; `deleteSectionStatus` in
   `Policies2018Validator`, `Innovations2018Validator`, `StudiesOICR2018Validator`, `OutcomeMilestonesValidator`.
   The `getGlobalUnitById` in each validator's `getAutoSaveFilePath()` runs only when `!saving` and is safe.

## Key References
- `marlo-web/src/main/resources/struts-projects.xml`
- `marlo-web/src/main/resources/struts-fundingSources.xml`
- `marlo-web/src/main/resources/struts-powb.xml`
- `marlo-web/src/main/resources/struts-impactPathway.xml`
- `marlo-web/src/main/resources/struts-annualReport.xml`
- `marlo-web/src/main/resources/struts-admin.xml`
