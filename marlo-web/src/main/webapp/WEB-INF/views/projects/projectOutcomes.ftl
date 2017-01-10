[#ftl]
[#assign title = "Project Outcomes" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = [ "${baseUrl}/js/global/autoSave.js", "${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = [ ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "projectOutcomes" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectOutcomes", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign startYear = (project.startDate?string.yyyy)?number /]
[#assign endYear = (project.endDate?string.yyyy)?number /]
[#assign fieldEmpty]<div class="select"><p>[@s.text name="form.values.fieldEmpty" /]</p></div>[/#assign]
[#-- List of years --]
[#assign years= [2019, currentCycleYear, currentCycleYear+1] /]
[#assign newProject = action.isProjectNew(projectID) /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectCcafsOutcomes.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
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
          
          <h3 class="headTitle">[@s.text name="projectOutcomes.title" /]</h3>  
          
          [#if project.startDate??]
          <div id="projectOutcomes" class="borderBox">
            
            [#assign canEditStatement = action.hasPermission("statement") /]
            [#-- Project  outcome block --]
            <div class="fullPartBlock clearfix"> 
              [#-- Project Outcome statement --]
              <div class="fullPartBlock" id="projectOutcomeStatement">
             [#assign index2019 = (action.getIndex(2019))!-1 /]
                [@customForm.textArea name="project.outcomesPandr[${index2019}].statement" className="limitWords-150" i18nkey="projectOutcomes.statement" editable=(editable && canEditStatement) /]
              </div>
              [#-- Annual progress --]
              [#list project.startDate?string.yyyy?number..2019?number-1 as year]
              
              <span class="label label-default">${year}</span>
               [#assign indexYear = (action.getIndex(year))!-1 /]
               [#assign outcome = (action.getOutcome(year))!{} /]
                [#assign yearEditable = editable && (year gte currentCycleYear?number) && canEditStatement /]
                [#assign yearRequired = !project.bilateralProject && ((year == currentCycleYear) || (year == currentCycleYear+1)) /]
                <div class="fullPartBlock">
                  <label>[@customForm.text name="projectOutcomes.annualProgress" readText=!editable param="${year}" /]:[@customForm.req required=yearRequired && editable /]</label>
                  [@customForm.textArea name="project.outcomesPandr[${indexYear}].statement" required=yearRequired && editable className="limitWords-150" showTitle=false editable=yearEditable /]
                </div>
                [#-- -- -- REPORTING BLOCK -- -- --]
                [#if reportingActive && (year == currentCycleYear) ]
                <div class="fullPartBlock bs-callout bs-callout-info">
                  <label>[@customForm.text name="projectOutcomes.annualProgressCurrentReporting" readText=!editable param="${year}" /]:[@customForm.req required=editable /]</label>
                  [@customForm.textArea name="project.outcomesPandr[${indexYear}].anualProgress" required=editable className="limitWords-300" showTitle=false editable=editable && action.hasPermission("annualProgress") /]
                </div>
                
                [#-- Comunication and engagement activities --]
                <div class="fullPartBlock bs-callout bs-callout-info">
                  [@customForm.textArea name="project.outcomesPandr[${indexYear}].comunication" className="limitWords-100" i18nkey="projectOutcomes.commEngagementOutcomes" required=true editable=editable && action.hasPermission("communicationEngagement") /]
                </div>
                
                [#-- Upload summary--]
                <div class="fullPartBlock fileUpload uploadSummary bs-callout bs-callout-info">
                  <label>[@customForm.text name="projectOutcomes.uploadSummary" readText=!editable /]:</label>
                  <div class="uploadContainer" title="[@s.text name="projectOutcomes.uploadSummary.help" /]">
                    [#if (outcome.file?has_content)!false]
                      [#if editable]<span id="remove-file" class="remove"></span>[#else]<span class="file"></span>[/#if] 
                      <p><a href="${ProjectOutcomeURL}${((outcome.file.fileName))!}">${(outcome.file.fileName)!}</a></p>
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
                
                [/#if]
              [/#list]
             <input name="project.outcomesPandr[${indexYear}].id" type="hidden" value="${project.outcomesPandr[indexYear].id?c}" />
            </div>
          </div>
          [#else]
            <p class="simpleBox center">[@s.text name="projectOutcomes.message.dateUndefined" /]</p>
          [/#if]
          
          
          [#-- Lessons learnt --]
          [#if !newProject]
          <div id="lessons" class="borderBox">
            [#-- Lessons learnt from last planning/reporting cycle --]
            [#if (projectLessonsPreview.lessons?has_content)!false]
            <div class="fullBlock">
              <h6>[@customForm.text name="${currentSection}.projectOutcomes.previousLessons" param="${currentCycleYear}" /]:</h6>
              <div class="textArea "><p>${projectLessonsPreview.lessons}</p></div>
            </div>
            [/#if]
            [#-- Planning/Reporting lessons --]
            <div class="fullBlock">
              <input type="hidden" name="projectLessons.id" value=${(projectLessons.id)!"-1"} />
              <input type="hidden" name="projectLessons.year" value=${currentCycleYear} />
              <input type="hidden" name="projectLessons.componentName" value="${actionName}">
              [@customForm.textArea name="projectLessons.lessons" i18nkey="projectOutcomes.lessons" editable=editable /]
            </div>
          </div>
          [/#if]
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
          
          
        [/@s.form] 
      </div>
    </div>  
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]


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