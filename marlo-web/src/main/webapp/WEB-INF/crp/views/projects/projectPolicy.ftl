[#ftl]

[#assign canEdit = true /]
[#assign editable = true /]


[#assign title = "Project Policy" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${policyID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "select2", "blueimp-file-upload" "flat-flags", "components-font-awesome"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectPolicy.js",
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectPolicies.css"
  ] 
/]

[#assign currentSection = "projects" /]
[#assign currentStage = "projectPolicies" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"policies", "nameSpace":"/projects", "action":"${(crpSession)!}/policies", "param": "projectID=${projectID}"},
  {"label":"policy", "nameSpace":"/projects", "action":""}
] /]

[#assign params = {
  "caseStudies": {"id":"caseStudiesName", "name":"project.caseStudies"}
  }
/] 

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /] 


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
        
        [@s.form action=actionName cssClass="pure-form" enctype="multipart/form-data" ]  

          [#-- Back --]
          <small class="pull-right">
            <a href="[@s.url action='${crpSession}/policies'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectPolicies.backProjectStudies" /]
            </a>
          </small>
          
          [#-- Outcome case studies list --]
          <h3 class="headTitle">[@s.text name="projectPolicies.policyTitle" /]</h3>
          <div id="" class="borderBox">
            [@policyMacro element=(policy)!{} name="policy" index=0  /]
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
         
        [/@s.form]
  
      </div>
    </div>  
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- MACROS --]
[#macro policyMacro element name index=-1 template=false ]
  [#local customName = "${name}"/]
  [#local customId = "policy-${template?string('template',index)}" /]

  <div id="${customId}" class="policy" style="display:${template?string('none','block')}">
    
    [#-- Title (up to 50 words) --]
    <div class="form-group">
      [@customForm.input name="${customName}.projectPolicyInfo.title" i18nkey="policy.title" help="policy.title.help" helpIcon=false className="limitWords-50"required=true editable=editable /]
    </div>
    
    <div class="form-group row ">
      [#local isBudgetInvestment = ((element.projectPolicyInfo.repIndPolicyInvestimentType.id == 3))!false]
      [#-- Policy/Investment Type --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.projectPolicyInfo.repIndPolicyInvestimentType.id" className="setSelect2 policyInvestimentTypes" i18nkey="policy.policyType" listName="policyInvestimentTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable/]
      </div>
      [#-- Amount (Only for Budget or Investment) --]
      <div class="col-md-6 block-budgetInvestment" style="display:${isBudgetInvestment?string('block', 'none')}">
        [@customForm.input name="${customName}.projectPolicyInfo.policyAmount" i18nkey="policy.amount" help="policy.amount.help" className="currencyInput" required=true editable=editable /]
      </div>
    </div>
    
    <div class="form-group row">
      [#-- Implementing Organization Type --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.projectPolicyInfo.repIndPolicyInvestimentType.id" className="setSelect2 policyInvestimentTypes" i18nkey="policy.organizationType" help="policy.organizationType.help" listName="organizationTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable/]
      </div>
      [#-- Level of Maturity of the Process: (Before Stage in Process) --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.projectPolicyInfo.repIndMaturityLevel.id" className="setSelect2" i18nkey="policy.maturityLevel" help="policy.maturityLevel.help" help="policy.maturityLevel.help" listName="stageProcesses" keyFieldName="id"  displayFieldName="description" required=true editable=editable/]
      </div>
    </div>
    
    <div class="row">
      [#-- Whose policy is this? (Max 2): --]
      <div class="col-md-6">
        [@customForm.elementsListComponent name="${customName}.policyOwners" elementType="repIndPolicyType" elementList=[] label="policy.policyOwners"  listName="policyTypes" maxLimit=2 keyFieldName="id" displayFieldName="name"/]
      </div>
      <div class="col-md-6">
      </div>
    </div>
    
    [#-- Evidence (OICR)  --]
    <div class="form-group">
      [@customForm.select name="${customName}.projectPolicyInfo.evidence.id" className="setSelect2" i18nkey="policy.evidence" help="policy.evidence.help" listName="expectedStudyList" keyFieldName="id"  displayFieldName="name" helpIcon=false required=true editable=editable/]
    </div>
    
    <hr />
    
    [#-- Contributing CRPs/PTFs  --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.contributingCrpsPtfs" elementType="globalUnit" elementList=[] label="policy.contributingCrpsPtfs"  listName="crps" keyFieldName="id" displayFieldName="composedName" /]
    </div>
    
    [#-- Sub IDOs (maxLimit=2) --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.subIdos" elementType="srfSubIdo" elementList=element.subIdos label="policy.subIDOs" help="policy.subIDOs.help" listName="subIdos" maxLimit=2 keyFieldName="id" displayFieldName="description"/]
    </div>
    
    [#-- CGIAR Cross-cutting Markers  --]
    <div class="form-group">
      <h5>[@s.text name="policy.crossCuttingMarkers" /]</h5>
      <div class="row">
        [#list [ "Gender", "Youth", "CapDev", "Climate Change"] as marker]
          <div class="col-md-3">
            <input type="hidden"  name="${customName}.crossCuttingMarkers[${marker_index}].id" value=""/>
            <input type="hidden"  name="${customName}.crossCuttingMarkers[${marker_index}].crossCuttingMarker.id" value="${marker}"/>
            [@customForm.select   name="${customName}.crossCuttingMarkers[${marker_index}].crossCuttingLevel.id" className="setSelect2" i18nkey="${marker}" listName="crossCuttingLevels" keyFieldName="id"  displayFieldName="name" required=true editable=editable/]
          </div>
        [/#list]
      </div>
    </div>
    
    [#--  Geographic scope  --]
    <div class="form-group geographicScopeBlock">
      [#local geographicScope = ((element.projectExpectedStudyInfo.repIndGeographicScope.id)!-1) ]
      
      [#local isRegional = ((geographicScope == action.reportingIndGeographicScopeRegional)!false) ]
      [#local isMultiNational = ((geographicScope == action.reportingIndGeographicScopeMultiNational)!false) ]
      [#local isNational = ((geographicScope == action.reportingIndGeographicScopeNational)!false) ]
      [#local isSubNational = ((geographicScope == action.reportingIndGeographicScopeSubNational)!false) ]
      
      <div class="form-group">
        <div class="form-group row">
          <div class="col-md-6">
            [#-- Geographic Scope --]
            [@customForm.select name="${customName}.projectPolicyInfo.repIndGeographicScope.id" className="setSelect2 geographicScopeSelect" i18nkey="policy.geographicScope" help="policy.geographicScope.help" listName="geographicScopes" keyFieldName="id"  displayFieldName="name" editable=editable required=true /]
          </div>
        </div>
        <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
          [#-- Regional scope --]
          [@customForm.elementsListComponent name="${customName}.regions" elementType="locElement" elementList=element.studyRegions label="policy.regions"  listName="regions" keyFieldName="id" displayFieldName="name" required=false /]
        </div>
        <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
          [#-- Multinational, National and Subnational scope --]
          [@customForm.select name="${customName}.countriesIds" label="" i18nkey="policy.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
        </div>
      </div>
    </div>
    
  </div>
[/#macro]