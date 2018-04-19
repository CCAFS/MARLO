[#ftl]
[#assign title = "Project Outcome Case Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${caseStudyID}-phase-${(actualPhase.id)!}" /]
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

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


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
            <a href="[@s.url action='${crpSession}/studies'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectStudies.backProjectStudies" /]
            </a>
          </small>
          
          <strong>STUDY NEW!</strong>
          
          [#-- Outcome case studies list --]
          <h3 class="headTitle">[@s.text name="projectStudies.caseStudyInformation" /]</h3>
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

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- -- MACROS -- --]

[#macro caseStudyMacro element name index=-1 template=false]
  [#local customName = "${name}"/]
  [#local customId = "caseStudy-${template?string('template',index)}" /]
  
  <div id="${customId}" class="caseStudy" style="display:${template?string('none','block')}">
    <div class="borderBox">
      [#-- Case study ID --]
      <input type="hidden" name="${customName}.id" class="caseStudyID" value="${(element.id)!}"/>
      
      [#-- 1. Title (up to 20 words) --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" i18nkey="study.title" help="study.title.help" className="limitWords-20" required=true editable=editable /]
      </div>
      
      [#-- 2. Short outcome/impact statement (up to 80 words) --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.outcomeStatement" i18nkey="study.outcomeStatement" help="study.outcomeStatement.help" className="limitWords-80" required=true editable=editable /]
      </div>

      [#-- 3. Link to Common Results Reporting Indicator #I3 --]
      <div class="form-group">
        [#-- Does this outcome reflect a contribution of the CGIAR in influencing or modifying policies/ strategies / laws/ regulations/ budgets/ investments or  curricula?  --]
        <label for="">[@s.text name="study.reportingIndicatorThree" /]:</label>
        [@customForm.radioFlat id="reportingIndicatorThree-yes" name="${name}.reportingIndicatorThree" label="Yes" value="true" checked=false cssClass="" cssClassLabel="radio-label-yes"/]
        [@customForm.radioFlat id="reportingIndicatorThree-no" name="${name}.reportingIndicatorThree" label="No" value="false" checked=false cssClass="" cssClassLabel="radio-label-no"/]
        
        <div class="form-group">
          Insert link to loop on disaggregates for I3 here.
        </div>
      </div>
      
      [#-- 4.  Maturity of change reported (tick-box)  --]
      <div class="form-group">
        <label for="">[@s.text name="study.maturityChange" /]:</label>
        <p><small>[@s.text name="study.maturityChange.help" /]</small></p>
        <div class="form-group">
          [@customForm.radioFlat id="maturityChange-1" name="${name}.maturityChange" label="Stage 1" value="1" checked=false cssClass="" cssClassLabel=""/] <br />
          [@customForm.radioFlat id="maturityChange-2" name="${name}.maturityChange" label="Stage 2" value="2" checked=false cssClass="" cssClassLabel=""/] <br />
          [@customForm.radioFlat id="maturityChange-3" name="${name}.maturityChange" label="Stage 3" value="3" checked=false cssClass="" cssClassLabel=""/] 
        </div>
      </div>

      [#-- 5. Links to the Strategic Results Framework  --]
      <div class="form-group">
        <label for="">[@s.text name="study.stratgicResultsLink" /]:</label>
        <p><small>[@s.text name="study.stratgicResultsLink.help" /]</small></p>
        
        [#-- 5. Links to the Strategic Results Framework  --]
        <div class="form-group">
          a) Sub-IDOs
        </div>
        <div class="form-group">
          b) SRF 2022/2030 targets 
        </div>
        <div class="form-group">
          c) Comment box 
        </div>
      </div>
      
      [#-- 6.  Geographic scope - Countries  --]
      <div class="form-group">
        6. Geographic scope - Countries (tick box and optional comment box)
      </div>

      [#-- 7. Key Contributors  --]
      <div class="form-group">
        7. Key Contributors
      </div>

      [#-- 8. Elaboration of Outcome/Impact Statement  --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.elaborationStatement" i18nkey="study.elaborationStatement" help="study.elaborationStatement.help" className="limitWords-400" required=true editable=editable /]
      </div>
      
      [#-- 9. References cited  --]
      <div class="form-group">
        9. References cited
      </div>
      
      [#-- 10. Quantification (where data is available)  --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.quantification" i18nkey="study.quantification" help="study.quantification.help" className=" " required=true editable=editable /]
      </div>
      
      [#-- 11. Gender, Youth, and Capacity Development  --]
      <div class="form-group">
        11. Gender, Youth, and Capacity Development 
      </div>
      
      [#-- 12. Other cross-cutting dimensions   --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.otherCrossCutting" i18nkey="study.otherCrossCutting" help="study.otherCrossCutting.help" className="limitWords-100" required=true editable=editable /]
      </div>
      
      [#-- 13. Communications materials    --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.communicationMaterials" i18nkey="study.communicationMaterials" help="study.communicationMaterials.help" className=" " required=true editable=editable /]
      </div>

      [#-- 14. Contact person    --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.contacts" i18nkey="study.contacts" help="study.contacts.help" className=" " required=true editable=editable /]
      </div>
    </div>
    
    <h3 class="headTitle"> Share Study </h3>
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
