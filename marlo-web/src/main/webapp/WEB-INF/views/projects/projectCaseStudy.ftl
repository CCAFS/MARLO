[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]


[#assign title = "Project Outcome Case Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = [ "select2" ] /]
[#assign customJS = ["${baseUrl}/js/projects/projectCaseStudy.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectCaseStudies.css"] /]
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

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectContributionsCrpList.help" /] </p>
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

        [@s.form action="caseStudies" cssClass="pure-form" enctype="multipart/form-data" ]  
         
          [#include "/WEB-INF/views/projects/dataInfo-projects.ftl" /] 
            
           
          <h3 class="headTitle">[@s.text name="projectCaseStudies.caseStudyInformation" /]</h3>
          [#assign projectCaseStudies = [
              { "id": "1",
                "title": "Succesful communications on the Projected Shifts in Coffea arabica Suitability among Major Global Producing Regions Due to Climate Change",
                "author":"Ovalle-Rivera O, Läderach P, Bunn C, Obersteiner M, Schroth G",
                "year":"2015",
                "subject":"Coffea arabica, climate change, productivity"
              }  
            ] /]
          
          [#-- Outcome case studies list --]
          <div id="caseStudiesBlock" class="">
          [#if projectCaseStudies?has_content ]
            [#list projectCaseStudies as item] 
              [@caseStudy element=item name="${params.caseStudies.name}" index=item_index /]
            [/#list]
          [#else]
              <p class="message center">There is not an Outcome Case Study added. 
              [#if !editable]
                <div class="editButton"><a href="[@s.url][@s.param name ="projectID"]${project.id}[/@s.param][@s.param name="edit"]true[/@s.param][/@s.url]">[@s.text name="form.buttons.edit" /]</a></div></a>
              [/#if]
              </p>
          [/#if]
          </div> 
         
        [/@s.form]
  
      </div>
      
      
    </div>  
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]

[#-- File upload Template--] 
[@customForm.inputFile name="annexesFile" className="annexesFile"  template=true /]

[#-- Case Study template --]
[@caseStudy element={} name="${params.caseStudies.name}" template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- -- MACROS -- --]

[#macro caseStudy element name index=-1 template=false]
  [#local customName = "${name}[${template?string('-1',index)}]"/]
  [#local study = (element)!{} /]
  [#local customId = "caseStudy-${template?string('template',index)}" /] 
  <div id="${customId}" class="caseStudy" style="display:${template?string('none','block')}">
    <div class="borderBox">
      [#-- Case study ID --]
      <input type="hidden" name="${customName}.id" class="caseStudyID" value="${(study.id)!-1}"/>
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
      [#-- Region indicators --]
      <div class="form-group"> 
        <div class="fullBlock caseStudyIndicators">
          <label for="${customName}.caseStudyIndicators">[@customForm.text name="caseStudy.caseStudyIndicators" readText=!editable /]:[@customForm.req required=editable /]</label>
          <div class="checkboxGroup">
          [#if editable]
            [@s.fielderror cssClass="fieldError" fieldName="${customName}.caseStudyIndicatorsIds"/]
            [@s.checkboxlist name="${customName}.caseStudyIndicatorsIds" list="caseStudyIndicators" value="${customName}.caseStudyIndicatorsIds" itemKey="id"    cssClass="caseStudyIndicators checkbox" /]
          [#else]
            [#if (study.caseStudyIndicators?has_content)!false]
              [#list study.caseStudyIndicators as element]<p class="checked">${element.description}</p>[/#list]
            [#else]
              <div class="select"><p>Field is empty</p></div>
            [/#if]
          [/#if]
          </div>   
        </div>
        [@customForm.textArea name="${customName}.explainIndicatorRelation" i18nkey="caseStudy.explainIndicatorRelation" className="caseStudyExplainIndicatorRelation limitWords-50" editable=editable /]
      </div>
  
      <div class="form-group">
        [#-- Year --]
        <div class="col-md-6">
          <div class="select">
            <label>[@s.text name="caseStudy.caseStudyYear" /]:</label>
            <div class="selectList"><p>${(study.year)!currentCycleYear}</p></div>
            <input type="hidden" name="${customName}.year" class="caseStudyYear" value="${(study.year)!currentCycleYear}" />
          </div>  
        </div>
        [#-- Upload Annexes --]
        <div class="col-md-6 fileUpload uploadAnnexes">
          <label>[@customForm.text name="caseStudy.uploadAnnexes" readText=!editable /]:[@customForm.req required=editable /]</label>
          <div class="uploadContainer">
            [#if (study.file?has_content)!false]
              [#if editable]<span id="remove-annexesFile" class="remove"></span>[/#if] 
              <p><a href="${CaseStudyURL}${study.file}">${study.file}</a><input type="hidden" name="${customName}.file" value="${study.file}" /> </p>
            [#else]
              [#if editable]
                [@customForm.inputFile name="${customName}.myFile" className="annexesFile"  /]
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
        <div id="genderLevelsList" class="panel-body" listname="deliverable.genderLevels"> 
          <ul class="list">
          [#if study.projects?has_content]
            [#list study.projects as projectLink]
              <li class="genderLevel clearfix">
                [#-- Remove button --]
                [#if editable]<div class="removeGenderLevel removeIcon" title="Remove Gender Level"></div>[/#if] 
                [#-- Hidden inputs --]
                <input class="id" type="hidden" name="deliverable.genderLevels[${project_index}].id" value="${(projectLink.id)!}" />
                <input class="projectId" type="hidden" name="caseStudy.genderLevels[${project_index}].projectLink" value="${(projectLink.project)!}" />
                [#-- title --]
                <span title="${(project.title)!'undefined'}" class="name">[@utils.wordCutter string=(project.title)!"undefined" maxPos=100 substr=" "/]</span>
                <div class="clearfix"></div>
              </li>
            [/#list]
          [#else]
            <p class="emptyText"> [@s.text name="caseStudy.projects.empty" /]</p> 
          [/#if]  
          </ul>
          [#if editable ]
            [@customForm.select name="" label="" showTitle=false i18nkey="" listName="projectsList"   required=true  className="" editable=editable/]
          [/#if] 
        </div>
      </div>
    </div>
  </div>
[/#macro]
  
