[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro projectsList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="4">General Information</th>
        [#if !reportingActive]
          <th colspan="3">[@s.text name="projectsList.projectBudget"] [@s.param]${(crpSession?upper_case)!}[/@s.param] [/@s.text] ${currentCycleYear}</th> 
        [/#if]
        <th colspan="3">Actions</th> 
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>
        [#--  <th id="projectType">[@s.text name="projectsList.projectType" /]</th>--]
        <th id="projectFlagships">[@s.text name="projectsList.projectFlagships" /]</th>
        [#if !reportingActive]
          <th id="projectBudget">[@s.text name="projectsList.W1W2projectBudget" /]</th>
          <th id="projectBudget">[@s.text name="projectsList.W3projectBudget" /]</th>
          <th id="projectBudget">[@s.text name="projectsList.BILATERALprojectBudget" /]</th>
        [/#if]
        <th id="projectActionStatus">[@s.text name="projectsList.projectActionStatus" /]</th>
        <th id="projectDownload">[@s.text name="projectsList.download" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
        [#if isPlanning]
          <th id="projectBudget">[@s.text name="planning.projects.completion" /]</th>
        [/#if]
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        [#assign isProjectNew = action.isProjectNew(project.id) /]
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="${projectUrl}"> P${project.id}</a>
        </td>
          [#-- Project Title --]
          <td class="left">
            [#if isProjectNew]<span class="label label-info">New</span>[/#if]
            [#if project.projectInfo.title?has_content]
              <a href="${projectUrl}" title="${(project.projectInfo.title)!}">[@utilities.wordCutter string=(project.projectInfo.title)!'' maxPos=120 /]</a>
            [#else]
              <a href="${projectUrl}">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Leader --]
          <td class=""> 
            [#if project.leader?has_content]${(project.leader.institution.acronym)!project.leader.institution.name}[#else][@s.text name="projectsList.title.none" /][/#if]
          </td>
          [#-- Project Type 
          <td>
            [@s.text name="project.type.${(project.type?lower_case)!'none'}" /]
          </td>
          --]
          [#-- Flagship / Regions --]
          <td>
          [#if !project.projectInfo.administrative]
            [#if project.flagships?has_content || project.regions?has_content]
              [#if project.flagships?has_content][#list project.flagships as element]<span class="programTag" style="border-color:${(element.color)!'#fff'}">${element.acronym}</span>[/#list][/#if][#if project.regions?has_content][#list project.regions as element]<span class="programTag" style="border-color:${(element.color)!'#fff'}">${element.acronym}</span>[/#list][/#if]
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          [#else]
            <span class="programTag" style="border-color:#444">PMU</span>
          [/#if]
          </td>
          [#if !reportingActive]
          [#-- Budget W1/W2 --]
          <td class="budget"> 
            [#if project.getCoreBudget(currentCycleYear)?has_content]
              <p id="">US$ <span id="">${((project.getCoreBudget(currentCycleYear))!0)?string(",##0.00")}</span></p> 
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Budget W3/ Bilateral --]
          <td class="budget"> 
            [#if project.getW3Budget(currentCycleYear)?has_content]
              <p id="">US$ <span id="">${((project.getW3Budget(currentCycleYear))!0)?string(",##0.00")}</span></p> 
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Budget Bilateral --]
          <td class="budget"> 
            [#if project.getBilateralBudget(currentCycleYear)?has_content]
              <p id="">US$ <span id="">${((project.getBilateralBudget(currentCycleYear))!0)?string(",##0.00")}</span></p> 
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [/#if]
          [#-- Project Action Status --]
          <td>
            [#assign currentCycleYear= currentCycleYear /]
            [#assign submission = action.isProjectSubmitted(project.id) /] [#-- (project.isSubmitted(currentCycleYear, cycleName))! --]
            [#assign completed = (action.isCompleteProject(project.id))!false /]
            [#assign canSubmit = (action.hasPersmissionSubmit(projectID))!false /]
            
            [#-- Check button 
            [#if !submission ]
              [#if canEdit && canSubmit && !completed]
                <a id="validateProject-${project.id}" title="Check for missing fields" class="validateButton ${(project.type)!''}" href="#" >[@s.text name="form.buttons.check" /]</a>
                <div id="progressbar-${project.id}" class="progressbar" style="display:none"></div>
              [/#if]
            [/#if]
            --] 
            
            [#-- Submit button --]
            [#if submission]
              <p title="Submitted">Submitted</p>
            [#else]
              [#if canSubmit]
                [#assign showSubmit=(canSubmit && !submission && completed)]
                [#--  <a id="submitProject-${project.id}" class="submitButton" href="[@s.url namespace=namespace action='submit'][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]" style="display:${showSubmit?string('block','none')}">[@s.text name="form.buttons.submit" /]</a>--]
              [/#if]
            [/#if]
            
            [#if !project.projectInfo.isProjectEditLeader()]
              <p>Pre-setting</p>
            [#else]
              [#if !submission]
                <p title="Ready for project leader completion">Ready for PL</p>
              [/#if]
            [/#if]
            
            
          </td>
          [#-- Track completition of entry --]
          [#if isPlanning]
          <td> <a href="#">Complete / Incomplete</a></td>
          [/#if]
          [#-- Summary PDF download --]
          <td>
            [#if true]
            
            <a href="[@s.url namespace="/projects" action='${(crpSession)!}/reportingSummary'][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='cycle']${action.getCurrentCycle()}[/@s.param][@s.param name='year']${action.getCurrentCycleYear()}[/@s.param][/@s.url]" target="__BLANK">
              <img src="${baseUrl}/images/global/pdf.png" height="25" title="[@s.text name="Download PDF" /]" />
            </a>
            [#else]
              <img src="${baseUrl}/images/global/download-summary-disabled.png" height="25" title="[@s.text name="global.comingSoon" /]" />
            [/#if]
          </td>
          [#-- Delete Project--]
          <td>
            [#if canEdit && isProjectNew && action.deletePermission(project.id) ]
              <a id="removeProject-${project.id}" class="removeProject" href="#" title="">
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="projectsList.deleteProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="projectsList.cantDeleteProject" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
    [/#if]
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
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
        <tr>
          [#-- ID --]
          <td class="projectId">
            <a href="${projectUrl}"> P${project.id}</a>
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
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]"> P${project.id}</a>
        </td>
          [#-- Project Title --]
          <td class="left"> 
            [#if (project.projectInfo.title?has_content)!false]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]" >
              [#if project.projectInfo.title?length < 120] ${project.projectInfo.title}</a> [#else] [@utilities.wordCutter string=project.projectInfo.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">
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
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${deliverable.id?c}[/@s.param][/@s.url]"> P${deliverable.id}</a>
        </td>
          [#-- Project Title --]
          <td class="left"> 
            [#if project.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]" title="${project.title}">
              [#if project.title?length < 120] ${project.title}</a> [#else] [@utilities.wordCutter string=project.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='projectID']${project.id?c}[/@s.param][/@s.url] ">
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