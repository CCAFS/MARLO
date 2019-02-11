[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "components-font-awesome", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js"
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js" ] /]
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
            [#-- Overall contribution towards SRF targets --]
            <div class="form-group">
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.overallContribution" i18nkey="${customLabel}.overallContribution" help="${customLabel}.overallContribution.help" className="limitWords-400" helpIcon=false required=true editable=editable /]
            </div>
            [#-- Areas of learning from past impact assessments --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.impactAssessments" i18nkey="${customLabel}.impactAssessments" help="${customLabel}.impactAssessments.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- PMU Flagships - Synthesis --]
            [#if PMU]
              <div class="form-group">
                <div class="viewMoreSyntesis-block" >
                  [@tableFlagshipSynthesis tableName="tableOverallProgress" list=flagshipCrpProgress columns=["SLOTarget", "CGIARContribution", "expectedContribution", "includeAR"] /]
                </div>
              </div>
            [/#if]
           
            
            
            [#-- Table 1: Evidence on progress towards SRF targets  --]
            [#if flagship]
             [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
             <hr />
            <div class="form-group">
              <h4 class="headTitle annualReport-table">[@s.text name="${customLabel}.evidenceProgress" /]</h4>
              [@customForm.helpLabel name="${customLabel}.evidenceProgress.help" showIcon=false editable=editable/]
              <div class="block-selectedSLOs">
                <div class="form-group sloTargetsList">
                  [#if reportSynthesis.reportSynthesisCrpProgress.sloTargets?has_content]
                    [#list reportSynthesis.reportSynthesisCrpProgress.sloTargets as slo]
                      [@sloTargetMacro name="${customName}.sloTargets" element=slo index=slo_index /]
                    [/#list]
                  [#else]
                    [#if !editable] <p class="text-center font-italic">No entries added yet.</p> [/#if]
                  [/#if]
                </div>
                [#if editable]
                <div class="dottedBox">
                  <div class="pull-left"> <span class="glyphicon glyphicon-plus"></span>  &nbsp</div>
                  [@customForm.select name="" className="setSelect2 addSloTarget" i18nkey="${customLabel}.selectSLOTarget" listName="sloTargets" keyFieldName="id"  displayFieldName="composedName" required=true /]
                </div>
                [/#if]
              </div>
            </div>
            [/#if]
            
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
    
    [#-- Templates --]
    [@sloTargetMacro name="${customName}.sloTargets" element={} index=-1 isTemplate=true /]
    
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]


[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableFlagshipSynthesis tableName="tableName" list=[] columns=[] ]
  <div class="form-group">
    <h4 class="simpleTitle">[@s.text name="${customLabel}.${tableName}.title" /]</h4>
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1 text-center"> FP </th>
          [#list columns as column]<th class="text-center"> [@s.text name="${customLabel}.${tableName}.column${column_index}" /] </th>[/#list]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local crpProgram = (item.reportSynthesis.liaisonInstitution.crpProgram)!{} ]
            <tr>
              <td>
                <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span>
              </td>
              [#list columns as column]
                <td>
                  [#if (item[column]?has_content)!false] 
                    ${item[column]?replace('\n', '<br>')} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledByFlagship"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro sloTargetMacro name element index=-1 isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  [#local customClass = "sloTarget" /]
  <div id="${customClass}-${isTemplate?string('template', index)}" class="simpleBox ${customClass}" style="display:${isTemplate?string('none', 'block')}">
    [#-- Hidden Inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    <input type="hidden" name="${customName}.srfSloIndicatorTarget.id" class="indicatorTargetID" value="${(element.srfSloIndicatorTarget.id)!}" />
    [#-- Remove button --]
    [#if editable]<div class="removeElement sm removeIcon removeSloTarget" title="Remove"></div>[/#if] 
    [#-- SLO Target --]
    <div class="form-group grayBox name"> <strong>SLO ${(element.srfSloIndicator.srfSlo.id)!} Target </strong> <br />${(element.srfSloIndicatorTarget.narrative)!}</div>
    [#-- Brief summary of new evidence of CGIAR contribution to relevant targets for this CRP (with citation) --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.birefSummary" value="${(element.birefSummary)!}" i18nkey="${customLabel}.summaryEvidence" className="limitWords-150" help="${customLabel}.summaryEvidence.help" helpIcon=false required=true editable=editable /]
    </div>
    [#-- Expected additional contribution before end of 2022 (if not already fully covered). --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.additionalContribution" value="${(element.additionalContribution)!}" i18nkey="${customLabel}.additionalContribution" className="limitWords-100" help="${customLabel}.additionalContribution.help" helpIcon=false required=false editable=editable /]
    </div>
  </div>
[/#macro]