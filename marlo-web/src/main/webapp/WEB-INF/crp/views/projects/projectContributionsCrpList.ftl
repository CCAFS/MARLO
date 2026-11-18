[#ftl]
[#assign title = "Cluster Contributions to Performance Indicators" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#-- The table reuses the deliverables list redesign (A2-2607); contributionsCrpList.css only adds what is new here --]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectContributionsCrpList.js?20261117",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectsContributionToLP6.css",
  "${baseUrlMedia}/css/projects/deliverableList.css?20261019-4",
  "${baseUrlMedia}/css/projects/contributionsCrpList.css?20261118-2"
  ] /]
[#assign currentStage = "contributionsCrpList" /]
[#assign isListSection = true /]


[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectContributionsCrpList", "nameSpace":"${currentSection}", "action":""}
  ] /]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectContributionsCrpList", "nameSpace":"${currentSection}", "action":""}
  ] /]
[/#if]


[#-- The cluster chip is drawn in the section header below instead of the shared top quote --]
[#assign hideTopQuote = true /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]

[#assign startYear = (project.projectInfo.startDate?string.yyyy)?number /]
[#assign endYear = (project.projectInfo.endDate?string.yyyy)?number /]
[#-- ---------- Indicator table (A2-2741 redesign) ---------- --]

[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign feedbackActive = action.hasSpecificities('feedback_active') /]
[#assign ipIndicatorActive = action.hasSpecificities('crp_ip_outcome_indicator') /]
[#assign phaseLabel = "${(actualPhase.name)!} ${(actualPhase.year?c)!}"?trim /]
[#assign componentLabel = (action.getText("global.flagship"))!"Flagship" /]

[#-- Grid tracks follow the columns this global unit shows --]
[#assign gridColumns = "104px minmax(0,1fr) 196px" /]
[#if feedbackActive][#assign gridColumns = gridColumns + " 72px" /][/#if]
[#assign gridColumns = gridColumns + " 150px 44px" /]

[#-- Components, in the order their indicators are listed --]
[#assign componentNames = {} /]
[#assign componentOrder = [] /]
[#list (project.outcomes)![] as po]
  [#assign acr = (po.crpProgramOutcome.crpProgram.acronym)!"" /]
  [#if acr?has_content && !(componentOrder?seq_contains(acr))]
    [#assign componentOrder = componentOrder + [acr] /]
    [#assign componentNames = componentNames + { (acr) : ((po.crpProgramOutcome.crpProgram.name)!acr) } /]
  [/#if]
[/#list]

[#-- Completeness of each indicator, from one status lookup per indicator --]
[#assign ciStatus = {} /]
[#assign completeCount = 0 /]
[#list (project.outcomes)![] as po]
  [#assign sectionStatus = action.getProjectOutcomeStatus(po.id)! /]
  [#assign hasStatus = (sectionStatus.id)?? /]
  [#assign missingText = "" /]
  [#if hasStatus][#assign missingText = (sectionStatus.missingFields)!"" /][/#if]
  [#assign isOk = hasStatus && !missingText?trim?has_content /]
  [#assign missingCount = 0 /]
  [#list missingText?split(";") as field][#if field?trim?has_content][#assign missingCount = missingCount + 1 /][/#if][/#list]
  [#if isOk][#assign completeCount = completeCount + 1 /][/#if]
  [#assign ciStatus = ciStatus + { (po.id?c) : {"ok": isOk, "missing": missingCount} } /]
[/#list]
[#assign totalIndicators = ((project.outcomes)![])?size /]

[#-- Indicators already mapped are not offered again in the picker --]
[#assign mappedOutcomeIds = [] /]
[#list (project.outcomes)![] as po]
  [#if (po.crpProgramOutcome.id)??][#assign mappedOutcomeIds = mappedOutcomeIds + [po.crpProgramOutcome.id?c] /][/#if]
[/#list]
[#assign availableOutcomes = [] /]
[#list (outcomes)![] as outcome]
  [#if !(mappedOutcomeIds?seq_contains(outcome.id?c))]
    [#assign availableOutcomes = availableOutcomes + [outcome] /]
  [/#if]
[/#list]

[#-- Sets: the outer tabs of the global units that have them (deprecated or portfolio). One set otherwise --]
[#assign useTabs = action.hasSpecificities('show_contribution_performance_indicator_deprecated_tab_active') /]
[#assign portfolioActive = action.hasSpecificities('portfolio_feature_active') /]
[#assign sets = [] /]
[#assign setRows = {} /]

[#if useTabs]
  [#assign sets = sets + [{"key": "current", "label": action.getText("projectContributionsCrpList.redesign.sets.current"), "canAdd": canEdit, "badge": ""}] /]
  [#assign setRows = setRows + {"current": (mainOutcomes)![]} /]
  [#if (showDeprecatedTab)!false]
    [#assign sets = sets + [{"key": "deprecated", "label": action.getText("projectContributionsCrpList.redesign.sets.deprecated"), "canAdd": canEdit, "badge": ""}] /]
    [#assign setRows = setRows + {"deprecated": (deprecatedOutcomes)![]} /]
  [/#if]

[#elseif portfolioActive]
  [#assign portfoliosList = (portfolios)![]]
  [#assign outcomesList   = (project.outcomes)![]]

  [#assign outcomesByPortfolio = {}]
  [#assign portfolioLabels = {}]

  [#list outcomesList as po]
    [#assign outcome = po.crpProgramOutcome!]
    [#assign pf = (outcome.portfolio)!]
    [#assign pid = (pf.id?string)!'__no_portfolio__']
    [#assign label = (pf.acronym)!((pf.name)!'Portfolio Unassigned')]

    [#assign currentList = (outcomesByPortfolio[pid])![]]
    [#assign outcomesByPortfolio = outcomesByPortfolio + { (pid) : currentList + [ po ] }]
    [#assign portfolioLabels = portfolioLabels + { (pid) : label }]
  [/#list]

  [#assign associatedMap = {}]
  [#list portfoliosList as p]
    [#if p?? && p.id??]
      [#assign associatedMap = associatedMap + { (p.id?string) : ((p.associatedToCurrentPhase)!false) }]
    [/#if]
  [/#list]

  [#-- Portfolios with at least one indicator --]
  [#assign pfVisible = []]
  [#list portfoliosList as pf]
    [#if pf?? && pf.id??]
      [#assign tid = pf.id?string]
      [#assign listByPf = (outcomesByPortfolio[tid])![]]
      [#if listByPf?has_content]
        [#assign pfVisible = pfVisible + [ tid ] ]
        [#assign portfolioLabels = portfolioLabels + { (tid) : (pf.acronym)!((pf.name)!('Portfolio ' + tid)) } ]
      [/#if]
    [/#if]
  [/#list]

  [#list pfVisible as tid]
    [#assign isAssociated = (associatedMap[tid])!false /]
    [#assign sets = sets + [{"key": "pf-" + tid, "label": (portfolioLabels[tid])!('Portfolio ' + tid), "canAdd": canEdit && isAssociated, "badge": isAssociated?then(action.getText("projectContributionsCrpList.redesign.sets.currentPortfolio"), "")}] /]
    [#assign pfRows = (outcomesByPortfolio[tid])![] /]
    [#assign setRows = setRows + {("pf-" + tid): pfRows?sort_by(['crpProgramOutcome','description'])} /]
  [/#list]
[/#if]

[#if !sets?has_content]
  [#assign sets = [{"key": "all", "label": "", "canAdd": canEdit, "badge": ""}] /]
  [#assign setRows = {"all": portfolioActive?then([], (project.outcomes)![])} /]
[/#if]

[#-- The portfolio tab of the current phase opens first --]
[#assign activeSet = sets[0].key /]
[#list sets as set]
  [#if set.badge?has_content][#assign activeSet = set.key /][#break][/#if]
[/#list]
[#assign anyCanAdd = false /]
[#list sets as set][#if set.canAdd][#assign anyCanAdd = true /][/#if][/#list]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]

        <div id="projectContributionsCrpList">
          [@ciList /]
        </div>
      [@contributionToLP6 /]
      </div>
    </div>
</section>
[/#if]

[#-- Other contribution template --]
[@otherContributionMacro element={} name="name" index=-1 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- -- MACROS -- --]

[#-- Toolbar, table and add dialog. Every set renders into the same body; the script shows the active one --]
[#macro ciList]
  <div class="dlv ci" data-ci data-project-id="${projectID?c}" data-active-set="${activeSet}"
    data-showing-all="[@s.text name="projectContributionsCrpList.redesign.showingAll" /]"
    data-showing="[@s.text name="projectContributionsCrpList.redesign.showing" /]">

    [#-- Header: title, summary and the cluster chip (the shared top quote, redrawn here) --]
    <div class="dlv-head">
      <div class="dlv-head__main">
        <h1 class="dlv-head__title">[@s.text name="projectContributionsCrpList.title" /]</h1>
        <span class="dlv-head__summary">[@s.text name="projectContributionsCrpList.redesign.summary"][@s.param]${totalIndicators?c}[/@s.param][@s.param]${componentOrder?size?c}[/@s.param][@s.param]${completeCount?c}[/@s.param][@s.param]${(totalIndicators - completeCount)?c}[/@s.param][/@s.text]</span>
      </div>
      [#if (project.id)??]
        <a class="dlv-quote" href="[@s.url namespace="/${currentSection}" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
          data-tip="C${project.id?c} - ${(project.projectInfo.title)!}">
          <span class="dlv-quote__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
          [#if (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
        </a>
      [/#if]
    </div>

    [#-- Section help --]
    <div class="dlv-note" role="note">
      <span class="dlv-note__icon" aria-hidden="true">i</span>
      <div class="dlv-note__body">
        <p class="dlv-note__text">[@s.text name="projectContributionsCrpList.help" /]</p>
      </div>
    </div>

    [#-- Toolbar: set, component, search and add --]
    <div class="dlv-card dlv-toolbar ci-toolbar">
      [#if sets?size gt 1]
        <span class="dlv-seg ci-sets" role="tablist" aria-label="[@s.text name="projectContributionsCrpList.redesign.sets.label" /]">
          [#list sets as set]
            <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-ci-set="${set.key}">
              ${set.label}[#if set.badge?has_content] <span class="ci-setBadge">${set.badge}</span>[/#if]
              <span class="dlv-seg__count">${((setRows[set.key])![])?size?c}</span>
            </button>
          [/#list]
        </span>
      [/#if]
      [#if componentOrder?size gt 1]
        <span class="dlv-seg ci-comps" role="tablist" aria-label="${componentLabel}">
          <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-ci-comp="">[@s.text name="projectContributionsCrpList.redesign.comp.all" /] <span class="dlv-seg__count" data-ci-comp-count=""></span></button>
          [#list componentOrder as acr]
            <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-ci-comp="${acr}">${acr} <span class="dlv-seg__count" data-ci-comp-count="${acr}"></span></button>
          [/#list]
        </span>
      [/#if]
      <span class="dlv-search">
        [@ciIcon "search" /]
        <input type="search" class="dlv-input" data-ci-search autocomplete="off"
          placeholder="[@s.text name="projectContributionsCrpList.redesign.search.placeholder" /]"
          aria-label="[@s.text name="projectContributionsCrpList.redesign.search.label" /]" />
      </span>
      [#if anyCanAdd]
        <span class="dlv-toolbar__end">
          <button type="button" class="dlv-btn dlv-btn--primary" data-ci-open-add
            data-ci-add-sets="[#list sets as set][#if set.canAdd]${set.key} [/#if][/#list]">
            [@ciIcon "plus" /][@s.text name="form.buttons.addOutcome" /]
          </button>
        </span>
      [/#if]
    </div>

    [#-- Indicator table --]
    <div class="dlv-card dlv-table" style="--dlv-cols: ${gridColumns}">

      [#-- Status filter --]
      <div class="dlv-filters" data-ci-filters>
        <span class="dlv-chips" role="group" aria-label="[@s.text name="project.deliverableList.redesign.status.label" /]">
          <button type="button" class="dlv-chip" aria-pressed="false" data-ci-status="">[@s.text name="project.deliverableList.redesign.status.all" /] <span class="dlv-chip__count" data-ci-status-count=""></span></button>
          <button type="button" class="dlv-chip" aria-pressed="false" data-ci-status="missing"><span class="dlv-dot dlv-dot--o"></span>[@s.text name="projectContributionsCrpList.redesign.status.missing" /] <span class="dlv-chip__count" data-ci-status-count="missing"></span></button>
          <button type="button" class="dlv-chip" aria-pressed="false" data-ci-status="complete"><span class="dlv-dot dlv-dot--c"></span>[@s.text name="projectContributionsCrpList.redesign.status.complete" /] <span class="dlv-chip__count" data-ci-status-count="complete"></span></button>
        </span>
        <span class="dlv-filters__end">
          <button type="button" class="dlv-link dlv-clearFilters" data-ci-clear data-ci-clear-filters hidden>
            [@ciIcon "close" /][@s.text name="project.deliverableList.redesign.empty.clear" /]
          </button>
        </span>
      </div>

      [#if reportingActive && canEdit]
        <div class="dlv-focus" data-ci-report-strip hidden>
          [@s.text name="projectContributionsCrpList.reportingHelp" /] [@s.text name="projectContributionsCrpList.redesign.reportTagged" /] <span class="dlv-report">[@ciIcon "flash" /][@s.text name="project.deliverableList.redesign.report" /]</span>
        </div>
      [/#if]

      <div class="dlv-grid" role="table" aria-label="[@s.text name="projectContributionsCrpList.title" /]" data-ci-grid>
        <div role="rowgroup">
          <div class="dlv-row__main dlv-row__main--head" role="row">
            [@ciHead "comp" componentLabel /]
            [@ciHead "code" action.getText("projectContributionsCrpList.redesign.col.indicator") /]
            [@ciHead "" action.getText("projectContributionsCrpList.redesign.col.mapped") /]
            [#if feedbackActive][@ciHead "" action.getText("project.deliverableList.redesign.col.feedback") /][/#if]
            [@ciHead "status" action.getText("project.deliverableList.redesign.col.status") /]
            <span class="dlv-th" role="columnheader"><span class="sr-only">[@s.text name="project.deliverableList.redesign.col.actions" /]</span></span>
          </div>
        </div>
        <div role="rowgroup" data-ci-body>
          [#list sets as set]
            [#list (setRows[set.key])![] as projectOutcome]
              [@ciRow projectOutcome=projectOutcome set=set.key /]
            [/#list]
          [/#list]
        </div>
      </div>

      [#-- Filters left nothing --]
      <div class="dlv-empty" data-ci-empty hidden>
        <span class="dlv-empty__title">[@s.text name="projectContributionsCrpList.redesign.empty.title" /]</span>
        <span class="dlv-empty__text">[@s.text name="projectContributionsCrpList.redesign.empty.text" /]</span>
        <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm dlv-empty__clear" data-ci-clear>[@s.text name="project.deliverableList.redesign.empty.clear" /]</button>
      </div>

      [#-- The active set has no indicator at all --]
      <div class="dlv-empty" data-ci-none hidden>
        <span class="dlv-empty__title">[@s.text name="projectContributionsCrpList.redesign.none.title"][@s.param]${sectionNoun}[/@s.param][/@s.text]</span>
        <span class="dlv-empty__text">
          [#if anyCanAdd]
            [@s.text name="projectContributionsCrpList.redesign.none.text"][@s.param]${sectionNoun}[/@s.param][/@s.text]
          [#else]
            [@s.text name="projectContributionsCrpList.redesign.none.textReadOnly" /]
          [/#if]
        </span>
        [#if anyCanAdd]
          <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm dlv-empty__clear" data-ci-open-add
            data-ci-add-sets="[#list sets as set][#if set.canAdd]${set.key} [/#if][/#list]">[@s.text name="form.buttons.addOutcome" /]</button>
        [/#if]
      </div>

      [#-- Footer: count and a second Add, so a long list does not need a scroll back up --]
      <div class="dlv-foot" data-ci-foot>
        <span class="dlv-foot__showing" data-ci-showing aria-live="polite"></span>
        [#if anyCanAdd]
          <button type="button" class="dlv-btn dlv-btn--primary ci-foot__add" data-ci-open-add
            data-ci-add-sets="[#list sets as set][#if set.canAdd]${set.key} [/#if][/#list]">
            [@ciIcon "plus" /][@s.text name="form.buttons.addOutcome" /]
          </button>
        [/#if]
      </div>
    </div>

    [#if anyCanAdd]
      [@ciAddDialog /]
    [/#if]
  </div>
[/#macro]

[#-- Sortable (key) or plain column header --]
[#macro ciHead key label]
  [#if key?has_content]
    <span class="dlv-th" role="columnheader" aria-sort="none" data-ci-sort-col="${key}">
      <button type="button" class="dlv-th__btn" data-ci-sort="${key}">${label}[@ciIcon "arrow" /]</button>
    </span>
  [#else]
    <span class="dlv-th" role="columnheader">${label}</span>
  [/#if]
[/#macro]

[#-- "(2024-2025) PDO Indicator 1: Partners…" splits into period, code and text; other wordings stay whole --]
[#function ciParse description]
  [#local clean = (description!"")?replace("\x200B", "")?trim /]
  [#local match = clean?matches(r"^\((DEPRECATED|[0-9]{4}-[0-9]{4})\)\s*(.*?\d+(?:\.\d+)?)\s*:?\s+(.*)$", "s") /]
  [#if match]
    [#local period = match?groups[1] /]
    [#return {"code": match?groups[2], "text": match?groups[3], "period": (period == "DEPRECATED")?then("", period), "deprecated": period == "DEPRECATED"} /]
  [/#if]
  [#return {"code": "", "text": clean, "period": "", "deprecated": clean?contains("DEPRECATED")} /]
[/#function]

[#-- Deduplicates a related-items list by id --]
[#function ciUnique items]
  [#local seen = [] /]
  [#local result = [] /]
  [#list items![] as item]
    [#if (item.id)?? && !(seen?seq_contains(item.id?c))]
      [#local seen = seen + [item.id?c] /]
      [#local result = result + [item] /]
    [/#if]
  [/#list]
  [#return result /]
[/#function]

[#-- One indicator mapped to the cluster --]
[#macro ciRow projectOutcome set]
  [#local id = projectOutcome.id?c /]
  [#local cpo = projectOutcome.crpProgramOutcome /]
  [#local parsed = ciParse((cpo.description)!"") /]
  [#local label = parsed.code?has_content?then(parsed.code, "#" + id) /]
  [#local comp = (cpo.crpProgram.acronym)!"" /]
  [#local compName = (cpo.crpProgram.name)!comp /]
  [#local url = "${baseUrl}/projects/${crpSession}/contributionCrp.do?projectOutcomeID=${id}&edit=true&phaseID=${(actualPhase.id)!}" /]
  [#local hasDraft = (action.getAutoSaveFilePath(projectOutcome.class.simpleName, "contributionCrp", projectOutcome.id))!false /]

  [#-- Completeness, from the pre-pass at the top --]
  [#local rowStatus = (ciStatus[id])!{"ok": false, "missing": 0} /]
  [#local isComplete = rowStatus.ok /]
  [#local missingCount = rowStatus.missing /]

  [#-- Feedback "answered/total" --]
  [#local feedback = (projectOutcome.commentStatus)!"" /]
  [#local fbClass = "is-empty" /]
  [#if feedback?contains("/")]
    [#local fbAnswered = (feedback?keep_before("/")?trim?number)!0 /]
    [#local fbTotal = (feedback?keep_after("/")?trim?number)!0 /]
    [#if fbTotal gt 0][#local fbClass = (fbAnswered lt fbTotal)?then("ci-fb--open", "ci-fb--done") /][/#if]
  [/#if]

  [#-- Items mapped to this indicator --]
  [#local deliverables = ciUnique((action.getDeliverableRelationsProject(projectOutcome.id, projectOutcome.class.name, project.id))![]) /]
  [#local studies = ciUnique((action.getexpectedCrpOutcomes(projectOutcome.id))![]) /]
  [#local innovations = ciUnique((action.getInnovationProjectOutcomes(projectOutcome.id))![]) /]
  [#local relCounts = {"d": deliverables?size, "o": studies?size, "i": innovations?size} /]
  [#local hasRelations = (deliverables?size + studies?size + innovations?size) gt 0 /]

  [#-- Remove: the legacy rule, word for word --]
  [#local canDelete = ((action.hasPermission("delete"))!true) && action.canBeDeleted((projectOutcome.id)!-1, (projectOutcome.class.name)!"") && !action.isCenterGlobalUnit() /]
  [#local deleteLocked = !canDelete && canEdit /]

  <div class="dlv-row" data-ci-row data-set="${set}" data-id="${id}" data-comp="${comp}" data-code="${label}"
    data-ok="${isComplete?then("1", "0")}" data-search="${label} ${parsed.text} ${comp}"[#if set != activeSet] hidden[/#if]>
    <div class="dlv-row__main" role="row">
      <span class="dlv-td ci-td--comp" role="cell" data-label="${componentLabel}">
        [#if comp?has_content]<span class="ci-comp" data-tip="${compName}">${comp}</span>[/#if]
      </span>
      <span class="dlv-td dlv-td--title" role="cell">
        <a class="dlv-title ci-title" href="${url}">
          [#if reportingActive && !isComplete]<span class="dlv-report">[@ciIcon "flash" /][@s.text name="project.deliverableList.redesign.report" /]</span>[/#if]
          [#if hasDraft]<span class="dlv-tag dlv-tag--draft">[@s.text name="project.deliverableList.redesign.draft" /]</span>[/#if]
          [#if parsed.code?has_content]<span class="ci-code">${parsed.code}</span>: [/#if]${parsed.text}
        </a>
        <span class="dlv-meta">
          [#if parsed.period?has_content]<span>${parsed.period}</span>[/#if]
          [#if parsed.deprecated]<span class="dlv-tag ci-tag--deprecated">[@s.text name="projectContributionsCrpList.redesign.deprecated" /]</span>[/#if]
          [#if ipIndicatorActive && (cpo.indicator)?has_content]
            <span class="ci-indicator"><span class="dlv-meta__strong">[@s.text name="projectContributionsCrpList.redesign.indicator" /]:</span> ${cpo.indicator}</span>
          [/#if]
        </span>
      </span>
      <span class="dlv-td ci-td--mapped" role="cell" data-label="[@s.text name="projectContributionsCrpList.redesign.col.mapped" /]">
        [#if hasRelations]
          [#list ["d", "o", "i"] as type]
            [#if relCounts[type] gt 0]
              [#local typeLabel = ciRelLabel(type, relCounts[type]) /]
              <button type="button" class="ci-relChip" data-ci-rel="${type}" aria-expanded="false" aria-controls="ci-rel-${id}"
                aria-label="[@s.text name="projectContributionsCrpList.redesign.rel.show"][@s.param]${relCounts[type]?c} ${typeLabel}[/@s.param][@s.param]${label}[/@s.param][/@s.text]">
                ${relCounts[type]?c} <span class="ci-relChip__label">${typeLabel}</span>
              </button>
            [/#if]
          [/#list]
        [#else]
          <span class="dlv-muted">[@s.text name="projectContributionsCrpList.redesign.rel.none" /]</span>
        [/#if]
      </span>
      [#if feedbackActive]
        <span class="dlv-td dlv-td--feedback ci-fb ${fbClass}" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.feedback" /]"
          data-tip="[#if feedback?has_content][@s.text name="project.deliverableList.redesign.feedback.title"][@s.param]${feedback}[/@s.param][/@s.text][#else][@s.text name="project.deliverableList.redesign.feedback.none" /][/#if]">
          [#if feedback?has_content][@ciIcon "comment" /]${feedback}[#else]—[/#if]
        </span>
      [/#if]
      <span class="dlv-td dlv-td--status" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.status" /]">
        [#if isComplete]
          <span class="dlv-status ci-status--ok">[@ciIcon "ok" /][@s.text name="projectContributionsCrpList.redesign.status.complete" /]</span>
        [#else]
          <span class="dlv-status ci-status--warn">[@ciIcon "warn" /][@s.text name="projectContributionsCrpList.redesign.status.missing" /]</span>
          [#if missingCount gt 0]
            <span class="ci-missing">[#if missingCount == 1][@s.text name="projectContributionsCrpList.redesign.missingOne" /][#else][@s.text name="projectContributionsCrpList.redesign.missingCount"][@s.param]${missingCount?c}[/@s.param][/@s.text][/#if]</span>
          [/#if]
        [/#if]
      </span>
      <span class="dlv-td dlv-td--action" role="cell">
        [#if canEdit && canDelete]
          <button type="button" class="dlv-iconBtn dlv-iconBtn--danger" data-ci-remove aria-expanded="false" aria-controls="ci-confirm-${id}"
            data-tip="[@s.text name="projectContributionsCrpList.redesign.remove"][@s.param]${label}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]"
            aria-label="[@s.text name="projectContributionsCrpList.redesign.remove"][@s.param]${label}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]">[@ciIcon "trash" /]</button>
        [#elseif deleteLocked]
          <button type="button" class="dlv-iconBtn ci-lockBtn" data-ci-locked aria-expanded="false" aria-controls="ci-locked-${id}"
            data-tip="[@s.text name="projectContributionsCrpList.redesign.locked"][@s.param]${label}[/@s.param][/@s.text]"
            aria-label="[@s.text name="projectContributionsCrpList.redesign.locked"][@s.param]${label}[/@s.param][/@s.text]">[@ciIcon "trashLock" /]</button>
        [#else]
          <a class="dlv-iconBtn" href="${url}" aria-label="[@s.text name="projectContributionsCrpList.redesign.open"][@s.param]${label}[/@s.param][/@s.text]">[@ciIcon "chevron" /]</a>
        [/#if]
      </span>
    </div>

    [#-- Mapped items, one tab per type --]
    [#if hasRelations]
      <div class="ci-rel" id="ci-rel-${id}" role="region" aria-label="[@s.text name="projectContributionsCrpList.redesign.rel.region"][@s.param]${label}[/@s.param][/@s.text]" data-ci-rel-panel hidden>
        <div class="ci-rel__head">
          <span class="dlv-seg ci-rel__tabs" role="tablist" aria-label="[@s.text name="projectContributionsCrpList.redesign.col.mapped" /]">
            [#list ["d", "o", "i"] as type]
              <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-ci-rel-tab="${type}"[#if relCounts[type] == 0] disabled[/#if]>
                ${ciRelLabel(type, 2)} <span class="dlv-seg__count">${relCounts[type]?c}</span>
              </button>
            [/#list]
          </span>
          <span class="ci-rel__note">[@s.text name="projectContributionsCrpList.redesign.rel.note"][@s.param]${label}[/@s.param][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
          <button type="button" class="dlv-iconBtn ci-rel__close" data-ci-rel-close aria-label="[@s.text name="projectContributionsCrpList.redesign.rel.close" /]">[@ciIcon "close" /]</button>
        </div>
        [@ciRelList type="d" items=deliverables /]
        [@ciRelList type="o" items=studies /]
        [@ciRelList type="i" items=innovations /]
      </div>
    [/#if]

    [#-- Why the indicator cannot be removed --]
    [#if deleteLocked]
      <div class="ci-locked" id="ci-locked-${id}" role="status" data-ci-locked-note hidden>
        [@ciIcon "lock" /]
        <span class="ci-locked__text">
          <strong>[@s.text name="projectContributionsCrpList.redesign.locked.title"][@s.param]${label}[/@s.param][/@s.text]</strong>
          [#if hasRelations]
            [#local parts = [] /]
            [#list ["d", "o", "i"] as type]
              [#if relCounts[type] gt 0][#local parts = parts + [relCounts[type]?c + " " + ciRelLabel(type, relCounts[type])] /][/#if]
            [/#list]
            [#local partsText = parts?join(", ") /]
            [#if parts?size gt 1][#local partsText = parts[0..parts?size - 2]?join(", ") + " " + action.getText("projectContributionsCrpList.redesign.and") + " " + parts?last /][/#if]
            [@s.text name="projectContributionsCrpList.redesign.locked.text"][@s.param]${partsText}[/@s.param][/@s.text]
          [#else]
            [@s.text name="projectContributionsCrpList.redesign.locked.generic"][@s.param]${sectionNoun}[/@s.param][/@s.text]
          [/#if]
        </span>
        [#if hasRelations]
          <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm" data-ci-show-mapped>[@s.text name="projectContributionsCrpList.redesign.locked.showMapped" /]</button>
        [/#if]
      </div>
    [/#if]

    [#-- Inline confirm: the link runs removeProjectOuctome.do --]
    [#if canEdit && canDelete]
      <div class="dlv-confirm" id="ci-confirm-${id}" role="alertdialog" aria-labelledby="ci-confirm-text-${id}" data-ci-confirm hidden>
        <span class="dlv-confirm__text" id="ci-confirm-text-${id}">[@s.text name="projectContributionsCrpList.redesign.remove.confirm"][@s.param]${label}[/@s.param][@s.param]${sectionNoun}[/@s.param][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
        <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm" data-ci-keep>[@s.text name="projectContributionsCrpList.redesign.remove.keep" /]</button>
        <a class="dlv-btn dlv-btn--danger dlv-btn--sm" href="${baseUrl}/projects/${crpSession}/removeProjectOuctome.do?projectID=${projectID}&outcomeId=${id}&phaseID=${(actualPhase.id)!}">[@s.text name="projectContributionsCrpList.redesign.remove.submit" /]</a>
      </div>
    [/#if]
  </div>
[/#macro]

[#-- Singular or plural label of a mapped-item type --]
[#function ciRelLabel type count]
  [#local keys = {"d": ["deliverable", "deliverables"], "o": ["study", "studies"], "i": ["innovation", "innovations"]}[type] /]
  [#return action.getText("projectContributionsCrpList.redesign.rel." + keys[(count == 1)?then(0, 1)]) /]
[/#function]

[#-- The items of one type inside the mapped-items panel --]
[#macro ciRelList type items]
  [#if items?has_content]
    <div class="ci-rel__list" role="tabpanel" data-ci-rel-list="${type}" hidden>
      [#list items as item]
        [#if type == "d"]
          [#local itemId = "D" + item.id?c /]
          [#local itemTitle = (item.deliverableInfo.title)!"" /]
          [#local itemUrl][@s.url namespace="/${currentSection}" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#elseif type == "o"]
          [#local itemId = item.id?c /]
          [#local itemTitle = (item.projectExpectedStudyInfo.title)!"" /]
          [#local itemUrl][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#else]
          [#local itemId = "I" + item.id?c /]
          [#local itemTitle = (item.projectInnovationInfo.title)!"" /]
          [#local itemUrl][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [/#if]
        <a class="ci-rel__item" href="${itemUrl}">
          <span class="ci-rel__id">${itemId}</span>
          <span class="ci-rel__title">[#if itemTitle?has_content]${itemTitle}[#else]<span class="dlv-title__none">[@s.text name="projectsList.title.none" /]</span>[/#if]</span>
        </a>
      [/#list]
    </div>
  [/#if]
[/#macro]

[#-- Picker dialog: indicators of the cluster's components that are not mapped yet --]
[#macro ciAddDialog]
  [#local live = [] /]
  [#local deprecated = [] /]
  [#list availableOutcomes as outcome]
    [#if (outcome.description!"")?contains("DEPRECATED") || ((outcome.portfolio.id)!2) lt 2]
      [#local deprecated = deprecated + [outcome] /]
    [#else]
      [#local live = live + [outcome] /]
    [/#if]
  [/#list]
  [#-- Components in the order the options come --]
  [#local groups = [] /]
  [#local groupNames = {} /]
  [#list live as outcome]
    [#local acr = (outcome.crpProgram.acronym)!"" /]
    [#if !(groups?seq_contains(acr))]
      [#local groups = groups + [acr] /]
      [#local groupNames = groupNames + { (acr) : ((outcome.crpProgram.name)!acr) } /]
    [/#if]
  [/#list]
  <dialog class="ci-dlg" aria-labelledby="ci-dlg-title" data-ci-dlg
    data-add-url="${baseUrl}/projects/${crpSession}/addNewProjectOuctome.do?projectID=${projectID}&phaseID=${(actualPhase.id)!}">
    <div class="ci-dlg__head">
      <span class="ci-dlg__heading">
        <span class="ci-dlg__title" id="ci-dlg-title">[@s.text name="form.buttons.addOutcome" /]</span>
        <span class="ci-dlg__sub">[@s.text name="projectContributionsCrpList.selectOutcome" /]</span>
      </span>
      <button type="button" class="ci-dlg__close" data-ci-dlg-close aria-label="[@s.text name="projectContributionsCrpList.redesign.dlg.close" /]">[@ciIcon "close" /]</button>
    </div>
    <div class="ci-dlg__search">
      <span class="dlv-search">
        [@ciIcon "search" /]
        <input type="search" class="dlv-input" data-ci-dlg-search autocomplete="off"
          placeholder="[@s.text name="projectContributionsCrpList.redesign.search.placeholder" /]"
          aria-label="[@s.text name="projectContributionsCrpList.redesign.dlg.search.label" /]" />
      </span>
    </div>
    <div class="ci-dlg__body" role="radiogroup" aria-label="[@s.text name="projectContributionsCrpList.redesign.dlg.options.label" /]">
      [#list groups as acr]
        <div class="ci-dlg__group" data-ci-dlg-group>
          <span class="ci-dlg__groupHead">${acr} <span class="ci-dlg__groupName">${(groupNames[acr])!""}</span></span>
          [#list live as outcome]
            [#if ((outcome.crpProgram.acronym)!"") == acr][@ciOption outcome=outcome /][/#if]
          [/#list]
        </div>
      [/#list]
      <span class="ci-dlg__empty" data-ci-dlg-empty[#if live?has_content] hidden[/#if]>
        [#if availableOutcomes?has_content][@s.text name="projectContributionsCrpList.redesign.dlg.empty" /][#else][@s.text name="projectContributionsCrpList.redesign.dlg.none" /][/#if]
      </span>
      [#if deprecated?has_content]
        <div class="ci-dlg__dep">
          <button type="button" class="ci-dlg__depToggle" data-ci-dlg-dep aria-expanded="false" aria-controls="ci-dlg-dep-list">
            [@ciIcon "chevron" /]
            <span data-ci-dlg-dep-show>[@s.text name="projectContributionsCrpList.redesign.dlg.showDeprecated"][@s.param]${deprecated?size?c}[/@s.param][/@s.text]</span>
            <span data-ci-dlg-dep-hide hidden>[@s.text name="projectContributionsCrpList.redesign.dlg.hideDeprecated"][@s.param]${deprecated?size?c}[/@s.param][/@s.text]</span>
          </button>
          <div class="ci-dlg__depList" id="ci-dlg-dep-list" data-ci-dlg-dep-list hidden>
            <span class="ci-dlg__depNote">[@s.text name="projectContributionsCrpList.redesign.dlg.deprecatedNote" /]</span>
            [#list deprecated as outcome][@ciOption outcome=outcome isDeprecated=true /][/#list]
          </div>
        </div>
      [/#if]
    </div>
    <div class="ci-dlg__foot">
      <span class="ci-dlg__error" role="alert" data-ci-dlg-error hidden>[@s.text name="projectContributionsCrpList.redesign.dlg.error"][@s.param]${sectionNoun}[/@s.param][/@s.text]</span>
      <span class="ci-dlg__count" data-ci-dlg-count>[@s.text name="projectContributionsCrpList.redesign.dlg.available"][@s.param]${live?size?c}[/@s.param][@s.param]${deprecated?size?c}[/@s.param][/@s.text]</span>
      <button type="button" class="dlv-btn dlv-btn--ghost" data-ci-dlg-close>[@s.text name="projectContributionsCrpList.redesign.dlg.cancel" /]</button>
      <button type="button" class="dlv-btn dlv-btn--primary" data-ci-dlg-add>[@ciIcon "plus" /][@s.text name="form.buttons.addOutcome" /]</button>
    </div>
  </dialog>
[/#macro]

[#-- One radio option of the picker --]
[#macro ciOption outcome isDeprecated=false]
  [#local parsed = ciParse((outcome.description)!"") /]
  [#local acr = (outcome.crpProgram.acronym)!"" /]
  <label class="ci-opt" data-ci-dlg-opt data-search="${(parsed.code + " " + parsed.text)?lower_case}">
    <input type="radio" name="ci-add-outcome" value="${outcome.id?c}" />
    <span class="ci-opt__body">
      <span class="ci-opt__text[#if isDeprecated] is-deprecated[/#if]">
        [#if isDeprecated]<span class="dlv-tag ci-tag--deprecated">[@s.text name="projectContributionsCrpList.redesign.deprecated" /]</span>[/#if]
        [#if parsed.code?has_content]<span class="ci-code">${parsed.code}</span>: [/#if]${parsed.text}
      </span>
      <span class="ci-opt__meta">[#if isDeprecated]${(outcome.crpProgram.name)!acr}[#else]${parsed.period}[/#if]</span>
    </span>
  </label>
[/#macro]

[#macro ciIcon name]
  [#switch name]
    [#case "plus"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
    [#case "search"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>[#break]
    [#case "arrow"]<svg class="dlv-icon dlv-th__arrow" width="9" height="9" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M6 10V2M2.8 5.2 6 2l3.2 3.2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "close"]<svg class="dlv-icon" width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M3 3l6 6M9 3 3 9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
    [#case "comment"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M2.6 3.4h10.8v7.2H7.2l-3 2.4v-2.4H2.6V3.4Z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"></path></svg>[#break]
    [#case "trash"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "trashLock"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="#9AA4B0" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path><rect x="9.2" y="9.6" width="5.6" height="4.4" rx="1" fill="#fff" stroke="#6B7280" stroke-width="1.2"></rect><path d="M10.4 9.6V8.8a1.6 1.6 0 0 1 3.2 0v.8" stroke="#6B7280" stroke-width="1.2"></path></svg>[#break]
    [#case "lock"]<svg class="dlv-icon ci-locked__icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>[#break]
    [#case "chevron"]<svg class="dlv-icon" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "flash"]<svg class="dlv-icon" width="8" height="10" viewBox="0 0 10 12" fill="none" aria-hidden="true"><path d="M6 1 1.6 6.8h3.2L4 11l4.4-5.8H5.2L6 1Z" fill="currentColor"></path></svg>[#break]
    [#case "ok"]<svg class="dlv-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="#2E6B2F"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "warn"]<svg class="dlv-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="#C08A2E"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>[#break]
  [/#switch]
[/#macro]


[#macro otherContributionMacro element name index template=false ]
  [#assign customName = "${name}[${template?string('-1',index)}]" /]
  [#assign contribution = (element)!{} /]
  <div id="otherContribution-${template?string('template',index)}" class="otherContribution simpleBox" style="display:${template?string('none','block')}">
    <div class="loading" style="display:none"></div>
    [#-- Edit/Back/remove buttons --]
    [#if editable]<div class="removeElement" title="[@s.text name="projectOtherContributions.removeOtherContribution" /]"></div>[/#if]
    [#-- Other Contribution ID --]
    <input type="hidden" name="${customName}.id" class="otherContributionId" value="${(contribution.id)!-1}"/>
    [#-- Indicator --]
    <div class="form-group ${reportingActive?string('fieldFocus','')}">
      <div class="row">
        <div class="col-md-12">
          [@customForm.select name="${customName}.outcome" className="otherContributionIndicator" label="" i18nkey="projectOtherContribution.outcome" listName="outcomes" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
        </div>
      </div>
    </div>
    [#-- Describe how you are contributing to the selected outcome --]
    <div class="form-group">
      <div class="row">
        <div class="col-md-12">
        <label>[@customForm.text name="projectOtherContribution.description" param="${currentCycleYear}" readText=!editable /]:[@customForm.req required=editable /]</label>
        [@customForm.textArea name="${customName}.description" className="otherContributionDescription limitWords-100 ${reportingActive?string('fieldFocus','')}"  i18nkey="" showTitle=false required=true editable=editable  /]
        </div>
      </div>
    </div>
    [#-- Target contribution --]
    <div class="form-group">
      <div class="row">
        <div class="col-md-5">
          <label>[@customForm.text name="projectOtherContribution.target" readText=!editable /]:</label>
          [@customForm.input name="${customName}.target" className="otherContributionTarget ${reportingActive?string('fieldFocus','')}" i18nkey="" showTitle=false editable=editable  /]
        </div>
      </div>
    </div>
  </div>
[/#macro]

[#macro contributionToLP6 template=false]
[#if action.hasSpecificities('crp_lp6_active') && reportingActive]
[#assign isContributing = ((action.getProjectLp6ContributionValue(project.id, actualPhase.id))!false) ]
  <div id="projectContributionToLP6" class="borderBox project-${project.id} phase-${actualPhase.id}">

   [#-- <a class="btn lp6-pdf btn-link" role="button" data-toggle="popover" data-trigger="focus" title="[@s.text name="projects.LP6Contribution.disabledPDF"/]"><img src="${baseUrlCdn}/global/images/pdf.png" height="25"/>[[@s.text name="projects.LP6Contribution.explanatoryPDF" /]]</a>--]
   <h4>[@s.text name="projects.LP6Contribution.title" /]</h4>
   <p class="note lp6-contribution-note"><small>[@s.text name="projects.LP6Contribution.infoText"/] (<span class="lp6-view-more" data-toggle="modal" data-target=".lp6info-modal">view more</span>)</small></p>
   <div class="form-group">
       <label>[@s.text name="projects.LP6Contribution.contribution"/][@customForm.req required=true /]</label>
       [@customForm.radioFlat id="lp6Contribution-yes" name="lp6Contribution" label="Yes" value="true" checked=(projectLp6Contribution.contribution)!false cssClassLabel="radio-label-yes" editable=Editable /]
       [@customForm.radioFlat id="lp6Contribution-no" name="lp6Contribution" label="No" value="false" checked=!((projectLp6Contribution.contribution)!true) cssClassLabel="radio-label-no" editable=Editable /]
  </div>
  <div class="form-group contributionNote note" style="display:${(isContributing)?string('block','none')}">
     [@s.text name="projects.LP6Contribution.contributionNote"/]
  </div>
  </div>

  [#-- LP6 Help Text expanded --]
    <div class="modal fade extended-table-modal lp6info-modal" tabindex="-1" role="dialog" aria-labelledby="extendedTableModal" aria-hidden="true">
      <div class="modal-dialog modal-lg">
         <div class="modal-content">
           <button type="button" class="close lp6-close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
             <div class="lp6-help">[@s.text name="projects.LP6Contribution.helpText"/]</div>
          </div>
      </div>
    </div>
[/#if]
[/#macro]

[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.endDate??]
    [#assign endDate = (project.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]