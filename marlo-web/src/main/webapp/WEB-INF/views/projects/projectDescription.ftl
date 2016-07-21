[#ftl]
[#assign title = "Project Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectDescription.js"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "description" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectDescription", "nameSpace":"/projects", "action":""}
] /]

[#assign canEdit = true /]
[#assign editable = true /]

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
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          <h3 class="headTitle">[@s.text name="projectDescription.title" /]</h3>  
          <div id="projectDescription" class="borderBox">
              [#-- Project Title --]
              <div class="fullBlock">
                [@customForm.textArea name="project.title" required=true className="project-title" editable=editable /]
              </div>
              <div class="fullBlock">
                [#-- Project Program Creator --]
                <div class="halfPartBlock">
                  [@customForm.select name="project.liaisonInstitution.id" i18nkey="project.liaisonInstitution"  disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="getComposedName()" required=true editable=editable /]
                </div>
                [#--  Project Owner Contact Person --]
                <div class="halfPartBlock">
                  [@customForm.select name="project.liaisonUser.id" i18nkey="project.liaisonUser"   listName="allOwners" keyFieldName="id"  displayFieldName="getUser().composedName" required=true editable=editable /]
                </div> 
              </div>  
              <div class="fullBlock">  
                [#-- Start Date --]
                <div class="halfPartBlock">
                  [@customForm.input name="project.startDate" type="text" disabled=!editable  required=true editable=editable  /]
                </div> 
                [#-- End Date --]
                <div class="halfPartBlock">
                  [@customForm.input name="project.endDate" type="text" disabled=!editable required=true editable=editable /]
                </div>
              </div>
              <div class="fullBlock">
                [#-- Project Type --]
                <div class="halfPartBlock"> 
                  [@customForm.select name="project.type" value="${project.type}" i18nkey="project.type" listName="projectTypes" disabled=true editable=false stringKey=true /]
                </div>
              </div> 
      
              [#-- Project upload work plan --]
              [#if !((project.bilateralProject)!false)]
              <div id="uploadWorkPlan" class="tickBox-wrapper fullBlock" style="[#if !((project.requiresWorkplanUpload)!false) && !project.workplanName?has_content && !editable]display:none[/#if]">
                [#if (action.hasPermission("workplan"))!false ]
                  [@customForm.checkbox name="project.requiresWorkplanUpload" value="true" checked=project.requiresWorkplanUpload  i18nkey="project.workplanRequired" disabled=!editable editable=editable /]
                [/#if]
                <div class="tickBox-toggle uploadContainer" [#if ! ((project.workplanRequired)!false)]style="display:none"[/#if]>
                  <div class="halfPartBlock fileUpload projectWorkplan"> 
                    [#if project.workplanName?has_content]
                      <p> 
                        [#if editable]<span id="remove-file" class="remove"></span>[#else]<span class="file"></span>[/#if] 
                        <a href="${(workplanURL)!}${project.workplanName}">${project.workplanName}</a>  <input type="hidden" name="project.workplanName" value="${project.workplanName}" /> 
                      </p>
                    [#else]
                      [#if editable]
                        [#if !((action.hasPermission("workplan"))!false) ]
                          <h6>[@s.text name="projectDescription.uploadProjectWorkplan" /]:</h6>
                        [/#if]
                        [@customForm.inputFile name="file"  /]
                      [/#if] 
                    [/#if] 
                  </div> 
                </div>  
              </div>
              [/#if]
      
              [#-- Project upload bilateral contract --]
              [#if (project.bilateralProject)!false && action.hasPermission("bilateralContract") ]
              <div class="fullBlock fileUpload bilateralContract">
                <h6>[@customForm.text name="projectDescription.uploadBilateral" readText=!editable /]:</h6>
                <div class="uploadContainer">
                  [#if project.bilateralContractProposalName?has_content]
                    [#if editable]<span id="remove-file" class="remove"></span>[#else]<span class="file"></span>[/#if] 
                    <p> <a href="${bilateralContractURL}${project.bilateralContractProposalName}">${project.bilateralContractProposalName}</a> 
                  [#else]
                    [#if editable]
                      [@customForm.inputFile name="file"  /]
                    [#else]  
                      <p>[@s.text name="form.values.notFileUploaded" /]</p> 
                    [/#if] 
                  [/#if]
                </div>  
              </div>
              [/#if]
              
              [#-- Project Summary --]
              <div class="fullBlock">
                [@customForm.textArea name="project.summary" required=!((project.bilateralProject)!false) className="project-description" editable=editable /]
              </div>
              
              [#-- -- -- REPORTING BLOCK -- -- --]
              
                [#-- Project upload annual report to donor--]
                [#if (project.bilateralProject)!false]
                <div class="fullBlock fileUpload annualreportDonor">
                  <h6>[@customForm.text name="projectDescription.annualreportDonor" readText=!editable /]:</h6>
                  <div class="uploadContainer">
                    [#if project.annualReportDonor?has_content]
                      [#if editable]<span id="remove-fileReporting" class="remove"></span>[#else]<span class="file"></span>[/#if] 
                      <p><a href="${AnualReportURL}${project.annualReportDonor}">${project.annualReportDonor}</a> </p>
                    [#else]
                      [#if editable]
                        [@customForm.inputFile name="fileReporting"  /]
                      [#else]  
                        <p> [@s.text name="form.values.notFileUploaded" /]</p>
                      [/#if] 
                    [/#if]
                  </div>  
                </div>
                [/#if]
               
            
              
              [#--  Regions/global and Flagships that the project is working on --]
              <h5>[@customForm.text name="projectDescription.projectWorking" readText=!editable /]:
             
              <div id="projectWorking" class="fullBlock clearfix">
                [#-- Flagships --] 
                <div id="projectFlagshipsBlock" class="grid_5">
                  <h6>[@s.text name="projectDescription.flagships" /]</h6>
                  <div class="checkboxGroup">  
                    [#if editable]
                      [@s.fielderror cssClass="fieldError" fieldName="project.flagships"/]
                      [@s.checkboxlist name="project.flagshipValue" list="programFlagships" listKey="id" listValue="getComposedName()" cssClass="checkbox" value="flagshipIds" /]
                    [#else]
                      [#if project.flagships?has_content]
                        [#list project.flagships as element]
                         <p class="checked">${element.composedName}</p>
                        [/#list]
                      [#else]
                        [#if !((project.bilateralProject)!false)]<span class="fieldError">[@s.text name="form.values.required" /]</span>[/#if]
                      [/#if]
                    [/#if]
                  </div>
                </div> 
                
      
            [#-- Bilateral/Core projects only for CCAFS Projects 
             <h4 id="bilateralProjects" class="subHeadTitle"> [@s.text name="projectDescription.projectsContributing" /] </h4> 
            <div class="panel tertiary">
              <div class="panel-head">
                [#if (project.bilateralProject)!false]
                  [@customForm.text name="projectDescription.selectCoreProject" readText=!editable /]:
                [#else]
                  [@customForm.text name="projectDescription.selectBilateralProject" readText=!editable /]:
                [/#if]
              </div>
              <div id="projectsList" class="panel-body"> 
                <ul class="list">
                [#if project.linkedProjects?has_content]
                  [#list project.linkedProjects as element]
                    <li class="clearfix [#if !element_has_next]last[/#if]">
                      <input class="id" type="hidden" name="project.linkedProjects" value="${element.id?c}" />
                      <a href="[@s.url action='description'][@s.param name='projectID']${element.id}[/@s.param][/@s.url]"><span class="name">${element.id} - ${element.title}</span> </a>
                      [#if editable]<span class="listButton remove popUpValidation">[@s.text name="form.buttons.remove" /]</span>[/#if] 
                    </li>
                  [/#list]
                [#else]
                  <p class="emptyText"> [@s.text name="projectDescription.${((project.bilateralProject)!false)?string('coreProjects','bilateralProjects')}.emptyText" /]</p>
                [/#if]  
                </ul>
                [#if editable ]
                
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="" keyFieldName="id" displayFieldName="" className="" value="" /]
                [/#if] 
              </div>
            </div>
          </div> 
            
            
            --]
           
          
          [#-- Project identifier --]
          <input name="projectID" type="hidden" value="${project.id}" />
          
          [#-- Section Buttons--]
          <div class="buttons">
            <div class="buttons-content">
              [#-- History Log --]
              [#if action.getListLog(project)?has_content]
                [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
                [@logHistory.logList list=action.getListLog(project) itemId=project.id /]
                <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
              [/#if]
              [#if editable]
                [#-- Back Button --]
                <a href="[@s.url][@s.param name="projectID" value=projectID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
                [#-- Cancel Button --]                
                [@s.submit type="button" cssStyle="display:${draft?string('inline-block','none')}"   name="cancel" cssClass="button-cancel"]<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> [@s.text name="form.buttons.discard" /] [/@s.submit]
                [#-- Save Button --]
                [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] <span class="draft">${draft?string('(Draft Version)','')}</span>[/@s.submit]
              [#elseif canEdit]
                [#-- Back Button --]
                <a href="[@s.url][@s.param name="projectID" value=projectID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
              [/#if]
            </div>
          </div>
             
         
          [/@s.form] 
      </div>
    </div>  
</section>

[#-- Hidden values used by js --]
<input id="crpProgramID" value="${project.crp.id}" type="hidden"/>

[#-- Core project list template --]
<ul style="display:none">
  <li id="cpListTemplate" class="clearfix">
    <input class="id" type="hidden" name="" value="" />
    <span class="name"></span> 
    <span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>
  </li>
</ul>

[#-- Remove project contribution popup --]
<div id="removeContribution-dialog" title="Remover project contribution" style="display:none">
  <ul class="messages"><li>[@s.text name="projectDescription.removeContributionDialog" /]</li></ul>
</div>

[#-- File upload Template--] 
[@customForm.inputFile name="file" template=true /] 
[@customForm.inputFile name="fileReporting" template=true /] 
  
[#include "/WEB-INF/global/pages/footer.ftl"]
