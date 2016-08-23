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
            <p>
              <strong>Your Project contributes to the following Flagships:</strong>  
              [#if project.flagships?has_content][#list project.flagships as element]<span>${element.acronym}</span>[/#list][/#if]
            </p>
            
            [#-- Project Outcomes List --]
        
            <ul id="projectOutcomesList" class="simpleBox">
              [#if project.outcomes?has_content]
                [#list project.outcomes as projectOutcome]
                  <li class="projectOutcome">
                    [#assign projectOutcomeID =  projectOutcome.id /] 
                    [#assign projectOutcomeUrl][@s.url namespace="projects" action="contributionCrp"][@s.param name='projectID' value=projectID /][@s.param name='projectOutcomeID' value=projectOutcomeID /][@s.param name='edit' value="true" /][/@s.url][/#assign]
                    <div class="row">
                      <div class="col-md-1"><a href="${projectOutcomeUrl}">${projectOutcome.crpProgramOutcome.crpProgram.acronym}</a></div>
                      <div class="col-md-10"><a href="${projectOutcomeUrl}">${projectOutcome.crpProgramOutcome.description}</a></div>
                      <div class="col-md-1">
                        [#if (projectOutcome.canDelete)!true]
                          <a id="removeOutcome-${projectOutcomeID}" class="removeOutcome" href="#" title=""><img src="${baseUrl}/images/global/trash.png" /></a>
                        [#else]
                          <img src="${baseUrl}/images/global/trash_disable.png" title="" />
                        [/#if]
                      </div>
                    </div>
                    [#if projectOutcome_has_next]<hr />[/#if]
                  </li>
                [/#list]
              [#else]
              
              [/#if]
            </ul>
            
            [#-- Add a new Outcomes --]
            [#if canEdit]
            <div class="addNewOutcome">
              <div class="outcomesListBlock">
                [@customForm.select name="outcomeId" label="" disabled=!canEdit i18nkey="projectContributionsCrpList.selectOutcome" listName="outcomes" keyFieldName="id" displayFieldName="description" className="" value="outcomeId" /]
              </div>
              <div class="addOutcomeBlock">
                <a href="${baseUrl}/projects/${crpSession}/addNewProjectOuctome.do?projectID=${projectID}&projectOutcomeID=-1">
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