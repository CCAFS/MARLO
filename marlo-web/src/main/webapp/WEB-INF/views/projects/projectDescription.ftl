[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectDescription.js", "${baseUrl}/js/global/autoSave.js"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectDescription", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectDescription.help" /] </p></div> 
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
           
          
          <h3 class="headTitle">[@s.text name="projectDescription.title" /]</h3>  
          <div id="projectDescription" class="borderBox">
            [#-- Project Title --]
            <div class="form-group">
              [@customForm.textArea name="project.title" required=true className="project-title" editable=editable /]
            </div>
            <div class="form-group row">
              [#-- Project Program Creator --]
              <div class="col-md-6">
                [@customForm.select name="project.liaisonInstitution.id" i18nkey="project.liaisonInstitution"  disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable /]
              </div>
              [#--  Project Owner Contact Person --]
              <div class="col-md-6">
                [@customForm.select name="project.liaisonUser.id" i18nkey="project.liaisonUser"  listName="allOwners" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable /]
              </div> 
            </div>  
            <div class="form-group row">  
              [#-- Start Date --]
              <div class="col-md-6">
                [@customForm.input name="project.startDate" type="text" disabled=!editable  required=true editable=editable  /]
              </div> 
              [#-- End Date --]
              <div class="col-md-6">
                [@customForm.input name="project.endDate" type="text" disabled=!editable required=true editable=editable /]
              </div>
            </div>
            <div class="form-group row">
              [#-- Project Type --]
              <div class="col-md-6"> 
                [@customForm.select name="project.type" value="${(project.type)!}" i18nkey="project.type" listName="projectTypes" disabled=true editable=false stringKey=true /]
              </div>
            </div> 
    
            [#-- Project upload work plan --]
            [#if !((project.bilateralProject)!false)]
            <div id="uploadWorkPlan" class="tickBox-wrapper fullBlock" style="[#if !((project.requiresWorkplanUpload)!false) && !project.workplan?has_content && !editable]display:none[/#if]">
              [#if action.hasPermission("workplan") ]
                [@customForm.checkbox name="project.requiresWorkplanUpload" value="true" checked=project.requiresWorkplanUpload  i18nkey="project.workplanRequired" disabled=!editable editable=editable /]
              [/#if]
              <div class="tickBox-toggle uploadContainer" [#if !((project.requiresWorkplanUpload)!false)]style="display:none"[/#if]>
                <div class="halfPartBlock fileUpload projectWorkplan">
                  [@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplan.fileName" editable=editable /]
                </div> 
              </div>  
            </div>
            [/#if]
    
            [#-- Project upload bilateral contract --]
            [#if (project.bilateralProject)!false && action.hasPermission("bilateralContract") ]
            <div class="fullBlock fileUpload bilateralContract">
              <label>[@customForm.text name="projectDescription.uploadBilateral" readText=!editable /]:</label>
              <div class="uploadContainer">
                [@customForm.inputFile name="file" fileUrl="${(bilateralContractURL)!}" fileName="project.bilateralContractName.fileName" editable=editable /]
              </div>  
            </div>
            [/#if]
            
            [#-- Project Summary --]
            <div class="form-group">
              [@customForm.textArea name="project.summary" required=!((project.bilateralProject)!false) className="project-description" editable=editable /]
            </div>
            
            [#-- -- -- REPORTING BLOCK -- -- --]
            [#if reportingActive]
              [#-- Project upload annual report to donor--]
              [#if (project.bilateralProject)!false]
              <div class="fullBlock fileUpload annualreportDonor">
                <label>[@customForm.text name="projectDescription.annualreportDonor" readText=!editable /]:</label>
                <div class="uploadContainer">
                  [@customForm.inputFile name="fileReporting" fileUrl="${(AnualReportURL)!}" fileName="project.annualReportToDonnor.fileName" editable=editable /]
                </div>  
              </div>
              [/#if]
            [/#if]
          
            
            [#--  Regions/global and Flagships that the project is working on --]
            <h5>[@customForm.text name="projectDescription.projectWorking" readText=!editable /]:[@customForm.req required=true /]
           
            <div id="projectWorking" class="fullBlock clearfix">
              [#-- Flagships --] 
              <div class="col-md-6">  
                <div id="projectFlagshipsBlock" class="">
                  <h5>[@s.text name="projectDescription.flagships" /]:</h5>
                  [#if editable]
                    [@s.fielderror cssClass="fieldError" fieldName="project.flagshipValue"/]
                    [@s.checkboxlist name="project.flagshipValue" list="programFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput"  value="flagshipIds" /]
                  [#else]
                    [#if project.flagships?has_content]
                      [#list project.flagships as element]<p class="checked">${element.composedName}</p>[/#list]
                    [#else]
                      [#if !((project.bilateralProject)!false)]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if]
                    [/#if]
                  [/#if]
                </div>
              </div>
              [#-- Regions --] 
              <div class="col-md-6">  
                <div id="projectRegionsBlock" class="">
                  <h5>[@s.text name="projectDescription.regions" /]:</h5>
                  [#if editable]
                    [@s.fielderror cssClass="fieldError" fieldName="project.regionsValue"/]
                    [@s.checkboxlist name="project.regionsValue" list="regionFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput"  value="regionsIds" /]
                  [#else]
                    [#if project.regions?has_content]
                      [#list project.regions as element]<p class="checked">${element.composedName}</p>[/#list]
                    [#else]
                      [#if !((project.bilateralProject)!false)]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if]
                    [/#if]
                  [/#if]
                </div>
              </div>
              <div class="clearfix"></div>
            </div> 
            
            [#-- Cluster of Activities --]
            <div class="panel tertiary">
              <div class="panel-head"> [@customForm.text name="projectDescription.clusterActivities" readText=!editable /]:[@customForm.req required=true /]</div>
              <div id="projectsList" class="panel-body"> 
                <ul class="list">
                [#if project.clusterActivities?has_content]
                  [#list project.clusterActivities as element]
                    <li class="clusterActivity clearfix [#if !element_has_next]last[/#if]">
                      [#if editable]<span class="listButton remove popUpValidation">[@s.text name="form.buttons.remove" /]</span>[/#if] 
                      <input class="id" type="hidden" name="project.clusterActivities[${element_index}].crpClusterOfActivity.id" value="${element.crpClusterOfActivity.id}" />
                      <input class="id" type="hidden" name="project.clusterActivities[${element_index}].id" value="${(element.id)!}" />
                      <div class=""><span class="name">${(element.crpClusterOfActivity.description)!'null'}</span></div>
                      <div class="clearfix"></div>
                      <ul class="leaders">
                        [#if element.crpClusterOfActivity.leaders??]
                          [#list element.crpClusterOfActivity.leaders as leader]<li class="leader">${(leader.user.composedName)!'null'}</li>[/#list]
                        [/#if]
                      </ul>
                    </li>
                  [/#list]
                [#else]
                  <p class="emptyText"> [@s.text name="projectDescription.clusterActivities.empty" /]</p>
                [/#if]  
                </ul>
                [#if editable ]
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="clusterofActivites" keyFieldName="id" displayFieldName="description" className="" value="" /]
                [/#if] 
              </div>
            </div>
            
            [#-- Scale of the project --]
            <div class="simpleBox"></div>
            
          </div> 
           
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

[#-- Core project list template --]
<ul style="display:none">
  <li id="cpListTemplate" class="clusterActivity clearfix">
    <span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.clusterActivities[-1].crpClusterOfActivity.id" value="" />
    <div><span class="name"></span></div>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- Remove project contribution popup --]
<div id="removeContribution-dialog" title="Remover project contribution" style="display:none">
  <ul class="messages"><li>[@s.text name="projectDescription.removeContributionDialog" /]</li></ul>
</div>

[#-- File upload Template--]
[@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplan.fileName" template=true /]

[@customForm.inputFile name="fileReporting" template=true /] 
  
[#include "/WEB-INF/global/pages/footer.ftl"]
