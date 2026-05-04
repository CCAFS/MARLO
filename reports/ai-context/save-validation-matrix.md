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

## Key References
- `marlo-web/src/main/resources/struts-projects.xml`
- `marlo-web/src/main/resources/struts-fundingSources.xml`
- `marlo-web/src/main/resources/struts-powb.xml`
- `marlo-web/src/main/resources/struts-impactPathway.xml`
- `marlo-web/src/main/resources/struts-annualReport.xml`
- `marlo-web/src/main/resources/struts-admin.xml`
