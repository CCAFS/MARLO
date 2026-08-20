# Funding Sources - Agent Context

Read this before editing Funding Sources. This is a compact context file for low-token sessions.
Use it to orient quickly, then inspect only the target files you will change.

## Scope

This context covers the internal Struts Funding Sources module (list, detail, save, copy, delete, mapping support).
It does not cover public API redesign or migration to Spring MVC.

## Primary Files

- Routes: marlo-web/src/main/resources/struts-fundingSources.xml
- Main action: marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/funding/FundingSourceAction.java
- List action: marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/funding/FundingSourceListAction.java
- Validator: marlo-web/src/main/java/org/cgiar/ccafs/marlo/validation/fundingSource/FundingSourceValidator.java
- Main view: marlo-web/src/main/webapp/WEB-INF/crp/views/fundingSources/fundingSource.ftl
- List view: marlo-web/src/main/webapp/WEB-INF/crp/views/fundingSources/fundingSourcesList.ftl
- JS: marlo-web/src/main/webapp/crp/js/fundingSources/fundingSource.js
- JS (sync): marlo-web/src/main/webapp/crp/js/fundingSources/syncFundingSource.js

## Route Map

- /fundingSources/{crp}/fundingSourcesList -> FundingSourceListAction -> projectsStack
- /fundingSources/{crp}/fundingSource -> FundingSourceAction -> editFSStack
- /fundingSources/{crp}/addNewFundingSources -> FundingSourceListAction.add
- /fundingSources/{crp}/deleteFundingSource -> FundingSourceListAction.delete
- /fundingSources/{crp}/copyFundingSource -> FundingSourceListAction.copy
- /fundingSources/{crp}/copy -> FundingSourceAction.copy

## Non-Negotiable Rules

- Keep internal Funding Sources on Struts .do flows and FreeMarker views.
- Preserve explicit interceptor stacks from struts-fundingSources.xml.
- Preserve Action.validate() guard pattern: validate business rules only when save=true.
- Do not bypass marlo-data manager layer for writes.
- Respect forward-only phase behavior when touching save/copy/delete logic.
- Keep user-facing messages i18n-based.

## Save Pipeline (Current Contract)

1. prepare() loads DB state and reference lists for rendering/binding.
2. On POST, editable collections are cleaned to avoid stale Struts-bound items.
3. validate() normalizes optional file IDs and delegates to FundingSourceValidator.
4. save() merges form state into DB entities and persists through managers.
5. saveLocations() handles region/country add/delete consistency.

If one step changes, verify the whole pipeline.

## High-Risk Areas

- Validation that is stricter than UI requirements (can silently block save).
- Null handling for optional nested fields:
  - fundingSource.fundingSourceInfo.leadCenter
  - fundingSource.fundingSourceInfo.financeCode
  - fundingSource.fundingSourceInfo.file / fileResearch
- POST list binding for:
  - budgets
  - institutions
  - divisions
  - fundingRegions
  - fundingCountry
- Side effects when institution/division sets change (permission cache refresh).

## Practical Guardrails

- When changing required fields, align validator with UI conditional requirements.
- Avoid adding mandatory checks in validator for fields that may be hidden/disabled in the view.
- Keep phase filters consistent (actualPhase) in all list stream operations.
- Do not alter copy/delete behavior without checking replication impact.

## Quick Debug Checklist

- Confirm route and interceptor stack in struts-fundingSources.xml.
- Confirm form field names in fundingSource.ftl match action model paths.
- Confirm validate() is reached with save=true.
- Inspect action invalidFields/actionMessages when save does not persist.
- Verify fundingSourceInfo IDs and nested donor/lead center IDs are not -1 when required.
- Rebuild with: mvn -pl marlo-web -am -DskipTests compile

## Related Context (Open Only If Needed)

- reports/ai-context/save-validation-matrix.md
- reports/ai-context/interceptor-validator-playbook.md
- reports/ai-context/struts-critical-routing-catalog.md
- reports/ai-context/persistence-replication-managerimpl.md

## Token-Saving Usage for Claude

Use this sequence:

1. Read this file first.
2. Read only one action or validator file related to the exact bug.
3. Read the exact FTL section tied to failing fields.
4. Avoid broad repository scans unless route ownership is unclear.
5. Run a targeted compile only for marlo-web.

This keeps context focused and token usage low while preserving correctness.
