# Struts Critical Routing Catalog

## Scope
Operational routing catalog for MARLO internal flows using Struts Actions (`.do`) and Struts JSON (`.json`) only when explicitly configured.

## Global Routing Rules
1. `struts.action.extension = do,,json` allows `.do` and `.json` routes.
2. `struts.action.excludePattern = /api/*` keeps Spring MVC API outside Struts internal routing.
3. Internal module routing should prioritize Struts action mappings in `struts-*.xml` files.

## Critical Modules Catalog

| Module | Struts Package / Namespace | Representative Action Route | Action Class | Interceptor Stack | View Result |
|---|---|---|---|---|---|
| Projects | `projects` / `/projects` | `{crp}/description` | `ProjectDescriptionAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectDescription.ftl` |
| Projects | `projects` / `/projects` | `{crp}/partners` | `ProjectPartnerAction` | `editProjectsStack` | `/WEB-INF/crp/views/projects/projectPartners.ftl` |
| Projects | `projects` / `/projects` | `{crp}/deliverable` | `DeliverableAction` | `editProjectListStack` + `editDeliverable` + `defaultStack` | `/WEB-INF/crp/views/projects/projectDeliverable.ftl` |
| Funding Sources | `fundingSources` / `/fundingSources` | `{crp}/fundingSource` | `FundingSourceAction` | `editFSStack` | `/WEB-INF/crp/views/fundingSources/fundingSource.ftl` |
| POWB | `powb` / `/powb` | `{crp}/financialPlan` | `FinancialPlanAction` | `editPowbStack` | `/WEB-INF/crp/views/powb/powb_financialPlan.ftl` |
| POWB | `powb` / `/powb` | `{crp}/managementGovernance` | `ManagementGovernanceAction` | `editPowbStack` | `/WEB-INF/crp/views/powb/powb_managementGovernance.ftl` |
| Impact Pathway | `impactPathway` / `/impactPathway` | `{crp}/outcomes` | `OutcomesAction` | `impactPathwayStack` | `/WEB-INF/crp/views/impactPathway/outcomes.ftl` |
| Annual Report | `annualReport` / `/annualReport` | `{crp}/melia` | `MeliaAction` | `editReportSynthesisStack` | `/WEB-INF/crp/views/annualReport/annualReport_melia.ftl` |
| Annual Report | `annualReport` / `/annualReport` | `{crp}/governance` | `ManagementGovernanceAction` | `editReportSynthesisStack` | `/WEB-INF/crp/views/annualReport/annualReport_governance.ftl` |
| Admin | `admin` / `/admin` | `{crp}/portfolioManagement` | `PortfolioManagementAction` | `crpAdminStack` | `/WEB-INF/crp/views/admin/portfoliosManagement.ftl` |
| Admin | `admin` / `/admin` | `{crp}/homepageBannerManagement` | `HomepageBannerManagementAction` | `crpAdminStack` | `/WEB-INF/crp/views/admin/homepageBannerManagement.ftl` |
| Data (public) | `data` / `/data` | `homepageBannerImage` | `DownloadHomepageBannerImageAction` | none (public stream) | `stream` result, dynamic `contentType` |

## Interceptor Stack Pointers
1. `crpAdminStack`: includes auth/session + admin access + `canEditCrpAdmin`.
2. `impactPathwayStack`: includes auth/session + `canEditImpactPathway`.
3. `editProjectsStack`: includes auth/session + `canEditProject`.
4. `editFSStack`: includes auth/session + `editFunding`.
5. `editPowbStack`: includes auth/session + `canEditPowbSynthesis`.
6. `editReportSynthesisStack`: includes auth/session + `canEditReportSynthesis`.
7. `editProjectListStack`: lightweight stack used before extra action-level interceptors like `editDeliverable`.

## Public Stream Routes
The `data` package is unauthenticated by design and serves binary content through Struts `stream` results:
`globalUnitLogo` (fixed `image/png`, falls back to a default image) and `homepageBannerImage`
(`${contentType}` supplied by the action, since a banner image may be PNG, JPEG or SVG; **no** default-image
fallback — a missing image is a 404, because the homepage only emits the `img` element when a banner names a
file). Both actions derive the file path from a Global Unit acronym and accept no path fragment from the
caller.

## JSON Usage Note
`projectListStack` includes `SecurityControl` and is the key stack for JSON security checks. Use it only for existing JSON endpoints and avoid introducing new JSON paths unless required by current architecture.

## Source References
- `marlo-web/src/main/resources/struts.xml`
- `marlo-web/src/main/resources/struts-projects.xml`
- `marlo-web/src/main/resources/struts-fundingSources.xml`
- `marlo-web/src/main/resources/struts-powb.xml`
- `marlo-web/src/main/resources/struts-impactPathway.xml`
- `marlo-web/src/main/resources/struts-annualReport.xml`
- `marlo-web/src/main/resources/struts-admin.xml`
