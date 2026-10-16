[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#--
  Clusters / projects list table (redesign: "MARLO Clusters List" in the MARLO homepage redesign project).

  One macro renders the three lists of the page: the active ones the user can edit, the active ones the user can only
  read, and the archived ones. projectsList.js drives sorting, filtering and pagination through DataTables, so every
  row carries the values the toolbar filters on as data attributes.
    - archived: the list of completed and cancelled projects, which also offers the summary PDF.
--]
[#macro projectsList projects=[] tableId="projects" canEdit=false archived=false namespace="/" defaultAction="description" ariaLabel=""]
  [#local showBudget = !archived && !reportingActive && !planningActive && !centerGlobalUnit /]
  <table class="clustersTable" id="${tableId}" aria-label="${ariaLabel}">
    <thead>
      <tr>
        <th class="cl-col-id">[@s.text name="projectsList.projectids" /]</th>
        <th class="cl-col-title">[@s.text name="projectsList.projectShortTitles" /]</th>
        <th class="cl-col-lead">[@s.text name="projectsList.projectLeader" /]</th>
        <th class="cl-col-leader">[@s.text name="projectsList.projectLeaderPerson" /]</th>
        <th class="cl-col-programs no-sort">
          [#if centerGlobalUnit]
            [@s.text name="projectsList.projectPrograms" /]
          [#elseif action.hasProgramnsRegions()]
            [@s.text name="projectsList.projectFlagshipsRegions" /]
          [#else]
            [@s.text name="projectsList.projectFlagships" /]
          [/#if]
        </th>
        [#if showBudget]
          <th class="cl-col-budget">[@s.text name="projectsList.W1W2projectBudget" /] ${currentCycleYear?c}</th>
        [/#if]
        <th class="cl-col-status">[@s.text name="projectsList.projectActionStatus" /]</th>
        <th class="cl-col-actions no-sort"><span class="cl-actionsHead">[@s.text name="projectsList.actions" /]</span></th>
      </tr>
    </thead>
    <tbody>
    [#list projects as project]
      [#local isProjectNew = action.isProjectNew(project) /]
      [#local isCrpProject = (action.isProjectCrpOrPlatformForList(project.id))!false /]
      [#local projectCode = action.isAiccra()?string('C', 'P') + project.id?c /]
      [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
      [#local projectTitle = (project.projectInfo.title)!'' /]

      [#-- Type: Management for the administrative ones, otherwise the cluster type --]
      [#local typeKey = "" /]
      [#local typeLabel = "" /]
      [#if (project.projectInfo.administrative)!false]
        [#local typeKey = "management" /][#local typeLabel][@s.text name="project.management" /][/#local]
      [#elseif action.hasClusterType(project)]
        [#local clusterTypeId = (project.projectInfo.clusterType.id)!0 /]
        [#if clusterTypeId == 1][#local typeKey = "country" /][#local typeLabel][@s.text name="project.countryProject" /][/#local][/#if]
        [#if clusterTypeId == 2][#local typeKey = "theme" /][#local typeLabel][@s.text name="project.themeProject" /][/#local][/#if]
        [#if clusterTypeId == 4][#local typeKey = "regional" /][#local typeLabel][@s.text name="project.regionalProject" /][/#local][/#if]
      [/#if]

      [#-- Leader --]
      [#if centerGlobalUnit && ((!(project.projectInfo.phase.crp.centerType))!false)]
        [#local pLeader = (project.getLeader(project.projectInfo.phase))! /]
        [#local pLeaderPerson = (project.getLeaderPersonDB(project.projectInfo.phase))! /]
      [#else]
        [#local pLeader = (project.getLeader(action.getActualPhase()))! /]
        [#local pLeaderPerson = (project.getLeaderPersonDB(action.getActualPhase()))! /]
      [/#if]
      [#local leadName = "" /]
      [#if pLeader?has_content][#local leadName = (pLeader.institution.acronym)!(pLeader.institution.name)!"" /][/#if]
      [#local leaderName = (pLeaderPerson.user.composedNameWithoutEmail)!'' /]
      [#local leaderEmail = (pLeaderPerson.user.email)!'' /]

      [#-- Status --]
      [#local statusName = (project.projectInfo.statusName)!'' /]
      [#local submitted = (!archived && isCrpProject && action.isProjectSubmitted(project))!false /]
      [#local presetting = !archived && isCrpProject && !((project.projectInfo.isProjectEditLeader())!false) /]
      [#local statusKey = statusName?lower_case?replace('[^a-z]', '', 'r') /]

      [#-- Remove: only a project created in this very phase, by someone allowed to, while the phase is open --]
      [#if archived]
        [#local canRemove = canEdit && isProjectNew && action.deletePermission(project.id) /]
      [#else]
        [#local canRemove = canEdit && isProjectNew && action.deletePermission(project.id) && action.getActualPhase().editable && project.projectInfo.phase.id == action.getActualPhase().id /]
      [/#if]

      <tr class="cl-row" data-id="${project.id?c}" data-type="${typeKey}" data-type-label="${typeLabel}"
        data-status="${statusName}" data-submitted="${submitted?c}"
        data-search="${(projectCode + ' ' + projectTitle + ' ' + leadName + ' ' + leaderName + ' ' + leaderEmail)?lower_case}">
        [#-- ID --]
        <td class="cl-col-id" data-order="${project.id?c}">
          <a class="cl-id" href="${projectUrl}">${projectCode}</a>
          [#if centerGlobalUnit && isCrpProject]
            <span class="cl-unitTag">${(project.projectInfo.phase.crp.acronym)!}</span>
          [/#if]
        </td>
        [#-- Title and dates --]
        <td class="cl-col-title" data-order="${projectTitle}">
          <span class="cl-titleLine">
            [#if typeKey?has_content]<span class="cl-type cl-type--${typeKey}">${typeLabel}</span>[/#if]
            [#if isProjectNew]<span class="cl-type cl-type--new">[@s.text name="global.new" /]</span>[/#if]
            <a class="cl-title" href="${projectUrl}" title="${projectTitle}">
              [#if projectTitle?has_content]${projectTitle}[#else][@s.text name="projectsList.title.none" /][/#if]
            </a>
          </span>
          [#if ((project.projectInfo.startDate??)!false) && ((project.projectInfo.endDate??)!false)]
            [#local validDate = ((project.projectInfo.endDate)?date?string('yyyy')?number >= actualPhase.year)!false /]
            [#local invalidDateTitle][@s.text name="projectsList.invalidEndDate" /][/#local]
            <span class="cl-dates[#if !validDate] cl-dates--invalid[/#if]"[#if !validDate] title="${invalidDateTitle}"[/#if]>
              ${(project.projectInfo.startDate)?date?string('MMM d, yyyy')} &ndash; ${(project.projectInfo.endDate)?date?string('MMM d, yyyy')}
            </span>
          [/#if]
        </td>
        [#-- Institution lead --]
        <td class="cl-col-lead">
          [#if leadName?has_content]${leadName}[#else]<span class="cl-none">[@s.text name="projectsList.title.none" /]</span>[/#if]
        </td>
        [#-- Leader --]
        <td class="cl-col-leader" data-order="${leaderName}">
          [#if leaderName?has_content]
            <span class="cl-leaderName">${leaderName}</span>
            [#if leaderEmail?has_content]<a class="cl-leaderEmail" href="mailto:${leaderEmail}">${leaderEmail}</a>[/#if]
          [#else]
            <span class="cl-none">[@s.text name="projectsList.title.none" /]</span>
          [/#if]
        </td>
        [#-- Components / Regions / Programs --]
        <td class="cl-col-programs">
          <span class="cl-chips">
          [#local tagsNumber = 0 /]
          [#if (project.projectInfo.administrative)!false]
            [#local li = (project.projectInfo.liaisonInstitution)!{} /]
            <span class="cl-chip"><span class="cl-chipDot" style="background:#6b7280"></span>
              [#if ((li.crpProgram??)!false) && ((li.crpProgram.crp.id == actualPhase.crp.id) || archived)]
                ${(li.crpProgram.acronym)!(li.crpProgram.name)}
              [#elseif (li.institution??)!false]
                ${(li.institution.acronym)!(li.institution.name)}
              [#else]
                [@s.text name="global.pmu" /]
              [/#if]
            </span>
            [#local tagsNumber = tagsNumber + 1 /]
          [#else]
            [#list ((project.flagships)![]) + ((project.regions)![]) as element]
              [#if archived || element.crp.id == actualPhase.crp.id]
                <span class="cl-chip" title="${(element.composedName)!}"><span class="cl-chipDot" style="background:${(element.color)!'#6b7280'}"></span>${(element.acronym)!}</span>
                [#local tagsNumber = tagsNumber + 1 /]
              [/#if]
            [/#list]
          [/#if]
          [#if tagsNumber < 1]<span class="cl-none">[@s.text name="projectsList.none" /]</span>[/#if]
          </span>
        </td>
        [#-- Budget W1/W2 (getCoreBudget also fills project.coreBudget) --]
        [#if showBudget]
          [#local hasBudget = (project.getCoreBudget(currentCycleYear, action.getActualPhase()))?has_content /]
          <td class="cl-col-budget" data-order="${((project.coreBudget)!0)?c}">
            [#if hasBudget]
              <span class="cl-budget">US$ ${((project.coreBudget)!0)?string(",##0.00")}</span>
            [#else]
              <span class="cl-none">[@s.text name="projectsList.none" /]</span>
            [/#if]
          </td>
        [/#if]
        [#-- Status --]
        <td class="cl-col-status" data-order="${statusName}">
          [#if archived || isCrpProject]
            [#if presetting]
              <span class="cl-status cl-status--neutral"><span class="cl-statusDot"></span>[@s.text name="projectsList.status.presetting" /]</span>
            [#else]
              [#if statusName?has_content]
                <span class="cl-status cl-status--${statusKey}"><span class="cl-statusDot"></span>${statusName}</span>
              [/#if]
              [#if submitted]
                <span class="cl-submitted">
                  <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3.5 8.4 6.6 11.4 12.5 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path></svg>
                  [@s.text name="projectsList.status.submitted" /]
                </span>
              [#elseif !archived && !reportingActive]
                <span class="cl-pending" title="[@s.text name="projectsList.status.readyForLeader.help" /]">[@s.text name="projectsList.status.readyForLeader" /]</span>
              [/#if]
            [/#if]
          [/#if]
        </td>
        [#-- Actions --]
        <td class="cl-col-actions">
          <span class="cl-actions">
            <a class="cl-iconBtn" href="${projectUrl}" aria-label="[@s.text name="projectsList.open" /] ${projectCode}" title="[@s.text name="projectsList.open" /]">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M6 3.5 10.5 8 6 12.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
            </a>
            [#if archived && action.getActualPhase().crp.id != 29]
              <a class="cl-iconBtn" target="_blank" rel="noopener" title="[@s.text name="projectsList.downloadPDF" /]" aria-label="[@s.text name="projectsList.downloadPDF" /] ${projectCode}"
                href="[@s.url namespace="/projects" action='${(crpSession)!}/reportingSummary'][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='cycle']${action.getCurrentCycle()}[/@s.param][@s.param name='year']${action.getCurrentCycleYear()}[/@s.param][/@s.url]">
                <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 2.5v7.5M4.8 7 8 10.2 11.2 7M3 13.5h10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path></svg>
              </a>
            [/#if]
            [#if canRemove]
              <button type="button" class="cl-iconBtn cl-iconBtn--danger removeProject" data-project-id="${project.id?c}"
                aria-label="[@s.text name="projectsList.deleteProject" /] ${projectCode}" title="[@s.text name="projectsList.deleteProject" /]">
                <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.5h10M6.5 4.5V3h3v1.5M4.5 4.5l.6 8.5h5.8l.6-8.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>
              </button>
            [/#if]
          </span>
          [#if canRemove]
            <span class="cl-confirm" hidden>
              <button type="button" class="cl-btn cl-btn--ghost cancelRemoveProject">[@s.text name="projectsList.cancel" /]</button>
              <button type="button" class="cl-btn cl-btn--danger confirmRemoveProject" data-project-id="${project.id?c}">[@s.text name="projectsList.delete" /]</button>
            </span>
          [/#if]
        </td>
      </tr>
    [/#list]
    </tbody>
  </table>
[/#macro]

[#macro evaluationProjects projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="evaluation"]
  <table class="evaluationProjects" id="projects">
    <thead>
      <tr class="subHeader">
        <th class="idsCol">[@s.text name="projectsList.projectids" /]</th>
        <th class="projectTitlesCol" >[@s.text name="projectsList.projectTitles" /]</th>
        <th class="leaderCol">Leader</th>
        <th class="focusCol">Region / Flagship</th>
        <th class="yearCol">Year</th>
        <th class="statusCol">Status</th>
        <th class="totalScoreCol">Total Score</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- ID --]
          <td class="projectId">
          [#if action.isAiccra()]
            <a href="${projectUrl}"> C${project.id}</a>
          [#else]
            <a href="${projectUrl}"> P${project.id}</a>
          [/#if]
          </td>
          [#-- Project Title --]
          <td class="left">
            [#if project.projectInfo.title?has_content]
              <a href="${projectUrl}" title="${project.projectInfo.title}">[@utilities.wordCutter string=project.projectInfo.title maxPos=120 /]</a>
            [#else]
              <a href="${projectUrl}">[@s.text name="projectsList.title.none" /]</a>
            [/#if]
          </td>
          [#-- Leader --]
          <td>[#if project.leadInstitutionAcronym?has_content]${project.leadInstitutionAcronym}[#else][@s.text name="projectsList.title.none" /][/#if]</td>
          [#-- Region / Flagship --]
          <td>
            [#if project.flagships?has_content][#list project.flagships as element]<p class="focus region">${(element.acronym)!}</p>[/#list][/#if]
            [#if project.regions?has_content][#list project.regions as element]<p class="focus flagship">${(element.acronym)!}</p>[/#list][/#if]
          </td>
          [#-- Year --]
          <td><p class="center">${project.projectInfo.yearEvaluation}</p></td>
          [#-- Status --]
          <td><p class="center">${project.projectInfo.statusEvaluation}</p></td>
          [#-- Total Score --]
          <td><p class="totalScore">${project.projectInfo.totalScoreEvaluation}</p></td>
        </tr>
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]

[#macro dashboardProjectsList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        [#-- <th id="projectType">[@s.text name="projectsList.projectType" /]</th> --]
        [#if isPlanning]
          <th id="projectBudget">[@s.text name="planning.projects.completion" /]</th>
        [/#if]
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        <tr>
        [#-- ID --]
        <td class="projectId center">
        [#if action.isAiccra()]
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> C${project.id}</a>
        [#else]
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> P${project.id}</a>
        [/#if]
        </td>
          [#-- Project Title --]
          <td class="left">
            [#if (project.projectInfo.title?has_content)!false]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
              [#if project.projectInfo.title?length < 120] ${project.projectInfo.title}</a> [#else] [@utilities.wordCutter string=project.projectInfo.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Type
          <td>
            [@s.text name="project.type.${(project.type?lower_case)!'none'}" /]
          </td>
          --]
        </tr>
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablesList deliverable={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="deliverables"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >Deliverable Name</th>
        <th id="deliverableType">[@s.text name="projectsList.projectType" /]</th>
        <th id="deliverableEDY">Expected delivery year</th>
        <th id="deliverableFC">FAIR compliance</th>
        <th id="deliverableStatus">Status</th>
        <th id="deliverableRF">Required Fields</th>
        <th id="deliverableDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> P${deliverable.id}</a>
        </td>
          [#-- Project Title --]
          <td class="left">
            [#if project.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${project.title}">
              [#if project.title?length < 120] ${project.title}</a> [#else] [@utilities.wordCutter string=project.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Type --]
          <td>
            [@s.text name="project.type.${(project.type?lower_case)!'none'}" /]
          </td>
        </tr>
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]
