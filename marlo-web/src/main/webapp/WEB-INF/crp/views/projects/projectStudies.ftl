[#ftl]
[#assign title = "Cluster Evidence" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#-- The list reuses the deliverables list redesign (A2-2607); oicrList.css only adds what is new here --]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/oicrList.js?20261125-1",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/deliverableList.css?20261019-4",
  "${baseUrlMedia}/css/projects/oicrList.css?20261125-2"
] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "projectStudies" /]
[#assign hideJustification = true /]
[#assign isListSection = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"caseStudies", "nameSpace":"/projects", "action":""}
] /]

[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign feedbackActive = action.hasSpecificities('feedback_active') /]
[#assign currentStudies = (projectStudies)![] /]
[#assign previousStudies = (projectOldStudies)![] /]
[#assign phaseLabel = "${(actualPhase.name)!} ${(actualPhase.year?c)!}"?trim /]
[#assign cycleYear = (actualPhase.year?c)!"" /]
[#-- The cluster chip is drawn in the section header below instead of the shared top quote --]
[#assign hideTopQuote = true /]

[#-- Summary: OICRs owned by this cluster, OICRs other clusters share with it, OICRs in the current set --]
[#assign ownCount = 0 /]
[#list currentStudies + previousStudies as study]
  [#if (study.project.id == projectID)!false][#assign ownCount = ownCount + 1 /][/#if]
[/#list]
[#assign sharedCount = (currentStudies?size + previousStudies?size) - ownCount /]

[#-- OICRs of this cluster from earlier years that are still On-going (2) or Extended (4): they can be reported again --]
[#assign carryStudies = [] /]
[#list previousStudies as study]
  [#assign carryOwner = (study.project.id == projectID)!false /]
  [#assign carryStatus = (study.projectExpectedStudyInfo.status.id?c)!"" /]
  [#if carryOwner && (carryStatus == "2" || carryStatus == "4")][#assign carryStudies = carryStudies + [study] /][/#if]
[/#list]

[#-- Grid tracks: the feedback column is only on the current set, so each set has its own --]
[#assign colsCurrent = "64px minmax(0,1fr) 96px 112px 58px" /]
[#if feedbackActive][#assign colsCurrent = colsCurrent + " 74px" /][/#if]
[#assign colsCurrent = colsCurrent + " 132px 76px" /]
[#assign colsPrevious = "64px minmax(0,1fr) 96px 112px 58px 132px 76px" /]

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

      <div class="dlv ocl" data-ocl data-project-id="${projectID?c}"
        data-removed="[@s.text name="project.oicrList.redesign.removed" /]">

        [#-- Header: title, summary and the cluster chip (the shared top quote, redrawn here) --]
        <div class="dlv-head">
          <div class="dlv-head__main">
            <h1 class="dlv-head__title">[@s.text name="projectStudies.studiesTitle" /]</h1>
            <span class="dlv-head__summary">[@s.text name="project.oicrList.redesign.summary"][@s.param]${ownCount?c}[/@s.param][@s.param]${sectionNoun}[/@s.param][@s.param]${sharedCount?c}[/@s.param][@s.param]${currentStudies?size?c}[/@s.param][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
          </div>
          [#if (project.id)??]
            <a class="dlv-quote" href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
              title="C${project.id?c} - ${(project.projectInfo.title)!}">
              <span class="dlv-quote__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
              [#if (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
            </a>
          [/#if]
        </div>

        [#-- Voluntary note; View more opens the full help text --]
        <div class="dlv-note" role="note">
          <span class="dlv-note__icon" aria-hidden="true">i</span>
          <div class="dlv-note__body">
            <p class="dlv-note__text">[@s.text name="project.oicrList.redesign.note"][@s.param]${sectionNoun}[/@s.param][/@s.text]</p>
            <p class="dlv-note__text ocl-note__more" id="ocl-note-more" hidden>[@s.text name="projectStudies.help" /]</p>
          </div>
          <button type="button" class="dlv-link dlv-note__more" data-ocl-more aria-expanded="false" aria-controls="ocl-note-more"
            data-more="[@s.text name="project.oicrList.redesign.note.more" /]" data-less="[@s.text name="project.oicrList.redesign.note.less" /]">[@s.text name="project.oicrList.redesign.note.more" /]</button>
        </div>

        [#-- OICR table card: set tabs, search and add; status chips and owner; the set's explanation; the rows --]
        <div class="dlv-card dlv-table ocl-table" style="--ocl-cols-current: ${colsCurrent}; --ocl-cols-previous: ${colsPrevious}" data-ocl-table>

          <div class="ocl-toolbar">
            <span class="dlv-seg" role="tablist" aria-label="[@s.text name="project.oicrList.redesign.tabs.label" /]">
              <button type="button" role="tab" class="dlv-seg__btn is-on" aria-selected="true" data-ocl-tab="current">${phaseLabel} <span class="dlv-seg__count">${currentStudies?size?c}</span></button>
              <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-ocl-tab="previous">[@s.text name="project.oicrList.redesign.tabs.previous" /] <span class="dlv-seg__count">${previousStudies?size?c}</span></button>
            </span>
            <span class="dlv-search">
              [@oclIcon "search" /]
              <input type="search" class="dlv-input" data-ocl-search autocomplete="off"
                placeholder="[@s.text name="project.oicrList.redesign.search.placeholder" /]"
                aria-label="[@s.text name="project.oicrList.redesign.search.label" /]" />
            </span>
            [#if canEdit]
              <a class="dlv-btn dlv-btn--primary ocl-add" href="[@s.url action='${crpSession}/addNewStudy'][@s.param name="projectID"]${projectID}[/@s.param][@s.param name="studyTypeID" value="1" /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                [@oclIcon "plus" /][@s.text name="project.oicrList.redesign.add" /]
              </a>
            [/#if]
          </div>

          <div class="dlv-filters ocl-filters" data-ocl-filters>
            <span class="dlv-chips" role="group" aria-label="[@s.text name="project.oicrList.redesign.status.label" /]">
              <button type="button" class="dlv-chip is-on" aria-pressed="true" data-ocl-status="">[@s.text name="project.oicrList.redesign.status.all" /] <span class="dlv-chip__count" data-ocl-count></span></button>
              [#list ["2", "3", "4", "5"] as statusId]
                <button type="button" class="dlv-chip" aria-pressed="false" data-ocl-status="${statusId}"[#if statusId == "4"] data-ocl-optional[/#if]>
                  <span class="dlv-dot ocl-dot--${statusId}" aria-hidden="true"></span>[@s.text name="project.oicrList.redesign.status.${statusId}" /] <span class="dlv-chip__count" data-ocl-count></span>
                </button>
              [/#list]
            </span>
            <span class="dlv-filters__end">
              <span class="dlv-select">
                <select data-ocl-owner aria-label="[@s.text name="project.oicrList.redesign.owner.label" /]">
                  <option value="">[@s.text name="project.oicrList.redesign.owner.all" /]</option>
                  <option value="own">[@s.text name="project.oicrList.redesign.owner.own"][@s.param]${sectionNoun}[/@s.param][/@s.text]</option>
                  <option value="shared">[@s.text name="project.oicrList.redesign.owner.shared"][@s.param]${sectionNoun}[/@s.param][/@s.text]</option>
                </select>
                [@oclIcon "caret" /]
              </span>
            </span>
          </div>

          [#-- What each set holds --]
          <div class="dlv-focus" data-ocl-focus="current">
            <span>[@s.text name="project.oicrList.redesign.focus.current"][@s.param]${cycleYear}[/@s.param][/@s.text]</span>
            [#if reportingActive]
              <span>[@s.text name="project.oicrList.redesign.focus.report" /]</span>
              <span class="dlv-report">[@oclIcon "flash" /][@s.text name="project.deliverableList.redesign.report" /]</span>
            [/#if]
          </div>
          <div class="dlv-focus" data-ocl-focus="previous" hidden>
            <span>[@s.text name="project.oicrList.redesign.focus.previous"][@s.param]${cycleYear}[/@s.param][/@s.text]</span>
          </div>

          <div class="dlv-grid" role="table" aria-label="[@s.text name="projectStudies.studiesTitle" /]" data-ocl-grid>
            <div role="rowgroup">
              <div class="dlv-row__main dlv-row__main--head" role="row">
                <span class="dlv-th" role="columnheader">[@s.text name="project.deliverableList.redesign.col.id" /]</span>
                <span class="dlv-th" role="columnheader">[@s.text name="project.oicrList.redesign.col.oicr" /]</span>
                <span class="dlv-th" role="columnheader">[@s.text name="project.oicrList.redesign.col.maturity" /]</span>
                <span class="dlv-th" role="columnheader">[@s.text name="project.oicrList.redesign.col.owner" /]</span>
                <span class="dlv-th" role="columnheader">[@s.text name="project.deliverableList.redesign.col.year" /]</span>
                [#if feedbackActive]<span class="dlv-th ocl-fb" role="columnheader">[@s.text name="project.deliverableList.redesign.col.feedback" /]</span>[/#if]
                <span class="dlv-th" role="columnheader">[@s.text name="project.deliverableList.redesign.col.status" /]</span>
                <span class="dlv-th ocl-th--actions" role="columnheader"><span class="sr-only">[@s.text name="project.deliverableList.redesign.col.actions" /]</span></span>
              </div>
            </div>
            <div role="rowgroup" data-ocl-body>
              [#list currentStudies as study]
                [@oclRow study=study set="current" /]
              [/#list]
              [#list previousStudies as study]
                [@oclRow study=study set="previous" /]
              [/#list]
            </div>
          </div>

          [#-- Filters left nothing --]
          <div class="dlv-empty" data-ocl-empty hidden>
            <span class="dlv-empty__title">[@s.text name="project.oicrList.redesign.empty.title" /]</span>
            <span class="dlv-empty__text">[@s.text name="project.oicrList.redesign.empty.text" /]</span>
            <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm dlv-empty__clear" data-ocl-clear>[@s.text name="project.deliverableList.redesign.empty.clear" /]</button>
          </div>

          [#-- The active set has no OICR at all --]
          <div class="ocl-none" data-ocl-none="current" hidden>
            <span class="ocl-none__title">[@s.text name="project.oicrList.redesign.none.current"][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
            <span class="ocl-none__text">
              [@s.text name="project.oicrList.redesign.none.currentText"][@s.param]${cycleYear}[/@s.param][/@s.text]
              [#if canEdit] [@s.text name="project.oicrList.redesign.none.currentEdit"][@s.param]${cycleYear}[/@s.param][/@s.text][/#if]
            </span>
          </div>
          <div class="ocl-none" data-ocl-none="previous" hidden>
            <span class="ocl-none__title">[@s.text name="project.oicrList.redesign.none.previous" /]</span>
            <span class="ocl-none__text">[@s.text name="project.oicrList.redesign.none.previousText" /]</span>
          </div>
        </div>

        [#-- Still On-going from earlier years: shown with the current set, for editors. No "Continue" yet (pending with
             the PM); each row opens the OICR, where the current report year is changed as before --]
        [#if canEdit && carryStudies?has_content]
          <div class="dlv-card ocl-carry" data-ocl-carry>
            <div class="ocl-carry__head">
              <span class="ocl-carry__title">
                [#if carryStudies?size == 1]
                  [@s.text name="project.oicrList.redesign.carry.titleOne"][@s.param]${sectionNoun}[/@s.param][/@s.text]
                [#else]
                  [@s.text name="project.oicrList.redesign.carry.title"][@s.param]${carryStudies?size?c}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]
                [/#if]
              </span>
              <span class="ocl-carry__note">[@s.text name="project.oicrList.redesign.carry.note"][@s.param]${cycleYear}[/@s.param][/@s.text]</span>
            </div>
            <div data-ocl-carry-body>
              [#list carryStudies as study]
                [#assign carryInfo = (study.projectExpectedStudyInfo)! /]
                [#assign carryId = study.id?c /]
                [#assign carryStatusId = (carryInfo.status.id?c)!"" /]
                [#assign carryYear = (carryInfo.year?c)!"" /]
                [#assign carryTitle = (carryInfo.title)!"" /]
                [#assign carryMaturity = (carryInfo.repIndStageStudy.name)!action.getText("project.oicrList.redesign.maturity.none") /]
                [#assign carryUrl][@s.url namespace=namespace action='${crpSession}/study'][@s.param name='expectedID']${carryId}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
                <div class="ocl-carry__row" data-ocl-carry-row data-id="${carryId}" data-year="${carryYear}">
                  <span class="ocl-carry__id">${carryId}</span>
                  <span class="ocl-carry__main">
                    <a class="ocl-carry__name" href="${carryUrl}">[#if carryTitle?trim?has_content]${carryTitle}[#else][@s.text name="projectsList.title.none" /][/#if]</a>
                    <span class="ocl-carry__meta">
                      <span class="dlv-dot ocl-dot--${carryStatusId}" aria-hidden="true"></span>
                      [@s.text name="project.oicrList.redesign.carry.meta"][@s.param][@s.text name="project.oicrList.redesign.status.${carryStatusId}" /][/@s.param][@s.param]${carryYear}[/@s.param][@s.param]${carryMaturity}[/@s.param][/@s.text]
                    </span>
                  </span>
                  <a class="dlv-btn dlv-btn--ghost dlv-btn--sm ocl-carry__open" href="${carryUrl}"
                    aria-label="[@s.text name="project.oicrList.redesign.carry.openLabel"][@s.param]${carryId}[/@s.param][/@s.text]">[@s.text name="project.oicrList.redesign.carry.open" /]</a>
                </div>
              [/#list]
            </div>
          </div>
        [/#if]

        <div class="ocl-toast" role="status" aria-live="polite" data-ocl-toast hidden></div>
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- -- MACROS -- --]

[#-- One OICR. Both sets render into the same body; the script shows the active one --]
[#macro oclRow study set]
  [#local info = (study.projectExpectedStudyInfo)! /]
  [#local id = study.id?c /]
  [#local isCurrent = set == "current" /]
  [#local isOwner = (study.project.id == projectID)!false /]
  [#local year = (info.year?c)!"" /]
  [#local oldFormat = ((info.year)!2017) lt 2017 /]
  [#local fullTitle = (info.title)!"" /]
  [#local statusId = (info.status.id?c)!"" /]
  [#local allianceId = (info.allianceOicr)!"" /]
  [#local feedback = (study.commentStatus)!"0/0" /]

  [#-- Missing fields are checked only where the list shows them: current OICRs this cluster owns --]
  [#local isComplete = true /]
  [#if isCurrent && isOwner][#local isComplete = (action.hasStudiesMissingFields(study.class.name, study.id))!false /][/#if]
  [#local showReport = reportingActive && isCurrent && isOwner && !isComplete && (year == ((actualPhase.year?c)!"")) /]

  [#-- Title cut at 120 characters, as before the redesign; the full title is the tooltip --]
  [#local shortTitle][#if fullTitle?has_content][@utils.wordCutter string=fullTitle maxPos=120 /][/#if][/#local]

  [#-- Owner: this cluster, or the owner's acronym --]
  [#if isOwner]
    [#local owner][@s.text name="project.deliverableList.redesign.thisSection"][@s.param]${sectionNoun}[/@s.param][/@s.text][/#local]
  [#elseif (study.project.acronym)?has_content]
    [#local owner = study.project.acronym /]
  [#elseif (study.project.id)??]
    [#local owner = "C" + study.project.id?c /]
  [#else]
    [#local owner = action.getText("project.deliverableList.redesign.notDefined") /]
  [/#if]

  [#-- Clusters an owned OICR is shared with, in this phase --]
  [#local shares = [] /]
  [#if isOwner]
    [#-- Each default on its own line: "(x)!false && y" would read as "(x)!(false && y)" and drop y --]
    [#list (study.expectedStudyProjects)![] as share]
      [#local shareActive = (share.active)!false /]
      [#local sharePhaseId = (share.phase.id)!0 /]
      [#local shareProjectId = (share.project.id)!0 /]
      [#if shareActive && sharePhaseId == actualPhase.id && shareProjectId != 0 && shareProjectId != projectID]
        [#local shareName = (share.project.acronym)!"" /]
        [#if !shareName?has_content][#local shareName = "C" + shareProjectId?c /][/#if]
        [#if !shares?seq_contains(shareName)][#local shares = shares + [shareName] /][/#if]
      [/#if]
    [/#list]
  [/#if]

  [#-- Maturity: Level 1-3 with its definition as tooltip --]
  [#local maturityId = (info.repIndStageStudy.id?c)!"" /]
  [#local maturity = (info.repIndStageStudy.name)!"" /]
  [#local maturityTitle = (info.repIndStageStudy.description)!"" /]

  [#local statusLabel = "" /]
  [#if ["2", "3", "4", "5"]?seq_contains(statusId)]
    [#local statusLabel = action.getText("project.oicrList.redesign.status." + statusId) /]
  [#else]
    [#local statusLabel = (info.statusName)!"" /]
  [/#if]

  [#local canDelete = isCurrent && canEdit && isOwner /]
  [#local deleteLocked = canEdit && !canDelete /]

  [#local url][@s.url namespace=namespace action='${crpSession}/study'][@s.param name='expectedID']${id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
  [#local pdfUrl][@s.url namespace="/projects" action='${(crpSession)!}/studySummary'][@s.param name='studyID']${id}[/@s.param][@s.param name='cycle']Reporting[/@s.param][@s.param name='year']${(actualPhase.year)!}[/@s.param][/@s.url][/#local]

  <div class="dlv-row" data-ocl-row data-set="${set}" data-id="${id}" data-status="${statusId}" data-own="${isOwner?then("1", "0")}"
    data-search="${id} ${fullTitle} ${allianceId} ${owner}"[#if !isCurrent] hidden[/#if]>
    <div class="dlv-row__main" role="row">
      <span class="dlv-td dlv-td--id" role="cell">
        [#if oldFormat]${id}[#else]<a class="ocl-id" href="${url}">${id}</a>[/#if]
      </span>
      <span class="dlv-td dlv-td--title" role="cell">
        [#if oldFormat]
          <span class="dlv-title">${shortTitle}</span>
        [#else]
          <a class="dlv-title" href="${url}">
            [#if showReport]<span class="dlv-report" title="[@s.text name="project.deliverableList.redesign.report.title" /]">[@oclIcon "flash" /][@s.text name="project.deliverableList.redesign.report" /]</span>[/#if]
            [#if fullTitle?trim?has_content]${shortTitle}[#else]<span class="dlv-title__none">[@s.text name="projectsList.title.none" /]</span>[/#if]
          </a>
        [/#if]
        [#if allianceId?has_content || !isOwner || shares?has_content]
          <span class="dlv-meta">
            [#if allianceId?has_content]<span class="ocl-aid" title="[@s.text name="project.oicrList.redesign.alliance" /]">${allianceId}</span>[/#if]
            [#if !isOwner]
              <span class="ocl-shared">[@oclIcon "link" /][@s.text name="project.oicrList.redesign.sharedBy"][@s.param]${owner}[/@s.param][/@s.text]</span>
            [#elseif shares?has_content]
              <span>[@s.text name="project.oicrList.redesign.sharedWith"][@s.param]${shares?join(", ")}[/@s.param][/@s.text]</span>
            [/#if]
          </span>
        [/#if]
      </span>
      <span class="dlv-td ocl-td--maturity" role="cell" data-label="[@s.text name="project.oicrList.redesign.col.maturity" /]">
        [#if maturity?has_content]
          <span class="ocl-maturity ocl-maturity--${maturityId}"[#if maturityTitle?has_content] title="${maturityTitle}"[/#if]>${maturity}</span>
        [#else]
          <span class="ocl-maturity">[@s.text name="project.oicrList.redesign.maturity.none" /]</span>
        [/#if]
      </span>
      <span class="dlv-td ocl-td--owner[#if !isOwner] is-shared[/#if]" role="cell" data-label="[@s.text name="project.oicrList.redesign.col.owner" /]">${owner}</span>
      <span class="dlv-td ocl-td--year" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.year" /]">[#if year?has_content]${year}[#else]—[/#if]</span>
      [#if feedbackActive]
        <span class="dlv-td dlv-td--feedback ocl-fb[#if feedback == "0/0"] is-empty[/#if]" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.feedback" /]"
          title="[@s.text name="project.deliverableList.redesign.feedback.title"][@s.param]${feedback}[/@s.param][/@s.text]">
          [@oclIcon "comment" /]${feedback}
        </span>
      [/#if]
      <span class="dlv-td dlv-td--status" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.status" /]">
        [#if statusLabel?has_content]
          <span class="dlv-status"><span class="dlv-dot ocl-dot--${statusId}" aria-hidden="true"></span>${statusLabel}</span>
        [#else]
          <span class="dlv-muted">—</span>
        [/#if]
        [#if isCurrent && isOwner && !isComplete]
          <span class="dlv-fields dlv-fields--warn">[@s.text name="project.oicrList.redesign.fields.missing" /]</span>
        [/#if]
      </span>
      <span class="dlv-td ocl-td--actions" role="cell">
        [#if (info.isPublic)!true]
          <a class="dlv-iconBtn" href="${pdfUrl}" target="_blank" rel="noopener"
            title="[@s.text name="project.oicrList.redesign.pdf"][@s.param]${id}[/@s.param][/@s.text]"
            aria-label="[@s.text name="project.oicrList.redesign.pdf"][@s.param]${id}[/@s.param][/@s.text]">[@oclIcon "pdf" /]</a>
        [/#if]
        [#if canDelete]
          <button type="button" class="dlv-iconBtn dlv-iconBtn--danger" data-ocl-remove aria-expanded="false" aria-controls="ocl-confirm-${id}"
            title="[@s.text name="project.oicrList.redesign.remove"][@s.param]${id}[/@s.param][/@s.text]"
            aria-label="[@s.text name="project.oicrList.redesign.remove"][@s.param]${id}[/@s.param][/@s.text]">[@oclIcon "trash" /]</button>
        [#elseif deleteLocked]
          [#local lockReason][#if !isOwner][@s.text name="project.oicrList.redesign.lock.owner"][@s.param]${owner}[/@s.param][/@s.text][#else][@s.text name="project.oicrList.redesign.lock.previous" /][/#if][/#local]
          <span class="dlv-iconBtn ocl-lock" tabindex="0" role="img" title="${lockReason}" aria-label="${lockReason}">[@oclIcon "lock" /]</span>
        [/#if]
      </span>
    </div>
    [#if canDelete]
      [#-- Inline confirm: posts deleteStudy.do (relative, so the cluster namespace is kept) with the optional justification --]
      <form class="ocl-confirm" id="ocl-confirm-${id}" role="alertdialog" aria-labelledby="ocl-confirm-title-${id}" data-ocl-confirm hidden
        method="post" action="deleteStudy.do">
        <span class="ocl-confirm__title" id="ocl-confirm-title-${id}">
          [@s.text name="project.oicrList.redesign.remove.confirm"][@s.param]${id}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]
          [#if shares?has_content][@s.text name="project.oicrList.redesign.remove.shared"][@s.param]${shares?join(", ")}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text][/#if]
          [@s.text name="project.oicrList.redesign.remove.final" /]
        </span>
        <label class="ocl-confirm__field" for="ocl-justification-${id}">
          <span class="ocl-confirm__label">[@s.text name="project.oicrList.redesign.remove.justification" /] <span class="ocl-confirm__optional">[@s.text name="project.oicrList.redesign.remove.optional" /]</span></span>
          <textarea id="ocl-justification-${id}" class="ocl-confirm__textarea" name="justification" rows="2"
            placeholder="[@s.text name="project.oicrList.redesign.remove.placeholder" /]"></textarea>
        </label>
        <input type="hidden" name="expectedID" value="${id}" />
        <input type="hidden" name="projectID" value="${projectID?c}" />
        <input type="hidden" name="phaseID" value="${(actualPhase.id?c)!}" />
        <span class="ocl-confirm__actions">
          <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm" data-ocl-keep>[@s.text name="project.oicrList.redesign.remove.keep" /]</button>
          <button type="submit" class="dlv-btn dlv-btn--danger dlv-btn--sm">[@s.text name="project.oicrList.redesign.remove.submit" /]</button>
        </span>
      </form>
    [/#if]
  </div>
[/#macro]

[#macro oclIcon name]
  [#switch name]
    [#case "plus"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
    [#case "search"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>[#break]
    [#case "caret"]<svg class="dlv-icon dlv-select__caret" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "flash"]<svg class="dlv-icon" width="8" height="10" viewBox="0 0 10 12" fill="none" aria-hidden="true"><path d="M6 1 1.6 6.8h3.2L4 11l4.4-5.8H5.2L6 1Z" fill="currentColor"></path></svg>[#break]
    [#case "comment"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M2.6 3.4h10.8v7.2H7.2l-3 2.4v-2.4H2.6V3.4Z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"></path></svg>[#break]
    [#case "trash"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "lock"]<svg class="dlv-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>[#break]
    [#case "link"]<svg class="dlv-icon" width="10" height="10" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M6.5 9.5 9.5 6.5M7 4.6l1.4-1.4a2.6 2.6 0 0 1 3.7 3.7L10.7 8.3M9 11.4l-1.4 1.4a2.6 2.6 0 0 1-3.7-3.7l1.4-1.4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>[#break]
    [#case "pdf"]<svg class="dlv-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M4 1.8h5.4L12.6 5v9.2H4V1.8Z" stroke="currentColor" stroke-width="1.3" stroke-linejoin="round"></path><path d="M9.2 1.8V5.2h3.4" stroke="currentColor" stroke-width="1.3" stroke-linejoin="round"></path><rect x="2" y="8" width="8.6" height="4.4" rx="1" fill="#B03028"></rect><path d="M3.4 11.2V9.2h.9a.6.6 0 0 1 0 1.2h-.9M6 11.2V9.2h.6c.6 0 1 .4 1 1s-.4 1-1 1H6ZM8.6 11.2V9.2h1.2M8.6 10.2h1" stroke="#fff" stroke-width=".7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
  [/#switch]
[/#macro]
