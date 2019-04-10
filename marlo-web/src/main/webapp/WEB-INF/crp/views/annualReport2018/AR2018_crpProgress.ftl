[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg", "components-font-awesome", "datatables.net", "datatables.net-bs"] /]
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
[#import "/WEB-INF/crp/views/annualReport2018/macros-AR2018.ftl" as macrosAR /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisSrfProgress" /]
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
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.overallContribution" help="${customLabel}.overallContribution.help" className="limitWords-${calculateLimitWords(400)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
              <br />
            </div>
            
            [#-- PMU Flagships - Synthesis --]
            [#if PMU]
              <div class="form-group">
                [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableOverallProgress" list=flagshipSrfProgress columns=["summary"] urlify=true /]
              </div>
            [/#if]
            
            [#if !isPlatform]
              [#-- Table 1: Evidence on progress towards SRF targets  --]
              [#if PMU]
                [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              [/#if]
              <hr />
              <div class="form-group">
                <h4 class="headTitle annualReport-table">[@s.text name="${customLabel}.evidenceProgress" /]</h4>
                [@customForm.helpLabel name="${customLabel}.evidenceProgress.help" showIcon=false editable=editable helpMore=true /]
                <div class="block-selectedSLOs">
                  <div class="form-group sloTargetsList">
                    [#if sloTargets?has_content]
                      [#list sloTargets as slo]
                        [@sloTargetMacro name="${customName}.sloTargets" element=slo index=slo_index /]
                      [/#list]
                    [#else]
                      [#if !editable] <p class="text-center font-italic">No entries added yet.</p> [/#if]
                    [/#if]
                  </div>
                </div>
              </div>
            [/#if]
            
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
    
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]


[#---------------------------------------------------- MACROS ----------------------------------------------------]



[#macro sloTargetMacro name element index=-1 isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  [#local customClass = "sloTarget" /]
  [#local sloTargetContribution = action.getTargetsInfo(element.id)!{} ]
  [#local otherContributions = action.getTargetsFlagshipInfo(element.id)![] ]
  
  <div id="${customClass}-${isTemplate?string('template', index)}" class="simpleBox ${customClass}" style="display:${isTemplate?string('none', 'block')}">
    [#-- Hidden Inputs --]
    <input type="hidden" name="${customName}.id" value="${(sloTargetContribution.id)!}" />
    <input type="hidden" name="${customName}.srfSloIndicatorTarget.id" class="indicatorTargetID" value="${(element.id)!}" />    
    [#-- SLO Target --]
    <div class="form-group grayBox name"> 
      <div class="pull-right">
        [@macrosAR.evidencesPopup element=(element)!{} list=(action.getEvidenceInfo(element.id))![]  /]
      </div> 
      <strong>SLO Target 2022</strong> <br />${(element.narrative)!}
    </div>
    [#-- Brief summary of new evidence of CGIAR contribution to relevant targets for this CRP (with citation) --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.birefSummary" value="${(sloTargetContribution.birefSummary?html)!}" i18nkey="${customLabel}.summaryEvidence" className="limitWords-150" help="${customLabel}.summaryEvidence.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
      [#-- FP Synthesis table --]
      [#if PMU]
        [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableSloTargetBriefSummary" list=otherContributions columns=["birefSummary"] crpProgramField="reportSynthesisSrfProgress.reportSynthesis.liaisonInstitution.crpProgram" showTitle=false showHeader=false showEmptyRows=false /]
      [/#if]
    </div>
    [#-- Expected additional contribution before end of 2022 (if not already fully covered). --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.additionalContribution" value="${(sloTargetContribution.additionalContribution)!}" i18nkey="${customLabel}.additionalContribution" className="limitWords-100" help="${customLabel}.additionalContribution.help" helpIcon=false required=false editable=editable allowTextEditor=true /]
      [#-- FP Synthesis table --]
      [#if PMU]
        [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableSloTargetBriefSummary" list=otherContributions columns=["additionalContribution"] crpProgramField="reportSynthesisSrfProgress.reportSynthesis.liaisonInstitution.crpProgram" showTitle=false showHeader=false showEmptyRows=false /]
      [/#if]
    </div>
    
  </div>
[/#macro]

[#macro helpViewMore name=""]

  [#local customName = "annualReport2018.crpProgress.${name}.more" /]
   <a id="helpViewMoreLink" class="btn-link" data-toggle="collapse" data-target="#helpViewMoreBlock" aria-expanded="false" aria-controls="helpViewMoreBlock">
     <i class="helpLabel">[[@s.text name="global.viewMore" /]]</i>
   </a>
   
   <div id="helpViewMoreBlock" class="collapse" aria-labelledby="helpViewMoreBlock" data-parent="#helpViewMoreLink">
      <i class="helpLabel">[@s.text name="${customName}" /]</i>
   </div>
  
[/#macro]