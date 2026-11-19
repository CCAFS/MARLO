[#ftl]
[#assign title = "Cluster Activities" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlMedia}/js/projects/projectActivities.js?20261119-3"
  ]
/]
[#-- The shell of the page (header, note, toolbar, confirm, empty state) reuses the deliverables list redesign
     (A2-2607); projectActivities.css only adds what is new here --]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/deliverableList.css?20261019-4",
  "${baseUrlMedia}/css/projects/projectActivities.css?20261119-6"
  ]
/]
[#assign currentStage = "activities" /]
[#assign hideJustification = true /]
[#-- The cluster chip is drawn in the section header below instead of the shared top quote --]
[#assign hideTopQuote = true /]

[#if !action.isAiccra()]
  [#assign currentSection = "projects" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"activities", "nameSpace":"${currentSection}", "action":""}
  ] /]
[#else]
  [#assign currentSection = "clusters" /]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"activities", "nameSpace":"${currentSection}", "action":""}
  ] /]
[/#if]

[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign canManageActivities = (action.canAccessSuperAdmin() || action.canAcessCrpAdmin() || action.isRole("PC") || action.isRole("PL"))!false /]
[#assign canChangeTitle = (action.canAccessSuperAdmin() || action.isRole("PC") || action.isRole("PL"))!false /]
[#assign canAddActivity = (editable && canEdit && canManageActivities)!false /]
[#assign freeTextTitle = (action.isProjectActivityCreationActive())!false /]
[#assign cycleYear = (action.getCurrentCycleYear())!0 /]
[#assign phaseLabel = "${(actualPhase.name)!} ${(actualPhase.year?c)!}"?trim /]

[#assign openActivities = (action.getActivities(true))![] /]
[#assign closedActivities = (action.getActivities(false))![] /]
[#assign allActivities = openActivities + closedActivities /]
[#assign missingDeliverables = (deliverablesMissingActivity)![] /]
[#assign deliverablePool = (project.projectDeliverables)![] /]

[#-- Catalog entries already taken by an activity of the cluster: another activity cannot pick them --]
[#assign usedTitleIds = [] /]
[#list allActivities as activity]
  [#if (activity.activityTitle.id)??][#assign usedTitleIds = usedTitleIds + [activity.activityTitle.id?c] /][/#if]
[/#list]

[#-- Activity leaders grouped by the institution of their cluster partner, in the order the partners come --]
[#assign leaderGroups = [] /]
[#list (partnerPersons)![] as person]
  [#assign groupName = (person.projectPartner.institution.acronymName)!"" /]
  [#if !leaderGroups?seq_contains(groupName)][#assign leaderGroups = leaderGroups + [groupName] /][/#if]
[/#list]

[#-- The link to a deliverable; the id is put in per row --]
[#assign deliverableUrlTemplate][@s.url namespace="/${currentSection}" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']__ID__[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
[#function deliverableUrl id]
  [#return (deliverableUrlTemplate?markup_string?replace("__ID__", id?c))?no_esc /]
[/#function]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
<script>
  var globalMaxYear = ${maxYear!"null"};
</script>

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Activities Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]

        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        <div class="dlv cac" data-cac
          data-editable="${editable?string('true','false')}"
          data-reporting="${reportingActive?string('true','false')}"
          data-free-title="${freeTextTitle?string('true','false')}"
          data-cycle-year="${cycleYear?c}"
          data-phase-label="${phaseLabel}">

          [#-- Header: title, summary and the cluster chip (the shared top quote, redrawn here) --]
          <div class="dlv-head">
            <div class="dlv-head__main">
              <h1 class="dlv-head__title">[@s.text name="project.activities.title" /]</h1>
              <span class="dlv-head__summary" data-cac-summary
                data-total="[@s.text name="project.activities.redesign.summary.total" /]"
                data-open="[@s.text name="project.activities.redesign.summary.open" /]"
                data-closed="[@s.text name="project.activities.redesign.summary.closed" /]"
                data-unmapped="[@s.text name="project.activities.redesign.summary.unmapped" /]"></span>
            </div>
            [#if (project.id)??]
              <a class="dlv-quote" href="[@s.url namespace="/projects" action='${crpSession}/description'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
                title="[#if action.isAiccra()]C${project.id?c} - [/#if]${(project.projectInfo.title)!}">
                <span class="dlv-quote__avatar" aria-hidden="true">${sectionNoun?cap_first?substring(0, 1)}</span>
                [#if !action.isAiccra()]P${project.id?c}[#elseif (project.acronym)?has_content]${project.acronym}[#else]C${project.id?c}[/#if]
              </a>
            [/#if]
          </div>

          [#-- Instructions; in reporting, the planning guidance sits behind the toggle --]
          <div class="dlv-note" role="note">
            <span class="dlv-note__icon" aria-hidden="true">i</span>
            <div class="dlv-note__body">
              <p class="dlv-note__text">[#if reportingActive][@s.text name="project.activities.help2" /][#else][@s.text name="project.activities.help1" /][/#if]</p>
              [#if reportingActive]
                <p class="dlv-note__text cac-note__more" id="cac-note-more" hidden>[@s.text name="project.activities.help1" /]</p>
              [/#if]
            </div>
            [#if reportingActive]
              <button type="button" class="dlv-link dlv-note__more" aria-expanded="false" aria-controls="cac-note-more" data-cac-note-toggle
                data-label-more="[@s.text name="project.activities.redesign.viewMore" /]"
                data-label-less="[@s.text name="project.activities.redesign.viewLess" /]">[@s.text name="project.activities.redesign.viewMore" /]</button>
            [/#if]
          </div>

          [#-- Deliverables of the cluster that no activity produces --]
          [#if missingDeliverables?has_content]
            <div class="cac-missing" role="status" data-cac-missing
              data-title-one="[@s.text name="project.activities.redesign.missing.title.one"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
              data-title-many="[@s.text name="project.activities.redesign.missing.title.many"][@s.param]{0}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]">
              <div class="cac-missing__head">
                <svg class="cac-missing__icon" width="20" height="20" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>
                <span class="cac-missing__body">
                  <span class="cac-missing__title" data-cac-missing-title></span>
                  <span class="cac-missing__text">[@s.text name="project.activities.redesign.missing.text"][@s.param]${sectionNoun}[/@s.param][/@s.text]</span>
                </span>
                <button type="button" class="cac-missing__toggle" aria-expanded="false" aria-controls="cac-missing-list" data-cac-missing-toggle
                  data-label-show="[@s.text name="project.activities.redesign.missing.show" /]"
                  data-label-hide="[@s.text name="project.activities.redesign.missing.hide" /]">
                  <span data-cac-missing-label>[@s.text name="project.activities.redesign.missing.show" /]</span>
                  <svg class="cac-missing__caret" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>
                </button>
              </div>
              <ul class="cac-missing__list" id="cac-missing-list" hidden>
                [#list missingDeliverables as d]
                  <li class="cac-missing__row" data-cac-missing-row="${d.id?c}">
                    <span class="cac-dlId">D${d.id?c}</span>
                    <a class="cac-dlTitle" href="${deliverableUrl(d.id)}">${(d.getDeliverableInfo(actualPhase).title)!(d.deliverableInfo.title)!'Untitled'}</a>
                    <a class="cac-missing__open" href="${deliverableUrl(d.id)}" target="_blank" rel="noopener"
                      title="[@s.text name="project.activities.redesign.missing.openNewTab" /]"
                      aria-label="[@s.text name="project.activities.redesign.missing.openNewTabLabel"][@s.param]D${d.id?c}[/@s.param][/@s.text]"><svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M9.5 2.5h4v4M13.5 2.5 7.5 8.5M12 9.5v3.2a.8.8 0 0 1-.8.8H3.3a.8.8 0 0 1-.8-.8V4.8a.8.8 0 0 1 .8-.8h3.2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path></svg></a>
                  </li>
                [/#list]
              </ul>
            </div>
          [/#if]

          [#-- Toolbar: activity set, search and add --]
          <div class="dlv-card dlv-toolbar">
            <span class="dlv-seg" role="tablist" aria-label="[@s.text name="project.activities.redesign.tabs.label" /]">
              <button type="button" role="tab" class="dlv-seg__btn is-on" aria-selected="true" data-cac-tab="open">[@s.text name="project.activities.redesign.tabs.open" /] <span class="dlv-seg__count" data-cac-count="open">${openActivities?size?c}</span></button>
              <button type="button" role="tab" class="dlv-seg__btn" aria-selected="false" data-cac-tab="closed">[@s.text name="project.activities.redesign.tabs.closed" /] <span class="dlv-seg__count" data-cac-count="closed">${closedActivities?size?c}</span></button>
            </span>
            <span class="dlv-search">
              <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
              <input type="search" class="dlv-input" data-cac-search autocomplete="off"
                placeholder="[@s.text name="project.activities.redesign.search.placeholder" /]"
                aria-label="[@s.text name="project.activities.redesign.search.label" /]" />
            </span>
            <span class="dlv-toolbar__end">
              <button type="button" class="dlv-link cac-expandAll" data-cac-expand-all
                data-label-expand="[@s.text name="project.activities.redesign.expandAll" /]"
                data-label-collapse="[@s.text name="project.activities.redesign.collapseAll" /]">[@s.text name="project.activities.redesign.expandAll" /]</button>
              [#if canAddActivity]
                <button type="button" class="dlv-btn dlv-btn--primary" data-cac-add>
                  <svg width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                  [@s.text name="project.activities.redesign.add" /]
                </button>
              [/#if]
            </span>
          </div>

          [#-- Activities of both sets; the tabs only hide cards, so every activity is always posted --]
          <div class="cac-list" data-cac-list>
            [#list openActivities as activity]
              [@activityCard element=activity index=action.getIndexActivities((activity.id)!-1) set="open" /]
            [/#list]
            [#list closedActivities as activity]
              [@activityCard element=activity index=action.getIndexActivities((activity.id)!-1) set="closed" /]
            [/#list]

            <div class="dlv-card dlv-empty" data-cac-empty hidden>
              <span class="dlv-empty__title" data-cac-empty-title
                data-search="[@s.text name="project.activities.redesign.empty.searchTitle" /]"
                data-open="[@s.text name="project.activities.redesign.empty.openTitle" /]"
                data-closed="[@s.text name="project.activities.redesign.empty.closedTitle" /]"></span>
              <span class="dlv-empty__text" data-cac-empty-text
                data-search="[@s.text name="project.activities.redesign.empty.searchText" /]"
                data-open="[@s.text name="project.activities.redesign.empty.openText"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
                data-closed="[@s.text name="project.activities.redesign.empty.closedText" /]"></span>
              <button type="button" class="dlv-btn dlv-btn--ghost dlv-empty__clear" data-cac-clear hidden>[@s.text name="project.activities.redesign.empty.clear" /]</button>
            </div>

            [#if canAddActivity]
              <button type="button" class="cac-addBottom" data-cac-add>
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                [@s.text name="project.activities.redesign.add" /]
              </button>
            [/#if]
          </div>

          [#-- Save bar: the save state, a short-lived notice and the section buttons --]
          <div class="cac-saveBar">
            <div class="cac-saveBar__info">
              [#if editable]
                <span class="cac-saveBar__state" role="status" aria-live="polite">
                  <span class="cac-saveBar__dot" aria-hidden="true"></span>
                  <span data-cac-save-state
                    data-saved="[@s.text name="project.deliverable.redesign.saveBar.saved" /]"
                    data-unsaved="[@s.text name="project.deliverable.redesign.saveBar.unsaved" /]">[@s.text name="project.deliverable.redesign.saveBar.saved" /]</span>
                </span>
              [/#if]
              <span class="cac-toast" role="status" aria-live="polite" data-cac-toast hidden>
                <span data-cac-toast-text></span>
                <button type="button" class="cac-toast__undo" data-cac-undo hidden>[@s.text name="project.activities.redesign.undo" /]</button>
              </span>
            </div>
            <div class="cac-saveBar__actions">
              [#-- Section Buttons & hidden inputs--]
              [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
            </div>
          </div>

        </div>
        [/@s.form]
      </div>
    </div>
</section>
[/#if]

[#-- Deliverables an activity can take; the add control of every activity reads them from here --]
<ul id="cacDeliverablePool" hidden>
  [#list deliverablePool as d]
    <li data-id="${d.id?c}" data-url="${deliverableUrl(d.id)}">${(d.getDeliverableInfo(actualPhase).title)!(d.deliverableInfo.title)!'Untitled'}</li>
  [/#list]
</ul>

[#-- Texts the script composes --]
<div id="cacTexts" hidden
  data-new-activity="[@s.text name="project.activities.redesign.newActivity" /]"
  data-new-code="[@s.text name="project.activities.redesign.newCode" /]"
  data-select-catalog="[@s.text name="project.activities.redesign.selectFromCatalog" /]"
  data-no-leader="[@s.text name="project.activities.redesign.noLeader" /]"
  data-no-dates="[@s.text name="project.activities.redesign.noDates" /]"
  data-deliverables-one="[@s.text name="project.activities.redesign.deliverables.one" /]"
  data-deliverables-many="[@s.text name="project.activities.redesign.deliverables.many" /]"
  data-issues-one="[@s.text name="project.activities.redesign.issues.one" /]"
  data-issues-many="[@s.text name="project.activities.redesign.issues.many" /]"
  data-expand="[@s.text name="project.activities.redesign.expand" /]"
  data-collapse="[@s.text name="project.activities.redesign.collapse" /]"
  data-remove="[@s.text name="project.activities.redesign.remove" /]"
  data-remove-confirm="[@s.text name="project.activities.redesign.remove.confirm" /]"
  data-remove-links="[@s.text name="project.activities.redesign.remove.links" /]"
  data-remove-note="[@s.text name="project.activities.redesign.remove.note" /]"
  data-removed="[@s.text name="project.activities.redesign.removed" /]"
  data-title-used="[@s.text name="project.activities.redesign.title.used"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
  data-moves-closed="[@s.text name="project.activities.redesign.status.movesToClosed" /]"
  data-moves-open="[@s.text name="project.activities.redesign.status.movesToOpen" /]"
  data-overdue="[@s.text name="project.activities.redesign.status.overdue" /]"
  data-error-required="[@s.text name="project.activities.redesign.error.required" /]"
  data-error-words="[@s.text name="project.activities.redesign.error.words" /]"
  data-error-title-used="[@s.text name="project.activities.redesign.error.titleUsed"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
  data-error-leader="[@s.text name="project.activities.redesign.error.leader" /]"
  data-remove-deliverable="[@s.text name="project.activities.redesign.deliverables.remove" /]"
  data-not-mapped="[@s.text name="project.activities.redesign.deliverables.notMapped" /]"
  data-date-invalid="[@s.text name="project.activities.dateInvalidFormat" /]"
  data-date-placeholder="[@s.text name="project.activities.redesign.date.placeholder" /]"
  data-prev-year="[@s.text name="project.activities.redesign.date.prevYear" /]"
  data-next-year="[@s.text name="project.activities.redesign.date.nextYear" /]"></div>

[#-- Progress labels, one per status (customForm draws the first one; the script swaps the text) --]
[#if reportingActive]
  <div id="cacProgressLabels" hidden>
    [#list ["2", "3", "4", "5"] as statusId]
      <span data-status="${statusId}">[@s.text name="project.activities.statusJustification.status${statusId}" /]</span>
    [/#list]
  </div>
[/#if]

[#-- Activity template, cloned by the add buttons --]
[#if canAddActivity]
  <div id="cacActivityTemplate" hidden>
    [@activityCard element={} index=-1 set="open" isTemplate=true /]
  </div>
[/#if]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- One activity: a summary row that opens the form below it --]
[#macro activityCard element index set isTemplate=false]
  [#local customName = "project.projectActivities[${index}]" /]
  [#local uid = isTemplate?string("template", ((element.id)!index)?c) /]
  [#local statusId = ((element.activityStatus)!2)?c /]
  [#if statusId == "-1"][#local statusId = "2" /][/#if]
  [#local isOpenSet = set == "open" /]
  [#local displayTitle = (element.displayTitle)!"" /]
  [#local code = "" /]
  [#local name = displayTitle /]
  [#local codeMatch = displayTitle?matches(r"^\s*([0-9]+(?:\.[0-9]+)*)\s+(.*)$", "s") /]
  [#if codeMatch][#local code = codeMatch?groups[1] /][#local name = codeMatch?groups[2] /][/#if]
  [#local label = (code?has_content)?then(code, (displayTitle?has_content)?then(displayTitle, action.getText("project.activities.redesign.newActivity"))) /]
  [#local leader = (element.projectPartnerPerson)!{} /]
  [#local leaderName = (leader.user.composedCompleteName)!"" /]
  [#local leaderInstitution = (leader.projectPartner.institution.acronymName)!"" /]
  [#local links = (element.deliverables)![] /]
  [#local canRemove = isOpenSet && editable && canManageActivities /]
  [#-- A completed activity keeps its catalog entry, but one that has none (its title was typed while the
       specificity was on) must be able to get one, or its missing title can never be fixed --]
  [#local titleEditable = editable && canChangeTitle && (isOpenSet || !((element.activityTitle.id)??)) /]

  <div class="cac-act[#if isTemplate] is-new[/#if]" id="projectActivity-${uid}" data-cac-activity data-set="${set}"
    data-saved-status="${statusId}" data-new="${isTemplate?string('true','false')}">
    <input class="activityId" type="hidden" name="${customName}.id" value="${(element.id?c)!-1}" />
    <input type="hidden" name="${customName}.composeID" value="${(element.composeID)!}" />
    <span class="index" hidden>${index?c}</span>

    <div class="cac-act__head">
      <button type="button" class="cac-act__caret" aria-expanded="false" aria-controls="act-${uid}-body" data-cac-toggle
        aria-label="[@s.text name="project.activities.redesign.expand"][@s.param]${label}[/@s.param][/@s.text]">
        <svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      </button>
      <button type="button" class="cac-act__summary" aria-controls="act-${uid}-body" data-cac-toggle>
        <span class="cac-act__titleLine">
          <span class="cac-act__code" data-cac-code>[#if code?has_content]${code}[#elseif !displayTitle?has_content][@s.text name="project.activities.redesign.newCode" /][/#if]</span>
          <span class="cac-act__name[#if !displayTitle?has_content] is-empty[/#if]" data-cac-name title="${name}">[#if displayTitle?has_content]${name}[#else][@s.text name="project.activities.redesign.selectFromCatalog" /][/#if]</span>
        </span>
        <span class="cac-act__meta">
          <span class="cac-act__metaItem">
            <span class="cac-avatar" aria-hidden="true" data-cac-leader-initials></span>
            <span data-cac-leader-line>[#if leaderName?has_content]${leaderName}[#if leaderInstitution?has_content] · ${leaderInstitution}[/#if][#else][@s.text name="project.activities.redesign.noLeader" /][/#if]</span>
          </span>
          <span class="cac-act__metaItem">
            <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="2.4" y="3.2" width="11.2" height="10.4" rx="1.8" stroke="currentColor" stroke-width="1.4"></rect><path d="M2.4 6.4h11.2M5.4 1.8v2.6M10.6 1.8v2.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"></path></svg>
            <span data-cac-date-line>[#if (element.startDate)?? && (element.endDate)??]${element.startDate?string("d MMM yyyy")} – ${element.endDate?string("d MMM yyyy")}[#else][@s.text name="project.activities.redesign.noDates" /][/#if]</span>
          </span>
          <span class="cac-act__metaItem">
            <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M4 1.8h5.4L12.4 4.8v9.4H4V1.8Z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"></path><path d="M9.2 1.8v3.2h3.2" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"></path></svg>
            <span data-cac-dl-line></span>
          </span>
        </span>
      </button>
      <span class="cac-issues" data-cac-issues hidden></span>
      <span class="cac-act__status"><span class="dlv-dot" data-cac-status-dot></span><span data-cac-status-label>[@s.text name="project.activities.redesign.status.${statusId}" /]</span></span>
      [#if canRemove]
        <button type="button" class="dlv-iconBtn dlv-iconBtn--danger cac-act__remove" aria-expanded="false" data-cac-remove
          title="[@s.text name="project.activities.redesign.remove"][@s.param]${label}[/@s.param][/@s.text]"
          aria-label="[@s.text name="project.activities.redesign.remove"][@s.param]${label}[/@s.param][/@s.text]">
          <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>
        </button>
      [/#if]
    </div>

    [#if canRemove]
      <div class="dlv-confirm cac-confirm" role="alertdialog" data-cac-confirm hidden>
        <span class="dlv-confirm__text" data-cac-confirm-text></span>
        <button type="button" class="dlv-btn dlv-btn--ghost" data-cac-remove-cancel>[@s.text name="project.activities.redesign.remove.keep" /]</button>
        <button type="button" class="dlv-btn dlv-btn--danger" data-cac-remove-confirm>[@s.text name="project.activities.redesign.remove.delete" /]</button>
      </div>
    [/#if]

    <div class="cac-act__body" id="act-${uid}-body" hidden>

      [#-- Title --]
      <div class="cac-field" data-cac-field="title">
        [#if freeTextTitle]
          [@customForm.input name="${customName}.title" value="${(element.title)!}" type="text" i18nkey="project.activities.inputTitle" placeholder="" className="activityTitle limitWords-30" required=true editable=editable /]
        [#else]
          <div class="select fieldReference">
            <label for="act-${uid}-title">[@s.text name="project.activities.inputTitle" /][@customForm.req required=editable /]</label>
            <div class="selectList">
              [#if titleEditable]
                <select id="act-${uid}-title" name="${customName}.activityTitle.id" class="form-control input-sm activityTitle">
                  <option value="-1">[@s.text name="project.activities.redesign.title.placeholder" /]</option>
                  [#list (activityTitles)![] as catalogTitle]
                    [#local isSelected = ((element.activityTitle.id)!-1) == catalogTitle.id /]
                    <option value="${catalogTitle.id?c}" data-label="${catalogTitle.title}"[#if isSelected] selected[/#if][#if !isSelected && usedTitleIds?seq_contains(catalogTitle.id?c)] disabled[/#if]>${catalogTitle.title}</option>
                  [/#list]
                </select>
              [#else]
                <input type="hidden" name="${customName}.activityTitle.id" value="${(element.activityTitle.id?c)!}" class="activityTitle" />
                <p class="cac-readValue">[#if displayTitle?has_content]${displayTitle}[#else]<span class="fieldEmpty">[@s.text name="form.values.fieldEmpty" /]</span>[/#if]</p>
              [/#if]
            </div>
          </div>
          [#if editable && !titleEditable]
            <span class="cac-hint">[#if canChangeTitle][@s.text name="project.activities.redesign.title.locked" /][#else][@s.text name="project.activities.redesign.title.noPermission"][@s.param]${sectionNoun}[/@s.param][/@s.text][/#if]</span>
          [/#if]
        [/#if]
        <span class="cac-error" data-cac-error="title"></span>
      </div>

      [#-- Description --]
      <div class="cac-field" data-cac-field="description">
        [@customForm.textArea name="${customName}.description" i18nkey="project.activities.inputDescription" value="${(element.description)!}" required=true placeholder="project.activities.redesign.description.placeholder" className="limitWords-150 activityDescription" editable=editable /]
        <span class="cac-error" data-cac-error="description"></span>
      </div>

      <div class="cac-grid">
        [#-- Start Date --]
        <div class="cac-field" data-cac-field="startDate">
          [@customForm.input name="${customName}.startDate" i18nkey="project.activities.inputStartDate" className="startDate" type="text" disabled=!editable required=true editable=editable /]
          <span class="cac-error" data-cac-error="startDate"></span>
        </div>
        [#-- End Date --]
        <div class="cac-field" data-cac-field="endDate">
          [@customForm.input name="${customName}.endDate" i18nkey="project.activities.inputEndDate" className="endDate" type="text" disabled=!editable required=true editable=editable /]
          <span class="cac-error" data-cac-error="endDate"></span>
        </div>
        [#-- Activity leader --]
        <div class="cac-field" data-cac-field="leader">
          <div class="select fieldReference">
            <label for="act-${uid}-leader">[@s.text name="project.activities.inputLeader" /][@customForm.req required=editable /]</label>
            <div class="selectList">
              [#if editable]
                <select id="act-${uid}-leader" name="${customName}.projectPartnerPerson.id" class="form-control input-sm activityLeader">
                  <option value="-1">[@s.text name="project.activities.redesign.leader.placeholder"][@s.param]${sectionNoun}[/@s.param][/@s.text]</option>
                  [#-- One flat list, by partner; each option already names its institution --]
                  [#list leaderGroups as groupName]
                    [#list partnerPersons as person]
                      [#if ((person.projectPartner.institution.acronymName)!"") == groupName]
                        <option value="${person.id?c}" data-name="${(person.user.composedCompleteName)!}" data-institution="${groupName}"[#if ((leader.id)!-1) == person.id] selected[/#if]>${(person.user.composedCompleteName)!}[#if groupName?has_content] · ${groupName}[/#if]</option>
                      [/#if]
                    [/#list]
                  [/#list]
                </select>
              [#else]
                <input type="hidden" name="${customName}.projectPartnerPerson.id" value="${(leader.id?c)!}" />
                <p class="cac-readValue">[#if leaderName?has_content]${leaderName}[#if leaderInstitution?has_content] · ${leaderInstitution}[/#if][#else]<span class="fieldEmpty">[@s.text name="form.values.fieldEmpty" /]</span>[/#if]</p>
              [/#if]
            </div>
          </div>
          [#if editable]<span class="cac-hint" data-cac-hint="leader">[@s.text name="project.activities.redesign.leader.hint"][@s.param]${sectionNoun}[/@s.param][/@s.text]</span>[/#if]
          <span class="cac-error" data-cac-error="leader"></span>
        </div>
      </div>

      [#-- Status, and in reporting the progress or the justification it asks for --]
      <div class="cac-statusBox" data-cac-status-box>
        <div class="cac-statusBox__row">
          <span class="cac-statusBox__label" id="act-${uid}-status">[@s.text name="project.activities.inputStatus" /][@customForm.req required=editable /]</span>
          [#if editable]
            [#local statusOptions = ["2", "3", "5"] /]
            [#if statusId == "4"][#local statusOptions = ["2", "3", "4", "5"] /][/#if]
            <span class="cac-seg radio-block" role="radiogroup" aria-labelledby="act-${uid}-status">
              [#list statusOptions as option]
                <label class="cac-seg__opt">
                  <input type="radio" class="activityStatus" name="${customName}.activityStatus" value="${option}"[#if option == statusId] checked[/#if] />
                  <span class="cac-seg__btn"><span class="dlv-dot cac-dot--${option}" aria-hidden="true"></span>[@s.text name="project.activities.redesign.status.${option}" /]</span>
                </label>
              [/#list]
            </span>
            <span class="cac-moves" data-cac-moves hidden></span>
          [#else]
            <span class="cac-statusRead"><span class="dlv-dot cac-dot--${statusId}" aria-hidden="true"></span>[@s.text name="project.activities.redesign.status.${statusId}" /]</span>
          [/#if]
        </div>
        [#if editable && reportingActive]
          <div class="cac-overdue" role="alert" data-cac-overdue hidden>
            <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>
            <span class="cac-overdue__text" data-cac-overdue-text></span>
            <button type="button" class="cac-overdue__btn" data-cac-mark-complete>[@s.text name="project.activities.redesign.status.markComplete" /]</button>
          </div>
        [/#if]
        [#if reportingActive]
          <div class="cac-field" data-cac-field="progress">
            [@customForm.textArea name="${customName}.activityProgress" i18nkey="project.activities.statusJustification.status${statusId}" value="${(element.activityProgress)!}" required=true className="limitWords-150 progressDescription" editable=editable /]
            <span class="cac-error" data-cac-error="progress"></span>
          </div>
        [/#if]
      </div>

      [#-- Deliverables the activity produces --]
      <div class="cac-dls" listname="${customName}.deliverables">
        <div class="cac-dls__head">
          <span class="cac-dls__title">[@s.text name="project.activities.deliverableList" /]</span>
          <span class="cac-dls__count" data-cac-dl-count>${links?size?c}</span>
        </div>
        <p class="cac-dls__empty" data-cac-dl-empty[#if links?has_content] hidden[/#if]>[#if editable][@s.text name="project.activities.redesign.deliverables.empty" /][#else][@s.text name="form.values.fieldEmpty" /][/#if]</p>
        <ul class="cac-dls__list" data-cac-dl-list>
          [#list links as link]
            [#if (link.deliverable.id)??]
              [@deliverableRow linkName="${customName}.deliverables[${link_index}]" link=link /]
            [/#if]
          [/#list]
        </ul>
        [#if editable]
          <div class="cac-combo" data-cac-combo>
            <span class="cac-combo__field">
              <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
              <input type="text" class="dlv-input cac-combo__input" data-cac-combo-input autocomplete="off" role="combobox"
                aria-expanded="false" aria-controls="act-${uid}-dlOptions" aria-autocomplete="list"
                aria-label="[@s.text name="project.activities.redesign.deliverables.pickLabel" /]"
                placeholder="[@s.text name="project.activities.redesign.deliverables.pick" /]" />
            </span>
            <ul class="cac-combo__list" id="act-${uid}-dlOptions" role="listbox" data-cac-combo-list hidden></ul>
            <span class="cac-combo__none" data-cac-combo-none hidden>[@s.text name="project.activities.redesign.deliverables.noMatch" /]</span>
          </div>
        [/#if]
      </div>

    </div>
  </div>
[/#macro]

[#-- A deliverable of an activity, with the inputs the action binds --]
[#macro deliverableRow linkName link]
  <li class="cac-dl" data-cac-dl="${link.deliverable.id?c}">
    <input class="cac-dl__deliverable" type="hidden" name="${linkName}.deliverable.id" value="${link.deliverable.id?c}" />
    <input class="cac-dl__link" type="hidden" name="${linkName}.id" value="${(link.id?c)!-1}" />
    <span class="cac-dlId">D${link.deliverable.id?c}</span>
    <a class="cac-dlTitle" href="${deliverableUrl(link.deliverable.id)}">${(link.deliverable.getDeliverableInfo(actualPhase).title)!(link.deliverable.deliverableInfo.title)!'Untitled'}</a>
    [#if editable]
      <button type="button" class="dlv-iconBtn dlv-iconBtn--danger cac-dl__remove" data-cac-dl-remove
        title="[@s.text name="project.activities.redesign.deliverables.remove"][@s.param]${link.deliverable.id?c}[/@s.param][/@s.text]"
        aria-label="[@s.text name="project.activities.redesign.deliverables.remove"][@s.param]${link.deliverable.id?c}[/@s.param][/@s.text]">
        <svg width="10" height="10" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M3 3l8 8M11 3l-8 8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
      </button>
    [/#if]
  </li>
[/#macro]
