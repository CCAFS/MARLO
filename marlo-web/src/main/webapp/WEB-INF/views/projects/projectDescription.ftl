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
              <div class="fullBlock">
                [@customForm.textArea name="project.title" required=true className="project-title" editable=editable /]
              </div>
              <div class="fullBlock">
                [#-- Project Program Creator --]
                <div class="halfPartBlock">
                  [@customForm.select name="project.liaisonInstitution" disabled=!editable  listName="liaisonInstitutions" keyFieldName="id"  displayFieldName="composedName" required=true editable=editable /]
                  <input type="hidden" name="project.liaisonInstitution.id" value="${(project.liaisonInstitution.id)!}" />
                </div>
                [#--  Project Owner Contact Person --]
                <div class="halfPartBlock">
                  [@customForm.select name="project.liaisonUser.id" i18nkey="project.liaisonUser"   listName="allOwners" keyFieldName="id"  displayFieldName="user.composedName" required=true editable=editable /]
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
                [#if action.hasPermission("workplan") ]
                  [@customForm.checkbox name="project.requiresWorkplanUpload" value="true" checked=project.requiresWorkplanUpload  i18nkey="project.workplanRequired" disabled=!editable editable=editable /]
                [/#if]
                <div class="tickBox-toggle uploadContainer" [#if !(project.requiresWorkplanUpload)]style="display:none"[/#if]>
                  <div class="halfPartBlock fileUpload projectWorkplan">
                    [@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplanName" editable=editable /]
                  </div> 
                </div>  
              </div>
              [/#if]
      
              [#-- Project upload bilateral contract --]
              [#if (project.bilateralProject)!false && action.hasPermission("bilateralContract") ]
              <div class="fullBlock fileUpload bilateralContract">
                <h6>[@customForm.text name="projectDescription.uploadBilateral" readText=!editable /]:</h6>
                <div class="uploadContainer">
                  [@customForm.inputFile name="file" fileUrl="${(bilateralContractURL)!}" fileName="project.bilateralContractName" editable=editable /]
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
                    [@customForm.inputFile name="fileReporting" fileUrl="${(AnualReportURL)!}" fileName="project.annualReportToDornor" editable=editable /]
                  </div>  
                </div>
                [/#if]
               
            
              
              [#--  Regions/global and Flagships that the project is working on --]
              <h5>[@customForm.text name="projectDescription.projectWorking" readText=!editable /]:
             
              <div id="projectWorking" class="fullBlock clearfix">
                [#-- Flagships --] 
                <div id="projectFlagshipsBlock" class="">
                  <div class="col-md-12">  
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
                  <div class="clearfix"></div>
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
            --]
            
            </div>
          </div> 
           
          
          [#-- Project identifier --]
          <input name="projectID" type="hidden" value="${project.id}" />
          <input type="hidden"  name="className" value="${(project.class.name)!}"/>
          <input type="hidden"  name="id" value="${(project.id)!}"/>
          <input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
          <input type="hidden"  name="actionName" value="${(actionName)!}"/>  
          
          [#-- Section Buttons--]
          <div class="buttons">
            <div class="buttons-content">
              [#-- History Log --]
              [#if action.getListLog(project)?has_content]
                [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
                [@logHistory.logList list=action.getListLog(project) itemName="projectID" itemId=project.id /]
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
[@customForm.inputFile name="file" fileUrl="${(workplanURL)!}" fileName="project.workplanName" template=true /]

[@customForm.inputFile name="fileReporting" template=true /] 
  
[#include "/WEB-INF/global/pages/footer.ftl"]
