[#ftl]
[#assign title = "Project Outcome Case Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${caseStudyID}" /]
[#assign pageLibs = [ "select2" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectCaseStudy.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectCaseStudies.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "caseStudies" /]


[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"caseStudies", "nameSpace":"/projects", "action":"${(crpSession)!}/caseStudies", "param": "projectID=${projectID}"},
  {"label":"caseStudy", "nameSpace":"/projects", "action":""}
] /]

[#assign params = {
  "caseStudies": {"id":"caseStudiesName", "name":"project.caseStudies"}
  }
/] 

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]


<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectContributionsCrpList.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-caseStudy.ftl" /]
        
        [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ]  

          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/caseStudies'][@s.param name="projectID" value=project.id /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project outcome case study
            </a>
          </small>
           
          <h3 class="headTitle">[@s.text name="projectCaseStudies.caseStudyInformation" /]</h3>
          
          [#-- Outcome case studies list --]
          <div id="caseStudiesBlock" class="">
            [@caseStudyMacro element=caseStudy name="caseStudy" index=0 /]
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projectOutcomesCaseStudies.ftl" /]
         
        [/@s.form]
  
      </div>
      
      
    </div>  
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]

[#-- File upload Template--] 
[@customForm.inputFile name="annexesFile" className="annexesFile"  template=true /]

[@shareOutcomeCaseStudy element={} name="caseStudy.projects" index=-1 template=true /]

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#-- -- MACROS -- --]

[#macro caseStudyMacro element name index=-1 template=false]
  [#local customName = "${name}"/]
  
  [#local customId = "caseStudy-${template?string('template',index)}" /] 
  <div id="${customId}" class="caseStudy" style="display:${template?string('none','block')}">
    <div class="borderBox">
      [#-- Case study ID --]
      <input type="hidden" name="${customName}.id" class="caseStudyID" value="${(element.id)!}"/>
      [#-- 1. Title --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" i18nkey="caseStudy.title" help="caseStudy.title.help" className="caseStudyTitle limitWords-15" required=true editable=editable /]
      </div>
      [#-- 2. Outcome statement --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.outcomeStatement" i18nkey="caseStudy.outcomeStatement" help="caseStudy.outcomeStatement.help" className="caseStudyOutcomeStatement limitWords-80" required=true editable=editable /]
      </div>
      [#-- 3. Research Outputs --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.researchOutputs" i18nkey="caseStudy.researchOutput" help="caseStudy.researchOutput.help" className="caseStudyResearchOutput limitWords-150" required=true editable=editable /]
      </div> 
      [#-- 4.  Research partners --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.researchPartners" i18nkey="caseStudy.researchPartners" help="caseStudy.researchPartners.help" className="caseStudyResearchPartners limitWords-150" required=true editable=editable /]
      </div>
      [#-- 5. Activities that contributed to the outcome --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.activities" i18nkey="caseStudy.activitiesContributed" help="caseStudy.activitiesContributed.help" className="caseStudyActivitiesContributed limitWords-150" required=true editable=editable /]
      </div>
      [#-- 6. Non-research partners --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.nonResearchPartneres" i18nkey="caseStudy.nonResearchPartners" help="caseStudy.nonResearchPartners.help" className="caseStudyNonResearchPartners limitWords-80" required=true editable=editable /]
      </div>
      [#-- 7. Output Users --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.outputUsers" i18nkey="caseStudy.outputUsers" help="caseStudy.outputUsers.help" className="caseStudyOutputUsers limitWords-50" required=true editable=editable /]
      </div>
      [#-- 8. How the output was used --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.outputUsed" i18nkey="caseStudy.outputUsed" help="caseStudy.outputUsed.help" className="caseStudyOutputUsed limitWords-50" required=true editable=editable /]
      </div>
      [#-- 9. Evidence of the outcome --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.evidenceOutcome" i18nkey="caseStudy.evidence" help="caseStudy.evidence.help" className="caseStudyEvidence limitWords-50" required=true editable=editable /]
      </div>
      [#-- 10. References --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.referencesCase" i18nkey="caseStudy.references" help="caseStudy.references.help" className="caseStudyReferences limitWords-150" required=true editable=editable /]
      </div>
      
      [#-- Primary 2019 outcome indicator that this case study is contributing to: --]
      [#if phaseOne]
      <div class="form-group" > 
        <div class="fullBlock caseStudyIndicators ${customForm.changedField('caseStudy.indicators')}">
          <label for="${customName}.caseStudyIndicators">[@customForm.text name="caseStudy.caseStudyIndicators" readText=!editable /]:[@customForm.req required=editable /]</label>
          <div class="checkboxGroup">
          [#if editable]
            [@s.fielderror cssClass="fieldError" fieldName="${customName}.caseStudyIndicatorsIds"/]
            [@s.checkboxlist name="${customName}.caseStudyIndicatorsIds" list="caseStudyIndicators" value="${customName}.caseStudyIndicatorsIds" itemKey="id"    cssClass="caseStudyIndicators checkbox" /]
          [#else]
            [#if (element.indicators?has_content)!false]
              [#list element.indicators as element]<p class="checked">${element.ipIndicator.description}</p>[/#list]
            [#else]
              <div class="select"><p>Field is empty</p></div>
            [/#if]
          [/#if]
          </div>   
        </div>
        [@customForm.textArea name="${customName}.explainIndicatorRelation" i18nkey="caseStudy.explainIndicatorRelation" className="caseStudyExplainIndicatorRelation limitWords-50" editable=editable /]
      </div>
      [#else]
      <div class="form-group"> 
        <div class="fullBlock caseStudyIndicators">
          <label for="${customName}.caseStudyIndicators">Please select the respective 2022 outcome that this case study is contributing to:[@customForm.req required=editable /]</label>
          <p>{Checkbox list of 2022 outcomes}</p>
        </div>
        [@customForm.textArea name="${customName}.explainIndicatorRelation" i18nkey="caseStudy.explainIndicatorRelation" className="caseStudyExplainIndicatorRelation limitWords-50" editable=editable /]
      </div>
      [/#if]
  
      <div class="form-group">
        [#-- Year --]
        <div class="col-md-6">
          <div class="select">
            <label>[@s.text name="caseStudy.caseStudyYear" /]:</label>
            <div class="selectList"><p>${(element.year)!currentCycleYear}</p></div>
            <input type="hidden" name="${customName}.year" class="caseStudyYear" value="${(element.year)!currentCycleYear}" />
          </div>  
        </div>
        [#-- Upload Annexes --]
        <div class="col-md-6 fileUpload uploadAnnexes">
          <label>[@customForm.text name="caseStudy.uploadAnnexes" readText=!editable /]:</label>
          <div class="uploadContainer">
            [#if (element.file?has_content)!false]
              [#if editable]<span id="remove-annexesFile" class="remove"></span>[/#if] 
              
              <p><a href="${(CaseStudyUrl)!}${element.file.fileName}">${element.file.fileName}</a><input type="hidden" name="${customName}.file.id" value="${element.file.id}" /> </p>
            [#else]
              [#if editable]
                [@customForm.inputFile name="file" className="annexesFile"  /]
              [#else]  
                [@s.text name="form.values.notFileUploaded" /]
              [/#if] 
            [/#if]
          </div>
        </div>
      </div>
      
    </div>
    
    <h3 class="headTitle"> Share Outcome Case Study </h3>
    <div class="borderBox">
      [#-- Projects shared --]
      <div id="gender-levels" class="panel tertiary col-md-12">
       <div class="panel-head"><label for=""> This outcome study is done jointly with the following project(s), please select below: </label></div>
        <div id="myProjectsList" class="panel-body" listname="deliverable.genderLevels"> 
          <ul class="list">
          [#if element.projects?has_content ]
            [#list element.projects as projectLink]
              [@shareOutcomeCaseStudy element=projectLink name="caseStudy.projects" index=projectLink_index template=false /]
            [/#list]
          [#else]
            <p class="emptyText"> [@s.text name="caseStudy.projects.empty" /]</p> 
          [/#if]  
          </ul>
          [#if editable ]
            [@customForm.select name="" label="" keyFieldName="id"  displayFieldName="composedName" showTitle=false i18nkey="" listName="myProjects"   required=true  className="projects" editable=editable/]
          [/#if] 
        </div>
      </div>
    </div>
  </div>
[/#macro]

[#macro shareOutcomeCaseStudy element name index=-1 template=false]
[#local own = (!template) && (element.project.id == projectID) /]
<li id="sharedProject-${template?string('template', index)}" class="sharedProject ${own?string('hide','')} clearfix" style="display:${template?string('none','block')}">
  [#-- Remove button --]
  [#if editable]<div class="removeProject removeIcon" title="Remove Project"></div>[/#if] 
  
  [#-- Hidden inputs --]
  <input class="id" type="hidden" name="${name}[${index}].id" value="${(element.id)!}" />
  <input class="projectId" type="hidden" name="${name}[${index}].project.id" value="${(element.project.id)!}" />
  
  [#-- title --]
  <span title="${(element.project.title)!'undefined'}" class="name">${(element.project.composedName)!'undefined'}</span>
  <div class="clearfix"></div>
</li>
[/#macro]
