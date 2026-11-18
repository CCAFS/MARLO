[#ftl]
[#assign title = "Cluster Deliverables" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/deliverables/deliverableList.js?20261019-4",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/deliverableList.css?20261019-4"] /]

[#assign currentStage = "deliverableList" /]
[#assign isListSection = true /]

[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"deliverableList", "nameSpace":"${currentSection}", "action":""}
  ]/]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"deliverableList", "nameSpace":"${currentSection}", "action":""}
  ]/]
[/#if]

[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign fairLegendActive = ((reportingActive || actualPhase.upkeep) && action.hasSpecificities("crp_has_disemination"))!false /]
[#assign feedbackActive = action.hasSpecificities('feedback_active') /]
[#assign duplicatedActive = action.hasSpecificities('duplicated_deliverables_functionality_active') /]
[#assign canAddDeliverable = canEdit && (action.hasPermissionDeliverable("addDeliverable", projectID, "deliverableList") || action.hasPermission("addDeliverable")) /]
[#assign currentDeliverables = (currentDeliverableList)![] /]
[#-- The action fills the responsible person on both previous lists --]
[#assign previousDeliverables = ((previousDeliverableList)![]) + ((previousSharedDeliverableList)![]) /]
[#assign phaseLabel = "${(actualPhase.name)!} ${(actualPhase.year?c)!}"?trim /]
[#assign templateUrl = "${baseUrlCdn}/global/documents/AICCRA_dissemination_templates_2025.xlsx" /]
[#assign supportEmail = "MARLOSupport@cgiar.org" /]
[#-- The cluster chip is drawn in the section header below instead of the shared top quote --]
[#assign hideTopQuote = true /]

[#-- DeliverableInfo.getStatusName() returns these labels; the list filters on the short keys --]
[#assign statusKeys = {"Complete":"c", "On-going":"o", "Extended":"e", "Ready to be reported on":"r", "Cancelled":"x"} /]
[#assign statusLabels = {
  "c": action.getText("project.deliverableList.deliverableStatus.complete"),
  "o": action.getText("project.deliverableList.deliverableStatus.onGoing"),
  "e": action.getText("project.deliverableList.deliverableStatus.extended"),
  "r": action.getText("project.deliverableList.deliverableStatus.readyToReport"),
  "x": action.getText("project.deliverableList.deliverableStatus.cancelled"),
  "": action.getText("project.deliverableList.redesign.status.none")
} /]
[#assign fairNames = {
  "F": action.getText("project.deliverableList.fairExplanation.findable"),
  "A": action.getText("project.deliverableList.fairExplanation.accessible"),
  "I": action.getText("project.deliverableList.fairExplanation.interoperable"),
  "R": action.getText("project.deliverableList.fairExplanation.reusable")?cap_first
} /]
[#assign fairStates = {
  "a": action.getText("project.deliverableList.fairExplanation.achieved"),
  "n": action.getText("project.deliverableList.fairExplanation.notAchieved"),
  "d": action.getText("project.deliverableList.fairExplanation.notDefined")
} /]

[#-- Grid tracks follow the columns this global unit shows --]
[#assign gridColumns = "72px minmax(0,1fr) 88px 140px" /]
[#if reportingActive][#assign gridColumns = gridColumns + " 104px" /][/#if]
[#if feedbackActive][#assign gridColumns = gridColumns + " 76px" /][/#if]
[#assign gridColumns = gridColumns + " 140px 40px" /]

[#assign toReportCount = 0 /]
[#if reportingActive]
  [#list currentDeliverables as deliverable]
    [#if (deliverable.deliverableInfo.isRequiredToBeReported())!false][#assign toReportCount = toReportCount + 1 /][/#if]
  [/#list]
[/#if]

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

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        <div class="dlv" data-dlv data-project-id="${projectID?c}"
          data-showing="[@s.text name="project.deliverableList.redesign.showing" /]"
          data-no-entries="[@s.text name="project.deliverableList.redesign.noEntries" /]">

          [#-- Header: title, summary and the cluster chip (the shared top quote, redrawn here) --]
          <div class="dlv-head">
            <div class="dlv-head__main">
              <h1 class="dlv-head__title">[@s.text name="project.deliverableList.title" /]</h1>
              <span class="dlv-head__summary">
                [#if reportingActive]
                  [@s.text name="project.deliverableList.redesign.summary"][@s.param]${currentDeliverables?size?c}[/@s.param][@s.param]${phaseLabel}[/@s.param][@s.param]${toReportCount?c}[/@s.param][@s.param]${previousDeliverables?size?c}[/@s.param][/@s.text]
                [#else]
                  [@s.text name="project.deliverableList.redesign.summaryNoReport"][@s.param]${currentDeliverables?size?c}[/@s.param][@s.param]${phaseLabel}[/@s.param][@s.param]${previousDeliverables?size?c}[/@s.param][/@s.text]
                [/#if]
              </span>
            </div>
            [#if (project.id)??]
              <a class="dlv-quote" href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
                title="[#if action.isAiccra()]C${project.id?c} - [/#if]${(project.projectInfo.title)!}">
                <span class="dlv-quote__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
                [#if !action.isAiccra()]P${project.id?c}[#elseif (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
              </a>
            [/#if]
          </div>

          [#-- Instructions, plus the FAIR explanation and legends behind the toggle --]
          <div class="dlv-note" role="note">
            <span class="dlv-note__icon" aria-hidden="true">i</span>
            <div class="dlv-note__body">
              <p class="dlv-note__text">[#if reportingActive][@s.text name="project.deliverableList.help2" /][#else][@s.text name="project.deliverableList.help1" /][/#if]</p>
              <div class="dlv-legend" id="dlv-legend" hidden>
                [#if fairLegendActive]
                  <p class="dlv-legend__fair">[@s.text name="project.deliverableList.fairExplanation" /]</p>
                [/#if]
                <div class="dlv-legend__grid">
                  [#if fairLegendActive]
                    <div class="dlv-legend__col">
                      <span class="dlv-legend__title">[@s.text name="project.deliverableList.fairExplanation.fair" /]</span>
                      [#list ["F", "A", "I", "R"] as letter]
                        <span class="dlv-legend__item"><span class="dlv-fair dlv-fair--key">${letter}</span>${fairNames[letter]}</span>
                      [/#list]
                    </div>
                    <div class="dlv-legend__col">
                      <span class="dlv-legend__title">[@s.text name="project.deliverableList.fairExplanation.fairColors" /]</span>
                      [#list ["a", "n", "d"] as state]
                        <span class="dlv-legend__item"><span class="dlv-fair dlv-fair--${state}">F</span>${fairStates[state]}</span>
                      [/#list]
                    </div>
                  [/#if]
                  <div class="dlv-legend__col">
                    <span class="dlv-legend__title">[@s.text name="project.deliverableList.deliverableStatus" /]</span>
                    [#list ["c", "x", "o", "e", "r"] as state]
                      <span class="dlv-legend__item"><span class="dlv-dot dlv-dot--${state}"></span>${statusLabels[state]}</span>
                    [/#list]
                  </div>
                  <div class="dlv-legend__col">
                    <span class="dlv-legend__title">[@s.text name="project.deliverableList.requiredStatus" /]</span>
                    <span class="dlv-legend__item dlv-legend__item--ok">[@dlvIcon "ok" /][@s.text name="project.deliverableList.requiredStatus.complete" /]</span>
                    <span class="dlv-legend__item dlv-legend__item--warn">[@dlvIcon "warn" /][@s.text name="project.deliverableList.requiredStatus.incomplete" /]</span>
                    [#if duplicatedActive]
                      <span class="dlv-legend__item dlv-legend__item--dup">[@dlvIcon "dup" /][@s.text name="project.deliverable.duplicated.legend" /]</span>
                    [/#if]
                  </div>
                </div>
              </div>
            </div>
            <button type="button" class="dlv-link dlv-note__more" aria-expanded="false" aria-controls="dlv-legend" data-dlv-legend-toggle
              data-label-more="[#if fairLegendActive][@s.text name="project.deliverableList.redesign.legend.more" /][#else][@s.text name="project.deliverableList.redesign.legend.moreNoFair" /][/#if]"
              data-label-less="[@s.text name="project.deliverableList.redesign.legend.less" /]">[#if fairLegendActive][@s.text name="project.deliverableList.redesign.legend.more" /][#else][@s.text name="project.deliverableList.redesign.legend.moreNoFair" /][/#if]</button>
          </div>
          [#if fairLegendActive]
            <div id="diagramPopup" style="display:none; text-align:center;" title="[@s.text name="project.deliverableList.redesign.fairDiagram" /]">
              <img src="${baseUrlCdn}/global/images/FAIR_Principles_in_MARLO_20170919.png" alt="" width="100%" />
            </div>
          [/#if]

          [#-- Dissemination template --]
          <div class="dlv-card dlv-template">
            <span class="dlv-template__icon" aria-hidden="true">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><rect x="2.4" y="1.8" width="11.2" height="12.4" rx="1.8" stroke="currentColor" stroke-width="1.4"></rect><path d="M5.2 5.6l2.6 4.6M7.8 5.6 5.2 10.2M9.6 6h1.8M9.6 8h1.8M9.6 10h1.8" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"></path></svg>
            </span>
            [#-- The support address in the message becomes a mail link --]
            [#assign templateMessage = action.getText("deliverable.message") /]
            <span class="dlv-template__text">
              [#if templateMessage?contains(supportEmail)]
                ${templateMessage?keep_before(supportEmail)}<a href="mailto:${supportEmail}">${supportEmail}</a>${templateMessage?keep_after(supportEmail)}
              [#else]
                ${templateMessage}
              [/#if]
            </span>
            <a class="dlv-btn dlv-btn--ghost dlv-template__btn" href="${templateUrl}" download>
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 2.6v7.6M5 7.4l3 3 3-3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path><path d="M3 11.6v1.8h10v-1.8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
              [@s.text name="project.deliverableList.redesign.downloadTemplate" /]
            </a>
          </div>

          [#-- Toolbar: deliverable set, search and add --]
          <div class="dlv-card dlv-toolbar">
            <span class="dlv-seg" role="tablist" aria-label="[@s.text name="project.deliverableList.redesign.tabs.label" /]">
              <button type="button" role="tab" class="dlv-seg__btn is-on" aria-selected="true" data-dlv-tab="current">${phaseLabel} <span class="dlv-seg__count">${currentDeliverables?size?c}</span></button>
              <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-dlv-tab="previous">[@s.text name="project.deliverableList.redesign.tabs.previous" /] <span class="dlv-seg__count">${previousDeliverables?size?c}</span></button>
            </span>
            <span class="dlv-search">
              <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
              <input type="search" class="dlv-input" data-dlv-search autocomplete="off"
                placeholder="[@s.text name="project.deliverableList.redesign.search.placeholder"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
                aria-label="[@s.text name="project.deliverableList.redesign.search.label" /]" />
            </span>
            [#if canAddDeliverable]
              <span class="dlv-toolbar__end">
                <a class="dlv-btn dlv-btn--primary" href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/addNewDeliverable'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                  <svg width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                  [@s.text name="project.deliverableList.redesign.add" /]
                </a>
              </span>
            [/#if]
          </div>

          [#-- Deliverables table --]
          <div class="dlv-card dlv-table" style="--dlv-cols: ${gridColumns}">

            [#-- Filters: status chips (built by the script from the rows), sub-category, year and display --]
            <div class="dlv-filters">
              <span class="dlv-chips" role="group" aria-label="[@s.text name="project.deliverableList.redesign.status.label" /]" data-dlv-chips
                data-label-all="[@s.text name="project.deliverableList.redesign.status.all" /]"
                [#list statusLabels?keys as key]data-label-${key?has_content?then(key, "none")}="${statusLabels[key]}" [/#list]></span>
              <span class="dlv-filters__end">
                <button type="button" class="dlv-link dlv-clearFilters" data-dlv-clear data-dlv-clear-filters hidden>
                  [@dlvIcon "close" /][@s.text name="project.deliverableList.redesign.empty.clear" /]
                </button>
                <span class="dlv-select">
                  <select data-dlv-sub aria-label="[@s.text name="project.deliverableList.redesign.sub.label" /]">
                    <option value="">[@s.text name="project.deliverableList.redesign.sub.all" /]</option>
                  </select>
                  [@dlvIcon "caret" /]
                </span>
                <span class="dlv-select">
                  <select data-dlv-year aria-label="[@s.text name="project.deliverableList.redesign.year.label" /]">
                    <option value="">[@s.text name="project.deliverableList.redesign.year.all" /]</option>
                  </select>
                  [@dlvIcon "caret" /]
                </span>
              </span>
            </div>

            [#if reportingActive]
              <div class="dlv-focus" data-dlv-only="current">
                [@s.text name="project.deliverableList.focusDeliverablesMessage"][@s.param]${currentCycleYear}[/@s.param][@s.param]<span class="dlv-report">[@dlvIcon "flash" /][@s.text name="project.deliverableList.redesign.report" /]</span>[/@s.param][/@s.text]
              </div>
            [/#if]

            <div class="dlv-grid" role="table" aria-label="[@s.text name="project.deliverableList.title" /]">
              <div role="rowgroup">
                <div class="dlv-row__main dlv-row__main--head" role="row">
                  [@dlvHead "id" "project.deliverableList.redesign.col.id" /]
                  [@dlvHead "title" "project.deliverableList.redesign.col.deliverable" /]
                  [@dlvHead "year" "project.deliverableList.redesign.col.year" /]
                  [@dlvHead "" "project.deliverableList.redesign.col.shared" /]
                  [#if reportingActive][@dlvHead "" "project.deliverableList.redesign.col.fair" /][/#if]
                  [#if feedbackActive][@dlvHead "" "project.deliverableList.redesign.col.feedback" /][/#if]
                  [@dlvHead "status" "project.deliverableList.redesign.col.status" /]
                  <span class="dlv-th" role="columnheader"><span class="sr-only">[@s.text name="project.deliverableList.redesign.col.actions" /]</span></span>
                </div>
              </div>
              <div role="rowgroup" data-dlv-body>
                [#list currentDeliverables as deliverable]
                  [@dlvRow deliverable=deliverable set="current" namespace="/clusters" /]
                [/#list]
                [#list previousDeliverables as deliverable]
                  [@dlvRow deliverable=deliverable set="previous" namespace="/projects" /]
                [/#list]
              </div>
            </div>

            <div class="dlv-empty" data-dlv-empty hidden>
              <span class="dlv-empty__title">[@s.text name="project.deliverableList.redesign.empty.title" /]</span>
              <span class="dlv-empty__text">[@s.text name="project.deliverableList.redesign.empty.text" /]</span>
              <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm dlv-empty__clear" data-dlv-clear>[@s.text name="project.deliverableList.redesign.empty.clear" /]</button>
            </div>
            <div class="dlv-empty" data-dlv-none="current" hidden>
              <span class="dlv-empty__text">[@s.text name="project.deliverableList.redesign.none.current"][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
            </div>
            <div class="dlv-empty" data-dlv-none="previous" hidden>
              <span class="dlv-empty__text">[@s.text name="project.deliverableList.redesign.none.previous" /]</span>
            </div>

            [#-- Footer: count, page size and pager --]
            <div class="dlv-foot">
              <span class="dlv-foot__showing" data-dlv-showing aria-live="polite"></span>
              <span class="dlv-foot__size">
                <label for="dlv-page-size">[@s.text name="project.deliverableList.redesign.rowsPerPage" /]</label>
                <span class="dlv-select dlv-select--sm">
                  <select id="dlv-page-size" data-dlv-size>
                    <option value="10">10</option>
                    <option value="25" selected>25</option>
                    <option value="50">50</option>
                  </select>
                  [@dlvIcon "caret" /]
                </span>
              </span>
              <span class="dlv-pager" data-dlv-pager
                data-label-prev="[@s.text name="project.deliverableList.redesign.page.prev" /]"
                data-label-next="[@s.text name="project.deliverableList.redesign.page.next" /]"></span>
            </div>
          </div>

        </div>

        <input type="hidden" name="projectID" value="${projectID}" />
        [/@s.form]
      </div>
    </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- Sortable (key) or plain column header --]
[#macro dlvHead key labelKey]
  [#if key?has_content]
    <span class="dlv-th" role="columnheader" aria-sort="${(key == "year")?then("ascending", "none")}" data-dlv-sort-col="${key}">
      <button type="button" class="dlv-th__btn" data-dlv-sort="${key}">[@s.text name=labelKey /][@dlvIcon "arrow" /]</button>
    </span>
  [#else]
    <span class="dlv-th" role="columnheader">[@s.text name=labelKey /]</span>
  [/#if]
[/#macro]

[#-- One deliverable. Both sets render into the same body; the script shows the active one --]
[#macro dlvRow deliverable set namespace]
  [#local info = deliverable.deliverableInfo /]
  [#local id = deliverable.id?c /]
  [#local isCurrent = set == "current" /]
  [#local isNew = action.isDeliverableNew(deliverable.id) /]
  [#local hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /]
  [#local toReport = (reportingActive && info.isRequiredToBeReported())!false /]
  [#local isOwner = (deliverable.project.id == projectID)!false /]
  [#local dlvTitle = (info.title)!"" /]
  [#local url][@s.url namespace=namespace action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]

  [#-- Status --]
  [#local statusName = "" /]
  [#attempt][#local statusName = (info.getStatusName(action.getActualPhase()))!"" /][#recover][/#attempt]
  [#local statusKey = statusKeys[statusName?trim]!"" /]

  [#-- Year: an extended deliverable shows its new year and where it came from --]
  [#local year = (info.year)!-1 /]
  [#local newYear = (info.newExpectedYear)!-1 /]
  [#local statusId = (info.status)!-1 /]
  [#local isExtended = (statusId == 3 || statusId == 4 || statusId == 5) && newYear != -1 /]
  [#if year == -1]
    [#local yearText = action.getText("project.deliverableList.redesign.noYear") /]
  [#elseif isExtended]
    [#local yearText = newYear?c /]
  [#else]
    [#local yearText = year?c /]
  [/#if]

  [#local subType = (info.deliverableType.name?capitalize)!"None" /]
  [#if isOwner]
    [#local owner][@s.text name="project.deliverableList.redesign.thisSection"][@s.param]${sectionNoun?cap_first}[/@s.param][/@s.text][/#local]
  [#else]
    [#local owner = (deliverable.owner)!"" /]
    [#if !owner?has_content][#local owner = action.getText("project.deliverableList.redesign.notDefined") /][/#if]
  [/#if]
  [#local shared = (deliverable.sharedWithProjects)!"" /]
  [#local feedback = (deliverable.commentStatus)!"" /]
  [#local isDuplicated = (duplicatedActive && info.duplicated)!false /]
  [#-- "Last, First <email>" reads as "First Last" --]
  [#local responsible = ((deliverable.responsible)!"")?keep_before(" <")?trim /]
  [#if responsible?contains(", ")]
    [#local responsible = responsible?keep_after(", ") + " " + responsible?keep_before(", ") /]
  [/#if]

  [#-- Delete is open to a new deliverable this cluster owns, unless shared trainees lock it --]
  [#local canDeleteShared = (action.canDeleteDeliverableWithSharedTrainees(deliverable.id, actualPhase.id))!false /]
  [#local canDelete = isCurrent && isNew && isOwner && canDeleteShared /]
  [#local deleteBlocked = isCurrent && isNew && isOwner && !canDeleteShared /]

  <div class="dlv-row" data-dlv-row data-set="${set}" data-id="${id}" data-status="${statusKey}" data-year="${yearText}" data-sub="${subType}"
    data-title="${dlvTitle?lower_case}" data-search="D${id} ${dlvTitle} ${subType} ${owner} ${shared} ${responsible}"[#if !isCurrent] hidden[/#if]>
    <div class="dlv-row__main" role="row">
      <span class="dlv-td dlv-td--id" role="cell">D${id}</span>
      <span class="dlv-td dlv-td--title" role="cell">
        <a class="dlv-title" href="${url}"[#if dlvTitle?has_content] title="${dlvTitle}"[/#if]>
          [#if hasDraft]<span class="dlv-tag dlv-tag--draft">[@s.text name="project.deliverableList.redesign.draft" /]</span>[/#if]
          [#if toReport]<span class="dlv-report" title="[@s.text name="project.deliverableList.redesign.report.title" /]">[@dlvIcon "flash" /][@s.text name="project.deliverableList.redesign.report" /]</span>[/#if]
          [#if isNew]<span class="dlv-tag dlv-tag--new">[@s.text name="project.deliverableList.redesign.new" /]</span>[/#if]
          [#if dlvTitle?has_content]${dlvTitle}[#else]<span class="dlv-title__none">[@s.text name="projectsList.title.none" /]</span>[/#if]
        </a>
        <span class="dlv-meta">
          <span>${subType}</span>
          <span class="dlv-meta__sep" aria-hidden="true">·</span>
          <span>[@s.text name="project.deliverableList.owner" /]: <span class="dlv-meta__strong">${owner}</span></span>
          [#if responsible?has_content]
            <span class="dlv-meta__opt">
              <span class="dlv-meta__sep" aria-hidden="true">·</span>
              <span class="dlv-person"><span class="dlv-person__avatar" aria-hidden="true">${dlvInitials(responsible)}</span><span class="dlv-meta__strong">${responsible}</span></span>
            </span>
          [/#if]
          [#if isDuplicated]
            <span class="dlv-meta__opt">
              <span class="dlv-tag dlv-tag--dup">[@dlvIcon "dup" /][@s.text name="project.deliverable.isDuplicated" /]</span>
            </span>
          [/#if]
        </span>
      </span>
      <span class="dlv-td dlv-td--year" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.year" /]">
        <span class="dlv-year">${yearText}</span>
        [#if isExtended && year != -1]<span class="dlv-year__ext">[@s.text name="project.deliverableList.redesign.extendedFrom"][@s.param]${year?c}[/@s.param][/@s.text]</span>[/#if]
      </span>
      <span class="dlv-td dlv-td--shared[#if !shared?has_content] is-empty[/#if]" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.shared" /]"[#if shared?has_content] title="${shared}"[/#if]>
        [#if shared?has_content]${shared}[#else][@s.text name="project.deliverableList.redesign.notShared" /][/#if]
      </span>
      [#if reportingActive]
        <span class="dlv-td dlv-td--fair" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.fair" /]">
          [#if (info.requeriedFair())!false]
            [#list ["F", "A", "I", "R"] as letter]
              [#local state = "d" /]
              [#attempt]
                [#if letter == "F"][#local state = dlvFairState(action.isF(deliverable.id)!"") /][/#if]
                [#if letter == "A"][#local state = dlvFairState(action.isA(deliverable.id)!"") /][/#if]
                [#if letter == "I"][#local state = dlvFairState(action.isI(deliverable.id)!"") /][/#if]
                [#if letter == "R"][#local state = dlvFairState(action.isR(deliverable.id)!"") /][/#if]
              [#recover]
                [#local state = "d" /]
              [/#attempt]
              <span class="dlv-fair dlv-fair--${state}" title="${fairNames[letter]}: ${fairStates[state]}">${letter}</span>
            [/#list]
          [#else]
            <span class="dlv-muted">[@s.text name="project.deliverableList.redesign.fair.notApplicable" /]</span>
          [/#if]
        </span>
      [/#if]
      [#if feedbackActive]
        <span class="dlv-td dlv-td--feedback[#if !feedback?has_content] is-empty[/#if]" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.feedback" /]"
          title="[#if feedback?has_content][@s.text name="project.deliverableList.redesign.feedback.title"][@s.param]${feedback}[/@s.param][/@s.text][#else][@s.text name="project.deliverableList.redesign.feedback.none" /][/#if]">
          [#if feedback?has_content][@dlvIcon "comment" /]${feedback}[#else]—[/#if]
        </span>
      [/#if]
      <span class="dlv-td dlv-td--status" role="cell" data-label="[@s.text name="project.deliverableList.redesign.col.status" /]">
        <span class="dlv-status"><span class="dlv-dot dlv-dot--${statusKey?has_content?then(statusKey, "none")}"></span>${statusLabels[statusKey]}</span>
        [#if isCurrent]
          [#if action.isDeliverableComplete(deliverable.id, actualPhase.id)]
            <span class="dlv-fields dlv-fields--ok">[@dlvIcon "ok" /][@s.text name="project.deliverableList.redesign.fields.complete" /]</span>
          [#else]
            <span class="dlv-fields dlv-fields--warn" title="[@s.text name="project.deliverableList.requiredStatus.incomplete" /]">[@dlvIcon "warn" /][@s.text name="project.deliverableList.redesign.fields.missing" /]</span>
          [/#if]
        [/#if]
      </span>
      <span class="dlv-td dlv-td--action" role="cell">
        [#if canDelete]
          <button type="button" class="dlv-iconBtn dlv-iconBtn--danger" data-dlv-remove aria-expanded="false" aria-controls="dlv-confirm-${id}"
            title="[@s.text name="project.deliverableList.redesign.remove"][@s.param]${id}[/@s.param][/@s.text]"
            aria-label="[@s.text name="project.deliverableList.redesign.remove"][@s.param]${id}[/@s.param][/@s.text]">[@dlvIcon "trash" /]</button>
        [#elseif deleteBlocked]
          <span class="dlv-iconBtn dlv-iconBtn--danger is-disabled" tabindex="0" role="img"
            title="[@s.text name="project.deliverableList.redesign.remove.blocked"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
            aria-label="[@s.text name="project.deliverableList.redesign.remove.blocked"][@s.param]${sectionNoun}[/@s.param][/@s.text]">[@dlvIcon "trash" /]</span>
        [#else]
          <a class="dlv-iconBtn" href="${url}" aria-label="[@s.text name="project.deliverableList.redesign.open"][@s.param]${id}[/@s.param][/@s.text]">[@dlvIcon "chevron" /]</a>
        [/#if]
      </span>
    </div>
    [#if canDelete]
      <div class="dlv-confirm" id="dlv-confirm-${id}" role="alertdialog" aria-labelledby="dlv-confirm-text-${id}" data-dlv-confirm hidden>
        <span class="dlv-confirm__text" id="dlv-confirm-text-${id}">[@s.text name="project.deliverableList.redesign.remove.confirm"][@s.param]${id}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]</span>
        <button type="button" class="dlv-btn dlv-btn--ghost dlv-btn--sm" data-dlv-keep>[@s.text name="project.deliverableList.redesign.remove.keep" /]</button>
        <a class="dlv-btn dlv-btn--danger dlv-btn--sm" href="${baseUrl}/projects/${crpSession}/deleteDeliverable.do?deliverableID=${id}&phaseID=${(actualPhase.id?c)!}">[@s.text name="project.deliverableList.redesign.remove.delete" /]</a>
      </div>
    [/#if]
  </div>
[/#macro]

[#-- Boolean FAIR answer to a state key: achieved, not achieved, or not defined when unanswered --]
[#function dlvFairState value]
  [#if value?is_boolean][#return value?then("a", "n") /][/#if]
  [#return "d" /]
[/#function]

[#-- "Sophia Huyer" to "SH"; a single word gives one letter --]
[#function dlvInitials name]
  [#local words = name?trim?split(" ")?filter(w -> w?has_content) /]
  [#if !words?has_content][#return "" /][/#if]
  [#local first = words?first?substring(0, 1) /]
  [#if words?size == 1][#return first?upper_case /][/#if]
  [#return (first + words?last?substring(0, 1))?upper_case /]
[/#function]

[#macro dlvIcon name]
  [#switch name]
    [#case "ok"]<svg class="dlv-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "warn"]<svg class="dlv-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>[#break]
    [#case "dup"]<svg class="dlv-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="5" y="2.2" width="8.4" height="9.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M3 5v7.6c0 .9.7 1.6 1.6 1.6H10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>[#break]
    [#case "flash"]<svg class="dlv-icon" width="9" height="10" viewBox="0 0 10 12" fill="none" aria-hidden="true"><path d="M6 1 1.6 6.8h3.2L4 11l4.4-5.8H5.2L6 1Z" fill="currentColor"></path></svg>[#break]
    [#case "caret"]<svg class="dlv-icon dlv-select__caret" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "arrow"]<svg class="dlv-icon dlv-th__arrow" width="9" height="9" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M6 10V2M2.8 5.2 6 2l3.2 3.2" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "comment"]<svg class="dlv-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M2.6 3.4h10.8v7.2H7.2l-3 2.4v-2.4H2.6V3.4Z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"></path></svg>[#break]
    [#case "trash"]<svg class="dlv-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "close"]<svg class="dlv-icon" width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M3 3l6 6M9 3 3 9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
    [#case "chevron"]<svg class="dlv-icon" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
  [/#switch]
[/#macro]
