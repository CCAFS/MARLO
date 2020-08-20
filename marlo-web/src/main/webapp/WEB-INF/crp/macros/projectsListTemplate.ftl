[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro projectsList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        [#if !centerGlobalUnit]
        <th colspan="5">General Information</th>
        [#else]
        <th colspan="7">General Information</th>
        [/#if]
        
        [#if !reportingActive && !centerGlobalUnit]
          <th colspan="3">[@s.text name="projectsList.projectBudget"] [@s.param]${(crpSession?upper_case)!}[/@s.param] [/@s.text] ${currentCycleYear}</th> 
        [/#if]
        [#if !centerGlobalUnit]         
        <th colspan="3">Actions</th> 
        [#else]
        <th colspan="2">Actions</th> 
        [/#if]
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>        
        <th id="projectType">[@s.text name="projectsList.projectLeaderPerson" /]</th>  
         [#if centerGlobalUnit]
        <th id="centerStaff" >[@s.text name="projectsList.centerStaff" /]</th>
        [/#if]
        
        <th id="projectFlagships">
          [#if centerGlobalUnit]
            [@s.text name="projectsList.projectPrograms" /]
          [#else]
            [#if action.hasProgramnsRegions()]
              [@s.text name="projectsList.projectFlagshipsRegions" /] 
            [#else]
               [@s.text name="projectsList.projectFlagships" /]
            [/#if]
          [/#if]
        </th>
       
        [#if !reportingActive && !centerGlobalUnit]
          <th id="projectBudget">[@s.text name="projectsList.W1W2projectBudget" /]</th>
          <th id="projectBudget">[@s.text name="projectsList.W3projectBudget" /]</th>
          <th id="projectBudget">[@s.text name="projectsList.BILATERALprojectBudget" /]</th>
        [/#if]
        <th id="projectActionStatus">[@s.text name="projectsList.projectActionStatus" /]</th>
          [#if centerGlobalUnit]
            <th id="centermapping" >[@s.text name="projectsList.centerMapping" /]</th>
          [/#if]
        [#if !centerGlobalUnit]
        <th id="projectDownload">[@s.text name="projectsList.download" /]</th>
        [/#if]
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
        [#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
        [#assign isCenterProject = (action.isProjectCenter(project.id))!false ]
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="text-center">
          <a href="${projectUrl}"> P${project.id}</a>
          [#if centerGlobalUnit]
            <span class="badge globalUnitTag"> ${(project.projectInfo.phase.crp.acronym)!} </span>
          [/#if]
          
        </td>
          [#-- Project Title --]
          <td class="left">
            [#if isProjectNew]<span class="label label-info">[@s.text name="global.new" /]</span>[/#if]
            [#if project.projectInfo.administrative]<span class="label label-primary">[@s.text name="project.management" /]</span>[/#if]
            [#if project.projectInfo.title?has_content]
              <a href="${projectUrl}" title="${(project.projectInfo.title)!}">[@utilities.wordCutter string=(project.projectInfo.title)!'' maxPos=120 /]</a>
            [#else]
              <a href="${projectUrl}">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
            [#if ((project.projectInfo.startDate??)!false) && ((project.projectInfo.endDate??)!false) ]
              [#local pYear = (project.projectInfo.endDate)?date?string('yyyy')?number ]
              [#local validDate = (pYear >= actualPhase.year)!false ]
              <p class="${(!validDate)?string('fieldError', '')}" title="${(!validDate)?string('Invalid End Date', '')}">
                <small class="">(${(project.projectInfo.startDate)!} - ${(project.projectInfo.endDate)!})</small>
              </p>
            [/#if]
          </td>
          [#-- Project Leader --]
          [#if centerGlobalUnit && ((!(project.projectInfo.phase.crp.centerType))!false)]
            [#assign pLeader =  (project.getLeader(project.projectInfo.phase))! ]
            [#assign pLeaderPerson =  (project.getLeaderPersonDB(project.projectInfo.phase))! ]
          [#else]
            [#assign pLeader =  (project.getLeader(action.getActualPhase()))! ]
            [#assign pLeaderPerson =  (project.getLeaderPersonDB(action.getActualPhase()))! ]
          [/#if]         
          <td class=""> 
            [#if pLeader?has_content]${(pLeader.institution.acronym)!pLeader.institution.name}[#else][@s.text name="projectsList.title.none" /][/#if]
          </td>
          <td class=""> 
          [#if centerGlobalUnit]
            [#if pLeaderPerson?has_content] ${(pLeaderPerson.user.composedCompleteName)!}[#else][@s.text name="projectsList.title.none" /][/#if]
          [#else]
            [#if pLeaderPerson?has_content] ${(pLeaderPerson.user.composedName)!}[#else][@s.text name="projectsList.title.none" /][/#if]
          [/#if]   
                        
          </td>
            [#-- Center Staf --]
          [#if centerGlobalUnit]
          <td class=""> 
            <div class="mCustomScrollbar staff-list" data-mcs-theme="dark">
            [#assign centerStaffList =  (project.getLeadersCenter(project.projectInfo.phase,actualPhase.crp.institution.id))! ]
            [#list (centerStaffList)![] as centerstaff]
              ${(centerstaff.user.composedCompleteName)!}
            [/#list]
           </div>
          </td>
          [/#if]    
          [#-- Flagship / Regions / Programs --]
          <td>
            [#assign tagsNumber = 0 /]
            [#if project.projectInfo.administrative]
              [#local li = (project.projectInfo.liaisonInstitution)!{}]
              <span class="programTag" style="border-color:#444">
                [#if ((li.crpProgram??)!false) && (li.crpProgram.crp.id == actualPhase.crp.id )]
                  ${(li.crpProgram.acronym)!(li.crpProgram.name)}
                [#elseif (li.institution??)!false]
                  ${(li.institution.acronym)!(li.institution.name)}
                [#else]
                  [@s.text name="global.pmu" /]
                [/#if]
              </span>
              [#assign tagsNumber = tagsNumber+1 /]
            [#else]
              [#assign programs = ((project.flagships)![]) + ((project.regions)![])]
              [#list (programs)![] as element]
                [#if element.crp.id == actualPhase.crp.id ]
                  <span class="programTag" style="border-color:${(element.color)!'#fff'}" title="${(element.composedName)}">${(element.acronym)!}</span>
                  [#assign tagsNumber = tagsNumber+1 /]
                [/#if]
              [/#list]
            [/#if]
            
            [#if tagsNumber < 1]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#if !reportingActive && !centerGlobalUnit]
          [#-- Budget W1/W2 --]
          <td class="budget"> 
            [#if project.getCoreBudget(currentCycleYear,action.getActualPhase())?has_content]
               <nobr> US$ <span id="">${((project.coreBudget)!0)?string(",##0.00")}</span></nobr>
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Budget W3/ Bilateral --]
          <td class="budget"> 
            [#if project.getW3Budget(currentCycleYear,action.getActualPhase())?has_content]
              <nobr>US$ <span id="">${((project.w3Budget)!0)?string(",##0.00")}</span></nobr> 
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Budget Bilateral --]
          <td class="budget"> 
            [#if project.getBilateralBudget(currentCycleYear,action.getActualPhase())?has_content]
              <nobr>US$ <span id="">${((project.bilateralBudget)!0)?string(",##0.00")}</span></nobr> 
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [/#if]
          [#-- Project Action Status --]
          <td>
            [#assign currentCycleYear= currentCycleYear /]
            [#if centerGlobalUnit]
              [#assign submission = action.isSubmit(project.id,(currentCycleYear-1),"Reporting") /]
            [#else]
              [#assign submission = action.isProjectSubmitted(project.id) /]
            [/#if]
            [#-- CRP Project --]
            [#if isCrpProject]
              [#if !project.projectInfo.isProjectEditLeader()]
                <p>Pre-setting</p>
              [#else]
                [#if !submission]
                  [#if !reportingActive]<p title="Ready for project leader completion">Ready for PL</p>[/#if]
                [#else]
                  <strong title="Submitted">Submitted</strong>
                [/#if]                
                [#-- Status --]
                [#if reportingActive]
                  <p>${(project.projectInfo.statusName)!}</p>
                [/#if]
              [/#if]
            [/#if]
            
          </td>
          [#if centerGlobalUnit]
            <td>  
              [#if isCrpProject]
                [#assign centerMappingComplete = action.isCenterMappingComplete(project.id,project.projectInfo.phase) /]
              [#else]
                [#assign centerMappingComplete = action.isCompleteCenterProject(project.id) /]                
              [/#if]
              [#if centerMappingComplete!]
              <span class="icon-20 icon-check" title="Complete"></span>            
              [#else]
              
              <span class="icon-20 icon-uncheck" title="Not Completed" ></span>
             
              [/#if]
            </td> 
          [/#if]
          [#-- Summary PDF download --]
          [#if !centerGlobalUnit]
          <td>        
            [#if action.getActualPhase().crp.id != 29]
              <a href="[@s.url namespace="/projects" action='${(crpSession)!}/reportingSummary'][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='cycle']${action.getCurrentCycle()}[/@s.param][@s.param name='year']${action.getCurrentCycleYear()}[/@s.param][/@s.url]" target="__BLANK">
                <img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
              </a>          
            [/#if]
          </td>
          [/#if]
          [#-- Delete Project--]
          <td>
            [#if canEdit && isProjectNew && action.deletePermission(project.id) && action.getActualPhase().editable && project.projectInfo.phase.id=action.getActualPhase().id]
              <a id="removeProject-${project.id}" class="removeProject" href="#" title="">
                <img src="${baseUrlCdn}/global/images/trash.png" title="[@s.text name="projectsList.deleteProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="projectsList.cantDeleteProject" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]


[#macro projectsListArchived projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
        <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>
          <th id="projectLeader" >[@s.text name="projectsList.projectLeaderPerson" /]</th>
        [#--  <th id="projectType">[@s.text name="projectsList.projectType" /]</th>--]
        <th id="projectFlagships">
          [#if action.hasProgramnsRegions()]
            [@s.text name="projectsList.projectFlagshipsRegions" /] 
          [#else]
             [@s.text name="projectsList.projectFlagships" /]
          [/#if]
        </th>
        <th id="projectActionStatus">[@s.text name="projectsList.projectActionStatus" /]</th>
        <th id="projectDownload">[@s.text name="projectsList.download" /]</th>
        <th id="projectDownload">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        [#assign isProjectNew = action.isProjectNew(project.id) /]
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="${projectUrl}"> P${project.id}</a>
        </td>
          [#-- Project Title --]
          <td class="left">
            [#if isProjectNew]<span class="label label-info">[@s.text name="global.new" /]</span>[/#if]
            [#if project.projectInfo.administrative]<span class="label label-primary">[@s.text name="project.management" /]</span>[/#if]
            [#if project.projectInfo.title?has_content]
              <a href="${projectUrl}" title="${project.projectInfo.title}">
              [#if project.projectInfo.title?length < 120] ${project.projectInfo.title}</a> [#else] [@utilities.wordCutter string=project.projectInfo.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="${projectUrl}">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Leader --]
          <td class=""> 
            [#if project.getLeader(action.getActualPhase())?has_content]${(project.getLeader(action.getActualPhase()).institution.acronym)!project.getLeader(action.getActualPhase()).institution.name}[#else][@s.text name="projectsList.title.none" /][/#if]
          </td>
              <td class=""> 
            [#if project.getLeaderPersonDB(action.getActualPhase())?has_content] ${(project.getLeaderPersonDB(action.getActualPhase()).user.composedName)!}[#else][@s.text name="projectsList.title.none" /][/#if]
          </td>
          [#-- Flagship / Regions --]
          <td>
          [#if !project.projectInfo.administrative]
            [#if project.flagships?has_content || project.regions?has_content]
              [#if project.flagships?has_content][#list project.flagships as element]<span class="programTag" style="border-color:${(element.color)!'#fff'}">${element.acronym}</span>[/#list][/#if][#if project.regions?has_content][#list project.regions as element]<span class="programTag" style="border-color:${(element.color)!'#fff'}">${element.acronym}</span>[/#list][/#if]
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          [#else] 
            [#local li = (project.projectInfo.liaisonInstitution)!{} ]
            <span class="programTag" style="border-color:#444">
              [#if (li.crpProgram??)!false]
                ${(li.crpProgram.acronym)!(li.crpProgram.name)}
              [#elseif (li.institution??)!false]
                ${(li.institution.acronym)!(li.institution.name)}
              [#else]
                [@s.text name="global.pmu" /]
              [/#if]
            </span>
          [/#if]
          </td>
          [#-- Project Action Status --]
          <td>
            <strong>${(project.projectInfo.statusName)!}</strong> 
          </td>
          [#-- Summary PDF download --]
          <td>
            [#if action.getActualPhase().crp.id != 29]
              <a href="[@s.url namespace="/projects" action='${(crpSession)!}/reportingSummary'][@s.param name='projectID']${project.id?c}[/@s.param][@s.param name='cycle']${action.getCurrentCycle()}[/@s.param][@s.param name='year']${action.getCurrentCycleYear()}[/@s.param][/@s.url]" target="__BLANK">
                <img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
              </a>
            [/#if]
          </td>
          [#-- Delete Project--] 
          <td>
            [#if canEdit && isProjectNew && action.deletePermission(project.id) ]
              <a id="removeProject-${project.id}" class="removeProject" href="#" title="">
                <img src="${baseUrlCdn}/global/images/trash.png" title="[@s.text name="projectsList.deleteProject" /]" /> 
              </a>
            [#else]
              <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="projectsList.cantDeleteProject" /]" />
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
        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
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
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> P${project.id}</a>
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