[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- 2.2.1 Highlights of External Partnerships  --]
            <h5 class="sectionSubTitle">[@s.text name="${customLabel}.highlights.title" /]</h5>
            
            [#-- Partnerships summary --]
            [#if PMU]
              <div class="form-group">
                [@customForm.textArea name="${customName}.highlights" i18nkey="${customLabel}.highlights" help="${customLabel}.highlights.help" className="limitWords-300" helpIcon=false required=true editable=editable /]
              </div>
            [#else]
              <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.highlights" readText=true /]</label>:
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
              </div>
            [/#if]
            
            [#-- Table 7: Key external partnerships --]
              <h4 class="simpleTitle headTitle">[@s.text name="${customLabel}.table7.title" /]</h4>
                <div class="listProgramCollaborations">
              [#--       [#if reportSynthesis.reportSynthesisCrossCgiar.collaborations?has_content]
                  [#list reportSynthesis.reportSynthesisCrossCgiar.collaborations as item]
                    [@flagshipCollaborationMacro element=item name="${customName}.collaborations" index=item_index  isEditable=editable/]
                  [/#list]
                 [/#if]--] 
                </div>
              [#if canEdit && editable]
                <div class="text-right">
                  <div class="addKeyPartnership bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.externalPartnerships.addPartnershipButton"/]</div>
                </div> 
              [/#if]
         
             [#-- Projects Key Partnerships --]
              <h4 class="simpleTitle">[@customForm.text name="${customLabel}.projectsPartnerships.title" param="${currentCycleYear}" /] ([#if PMU]${flagshipPlannedList?size}[#else]${partnerShipList?size}[/#if])</h4>
              <div class="form-group margin-panel">
                  [@projectsKeyPartnershipsTable name="${customName}.partnershipsValue" list="" /]
              </div>
              
              [#-- 2.2.2 Cross-CGIAR Partnerships  --]
              <h5 class="sectionSubTitle">[@s.text name="${customLabel}.crossCGIAR.title" /]</h5>
              [#-- Summary --]
              [#if PMU]
                <div class="form-group">
                  [@customForm.textArea name="${customName}.crossCGIAR.summary" i18nkey="${customLabel}.crossCGIAR.summary" help="${customLabel}.crossCGIAR.summary.help" className="limitWords-300" helpIcon=false required=true editable=editable /]
                </div>
              [#else]
                <div class="textArea">
                  <label for="">[@customForm.text name="${customLabel}.crossCGIAR.summary" readText=true /]:</label>
                  <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                </div>
              [/#if]
              
              [#-- Table 8: Internal Cross-CGIAR Collaborations --]
              [#if (reportSynthesis.reportSynthesisCrossCgiar.collaborations?has_content)!false]
                  [#list reportSynthesis.reportSynthesisCrossCgiar.collaborations as item]
                    [@addCrossCGIARPartnerships element=item name="${customName}.collaborations" index=item_index  isEditable=editable/]
                  [/#list]
              [/#if]
            
          </div>
          [#-- Section Buttons & hidden inputs --]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form]
      </div>
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro addKeyExternalPartnership]

  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-flagship" width="8%">FP</th>
        <th id="tb-crp" width="20%">[@s.text name="${customLabel}.tableH.crp" /]</th>
        <th id="tb-description" width="50%">[@s.text name="${customLabel}.tableH.description" /]</th>
        <th id="tb-relevantFP" width="20%">[@s.text name="${customLabel}.tableH.relevantFP" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local crpProgram = (item.reportSynthesisCrossCgiar.reportSynthesis.liaisonInstitution.crpProgram)!{} ]
        <tr>
          [#-- Flagship --]
          <td class="tb-flagship text-center">
            <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
          </td>
          [#-- CRP/Platform --]
          <td class="text-center">
          [#if item.globalUnit?has_content]
            ${item.globalUnit.acronym}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
          [#-- Description of collaboration --]
          <td class="">
          [#if item.description?has_content]
            ${item.description?replace('\n', '<br>')}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
          [#-- Relevant FP --]
          <td class="text-center">
          [#if item.flagship?has_content]
            ${item.flagship}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
          [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="4">
          <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>

[/#macro]


[#macro projectsKeyPartnershipsTable name="" list=[]]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId">[@s.text name="${customLabel}.projectsPartnerships.id" /]</th>
        <th id="tb-phase">[@s.text name="${customLabel}.projectsPartnerships.phase" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.projectsPartnerships.type" /]</th>
        <th id="tb-geographicScope">[@s.text name="${customLabel}.projectsPartnerships.geographicScope" /]</th>
        <th id="tb-mainArea" width="34%">[@s.text name="${customLabel}.projectsPartnerships.mainArea" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#if isPMU]
          [#local element = (item.projectPartnerPartnership)!{} ]
        [#else]
          [#local element = item ]
        [/#if]
        [#local customName = "${name}" /]
        [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/partners"][@s.param name='projectID']${(element.projectPartner.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Flagships --]
          [#if isPMU]
          <td>
            <div class="clearfix"></div>
            [#list (item.liaisonInstitutions)![] as li]
              <span class="programTag" style="border-color:${(li.crpProgram.color)!'#4a4a4a'}">${(li.crpProgram.acronym)!(li.crpProgram.institution.acronymName)!(li.acronym)!'NULL'}</span>
            [/#list]
          </td>
          [/#if]
          <td class="tb-projectId text-center">
            <a href="${URL}" target="_blank">
              [#-- Partner Name --]
              ${(element.projectPartner.institution.acronymName)!'--'}
              [#-- Project ID --]
              <br /><i><small>(From P${(element.projectPartner.project.id)!''})</small></i>
            </a>
          </td>
          [#-- Phase of research --]
          <td class="text-center">
          [#if element.partnershipResearchPhases?has_content]
            [#list element.partnershipResearchPhases as reseacrhPhase]
              <span>${reseacrhPhase.repIndPhaseResearchPartnership.name}</span><br />
            [/#list]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Partner type --]
          <td class="text-center">
          [#if element.projectPartner.institution.institutionType.repIndOrganizationType?has_content]
            ${element.projectPartner.institution.institutionType.repIndOrganizationType.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if element.geographicScope?has_content]
            ${element.geographicScope.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Main area of partnership --]
          <td class="">
          [#if element.mainArea?has_content]
            ${element.mainArea}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="6">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]


[#macro addCrossCGIARPartnerships element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="flagshipCollaboration-${template?string('template', index)}" class="flagshipCollaboration borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeProgramCollaboration removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />

    <div class="form-group row">
      [#-- CRP/Platform --] 
      <div class="col-md-5">
        [@customForm.select name="${customName}.globalUnit.id" label="" keyFieldName="id" displayFieldName="acronym" i18nkey="${customLabel}.collaboration.crp" listName="globalUnitList"  required=true  className="globalUnitSelect" editable=isEditable/]
      </div>
      [#-- Flagship/Module --]
      <div class="col-md-7">
        [@customForm.input name="${customName}.flagship" i18nkey="${customLabel}.collaboration.flagship" required=true className="globalUnitPrograms" editable=isEditable /]
      </div>
    </div>
    
    [#-- Collaboration type --]
    <div class="form-group row">
      <div class="col-md-7">
        <label>[@s.text name="${customLabel}.collaboration.type" /]:[@customForm.req required=editable  /]</label><br />
        [#local collaborationTypeSelected = (element.repIndCollaborationType.id)!-1]
        
        [#list (collaborationList)![] as collaboration]
          [@customForm.radioFlat id="${customName}-collaboration-${collaboration_index}" name="${customName}.repIndCollaborationType.id" label="${collaboration.name}" value="${collaboration.id}" checked=(collaborationTypeSelected == collaboration.id)!false editable=isEditable cssClass="" cssClassLabel="font-normal"/]
        [/#list]
        
        [#if !editable && (collaborationTypeSelected == -1)][@s.text name="form.values.fieldEmpty"/][/#if]
      </div>
      
      [#-- Status --]
      <div class="col-md-5">
        [@customForm.select name="${customName}.status" i18nkey="${customLabel}.collaboration.status" value="${(element.status)!-1}" listName="statuses"  required=true  className="" editable=isEditable/]
      </div>
    </div>
    
    [#-- Brief Description --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.description" i18nkey="${customLabel}.collaboration.description" className="" required=true editable=isEditable /]
    </div>
    
  </div>
[/#macro]