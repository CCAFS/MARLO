[#ftl]
[#assign title = "Project Contributions to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectContributionsCrpList.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectContributionsCrpList.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionsCrpList" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectContributionsCrpList", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectContributionsCrpList.help" /] </p></div> 
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectContributionsCrpList.title" /]</h3>  
          <div id="projectContributionsCrpList" class="borderBox">
            [#-- Your project contributes to the flagships --]
            <div class="form-group">
              <p>
                <strong>Your Project contributes to the following Flagships:</strong>  
                [#if project.flagships?has_content][#list project.flagships as element]<span class="programTag" style="border-color:${element.color}">${element.acronym}</span>[/#list][/#if]
                <div class="clearfix"></div>
              </p>
            </div>
            
            [#-- Project Outcomes List --]
            <table id="projectOutcomesList" class="table table-striped table-hover ">
              <thead>
                <tr>
                  <th>Flagship</th>
                  <th>Outcome 2022</th>
                  <th>Status</th>
                  <th>Remove</th>
                </tr>
              </thead>
              <tbody>
              [#if project.outcomes?has_content]
                [#list project.outcomes as projectOutcome]
                  [@outcomeContributionMacro projectOutcome=projectOutcome name="" index=projectOutcome_index  /]
                [/#list]
              [#else]
                <p class=" text-center">There is not a project outcome added</p>
              [/#if]
              </tbody> 
            </table>
            
            [#-- Add a new Outcomes --]
            [#if canEdit]
            <div class="addNewOutcome">
              <div class="outcomesListBlock">
                <span id="outcomesSelectedIds" style="display:none">[#if project.outcomes?has_content][#list project.outcomes as e]${e.crpProgramOutcome.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                [@customForm.select name="outcomeId" label="" disabled=!canEdit i18nkey="projectContributionsCrpList.selectOutcome" listName="outcomes" keyFieldName="id" displayFieldName="composedName" className="" /]
              </div>
              <div class="addOutcomeBlock">
                <a href="${baseUrl}/projects/${crpSession}/addNewProjectOuctome.do?projectID=${projectID}&outcomeId=-1">
                  <div class="button-blue"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addOutcome"/]</div>
                </a>
              </div>
            </div>
            [/#if]
            
          </div> 
         
        [/@s.form] 
      </div>
    </div>  
</section>
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro outcomeContributionMacro projectOutcome name index isTemplate=false ]
  [#local projectOutcomeID =  projectOutcome.id /] 
  [#local projectOutcomeUrl][@s.url namespace="projects" action="contributionCrp"][@s.param name='projectOutcomeID' value=projectOutcomeID /][@s.param name='edit' value="true" /][/@s.url][/#local]
  <tr class="projectOutcome">
      [#-- Flagship outcome --]
      <td class="text-center">${projectOutcome.crpProgramOutcome.crpProgram.acronym}</td>
      [#-- Title --]
      <td><a href="${projectOutcomeUrl}">${projectOutcome.crpProgramOutcome.description}</a></td>
      [#-- Contribution Status --]
      <td class="text-center">
        [#assign contributionStatus = false /]
        [#if !contributionStatus?has_content] 
          <span class="icon-20 icon-check" title="Complete"></span> 
        [#else]
          <span class="icon-20 icon-uncheck" title=""></span>  
        [/#if]
      </td>
      [#-- Remove Contribution--]
      <td class="text-center">
        [#if ((action.hasPermission("delete"))!true) && canEdit]
          <a id="removeOutcome-${projectOutcomeID}" class="removeOutcome" href="${baseUrl}/projects/${crpSession}/removeProjectOuctome.do?projectID=${projectID}&outcomeId=${projectOutcomeID}" title="">
            <img src="${baseUrl}/images/global/trash.png" />
          </a>
        [#else]
          <img src="${baseUrl}/images/global/trash_disable.png" title="" />
        [/#if]
      </td>
  </tr>
[/#macro]