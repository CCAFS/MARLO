[#ftl]
[#assign title = "Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#-- The list reuses the deliverables list redesign (A2-2607); innovationList.css only adds what is new here --]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/innovationList.js?20261021",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/deliverableList.css?20261019-4",
  "${baseUrlMedia}/css/projects/innovationList.css?20261021"
] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]
[#assign isListSection = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":""}
]/]

[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign feedbackActive = action.hasSpecificities('feedback_active') /]
[#assign currentInnovations = (projectInnovations)![] /]
[#assign previousInnovations = (projectOldInnovations)![] /]
[#assign phaseLabel = "${(actualPhase.name)!} ${(actualPhase.year?c)!}"?trim /]
[#-- The cluster chip is drawn in the section header below instead of the shared top quote --]
[#assign hideTopQuote = true /]

[#-- Readiness scale 1-10 is shown as level 0-9 --]
[#assign readinessNames = ["Idea", "Basic Research", "Formulation", "Proof of Concept", "Controlled Testing", "Model/Early Prototype", "Semi-Controlled Testing", "Prototype", "Uncontrolled Testing", "Proven Innovation"] /]

[#-- Grid tracks follow the columns this global unit shows --]
[#assign gridColumns = "72px minmax(0,1fr) 150px 172px 62px" /]
[#if feedbackActive][#assign gridColumns = gridColumns + " 72px" /][/#if]
[#assign gridColumns = gridColumns + " 132px 76px" /]

[#assign missingCount = 0 /]
[#list currentInnovations as innovation]
  [#if !((action.hasInnovationMissingFields(innovation.id))!false)][#assign missingCount = missingCount + 1 /][/#if]
[/#list]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

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

      <div class="dlv inv" data-inv data-project-id="${projectID?c}"
        data-showing="[@s.text name="project.deliverableList.redesign.showing" /]"
        data-no-entries="[@s.text name="project.deliverableList.redesign.noEntries" /]">

        [#-- Header: title, summary and the cluster chip (the shared top quote, redrawn here) --]
        <div class="dlv-head">
          <div class="dlv-head__main">
            <h1 class="dlv-head__title">[@s.text name="project.innovationList.title" /]</h1>
            <span class="dlv-head__summary">[@s.text name="project.innovationList.redesign.summary"][@s.param]${currentInnovations?size?c}[/@s.param][@s.param]${phaseLabel}[/@s.param][@s.param]${missingCount?c}[/@s.param][@s.param]${previousInnovations?size?c}[/@s.param][/@s.text]</span>
          </div>
          [#if (project.id)??]
            <a class="dlv-quote" href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
              title="C${project.id?c} - ${(project.projectInfo.title)!}">
              <span class="dlv-quote__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
              [#if (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
            </a>
          [/#if]
        </div>

        [#-- Toolbar: innovation set and add --]
        <div class="dlv-card dlv-toolbar">
          <span class="dlv-seg" role="tablist" aria-label="[@s.text name="project.innovationList.redesign.tabs.label" /]">
            <button type="button" role="tab" class="dlv-seg__btn is-on" aria-selected="true" data-inv-tab="current">${phaseLabel} <span class="dlv-seg__count">${currentInnovations?size?c}</span></button>
            <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-inv-tab="previous">[@s.text name="project.innovationList.redesign.tabs.previous" /] <span class="dlv-seg__count">${previousInnovations?size?c}</span></button>
          </span>
          [#if canEdit]
            <span class="dlv-toolbar__end">
              <a class="dlv-btn dlv-btn--primary" href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewInnovation'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                [@invIcon "plus" /][@s.text name="project.innovationList.redesign.add" /]
              </a>
            </span>
          [/#if]
        </div>

        [#-- Innovations table --]
        <div class="dlv-card dlv-table" style="--dlv-cols: ${gridColumns}">

          [#-- Filters: search, then readiness, type and year (options built by the script from the rows) --]
          <div class="dlv-filters" data-inv-filters>
            <span class="dlv-search">
              [@invIcon "search" /]
              <input type="search" class="dlv-input inv-search" data-inv-search autocomplete="off"
                placeholder="[@s.text name="project.innovationList.redesign.search.placeholder" /]"
                aria-label="[@s.text name="project.innovationList.redesign.search.label" /]" />
            </span>
            <span class="dlv-filters__end">
              <button type="button" class="dlv-link dlv-clearFilters" data-inv-clear data-inv-clear-filters hidden>
                [@invIcon "close" /][@s.text name="project.deliverableList.redesign.empty.clear" /]
              </button>
              <span class="dlv-select">
                <select data-inv-filter="rl" aria-label="[@s.text name="project.innovationList.redesign.readiness.label" /]">
                  <option value="">[@s.text name="project.innovationList.redesign.readiness.all" /]</option>
                </select>
                [@invIcon "caret" /]
              </span>
              <span class="dlv-select inv-select--type">
                <select data-inv-filter="type" aria-label="[@s.text name="project.innovationList.redesign.type.label" /]">
                  <option value="">[@s.text name="project.innovationList.redesign.type.all" /]</option>
                </select>
                [@invIcon "caret" /]
              </span>
              <span class="dlv-select">
                <select data-inv-filter="year" aria-label="[@s.text name="project.innovationList.redesign.year.label" /]">
                  <option value="">[@s.text name="project.deliverableList.redesign.year.all" /]</option>
                </select>
                [@invIcon "caret" /]
              </span>
            </span>
          </div>

          <div class="dlv-grid" role="table" aria-label="[@s.text name="project.innovationList.title" /]" data-inv-grid>
            <div role="rowgroup">
              <div class="dlv-row__main dlv-row__main--head" role="row">
                [@invHead "id" "project.deliverableList.redesign.col.id" /]
                [@invHead "title" "project.innovationList.redesign.col.innovation" /]
                [@invHead "" "project.innovationList.redesign.col.type" /]
                [@invHead "rl" "project.innovationList.redesign.col.readiness" /]
                [@invHead "year" "project.deliverableList.redesign.col.year" /]
                [#if feedbackActive][@invHead "" "project.deliverableList.redesign.col.feedback" /][/#if]
                [@invHead "" "project.deliverableList.redesign.col.status" /]
                <span class="dlv-th inv-th--actions" role="columnheader">[@s.text name="project.deliverableList.redesign.col.actions" /]</span>
              </div>
            </div>
            <div role="rowgroup" data-inv-body>
              [#list currentInnovations as innovation]
                [@invRow innovation=innovation set="current" /]
              [/#list]
              [#list previousInnovations as innovation]
                [@invRow innovation=innovation set="previous" /]
              [/#list]
            </div>
          </div>

          [#-- Filters left nothing --]
          <div class="dlv-empty" data-inv-empty hidden>
            <span class="dlv-empty__title">[@s.text name="project.innovationList.redesign.empty.title" /]</span>
            <span class="dlv-empty__text">[@s.text name="project.innovationList.redesign.empty.text" /]</span>
            <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm dlv-empty__clear" data-inv-clear>[@s.text name="project.deliverableList.redesign.empty.clear" /]</button>
          </div>

          [#-- The active set has no innovation at all --]
          [#list ["current", "previous"] as set]
            <div class="inv-none" data-inv-none="${set}" hidden>
              <span class="inv-none__icon" aria-hidden="true">[@invIcon "bulb" /]</span>
              <span class="inv-none__title">[@s.text name="project.innovationList.redesign.none.title" /]</span>
              <span class="inv-none__text">
                [#if set == "previous"]
                  [@s.text name="project.innovationList.redesign.none.previous" /]
                [#elseif canEdit]
                  [@s.text name="project.innovationList.redesign.none.current"][@s.param]${sectionNoun}[/@s.param][@s.param]${phaseLabel}[/@s.param][/@s.text]
                [#else]
                  [@s.text name="project.innovationList.redesign.none.currentReadOnly" /]
                [/#if]
              </span>
              [#if set == "current" && canEdit]
                <a class="dlv-btn dlv-btn--primary dlv-btn--sm inv-none__add" href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewInnovation'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                  [@invIcon "plus" /][@s.text name="project.innovationList.redesign.add" /]
                </a>
              [/#if]
            </div>
          [/#list]

          [#-- Footer: count, page size and pager --]
          <div class="dlv-foot" data-inv-foot>
            <span class="dlv-foot__showing" data-inv-showing aria-live="polite"></span>
            <span class="dlv-foot__size">
              <label for="inv-page-size">[@s.text name="project.deliverableList.redesign.rowsPerPage" /]</label>
              <span class="dlv-select dlv-select--sm">
                <select id="inv-page-size" data-inv-size>
                  <option value="10">10</option>
                  <option value="25" selected>25</option>
                  <option value="50">50</option>
                </select>
                [@invIcon "caret" /]
              </span>
            </span>
            <span class="dlv-pager" data-inv-pager
              data-label-prev="[@s.text name="project.deliverableList.redesign.page.prev" /]"
              data-label-next="[@s.text name="project.deliverableList.redesign.page.next" /]"></span>
          </div>
        </div>

      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- -- MACROS -- --]

[#-- Sortable (key) or plain column header --]
[#macro invHead key labelKey]
  [#if key?has_content]
    <span class="dlv-th" role="columnheader" aria-sort="${(key == "id")?then("descending", "none")}" data-inv-sort-col="${key}">
      <button type="button" class="dlv-th__btn" data-inv-sort="${key}">[@s.text name=labelKey /][@invIcon "arrow" /]</button>
    </span>
  [#else]
    <span class="dlv-th" role="columnheader">[@s.text name=labelKey /]</span>
  [/#if]
[/#macro]

[#-- One innovation. Both sets render into the same body; the script shows the active one --]
[#macro invRow innovation set]
  [#local info = (innovation.projectInnovationInfo)! /]
  [#local id = innovation.id?c /]
  [#local isCurrent = set == "current" /]
  [#local isNew = isCurrent && ((action.isInnovationNew(innovation.id))!false) /]
  [#local isOwner = (innovation.project.id == projectID)!false /]
  [#local isComplete = isCurrent && ((action.hasInnovationMissingFields(innovation.id))!false) /]
  [#local invTitle = (info.title)!"" /]
  [#local url][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${id}[/@s.param][@s.param name='projectID']${(innovation.project.id?c)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
  [#local pdfUrl][@s.url namespace="/summaries" action='${(crpSession)!}/projectInnovationSummary'][@s.param name='innovationID']${id}[/@s.param][@s.param name='phaseID']${(info.phase.id)!''}[/@s.param][/@s.url][/#local]

  [#local type = (info.repIndInnovationType.name)!"" /]
  [#local nature = (info.repIndInnovationNature.name)!"" /]
  [#local year = (info.year?c)!"" /]
  [#local feedback = (innovation.commentStatus)!"0/0" /]

  [#-- Readiness: the 1-10 scale reads as level 0-9; older innovations only carry a named level --]
  [#local rl = -1 /]
  [#if (info.readinessScale)?has_content && info.readinessScale gte 1 && info.readinessScale lte 10]
    [#local rl = info.readinessScale - 1 /]
  [/#if]
  [#local rlName = (rl gte 0)?then(readinessNames[rl], (info.readinessLevel.name)!"") /]

  [#if isOwner]
    [#local owner][@s.text name="project.deliverableList.redesign.thisSection"][@s.param]${sectionNoun?cap_first}[/@s.param][/@s.text][/#local]
  [#else]
    [#local owner = (innovation.owner)!"" /]
    [#if !owner?has_content][#local owner = (innovation.project.acronym)!action.getText("project.deliverableList.redesign.notDefined") /][/#if]
  [/#if]

  [#local canDelete = isCurrent && canEdit && isOwner /]
  [#local deleteLocked = isCurrent && canEdit && !isOwner /]

  <div class="dlv-row" data-inv-row data-set="${set}" data-id="${id}" data-title="${invTitle?lower_case}" data-rl="${rl?c}"
    data-rl-label="[#if rl gte 0]${rl?c} · ${rlName}[/#if]" data-type="${type}" data-year="${year}" data-ok="${isComplete?then("1", "0")}"
    data-search="I${id} ${invTitle} ${type} ${nature} ${owner}"[#if !isCurrent] hidden[/#if]>
    <div class="dlv-row__main" role="row">
      <span class="dlv-td dlv-td--id" role="cell">I${id}</span>
      <span class="dlv-td dlv-td--title" role="cell">
        <a class="dlv-title" href="${url}"[#if invTitle?has_content] title="${invTitle}"[/#if]>
          [#if isNew]<span class="dlv-tag inv-tag--new">[@s.text name="project.deliverableList.redesign.new" /]</span>[/#if]
          [#if invTitle?has_content]${invTitle}[#else]<span class="dlv-title__none">[@s.text name="projectsList.title.none" /]</span>[/#if]
        </a>
        <span class="dlv-meta">
          [#if nature?has_content]
            <span>${nature}</span>
            <span class="dlv-meta__sep" aria-hidden="true">·</span>
          [/#if]
          [#if isOwner]
            <span>[@s.text name="project.deliverableList.owner" /]: <span class="dlv-meta__strong">${owner}</span></span>
          [#else]
            <span class="inv-shared">[@invIcon "share" /][@s.text name="project.innovationList.redesign.sharedFrom"][@s.param]${owner}[/@s.param][/@s.text]</span>
          [/#if]
        </span>
      </span>
      <span class="dlv-td inv-td--type[#if !type?has_content] is-empty[/#if]" role="cell" data-label="[@s.text name="project.innovationList.redesign.col.type" /]">
        [#if type?has_content]${type}[#else]—[/#if]
      </span>
      <span class="dlv-td inv-td--rl" role="cell" data-label="[@s.text name="project.innovationList.redesign.col.readiness" /]">
        [#if rl gte 0]
          <span class="inv-rl inv-rl--${rl?c}" title="[@s.text name="project.innovationList.redesign.readiness.title"][@s.param]${rl?c}[/@s.param][@s.param]${rlName}[/@s.param][/@s.text]">${rl?c}</span>
          <span class="inv-rl__name">${rlName}</span>
        [#elseif rlName?has_content]
          <span class="inv-rl__name">${rlName}</span>
        [#else]
          <span class="dlv-muted">[@s.text name="project.innovationList.redesign.readiness.none" /]</span>
        [/#if]
      </span>
      <span class="dlv-td inv-td--year" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.year" /]">
        [#if year?has_content]${year}[#else]—[/#if]
      </span>
      [#if feedbackActive]
        <span class="dlv-td dlv-td--feedback[#if feedback == "0/0"] is-empty[/#if]" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.feedback" /]"
          title="[@s.text name="project.deliverableList.redesign.feedback.title"][@s.param]${feedback}[/@s.param][/@s.text]">
          [@invIcon "comment" /]${feedback}
        </span>
      [/#if]
      <span class="dlv-td inv-td--status" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.status" /]">
        [#if !isCurrent]
          <span class="inv-status inv-status--none">—</span>
        [#elseif isComplete]
          <span class="inv-status inv-status--ok"><span class="dlv-dot dlv-dot--c"></span>[@s.text name="project.deliverableList.redesign.fields.complete" /]</span>
        [#else]
          <span class="inv-status inv-status--warn"><span class="dlv-dot dlv-dot--o"></span>[@s.text name="project.deliverableList.redesign.fields.missing" /]</span>
        [/#if]
      </span>
      <span class="dlv-td inv-td--actions" role="cell">
        <a class="dlv-iconBtn" href="${pdfUrl}" target="_blank" rel="noopener"
          title="[@s.text name="project.innovationList.redesign.pdf"][@s.param]${id}[/@s.param][/@s.text]"
          aria-label="[@s.text name="project.innovationList.redesign.pdf"][@s.param]${id}[/@s.param][/@s.text]">[@invIcon "pdf" /]</a>
        [#if canDelete]
          <button type="button" class="dlv-iconBtn dlv-iconBtn--danger" data-inv-remove aria-expanded="false" aria-controls="inv-confirm-${id}"
            title="[@s.text name="project.innovationList.redesign.remove"][@s.param]${id}[/@s.param][/@s.text]"
            aria-label="[@s.text name="project.innovationList.redesign.remove"][@s.param]${id}[/@s.param][/@s.text]">[@invIcon "trash" /]</button>
        [#elseif deleteLocked]
          <span class="dlv-iconBtn dlv-iconBtn--danger is-disabled" tabindex="0" role="img"
            title="[@s.text name="projectInnovations.table.cantDelete" /]"
            aria-label="[@s.text name="projectInnovations.table.cantDelete" /]">[@invIcon "trash" /]</span>
        [/#if]
      </span>
    </div>
    [#if canDelete]
      [#-- Inline confirm: posts deleteInnovation.do with the optional justification --]
      <form class="inv-confirm" id="inv-confirm-${id}" role="alertdialog" aria-labelledby="inv-confirm-title-${id}" data-inv-confirm hidden
        method="post" action="${baseUrl}/projects/${crpSession}/deleteInnovation.do">
        <span class="inv-confirm__head">
          <span class="inv-confirm__title" id="inv-confirm-title-${id}">[@s.text name="project.innovationList.redesign.remove.confirm"][@s.param]${id}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]</span>
          <span class="inv-confirm__note">[@s.text name="project.innovationList.redesign.remove.note"][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
        </span>
        <label class="inv-confirm__field" for="inv-justification-${id}">
          <span class="inv-confirm__label">[@s.text name="project.innovationList.redesign.remove.justification" /] <span class="inv-confirm__optional">[@s.text name="project.innovationList.redesign.remove.optional" /]</span></span>
          <textarea id="inv-justification-${id}" class="inv-confirm__textarea" name="justification" rows="2"
            placeholder="[@s.text name="project.innovationList.redesign.remove.placeholder" /]"></textarea>
        </label>
        <input type="hidden" name="innovationID" value="${id}" />
        <input type="hidden" name="projectID" value="${projectID?c}" />
        <input type="hidden" name="phaseID" value="${(actualPhase.id?c)!}" />
        <span class="inv-confirm__actions">
          <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm" data-inv-keep>[@s.text name="project.innovationList.redesign.remove.keep" /]</button>
          <button type="submit" class="dlv-btn dlv-btn--danger dlv-btn--sm">[@s.text name="project.innovationList.redesign.remove.submit" /]</button>
        </span>
      </form>
    [/#if]
  </div>
[/#macro]

[#macro invIcon name]
  [#switch name]
    [#case "plus"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
    [#case "search"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>[#break]
    [#case "caret"]<svg class="dlv-icon dlv-select__caret" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "arrow"]<svg class="dlv-icon dlv-th__arrow" width="9" height="9" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M6 10V2M2.8 5.2 6 2l3.2 3.2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "close"]<svg class="dlv-icon" width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M3 3l6 6M9 3 3 9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
    [#case "comment"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M2.6 3.4h10.8v7.2H7.2l-3 2.4v-2.4H2.6V3.4Z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"></path></svg>[#break]
    [#case "trash"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "share"]<svg class="dlv-icon" width="10" height="10" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="4" cy="8" r="2" stroke="currentColor" stroke-width="1.6"></circle><circle cx="12" cy="3.6" r="2" stroke="currentColor" stroke-width="1.6"></circle><circle cx="12" cy="12.4" r="2" stroke="currentColor" stroke-width="1.6"></circle><path d="M5.8 7 10.2 4.6M5.8 9l4.4 2.4" stroke="currentColor" stroke-width="1.6"></path></svg>[#break]
    [#case "bulb"]<svg class="dlv-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M10 2.6a5 5 0 0 0-3 9v2.2h6v-2.2a5 5 0 0 0-3-9Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"></path><path d="M7.6 16.4h4.8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>[#break]
    [#case "pdf"]<svg class="dlv-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M4 1.8h5.4L12.6 5v9.2H4V1.8Z" stroke="currentColor" stroke-width="1.3" stroke-linejoin="round"></path><path d="M9.2 1.8V5.2h3.4" stroke="currentColor" stroke-width="1.3" stroke-linejoin="round"></path><rect x="2" y="8" width="8.6" height="4.4" rx="1" fill="#B03028"></rect><path d="M3.4 11.2V9.2h.9a.6.6 0 0 1 0 1.2h-.9M6 11.2V9.2h.6c.6 0 1 .4 1 1s-.4 1-1 1H6ZM8.6 11.2V9.2h1.2M8.6 10.2h1" stroke="#fff" stroke-width=".7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
  [/#switch]
[/#macro]
