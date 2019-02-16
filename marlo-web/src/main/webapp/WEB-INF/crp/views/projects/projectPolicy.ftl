[#ftl]
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
              <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectPolicies.backProjectPolicies" /]
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

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- MACROS --]
[#macro policyMacro element name index=-1 template=false ]
  [#local customName = "${name}"/]
  [#local customId = "policy-${template?string('template',index)}" /]
  [#-- Is new --]
  [#local isNew = (action.isPolicyNew(element.id)) /]

  <div id="${customId}" class="policy" style="display:${template?string('none','block')}">
    
    [#-- Year --]
    <div class="form-group row">
      <div class="col-md-3"></div>
      <div class="col-md-3"></div>
      <div class="col-md-3"></div>
      <div class="col-md-3">
        [@customForm.select name="${customName}.projectPolicyInfo.year" className="setSelect2" i18nkey="policy.year" listName="years" required=true editable=editable/]
      </div>
    </div>
    <hr />
    
    [#-- Title (up to 50 words) --]
    <div class="form-group">
      [@customForm.input name="${customName}.projectPolicyInfo.title" i18nkey="policy.title" className="limitWords-50"required=true editable=editable /]
    </div>
    
    <div class="form-group row ">
      [#local isBudgetInvestment = ((element.projectPolicyInfo.repIndPolicyInvestimentType.id == 3))!false]
      [#-- Policy/Investment Type --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.projectPolicyInfo.repIndPolicyInvestimentType.id" className="setSelect2 policyInvestimentTypes" i18nkey="policy.policyType" listName="policyInvestimentTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable/]
      </div>
      [#-- Amount (Only for Budget or Investment) --]
      <div class="col-md-6 block-budgetInvestment" style="display:${isBudgetInvestment?string('block', 'none')}">
        [@customForm.input name="${customName}.projectPolicyInfo.amount" i18nkey="policy.amount" help="policy.amount.help" className="currencyInput" required=true editable=editable /]
      </div>
    </div>
    
    <div class="form-group row">
      [#-- Implementing Organization Type --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.projectPolicyInfo.repIndOrganizationType.id" className="setSelect2 policyOrganizationType" i18nkey="policy.organizationType" help="policy.organizationType.help" listName="organizationTypes" keyFieldName="id"  displayFieldName="name" required=true editable=editable/]
      </div>
      [#-- Level of Maturity of the Process: (Before Stage in Process) --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.projectPolicyInfo.repIndStageProcess.id" className="setSelect2" i18nkey="policy.maturityLevel" help="policy.maturityLevel.help" help="policy.maturityLevel.help" listName="stageProcesses" keyFieldName="id"  displayFieldName="description" required=true editable=editable/]
      </div>
    </div>
    
    <div class="row">
      [#-- Whose policy is this? (Max 2)  --]
      <div class="col-md-6">
        [@customForm.elementsListComponent name="${customName}.owners" elementType="repIndPolicyType" elementList=(element.owners)![] label="policy.policyOwners" help="policy.policyOwners.help"  listName="policyTypes" maxLimit=2 keyFieldName="id" displayFieldName="name"/]
      </div>
      [#local ownerOther = false /]
      [#list (element.owners)![] as owner]
        [#if (owner.repIndPolicyType.id == 4)!false][#local ownerOther = true /][#break][/#if]
      [/#list]
      <div class="col-md-6 block-pleaseSpecify" style="display:${ownerOther?string('block', 'none')}">
        [@customForm.input name="${customName}.projectPolicyInfo.other" i18nkey="policy.otherOwner" className="" required=false editable=editable /]
      </div>
    </div>
    
    [#-- Evidence (OICR)  --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.evidences" elementType="projectExpectedStudy" elementList=element.evidences label="policy.evidence" help="policy.evidence.help" helpIcon=false listName="expectedStudyList" keyFieldName="id" displayFieldName="composedName" /]
      <div class="note">[@s.text name="policy.evidence.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/studies'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
    </div>
    
    [#-- Innovations  --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.innovations" elementType="projectInnovation" elementList=element.innovations label="policy.innovations" helpIcon=false listName="innovationList" keyFieldName="id" displayFieldName="composedName" required=false /]
      <div class="note">[@s.text name="policy.innovations.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/innovationsList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
    </div>

    <hr />
    <br />
    
    [#-- Contributing CRPs/PTFs  --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.crps" elementType="globalUnit" elementList=element.crps label="policy.contributingCrpsPtfs"  listName="crps" keyFieldName="id" displayFieldName="composedName" /]
    </div>
    
    [#-- Sub IDOs (maxLimit=2) --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.subIdos" elementType="srfSubIdo" elementList=element.subIdos label="policy.subIDOs" listName="subIdos" maxLimit=2 keyFieldName="id" displayFieldName="description"/]
    </div>
    
    [#-- CGIAR Cross-cutting Markers  --]
    <div class="form-group">
      <h5 class="labelheader">[@s.text name="policy.crossCuttingMarkers" /]</h5>
      <div class="row">
        [#list cgiarCrossCuttingMarkers as marker]
          <div class="col-md-3">
            [#local markerElement = (action.getPolicyCrossCuttingMarker(marker.id))!{} ]
            <input type="hidden"  name="${customName}.crossCuttingMarkers[${marker_index}].id" value="${(markerElement.id)!}"/>
            <input type="hidden"  name="${customName}.crossCuttingMarkers[${marker_index}].cgiarCrossCuttingMarker.id" value="${marker.id}"/>
            [@customForm.select   name="${customName}.crossCuttingMarkers[${marker_index}].repIndGenderYouthFocusLevel.id" value="${(markerElement.repIndGenderYouthFocusLevel.id)!-1}" className="setSelect2" i18nkey="${marker.name}" listName="focusLevels" keyFieldName="id"  displayFieldName="powbName" required=true editable=editable/]
          </div>
        [/#list]
      </div>
    </div>
    
    [#--  Geographic scope  --]
    <div class="form-group geographicScopeBlock">
      [#local geographicScopeList = (element.geographicScopes)![] ]
      [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
      [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
      [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
      [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
      <div class="form-group">
        <div class="row">
          [#-- Geographic Scope --]
          <div class="col-md-6">
            [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementList=element.geographicScopes maxLimit=1 label="policy.geographicScope" help="policy.geographicScope.help" listName="geographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
          </div>
        </div>
        [#-- Regional scope --]
        <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
          [@customForm.elementsListComponent name="${customName}.regions" elementType="locElement" elementList=element.regions label="policy.regions"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=true /]
        </div>
        [#-- Multinational, National and Subnational scope --]
        <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
          [@customForm.select name="${customName}.countriesIds" label="" i18nkey="policy.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="${customName}.countriesIds" multiple=true required=true className="countriesSelect" disabled=!editable/]
        </div>
      </div>
    </div>
    
  </div>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]