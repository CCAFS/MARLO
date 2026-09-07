# Frontend Composition Map

## Scope
This document maps how MARLO composes FTL pages through includes, imports, and macros/components.

## Composition Rules
1. `[#include]` composes page fragments (header, menu, footer, messages, submenus).
2. `[#import ... as alias]` imports macro libraries and template components.
3. Macro calls use aliases (`[@customForm.input ... /]`, `[@projectList.dashboardProjectsList ... /]`).
4. Dynamic list blocks use template macros (`isTemplate=true`) hidden in DOM and cloned by JS.

## Flow A: Home Dashboard (Projects)
- Main view: `marlo-web/src/main/webapp/WEB-INF/crp/views/home/dashboard.ftl`
- Includes:
  - `[#include "/WEB-INF/global/pages/header.ftl" /]`
  - `[#include "/WEB-INF/global/pages/main-menu.ftl" /]`
  - `[#include "/WEB-INF/global/pages/footer.ftl" /]`
- Imports:
  - `[#import "/WEB-INF/crp/macros/projectsListTemplate.ftl" as projectList /]`
  - `[#import "/WEB-INF/global/macros/homeDashboard.ftl" as indicatorLists /]`
- Component calls:
  - `[@projectList.dashboardProjectsList ... /]`
  - `[@indicatorLists.deliverablesHomeList ... /]`

## Flow B: POWB Financial Plan
- Main view: `marlo-web/src/main/webapp/WEB-INF/crp/views/powb/powb_financialPlan.ftl`
- Includes:
  - global layout: header/main-menu/footer
  - POWB partials: `submenu-powb.ftl`, `menu-powb.ftl`, `messages-powb.ftl`, `buttons-powb.ftl`
- Imports:
  - `[#import "/WEB-INF/crp/views/powb/macros-powb.ftl" as powbMacros /]`
- Macro/component usage:
  - Form controls via `forms.ftl` alias `customForm` (inherited from header import)
  - `[@customForm.textArea ... /]`, `[@customForm.input ... /]`
  - `[@powbMacros.projectBudgetsByFlagshipMacro ... /]`

## Flow C: Admin Portfolios Management
- Main view: `marlo-web/src/main/webapp/WEB-INF/crp/views/admin/portfoliosManagement.ftl`
- Includes:
  - `header.ftl`, `breadcrumb.ftl`, `generalMessages.ftl`, `menu-admin.ftl`, `footer.ftl`
- Local template macro in same file:
  - `[#macro feedbackCommentFieldsMacro ... isTemplate=false]`
  - template instance: `[@feedbackCommentFieldsMacro element={} ... isTemplate=true /]`
- Component usage:
  - `[@customForm.input ... /]`
  - `[@customForm.select ... /]`

## forms.ftl Usage Pattern
- Source macro library: `marlo-web/src/main/webapp/WEB-INF/global/macros/forms.ftl`
- Common macros used across sections:
  - `input`, `textArea`, `select`, `checkbox`, `radioButtonGroup`
- Binding convention:
  - Input names mirror Struts model paths (example: `powbSynthesis.financialPlan.financialPlanIssues`).

## Relations Popups
- Macro library: `marlo-web/src/main/webapp/WEB-INF/crp/macros/relationsPopupMacro.ftl`
- Button plus modal with a searchable, sortable, paginated and filterable table.
- Markup contract, shared JS/CSS, DataTables traps and the testing recipe:
  `reports/ai-context/relations-popup-pattern.md`. Read it before adding or upgrading one.

## Implementation Notes for AI
1. Default approach for UI changes: reuse existing macros from `forms.ftl` before adding raw HTML fields.
2. For expandable/dynamic blocks, follow `isTemplate=true` + hidden DOM template pattern.
3. Keep composition in FTL layer (includes/imports) and avoid introducing new rendering stacks.
4. For a relations popup, follow `reports/ai-context/relations-popup-pattern.md` rather than copying
   an existing macro: the older ones query inside the macro, once per related entity type.
