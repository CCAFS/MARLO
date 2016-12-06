[#ftl]
[#assign title = "Project Other contributions" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectOtherContributions.js", "${baseUrl}/js/global/autoSave.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "otherContributions" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectOtherContributions", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectOtherContributions.help" /] </p></div> 
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
          
          <h3 class="headTitle">[@s.text name="projectOtherContributions.title" /]</h3>  
          <div id="projectOtherContributions" class="borderBox">
            
            [#-- Collaborating with other CRPs --]
            [#assign crpsName= "project.ipOtherContribution.crps"/]
            <div class="fullPartBlock">      
              <div class="crpContribution panel tertiary">
                <div class="panel-head">[@customForm.text name="projectOtherContributions.collaboratingCRPs" readText=!editable /]</div> 
                <div class="panel-body"> 
                  <ul id="contributionsBlock" class="list">
                  [#if project.ipOtherContribution?? && project.ipOtherContribution.crpContributions?has_content]  
                    [#list project.ipOtherContribution.crpContributions as crp]
                      <li class="clearfix [#if !crp_has_next]last[/#if]">
                        <input class="id" type="hidden" name="project.ipOtherContribution.crpContributions[${crp_index}].crp.id" value="${crp.crp.id}" />
                        [#-- CRP Title --]
                        <div class="fullPartBlock clearfix">
                           [#if (project.ipOtherContribution.crpContributions[crp_index]?has_content)!false]
                            <span class="name crpName">${project.ipOtherContribution.crpContributions[crp_index].crp.name!}</span>
                           [/#if]
                        </div>
                        [#-- CRP Collaboration nature --]
                        [@customForm.input name="project.ipOtherContribution.crpContributions[${crp_index}].id" display=false className="crpContributionId" showTitle=false /]
                        <div class="fullPartBlock">
                          [@customForm.textArea name="project.ipOtherContribution.crpContributions[${crp_index}].natureCollaboration" className="crpCollaborationNature limitWords-50" i18nkey="projectOtherContributions.collaborationNature" required=true editable=editable /]  
                        </div>
                        
                        [#if editable]<span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>[/#if]
                      </li>
                    [/#list] 
                  [#else]
                    <p class="emptyText"> [@s.text name="projectOtherContributions.crpsEmpty" /] </p>  
                  [/#if]  
                  </ul>
                  [#if editable]
                    [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="crps" keyFieldName="id"  displayFieldName="name" className="crpsSelect" value="" /]
                  [/#if] 
                </div>
              </div> 
            </div>
            
            
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
         
        [/@s.form] 
      </div>
    </div>  
</section>
  
[#include "/WEB-INF/global/pages/footer.ftl"]