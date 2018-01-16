[#ftl]
[#assign title = "Project Outcomes" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectOutcomes.js", 
  "${baseUrl}/global/js/autoSave.js", 
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [ ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "projectOutcomes" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectOutcomes", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]
[#assign fieldEmpty]<div class="select"><p>[@s.text name="form.values.fieldEmpty" /]</p></div>[/#assign]
[#-- List of years --]
[#assign years= [2019, currentCycleYear, currentCycleYear+1] /]
[#assign newProject = action.isProjectNew(projectID) /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectOutcomes.help1" ] [@s.param] <a href="${baseUrl}/glossary.do" target="_blank">Glossary</a>[/@s.param][/@s.text]</p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectOutcomes.title" /]</h3>  
          
          [#if project.startDate??]
          <div id="projectOutcomes" class="borderBox">
            
            [#assign canEditStatement = action.hasPermission("statement") /]
            [#-- Project  outcome block --]
            <div class="fullPartBlock clearfix"> 
              [#-- Project Outcome statement --]
              <div class="fullPartBlock" id="projectOutcomeStatement">
             [#assign index2019 = (action.getIndex(2019))!-1 /]
                [@customForm.textArea name="project.outcomesPandr[${index2019}].statement" className="limitWords-150" i18nkey="projectOutcomes.statement" editable=false /]
                <input name="project.outcomesPandr[${index2019}].id" type="hidden" value="${(project.outcomesPandr[index2019].id?c)!}" />
               <input name="project.outcomesPandr[${index2019}].year" type="hidden" value="2019" />
        
              </div>
              [#-- Annual progress --]
              [#list project.startDate?string.yyyy?number..2019?number-1 as year]
              
              <span class="label label-default">${year}</span>
               [#assign indexYear = (action.getIndex(year))!-1 /]
               [#assign outcome = (action.getOutcome(year))!{} /]
                [#assign yearEditable = false /]
                [#assign yearRequired = !project.bilateralProject && ((year == currentCycleYear) || (year == currentCycleYear+1)) /]
                <div class="fullPartBlock">
                  <label>[@customForm.text name="projectOutcomes.annualProgress" readText=!editable param="${year}" /]:[@customForm.req required=yearRequired && editable /]</label>
                  [@customForm.textArea name="project.outcomesPandr[${indexYear}].statement" required=yearRequired && editable className="limitWords-150" showTitle=false editable=yearEditable /]
                </div>
                [#-- -- -- REPORTING BLOCK -- -- --]
                [#if reportingActive && (year == currentCycleYear) ]
                <div class="fieldFocus">
                  <div class="fullPartBlock bs-callout bs-callout-info " style="padding-bottom:20px !important;">
                    <label>[@customForm.text name="projectOutcomes.annualProgressCurrentReporting" readText=!editable param="${year}" /]:[@customForm.req required=editable /]</label>
                    [@customForm.textArea name="project.outcomesPandr[${indexYear}].anualProgress" required=editable className="limitWords-300" showTitle=false editable=editable && action.hasPermission("annualProgress") /]
                  </div>
                  
                  [#-- Comunication and engagement activities --]
                  <div class="fullPartBlock bs-callout bs-callout-info " style="padding-bottom:20px !important;">
                    [@customForm.textArea name="project.outcomesPandr[${indexYear}].comunication" className="limitWords-100" i18nkey="projectOutcomes.commEngagementOutcomes" required=true editable=editable && action.hasPermission("communicationEngagement") /]
                  </div>
                  
                  [#-- Upload summary--]
                  <div class="fullPartBlock fileUpload uploadSummary bs-callout bs-callout-info ">
                    <label>[@customForm.text name="projectOutcomes.uploadSummary" readText=!editable /]:</label>
                    <div class="uploadContainer" title="[@s.text name="projectOutcomes.uploadSummary.help" /]">
                      [#if (outcome.file?has_content)!false]
                        [#if editable]<span id="remove-file" class="remove"></span>[#else]<span class="file"></span>[/#if] 
                        <p><a href="${action.getProjectOutcomeUrl()}${((outcome.file.fileName))!}">${(outcome.file.fileName)!}</a></p>
                         <input id="fileID" type="hidden" name="project.outcomesPandr[${indexYear}].file.id" value="${(outcome.file.id)!}" />
                      [#else]
                        [#if editable && action.hasPermission("uploadSummary")]
                          [@customForm.inputFile name="file"  /]
                        [#else]  
                          [@s.text name="form.values.notFileUploaded" /]
                        [/#if] 
                      [/#if]
                    </div>  
                  </div>
                </div>
                [/#if]
                  <input name="project.outcomesPandr[${indexYear}].id" type="hidden" value="${(project.outcomesPandr[indexYear].id?c)!}" />
               <input name="project.outcomesPandr[${indexYear}].year" type="hidden" value="${year}" />
        
              [/#list]
               </div>
          </div>
          [#else]
            <p class="simpleBox center">[@s.text name="projectOutcomes.message.dateUndefined" /]</p>
          [/#if]
          
          
          [#-- Lessons learnt --]
          [#if !newProject]
          <div id="lessons" class="borderBox fieldFocus">
            [#-- Lessons learnt from last planning/reporting cycle --]
               [#if (project.projectComponentLessonPreview.lessons?has_content)!false]
            <div class="fullBlock">
               <label>[@customForm.text name="projectPartners.previousLessons.${reportingActive?string('reporting','planning')}" param="${reportingActive?string(reportingYear,planningYear-1)}" /]:</label>
                    <div class="textArea "><p>${project.projectComponentLessonPreview.lessons}</p></div>
                 
            </div>
            [/#if]
            [#-- Planning/Reporting lessons --]
            <div class="fullBlock">
              <input type="hidden" name="project.projectComponentLesson.id" value=${(project.projectComponentLesson.id)!"-1"} />
                    <input type="hidden" name="project.projectComponentLesson.year" value=${reportingActive?string(reportingYear,planningYear)} />
                    <input type="hidden" name="project.projectComponentLesson.componentName" value="${actionName}">
                    [@customForm.textArea name="project.projectComponentLesson.lessons" i18nkey="projectOutcomes.lessons.${reportingActive?string('reporting','planning')}" required=!project.bilateralProject editable=editable /]
                 </div>
          </div>
          [/#if]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
          
        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]

[#-- File upload Template--]
[@customForm.inputFile name="file" fileUrl="" fileName="project.outcomesPandr[${currentCycleYear}].file.id" template=true /]

[#include "/WEB-INF/crp/pages/footer.ftl"]


[#-- Get if the year is required--]
[#function isYearRequired year]
  [#if project.endDate??]
    [#assign endDate = (project.endDate?string.yyyy)?number]
    [#if reportingActive]
      [#return  (year == currentCycleYear)  && (endDate gte year) ]
    [#else]
      [#return  (year == currentCycleYear) && (endDate gte year) ]
    [/#if]
  [#else]
    [#return false]
  [/#if]
[/#function]