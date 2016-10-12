[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectDescription.js", "${baseUrl}/js/global/autoSave.js","${baseUrl}/js/global/fieldsValidation.js"] /]
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
              [@customForm.textArea name="project.title" required=true className="project-title" editable=editable && action.hasPermission("title") /]
            </div>
            <div class="form-group row">
              [#-- Project Program Creator --]
              <div class="col-md-6">
                [@customForm.select name="project.liaisonInstitution.id" className="liaisonInstitutionSelect" i18nkey="project.liaisonInstitution"  disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison") /]
              </div>
              [#--  Project Owner Contact Person --]
              <div class="col-md-6">
                [@customForm.select name="project.liaisonUser.id" className="liaisonUserSelect" i18nkey="project.liaisonUser"  listName="allOwners" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable && action.hasPermission("managementLiaison")/]
              </div> 
            </div>  
            <div class="form-group row">  
              [#-- Start Date --]
              <div class="col-md-6">
                [@customForm.input name="project.startDate" type="text" disabled=!editable  required=true editable=editable && action.hasPermission("startDate")  /]
              </div> 
              [#-- End Date --]
              <div class="col-md-6">
                [@customForm.input name="project.endDate" type="text" disabled=!editable required=true editable=editable && action.hasPermission("endDate")  /]
              </div>
            </div>
            <div class="form-group row">
              [#-- Project Type 
              <div class="col-md-6"> 
                [@customForm.select name="project.type" value="${(project.type)!}" i18nkey="project.type" listName="projectTypes" disabled=true editable=false stringKey=true /]
              </div>
              --]
            </div> 
    
            [#-- Project upload work plan 
            [#if !((project.bilateralProject)!false)]
            <div id="uploadWorkPlan" class="tickBox-wrapper fullBlock" style="[#if !((project.requiresWorkplanUpload)!false) && !project.workplan?has_content && !editable]display:none[/#if]">
              [#if action.hasPermission("workplan") ]
                [@customForm.checkbox name="project.requiresWorkplanUpload" value="true" checked=project.requiresWorkplanUpload  i18nkey="project.workplanRequired" disabled=!editable editable=editable  && action.hasPermission("workplan") /]
              [/#if]
              <div class="tickBox-toggle uploadContainer" [#if !((project.requiresWorkplanUpload)!false)]style="display:none"[/#if]>
                <div class="halfPartBlock fileUpload projectWorkplan">
                  [@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplan.fileName" editable=editable && action.hasPermission("workplan") /]
                </div> 
              </div>  
            </div>
            [/#if]--]
            
            [#-- Project upload bilateral contract --]
            [#if (project.bilateralProject)!false && action.hasPermission("bilateralContract") ]
            <div class="fullBlock fileUpload bilateralContract">
              <label>[@customForm.text name="projectDescription.uploadBilateral" readText=!editable /]:</label>
              <div class="uploadContainer">
                [@customForm.inputFile name="file" fileUrl="${(bilateralContractURL)!}" fileName="project.bilateralContractName.fileName" editable=editable && action.hasPermission("bilateralContract") /]
              </div>  
            </div>
            [/#if]
            
            [#-- Project Summary --]
            <div class="form-group">
              [@customForm.textArea name="project.summary" required=!((project.bilateralProject)!false) className="project-description" editable=editable && action.hasPermission("summary") /]
            </div>
            
            [#-- -- -- REPORTING BLOCK -- -- --]
            [#if reportingActive]
              [#-- Project upload annual report to donor--]
              [#if (project.bilateralProject)!false]
              <div class="fullBlock fileUpload annualreportDonor">
                <label>[@customForm.text name="projectDescription.annualreportDonor" readText=!editable /]:</label>
                <div class="uploadContainer">
                  [@customForm.inputFile name="fileReporting" fileUrl="${(AnualReportURL)!}" fileName="project.annualReportToDonnor.fileName" editable=editable && action.hasPermission("bilateralContract") /]
                </div>  
              </div>
              [/#if]
            [/#if]
            
            [#--  Regions/global and Flagships that the project is working on --]
            <h5>[@customForm.text name="projectDescription.projectWorking" readText=!editable /]:</h5>
           
            <div id="projectWorking" class="fullBlock clearfix">
              [#-- Flagships --] 
              <div class="col-md-6">
                <div id="projectFlagshipsBlock" class="">
                  <h5>[@s.text name="projectDescription.flagships" /]:[@customForm.req required=editable && action.hasPermission("flagships") /] </h5>
                  [#if editable && action.hasPermission("flagships")]
                    [@s.fielderror cssClass="fieldError" fieldName="project.flagshipValue"/]
                    [@s.checkboxlist name="project.flagshipValue" list="programFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput fpInput"  value="flagshipIds" /]
                  [#else]
                    <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
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
                [#if regionFlagships?has_content] 
                  <div id="projectRegionsBlock" class="">
                    <h5>[@s.text name="projectDescription.regions" /]:[@customForm.req required=editable && action.hasPermission("regions") /]</h5>
                    [#if editable && action.hasPermission("regions")]
                      [@s.fielderror cssClass="fieldError" fieldName="project.regionsValue"/]
                      <input type="checkbox" name="project.noRegional" value="true" id="projectNoRegional" class="checkboxInput" [#if (project.isNoRegional())!false ]checked="checked"[/#if] />
                      <label for="projectNoRegional" class="checkboxLabel"> <i>[@s.text name="project.noRegional" /]</i> </label>
                      [@s.checkboxlist name="project.regionsValue" list="regionFlagships" listKey="id" listValue="composedName" cssClass="checkboxInput rpInput" value="regionsIds" /]
                    [#else]
                      <input type="hidden" name="project.regionsValue" value="${(project.regionsValue)!}"/>
                      [#if project.regions?has_content]
                        [#list project.regions as element]<p class="checked">${element.composedName}</p>[/#list]
                      [#else]
                        [#--  --if !((project.bilateralProject)!false)]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if--]
                      [/#if]
                    [/#if]
                  </div>
                [/#if]
              </div>
              <div class="clearfix"></div>
            </div> 
            
            [#-- Cluster of Activities --]
            <div class="panel tertiary">
              <div class="panel-head"> [@customForm.text name="projectDescription.clusterActivities" readText=!editable /]:[@customForm.req required=editable  && action.hasPermission("activities") /]</div>
              <div id="projectsList" class="panel-body" listname="project.clusterActivities"> 
                <ul class="list">
                [#if project.clusterActivities?has_content]
                  [#list project.clusterActivities as element]
                    <li class="clusterActivity clearfix [#if !element_has_next]last[/#if]">
                      [#if editable && action.hasPermission("activities") ]<span class="listButton remove popUpValidation">[@s.text name="form.buttons.remove" /]</span>[/#if] 
                      <input class="id" type="hidden" name="project.clusterActivities[${element_index}].crpClusterOfActivity.id" value="${element.crpClusterOfActivity.id}" />
                      <input class="cid" type="hidden" name="project.clusterActivities[${element_index}].id" value="${(element.id)!}" />
                      <span class="name">${(element.crpClusterOfActivity.description)!'null'}</span>
                      <div class="clearfix"></div>
                      <ul class="leaders">
                        [#if element.crpClusterOfActivity.leaders??]
                          [#list element.crpClusterOfActivity.leaders as leader]<li class="leader">${(leader.user.composedName?html)!'null'}</li>[/#list]
                        [/#if]
                      </ul>
                    </li>
                  [/#list]
                [#else]
                  [#if !action.hasPermission("activities")] <p class="emptyText"> [@s.text name="projectDescription.clusterActivities.empty" /]</p> [/#if]
                [/#if]  
                </ul>
                [#if editable  && action.hasPermission("activities")]
                  <span id="coaSelectedIds" style="display:none">[#if project.clusterActivities?has_content][#list project.clusterActivities as e]${e.crpClusterOfActivity.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="clusterofActivites" keyFieldName="id" displayFieldName="description" className="" value="" /]
                [/#if] 
              </div>
            </div>
            
            [#-- Scale of the project 
            <div class="panel tertiary">
              <div class="panel-head"> [@customForm.text name="projectDescription.scale" readText=!editable /]:[@customForm.req required=true /]</div>
              <div id="" class="projectScale">
                <label class="radio-inline" for="isProjectNational">
                  [#if editable]<input type="radio" name="project.scale" id="isProjectNational"  value="1"  [#if (project.scale?? && (project.scale == 1))]checked="checked"[/#if]/> [/#if]
                  [#if editable || (project.scale?? && (project.scale == 1))] [@s.text name="project.national"/] [/#if]
                </label>
                <label class="radio-inline" for="isProjectRegional">
                  [#if editable]<input type="radio" name="project.scale" id="isProjectRegional" value="2" [#if (project.scale?? && (project.scale == 2))]checked="checked"[/#if]/> [/#if]
                  [#if editable || (project.scale?? && (project.scale == 2))] [@s.text name="project.regional"/] [/#if]
                </label>
                <label class="radio-inline" for="isProjectGlobal">
                  [#if editable]<input type="radio" name="project.scale" id="isProjectGlobal" value="3" [#if (project.scale?? && (project.scale == 3))]checked="checked"[/#if]/> [/#if]
                  [#if editable || (project.scale?? && (project.scale == 3))] [@s.text name="project.global"/] [/#if]
                </label>
              </div>
            </div>--]
            
            [#-- Scope of the project 
            [#if locScopeElements?has_content]
            <div class="panel tertiary">
              <div class="panel-head"> [@customForm.text name="projectDescription.scope" readText=!editable /]:</div>
              <div id="projectsScopes" class="panel-body"> 
                <ul class="list">
                [#if project.scopes?has_content]
                  [#list project.scopes as element]
                    <li class="projectScope clearfix [#if !element_has_next]last[/#if]">
                      [#if editable && action.hasPermission("scope")]<span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>[/#if]
                      <input class="id" type="hidden" name="project.scopes[${element_index}].id" value="${(element.id)!}" />
                      <input class="cid" type="hidden" name="project.scopes[${element_index}].locElementType.id" value="${(element.locElementType.id)!}" />
                      <span class="name">${(element.locElementType.name)!'null'}</span>
                    </li>
                  [/#list]
                [#else]
                  [#if !editable]<p class="emptyText"> [@s.text name="projectDescription.scope.empty" /]</p> [/#if]
                [/#if]  
                </ul>
                [#if editable && action.hasPermission("scope") ]
                  <span id="scopesSelectedIds" style="display:none">[#if project.scopes?has_content][#list project.scopes as e]${e.locElementType.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="locScopeElements " keyFieldName="id" displayFieldName="name" className="" value="" /]
                [/#if] 
              </div>
            </div>
            [/#if]--]
            
          </div> 
           
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as institution]"${institution}" : ${(institution.crpProgram)!-1}[#if institution_has_next], [/#if][/#list]}</span>


[#-- Cluster of activity list template --]
<ul style="display:none">
  <li id="cpListTemplate" class="clusterActivity clearfix">
    <span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.clusterActivities[-1].crpClusterOfActivity.id" value="" />
    <input class="cid" type="hidden" name="project.clusterActivities[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- project scope template --]
<ul style="display:none">
  <li id="projecScope-template" class="projecScope clearfix">
    <span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.scopes[-1].locElementType.id" value="" />
    <input class="cid" type="hidden" name="project.scopes[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- Remove project contribution popup --]
<div id="removeContribution-dialog" title="Remover Cluster of Activity" style="display:none">
  <ul class="messages"><li>[@s.text name="projectDescription.removeCoADialog" /]</li></ul>
</div>

[#-- File upload Template--]
[@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplan.fileName" template=true /]

[@customForm.inputFile name="fileReporting" template=true /] 
  
[#include "/WEB-INF/global/pages/footer.ftl"]