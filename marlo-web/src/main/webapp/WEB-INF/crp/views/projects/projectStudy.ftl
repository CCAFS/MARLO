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
      [#-- Hidden ID --]
      <input type="hidden" name="${customName}.id" class="caseStudyID" value="${(element.id)!}"/>
      
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.select name="" className="setSelect2" i18nkey="study.type" listName="" keyFieldName="id"  displayFieldName="name" /]
        </div>
        <div class="col-md-6">
          [@customForm.select name="" className="setSelect2" i18nkey="study.status" listName="" keyFieldName="id"  displayFieldName="name" /]
        </div>
      </div>
    </div>
    <div class="borderBox">
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
        <div class="form-group">
          <label for="">[@s.text name="study.reportingIndicatorThree" /]:[@customForm.req required=editable /]</label>
          <p><small>[@s.text name="study.reportingIndicatorThree.help" /]</small></p>
          [@customForm.radioFlat id="reportingIndicatorThree-yes" name="${name}.reportingIndicatorThree.value" label="Yes" value="true" checked=false cssClass="" cssClassLabel="radio-label-yes"/]
          [@customForm.radioFlat id="reportingIndicatorThree-no" name="${name}.reportingIndicatorThree.value" label="No" value="false" checked=false cssClass="" cssClassLabel="radio-label-no"/]
        </div>
        
        [#-- Disaggregates for CGIAR Indicator I3  --]
        <div class="form-group simpleBox">
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Policy/Investment Type --]
              [@customForm.select name="${customName}.reportingIndicatorThree.policyType.id" className="setSelect2" i18nkey="study.reportingIndicatorThree.policyType" listName="" keyFieldName="id"  displayFieldName="name" required=true /]
            </div>
            <div class="col-md-6">
              [#-- Amount (Only for Budget or Investment) --]
              [@customForm.input name="${customName}.reportingIndicatorThree.amount" i18nkey="study.reportingIndicatorThree.amount" help="study.reportingIndicatorThree.amount.help" className="" required=true editable=editable /]
            </div>
          </div>
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Implementing Organization Type --]
              [@customForm.select name="${customName}.reportingIndicatorThree.organizationType.id" className="setSelect2" i18nkey="study.reportingIndicatorThree.organizationType" listName="" keyFieldName="id"  displayFieldName="name" required=true /]
            </div>
            <div class="col-md-6">
              [#-- Stage in Process --]
              [@customForm.select name="${customName}.reportingIndicatorThree.stage.id" className="setSelect2" i18nkey="study.reportingIndicatorThree.stage" listName="" keyFieldName="id"  displayFieldName="name"required=true  /]
            </div>
          </div>
        </div>
      </div>
      
      [#-- 4.  Maturity of change reported (tick-box)  --]
      <div class="form-group">
        <label for="">[@s.text name="study.maturityChange" /]:[@customForm.req required=editable /]</label>
        <p><small>[@s.text name="study.maturityChange.help" /]  [@customForm.helpLabel name="study.maturityChange.help2" showIcon=true editable=editable/]</small></p>
        <div class="form-group">
          <p>[@customForm.radioFlat id="maturityChange-1" name="${name}.maturityChange" label="Stage 1" value="1" checked=false cssClass="" cssClassLabel=""/]</p> 
          <p>[@customForm.radioFlat id="maturityChange-2" name="${name}.maturityChange" label="Stage 2" value="2" checked=false cssClass="" cssClassLabel=""/]</p>
          <p>[@customForm.radioFlat id="maturityChange-3" name="${name}.maturityChange" label="Stage 3" value="3" checked=false cssClass="" cssClassLabel=""/]</p>
        </div>
      </div>

      [#-- 5. Links to the Strategic Results Framework  --]
      <div class="form-group">
        <label for="">[@s.text name="study.stratgicResultsLink" /]:[@customForm.req required=editable /]</label>
        <p><small>[@s.text name="study.stratgicResultsLink.help" /]</small></p>
        
        [#-- Sub IDOs  --]
        <div class="form-group simpleBox">
          [@customForm.select name="" className="setSelect2" i18nkey="study.stratgicResultsLink.subIDOs" listName="" keyFieldName="id"  displayFieldName="name" /]
          [#-- List --]
          <div class="panel tertiary">
            <div class="panel-body">
              <ul class="list">
                <li>Increased capacity for innovation in partner development organizations and in poor and vulnerable communities</li>
              </ul>
            </div>
          </div>
        </div>
        
        [#-- SRF Targets  --]
        <div class="form-group simpleBox">
          [@customForm.select name="" className="setSelect2" i18nkey="study.stratgicResultsLink.srfTargets" listName="" keyFieldName="id"  displayFieldName="name" /]
          [#-- List --]
          <div class="panel tertiary">
            <div class="panel-body">
              <ul class="list">
                <li>1.2. 30 million people, of which 50% are women, assisted to exit poverty</li>
                <li>3.1. 5% increase in water and nutrient efficiency in agroecosystems</li>
              </ul>
            </div>
          </div>
        </div>
        
        [#-- Comments  --]
        <div class="form-group simpleBox">
          [@customForm.textArea name="${customName}.stratgicResultsLink.comments" i18nkey="study.stratgicResultsLink.comments" help="study.stratgicResultsLink.comments.help" className="" editable=editable /]
        </div>
      </div>
      
      [#-- 6.  Geographic scope - Countries  --]
      <div class="form-group">
        [#assign isRegional = true ]
        [#assign isMultiNational = true ]
        [#assign isNational = true ]
        [#assign isSubNational = true ]
        
        <label for="">[@s.text name="study.geographicScopeTopic" /]:[@customForm.req required=editable /]</label>
        <div class="form-group simpleBox">
          <div class="form-group row">
            <div class="col-md-6">
              [#-- Geographic Scope --]
              [@customForm.select name="${customName}.geographicScope.id" className="setSelect2 geographicScopeSelect" i18nkey="study.geographicScope" listName="allRepIndGeographicScope" keyFieldName="id"  displayFieldName="name" required=true /]
            </div>
          </div>
          <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
            [#-- Regional scope --]
            [@customForm.select name="${customName}.region.id" className="setSelect2" i18nkey="study.region" listName="allRepIndRegions" keyFieldName="id"  displayFieldName="name" required=true /]
          </div>
          <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
            [#-- Multinational, National and Subnational scope --]
            [@customForm.select name="${customName}.countriesIds" label="" i18nkey="study.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${name}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
          </div>
          <div class="form-group">
            [#-- Comment box --]
            [@customForm.textArea name="${customName}.geographicScopeComments" className="limitWords-30" i18nkey="study.geographicScopeComments" help="study.geographicScopeComments.help" editable=editable required=true/]
          </div>
        </div>
      </div>

      [#-- 7. Key Contributors  --]
      <div class="form-group">
        <label for="">[@s.text name="study.keyContributors" /]:</label>
        <div class="form-group simpleBox">
          [@customForm.select name="" className="setSelect2" i18nkey="study.keyContributors.crps" listName="" keyFieldName="id"  displayFieldName="name" /]
          [#-- List --]
          <div class="panel tertiary">
            <div class="panel-body">
              <ul class="list">
                <li>A4NH - Agriculture for Nutrition and Health</li>
              </ul>
            </div>
          </div>
          
        </div>
        <div class="form-group simpleBox">
          [@customForm.select name="" className="setSelect2" i18nkey="study.keyContributors.flagships" listName="" keyFieldName="id"  displayFieldName="name" /]
          [#-- List --]
          <div class="panel tertiary">
            <div class="panel-body">
              <ul class="list">
                <li>F1 - Priorities and Policies for CSA</li>
                <li>F3 - Low emissions development</li>
              </ul>
            </div>
          </div>
          
        </div>
        <div class="form-group simpleBox">
          [@customForm.select name="" className="setSelect2" i18nkey="study.keyContributors.externalPartners" listName="" keyFieldName="id"  displayFieldName="name" /]
          [#-- List --]
          <div class="panel tertiary">
            <div class="panel-body">
              <ul class="list">
                <li>Ministries of Agriculture for Bangladesh</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      [#-- 8. Elaboration of Outcome/Impact Statement  --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.elaborationStatement" i18nkey="study.elaborationStatement" help="study.elaborationStatement.help" className="limitWords-400" required=true editable=editable /]
      </div>
      
      [#-- 9. References cited  --]
      <div class="form-group">
        <label for="">[@s.text name="study.referencesCited" /]:</label>
        <p><small>[@s.text name="study.referencesCited.help" /]</small></p>
      </div>
      
      [#-- 10. Quantification (where data is available)  --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.quantification" i18nkey="study.quantification" help="study.quantification.help" className=" " required=true editable=editable /]
      </div>
      
      [#-- 11. Gender, Youth, and Capacity Development  --]
      <div class="form-group">
        [#local ccDimensions = [
          { "name": "0 - Not Targeted" },
          { "name": "1 - Significant" },
          { "name": "2 - Principal" }
        ] /]
        <label for="">[@s.text name="study.crossCuttingRelevance" /]:</label>
        [#-- Gender --]
        <div class="simpleBox">
          [@customForm.textArea name="${customName}.achievementsGenderRelevance" i18nkey="study.achievementsGenderRelevance" help="study.achievementsGenderRelevance.help" className="limitWords-100" required=true editable=editable /]
          [#list ccDimensions as cc]
            [@customForm.radioFlat id="genderRelevance-${cc_index}" name="${name}.genderRelevance" label="${cc.name}" value="1" checked=false cssClass="" cssClassLabel="" /]
          [/#list]
        </div>
        [#-- Youth  --]
        <div class="simpleBox">
          [@customForm.textArea name="${customName}.achievementsYouthRelevance" i18nkey="study.achievementsYouthRelevance" help="study.achievementsYouthRelevance.help" className="limitWords-100" required=true editable=editable /]
          [#list ccDimensions as cc]
            [@customForm.radioFlat id="youthRelevance-${cc_index}" name="${name}.youthRelevance" label="${cc.name}" value="1" checked=false cssClass="" cssClassLabel="" /]
          [/#list]
        </div>
        [#-- CapDev   --]
        <div class="simpleBox">
          [@customForm.textArea name="${customName}.achievementsCapDevRelevance" i18nkey="study.achievementsCapDevRelevance" help="study.achievementsCapDevRelevance.help" className="limitWords-100" required=true editable=editable /]
          [#list ccDimensions as cc]
            [@customForm.radioFlat id="capDevRelevance-${cc_index}" name="${name}.capDevRelevance" label="${cc.name}" value="1" checked=false cssClass="" cssClassLabel="" /]
          [/#list]
        </div>
        
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
