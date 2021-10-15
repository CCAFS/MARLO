[#ftl]
[#assign title = "Project Policy" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${policyID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "flag-icon-css", "components-font-awesome"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectPolicy.js",
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectPolicies.css",
  "${baseUrlMedia}/css/projects/projectSubIdos.css"
  ] 
/]

[#assign currentSection = "projects" /]
[#assign currentStage = "projectPolicies" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"policies", "nameSpace":"/projects", "action":"${(crpSession)!}/policies", "param": "projectID=${projectID}"},
  {"label":"policy", "nameSpace":"/projects", "action":""}
] /]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /] 

[#-- Helptext --]
[@utilities.helpBox name="projectPolicies.help" /]

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
          
          [#--  <div class="containerTitleElements">
            <div class="containerTitleMessage">
              <div id="qualityAssessedIcon" class="qualityAssessed-mode text-center animated flipInX">
                [#assign lastSubmission=action.getProjectSubmissions(audit.id)?last /]
                <p>
                  [@s.text name="message.qualityAssessed"]
                    [@s.param]Policy[/@s.param]
                    [@s.param]${(lastSubmission.dateTime?string["MMMM dd, yyyy"])!}[/@s.param]
                  [/@s.text]
                </p>
              </div> 
              <p class="messageQAInfo">[@s.text name="message.qualityAssessedInfo"][/@s.text]</p>
            </div>  
          </div>    --]

          <div id="" class="">
            [@policyMacro element=(policy)!{} name="policy" index=0  /]
          </div> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
         
        [/@s.form]
  
      </div>
    </div>  
</section>

[#-- PopUp to select SubIDOs --]
<div id="subIDOs-graphic" style="overflow:auto; display:none;" >
  <div class="graphic-container" >
  <div class="filterPanel panel-default">
    <div class="panel-heading"> 
      <form id="filterForm"  role="form">
        <label class="checkbox-inline">Filter By:</label>
        <label class="checkbox-inline">
          <input type="checkbox" value="IDO" checked>IDOs
        </label>
        <label class="checkbox-inline">
          <input type="checkbox" value="CCIDO" checked>Cross-cutting IDOs
        </label>
      </form>
    </div>
  </div>        
  [#list srfIdos as ido]
    <div class="idoWrapper ${ido.isCrossCutting?string("crossCutting","ido")} ">    
      <div class="IDO${ido.isCrossCutting?string("-CrossCutting","")}"><strong>${ido.isCrossCutting?string("CrossCutting:","")} ${ido.description}</strong></div>
      <div class="subIdoWrapper">
        [#list ido.subIdos as subIdo]
          <div class="line"></div>
          <div id="subIdo-${subIdo.id}" class="subIDO subIDO${ido.isCrossCutting?string("-CrossCutting","")}">${subIdo.smoCode} ${subIdo.description}</div>
        [/#list]
      </div>
    </div>
  [/#list]
  </div>      
</div>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- MACROS --]
[#macro policyMacro element name index=-1 template=false ]
  [#local customName = "${name}"/]
  [#local customId = "policy-${template?string('template',index)}" /]
  [#-- Is new --]
  [#local isNew = (action.isPolicyNew(element.id)) /]

  <div id="${customId}" class="policy borderBox" style="display:${template?string('none','block')}">
    
    [#-- Year --]
    <div class="form-group row">
      <div class="col-md-4">
        [@customForm.select name="${customName}.projectPolicyInfo.year" className="setSelect2" i18nkey="policy.year" listName="getPoliciesYears(${policyID})" required=true editable=editable/]
      </div>      
      <div class="col-md-8">
        <div class="form-group">
              [#assign guideSheetURL = "https://docs.google.com/document/d/1KcNKtAdexpISekoVaAKPmHK223ohkqgb/edit?rtpof=true&sd=true" /]
              <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #I1 Policies  -  Guideline </a> </small>
            </div>
      </div>
    </div>
    <hr />
    
    [#-- Title (up to 30 words - Requested for AR2019) --]
    <div class="form-group">
      [@customForm.input name="${customName}.projectPolicyInfo.title" i18nkey="policy.title" className="limitWords-30"required=true editable=editable /]
    </div>
    
    [#-- Description --]
    <div class="form-group">
  	  [@customForm.textArea name="${customName}.projectPolicyInfo.description" i18nkey="policy.description" className="limitWords-30" editable=editable required=true /]
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
    
    <div class="form-group">
      [#-- Level of Maturity of the Process: (Before Stage in Process) --]
        [@customForm.select name="${customName}.projectPolicyInfo.repIndStageProcess.id" className="setSelect2 maturityLevel" i18nkey="policy.maturityLevel" help="policy.maturityLevel.help" help="policy.maturityLevel.help" listName="stageProcesses" keyFieldName="id"  displayFieldName="description" required=true editable=editable/]
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
    [#local isEvidenceRequired = ([4, 5]?seq_contains(element.projectPolicyInfo.repIndStageProcess.id))!false /]
    <div class="form-group evidences-block">
      [@customForm.elementsListComponent name="${customName}.evidences" elementType="projectExpectedStudy" elementList=element.evidences label="policy.evidence" help="policy.evidence.help" helpIcon=false listName="expectedStudyList" keyFieldName="id" displayFieldName="composedNameAlternative" required=isEvidenceRequired /]
      <div class="note">[@s.text name="policy.evidence.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/studies'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
    </div>
    
    [#local isResearchMaturity = ((element.projectPolicyInfo.repIndStageProcess.id == 3))!false]
    [#-- Narrative --] 
    <div class="form-group block-researchMaturity" style="display:${isResearchMaturity?string('block', 'none')}">
      [@customForm.textArea name="${customName}.projectPolicyInfo.narrativeEvidence"  i18nkey="policy.narrative"  placeholder="" className="limitWords-200" help="policy.evidenceNarrative.help" required=false editable=editable /]
    <br>
    </div>
          
    [#-- Innovations  --]
    <div class="form-group simpleBox">
      [@customForm.elementsListComponent name="${customName}.innovations" elementType="projectInnovation" elementList=element.innovations label="policy.innovations" helpIcon=false listName="innovationList" keyFieldName="id" displayFieldName="composedNameAlternative" required=false /]
      <div class="note">[@s.text name="policy.innovations.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/innovationsList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
    </div>

    <hr />
    <br />
    
    [#-- Milestones Contribution --]
    <div class="form-group">          
      <label for="">[@s.text name="policy.milestones" /]:[@customForm.req required=editable /][@customForm.helpLabel name="policy.milestones.help" showIcon=false editable=editable/]</label>
      [#assign policyMilestoneLink = "policyMilestoneLink"]
      [#assign showMilestoneIndicator = (policy.projectPolicyInfo.hasMilestones?string)!"" /]
      [@customForm.radioFlat id="${policyMilestoneLink}-yes" name="${customName}.projectPolicyInfo.hasMilestones" label="Yes" value="true" checked=(showMilestoneIndicator == "true") cssClass="radioType-${policyMilestoneLink}" cssClassLabel="radio-label-yes" editable=editable /]
      [@customForm.radioFlat id="${policyMilestoneLink}-no" name="${customName}.projectPolicyInfo.hasMilestones" label="No" value="false" checked=(showMilestoneIndicator == "false") cssClass="radioType-${policyMilestoneLink}" cssClassLabel="radio-label-no" editable=editable /]
    </div>
        
     <div class="form-group simpleBox block-${policyMilestoneLink}" style="display:${(showMilestoneIndicator == "true")?string('block','none')}">
       [@customForm.elementsListComponent name="${customName}.milestones" elementType="crpMilestone" elementList=(element.milestones)![] label="policy.milestones" helpIcon=false listName="milestoneList" keyFieldName="id" displayFieldName="composedName" required=false hasPrimary=true/]
       
       [#--[@customForm.primaryListComponent name="${customName}.milestones" checkName="milestonePrimaryId" elementType="crpMilestone" elementList=(element.milestones)!"" label="policy.milestones" labelPrimary="policy.primaryMilestone" helpIcon=false listName="milestoneList" keyFieldName="id" displayFieldName="composedName" required=false /]
       --]
       <div class="note">[@s.text name="policy.milestones.note"][@s.param] <a href="[@s.url namespace="/projects" action='${crpSession}/contributionsCrpList'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]</div>
       <br>
     </div> 

    <br />
    
    [#-- Contributing Centers/ PPA partners  --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.centers" elementType="institution" elementList=(element.centers)![] label="policy.contributingCenters"  listName="centers" keyFieldName="id" displayFieldName="composedName" /]
    </div>
    
    [#-- Contributing CRPs/PTFs  --]
    <div class="form-group">
      [@customForm.elementsListComponent name="${customName}.crps" elementType="globalUnit" elementList=element.crps label="policy.contributingCrpsPtfs"  listName="crps" keyFieldName="id" displayFieldName="composedName" required=false/]
    </div>

    
    [#-- Sub IDOs (maxLimit=2 -Requested for AR2021) --]      
    <div class="form-group simpleBox">
      [@customForm.elementsListComponent name="${customName}.subIdos" elementType="srfSubIdo" elementList=(element.subIdos)![] label="policy.subIDOs" helpIcon=false listName="subIdos" maxLimit=2 keyFieldName="id" displayFieldName="composedName" required=true hasPrimary=true /]
     [#--  <div class="buttonSubIdo-content"><br> <div class="selectSubIDO" ><span class=""></span>View sub-IDOs</div></div> --]
      [#--[@customForm.primaryListComponent name="${customName}.subIdos" checkName="subIdoPrimaryId" elementType="srfSubIdo" elementList=(element.subIdos)!"" label="policy.subIDOs" labelPrimary="policy.primarySubIdo" listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=false /]
       --]
    </div>  
[#--
    <div class="form-group">
       [@customForm.select name="${customName}.principalSubIdo" className="setSelect2 principalSubIdo" i18nkey="policy.subIDO.primary" listName="" keyFieldName="id"  displayFieldName="description" required=true editable=editable/]
    </div>--]
       
        
    [#-- CGIAR Cross-cutting Markers  --]
    <div class="form-group">
        [#assign ccGuideSheetURL = "https://drive.google.com/file/d/1oXb5UHABZIbyUUczZ8eqnDsgdzwABXPk/view?usp=sharing" /]
        <small class="pull-right"><a href="${ccGuideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" />Cross-Cutting Markers  -  Guideline </a> </small>
      </div>
    <div class="form-group">
      <h5 class="labelheader">[@s.text name="policy.crossCuttingMarkers" /]</h5>
      <div class="row">
        [#list cgiarCrossCuttingMarkers as marker]
          <div class="col-md-3">
            [#local markerElement = (action.getPolicyCrossCuttingMarker(marker.id))!{} ]
            <input type="hidden"  name="${customName}.crossCuttingMarkers[${marker_index}].id" value="${(markerElement.id)!}"/>
            <input type="hidden"  name="${customName}.crossCuttingMarkers[${marker_index}].cgiarCrossCuttingMarker.id" value="${marker.id}"/>
            [@customForm.select   name="${customName}.crossCuttingMarkers[${marker_index}].repIndGenderYouthFocusLevel.id" value="${(markerElement.repIndGenderYouthFocusLevel.id)!-1}" valueName="${(markerElement.repIndGenderYouthFocusLevel.powbName)!}" className="setSelect2" i18nkey="${marker.name}" listName="focusLevels" keyFieldName="id"  displayFieldName="powbName" required=true editable=editable/]
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
            [@customForm.elementsListComponent name="${customName}.geographicScopes" elementType="repIndGeographicScope" elementList=element.geographicScopes label="policy.geographicScope" help="policy.geographicScope.help" listName="geographicScopes" keyFieldName="id" displayFieldName="name" required=true /]
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
    
    
  [#-- Projects shared --]
  [#if false]
  <h3 class="headTitle">[@s.text name="policy.sharedProjects.title" /]</h3>
  <div class="borderBox">
    [@customForm.elementsListComponent name="${customName}.sharedInnovations" elementType="project" elementList=(element.sharedInnovations)![] label="policy.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
  </div>
  [/#if]
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]