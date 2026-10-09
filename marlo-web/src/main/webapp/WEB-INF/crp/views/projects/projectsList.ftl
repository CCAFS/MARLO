[#ftl]
[#assign title = "MARLO Projects" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net"] /]
[#assign customJS = ["${baseUrlMedia}/js/projects/projectsList.js?20261001"] /]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectsList.css?20261001d"] /]

[#assign currentSection = "projects" /]
[#assign currentStage = (filterBy)!"all" /]

[#if !action.isAiccra()]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"projects", "action":""}
  ]/]
[#else]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"clusters", "action":""}
  ]/]
[/#if]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/projectsListTemplate.ftl" as projectList /]

[#assign phaseName = (actualPhase.composedName)!"" /]
[#assign phaseEditable = (action.getActualPhase().editable)!false /]
[#assign myProjectsList = (myProjects)![] /]
[#assign otherProjectsList = (allProjects)![] /]
[#assign archivedProjectsList = (closedProjects)![] /]
[#assign defaultProjectAction = "${(crpSession)!}/description" /]

[#-- Strings the list script needs; __N__ marks where it puts a number --]
[#macro jsText name][#compress][@s.text name=name][@s.param]__1__[/@s.param][@s.param]__2__[/@s.param][@s.param]__3__[/@s.param][@s.param]__4__[/@s.param][/@s.text][/#compress][/#macro]

<section class="container clustersPage" id="clustersPage"
  data-text-all="[@jsText name='projectsList.filter.all' /]"
  data-text-all-statuses="[@jsText name='projectsList.filter.allStatuses' /]"
  data-text-submitted="[@jsText name='projectsList.status.submitted' /]"
  data-text-showing="[@jsText name='projectsList.showing' /]"
  data-text-showing-filtered="[@jsText name='projectsList.showingFiltered' /]"
  data-text-showing-none="[@jsText name='projectsList.showingNone' /]"
  data-text-page="[@jsText name='projectsList.page' /]"
  data-text-previous="[@jsText name='projectsList.previous' /]"
  data-text-next="[@jsText name='projectsList.next' /]">

  [#-- Closed phase --]
  [#if !phaseEditable]
    <div class="cl-banner" role="note">
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>
      <strong class="cl-bannerTitle">[@s.text name="projectsList.phaseClosed"][@s.param]${phaseName}[/@s.param][/@s.text]</strong>
      <span class="cl-bannerText">[@s.text name="projectsList.phaseClosed.help" /]</span>
    </div>
  [/#if]

  [#-- Heading --]
  <div class="cl-header">
    <div class="cl-headings">
      <h1 class="cl-heading">[@s.text name="projectsList.activeTitle" /]</h1>
      <p class="cl-subheading">
        [@s.text name="projectsList.subtitle"]
          [@s.param][@s.text name="projectsList.active${reportingActive?string('Reporting', 'Planning')}.help" /][/@s.param]
          [@s.param]${phaseName}[/@s.param]
          [@s.param]${myProjectsList?size}[/@s.param]
        [/@s.text]
      </p>
    </div>
    [#if action.canAddCoreProject() && (!crpClosed) && !reportingActive && phaseEditable]
      <div class="cl-headerActions">
        <a class="cl-btn cl-btn--secondary" href="[@s.url action='${crpSession}/addNewAdminProject'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 3v10M3 8h10" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
          [@s.text name="projectsList.addManagementProject" /]
        </a>
        <a class="cl-btn cl-btn--primary" href="[@s.url action='${crpSession}/addNewCoreProject'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 3v10M3 8h10" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
          [@s.text name="projectsList.addResearchProject" /]
        </a>
      </div>
    [/#if]
  </div>

  [#-- Toolbar: one set of filters for every list on the page --]
  <div class="cl-toolbar">
    <label class="cl-search">
      <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.8" stroke="currentColor" stroke-width="1.6"></circle><path d="m10.6 10.6 3 3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>
      <input type="search" class="cl-searchInput" id="clustersSearch" autocomplete="off"
        placeholder="[@s.text name="projectsList.search.placeholder" /]" aria-label="[@s.text name="projectsList.search.label" /]" />
    </label>
    <div class="cl-segmented" id="clustersTypeFilter" role="group" aria-label="[@s.text name="projectsList.filter.type" /]" hidden></div>
    <label class="cl-statusFilter">[@s.text name="projectsList.projectActionStatus" /]
      <select class="cl-select" id="clustersStatusFilter"></select>
    </label>
    <button type="button" class="cl-linkBtn" id="clustersClearFilters" hidden>[@s.text name="projectsList.filter.clear" /]</button>
  </div>

  [#-- Active, with editing privileges --]
  [#assign myListLabel][@s.text name="projectsList.yourProjects" /][/#assign]
  <div class="cl-card cl-listCard">
    [@projectList.projectsList projects=myProjectsList tableId="myProjects" canEdit=true namespace="/${currentSection}" defaultAction=defaultProjectAction ariaLabel=myListLabel /]
    <div class="cl-empty" hidden>
      [#if myProjectsList?has_content]
        <span class="cl-emptyTitle">[@s.text name="projectsList.noMatches" /]</span>
        <button type="button" class="cl-linkBtn clearFilters">[@s.text name="projectsList.filter.clear" /]</button>
      [#else]
        <span class="cl-emptyTitle">[@s.text name="projectsList.mine.empty" /]</span>
      [/#if]
    </div>
    <div class="cl-cardFooter">
      <span class="cl-showing"></span>
      <span class="cl-pager"></span>
    </div>
  </div>

  [#-- Active, read-only access --]
  [@listSection id="otherProjects" projects=otherProjectsList icon="eye"
    title="projectsList.readOnly.title" help="projectsList.otherProjects.help" /]

  [#-- Archived (completed and cancelled) --]
  [#if !reportingActive]
    [@listSection id="archivedProjects" projects=archivedProjectsList icon="archive" archived=true
      title="projectsList.archivedProjects" help="projectsList.archived.help" /]
  [/#if]
</section>

[#-- Removes the project the row confirmation names --]
<form id="removeProjectForm" class="cl-removeForm" method="post" action="[@s.url namespace='/${currentSection}' action='${crpSession}/deleteProject' /]">
  <input type="hidden" name="projectID" value="" />
  <input type="hidden" name="phaseID" value="${(actualPhase.id)!}" />
</form>

[#-- A collapsible card holding one more list; it stays a plain card while the list is empty --]
[#macro listSection id projects icon title help archived=false]
  [#local sectionTitle][@s.text name=title /][/#local]
  [#local summary]
    [#if icon == "eye"]
      <svg class="cl-sectionIcon" width="18" height="18" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M1.5 8S4 3.5 8 3.5 14.5 8 14.5 8 12 12.5 8 12.5 1.5 8 1.5 8Z" stroke="currentColor" stroke-width="1.4"></path><circle cx="8" cy="8" r="2" stroke="currentColor" stroke-width="1.4"></circle></svg>
    [#else]
      <svg class="cl-sectionIcon" width="18" height="18" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="2" y="3" width="12" height="3.2" rx="1" stroke="currentColor" stroke-width="1.4"></rect><path d="M3.2 6.2V12a1 1 0 0 0 1 1h7.6a1 1 0 0 0 1-1V6.2M6.5 8.8h3" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"></path></svg>
    [/#if]
    <span class="cl-sectionText">
      <span class="cl-sectionTitle">${sectionTitle}</span>
      <span class="cl-sectionHelp">
        [#local helpText][@s.text name=help /][/#local]
        ${helpText}[#if !projects?has_content][#if !helpText?markup_string?trim?ends_with('.')].[/#if] [@s.text name="projectsList.section.empty" /][/#if]
      </span>
    </span>
    <span class="cl-count" data-total="${projects?size}">${projects?size}</span>
  [/#local]
  [#if projects?has_content]
    <details class="cl-card cl-section" id="${id}Section">
      <summary class="cl-sectionHead">
        ${summary}
        <svg class="cl-sectionChevron" width="14" height="14" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      </summary>
      <div class="cl-listCard">
        [@projectList.projectsList projects=projects tableId=id archived=archived namespace="/${currentSection}" defaultAction=defaultProjectAction ariaLabel=sectionTitle /]
        <div class="cl-empty" hidden>
          <span class="cl-emptyTitle">[@s.text name="projectsList.noMatches" /]</span>
          <button type="button" class="cl-linkBtn clearFilters">[@s.text name="projectsList.filter.clear" /]</button>
        </div>
        <div class="cl-cardFooter">
          <span class="cl-showing"></span>
          <span class="cl-pager"></span>
        </div>
      </div>
    </details>
  [#else]
    <div class="cl-card cl-section cl-section--empty">
      <div class="cl-sectionHead">${summary}</div>
    </div>
  [/#if]
[/#macro]

[#include "/WEB-INF/global/pages/footer.ftl"]
