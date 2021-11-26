[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg", "components-font-awesome", "datatables.net", "datatables.net-bs","flag-icon-css"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js?20211126a"
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20211026a" ] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20211026a",
  "${baseUrlCdn}/global/css/global.css?20211126a"
] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/crp/views/annualReport2018/macros-AR2018.ftl" as macrosAR /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/ARMacros.ftl" as arMacros /]
[#assign customName= "reportSynthesis.reportSynthesisSrfProgress" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]

[#if PMU && false]
  [@utilities.helpBox name="Inputs received by Flagship leaders will be displayed here soon. Meanwhile, we suggest you to please go to the respective Flagship directly" /]
[/#if]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is available only at Reporting cycle</div>
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
          <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
          <span id="isSubmitted" style="display: none;">${submission?c}</span>
          [#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- Overall contribution towards SRF targets --]
            <div class="form-group">
              [#-- Word Document Tag --]
              [#if PMU]
                [#assign guideSheetURL = "https://docs.google.com/document/d/1bywPVopMJrNf3DkUBOiXJSadTQ6AG3WY/edit?usp=sharing&ouid=105658504509873937053&rtpof=true&sd=true" /]
                <small class="pull-left"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> SLO contributions  -  Guideline </a> </small>
                [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              [#else]
                [#assign guideSheetURL = "https://docs.google.com/document/d/1bywPVopMJrNf3DkUBOiXJSadTQ6AG3WY/edit?usp=sharing&ouid=105658504509873937053&rtpof=true&sd=true" /]
                <small class="pull-left"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> SLO contributions  -  Guideline </a> </small>
                [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
              [/#if]
              [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.overallContribution" help="${customLabel}.overallContribution.help" className="limitWords-${calculateLimitWords(400)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
              <br />
            </div>
            
            [#-- PMU Flagships - Synthesis --]
            [#if PMU]
              [#-- Missing fields in FPs --]
              [#if listOfFlagships?has_content]
                <div class="missingFieldFp">
                  <div><span class="glyphicon glyphicon-exclamation-sign mffp-icon" title="Incomplete"></span> Missing fields in
                  [#list listOfFlagships as fp]
                   ${fp}[#if fp?index !=(listOfFlagships?size-1) ],[/#if]
                  [/#list]
                  </div>
                 </div>
              [/#if]
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
                [@customForm.helpLabel name="${customLabel}.evidenceProgress2020.help" showIcon=false editable=editable helpMore=true /]
                <div class="block-selectedSLOs">
                  <div class="form-group sloTargetsList">
                    [#if sloTargets?has_content]
                      [#list sloTargets as slo]
                        [@sloTargetMacro name="${customName}.sloTargetsCases" element=slo index=slo_index /]
                      [/#list]
                    [#else]
                      [#if !editable] <p class="text-center font-italic">No entries added yet.</p> [/#if]
                    [/#if]
                  </div>
                </div>
              </div>
            [/#if]
            
          </div>
          [#--  review this commented code   --]
          [@sloContribution element={} name="outcomes[0].milestones" cssClass="slo-contribution-section-hide slo-contribution-template"  indexSlo=-1 index=-1 isTemplate=true /]
        
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
  [#local sloTargetContribution = action.getTargetsCasesInfo(element.id)!{} ]
  [#local otherContributions = action.getTargetsCasesFlagshipInfo(element.id)![] ]
  [#--[#local flagshipsInfo = action.getEvidencesBySLO(element.id)![] ]--]

  <div id="${customClass}-${isTemplate?string('template', index)}" class="simpleBox a-slo ${customClass}" style="display:${isTemplate?string('none', 'block')}">

    
    [#-- Hidden Inputs --]
    <input type="hidden" name="sloTargets[${index}].id" value="${(element.id)!}" />
    [#--  
    <input type="hidden" name="${customName}.id" value="${(sloTargetContribution.id)!}" />
    <input type="hidden" name="${customName}.srfSloIndicatorTarget.id" class="indicatorTargetID" value="${(element.id)!}" />    
    --]
    [#-- SLO Target --]
    <div class="form-group grayBox name"> 
      
      <div class="pull-right">
        [@macrosAR.evidencesPopup element=(element)!{} list=(action.getEvidenceInfo(element.id))![]  /]
      </div> 
      <strong class="highlightedSLO-${element.id}">SLO Target 2022: ${(element.title)!}</strong>
       <br/><span class="highlightedTitle-${element.id}">${(element.narrative)!}</span><br>
       <div class="checkboxDiTeAr">
         <div class="contentCheckBox">
          [@customForm.checkbox name="sloTargets[${index}].hasEvidence" value="${element.hasEvidence?string('false', 'true')}" checked=element.hasEvidence!false i18nkey="annualReport2018.flagshipProgress.targetNotApplicable" className="checkboxDiTeArClick" required=false editable=editable /]
         </div>
       </div>
      
       [#if (PMU)]

       <div class="checkboxDiTeAr">
        <div class="">
          <button class="btn btn-primary flagshipBtn-${element.id}" type="button" data-toggle="collapse" data-target="#collapseExample-${index}"
          aria-expanded="false" aria-controls="collapseExample" style="outline: none;">Show flagships information</button>
        </div>
      </div>

     <br>
      
      <div class="collapse crpProgressflagships col-md-3" id="collapseExample-${index}">
      <button type="button" class="fpPopupClose close btnClose-${element.id}" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <ul class="nav nav-tabs insertHtmlSlo-tabs-${element.id}" role="tablist">
        </ul>
        <div class="tab-content insertHtmlSlo-tabpanel-${element.id}">
        <span class="highlightedTitle-${element.id}">${(element.narrative)!}</span>
        <br>
        </div>
      </div>

      [/#if]

       [#if PMU && false] 
       <div class="checkboxDiTeAr">
        <div class="">
          <button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#collapseExample-${index}"
          aria-expanded="false" aria-controls="collapseExample" style="outline: none;">
          Show table
        </button>
        </div>
      </div>


      <div class="collapse crpProgressflagships col-md-3" id="collapseExample-${index}">

          <div>


      <ul class="nav nav-tabs" role="tablist">
      [#list liaisonInstitutions as flagship]
            [#if (flagship.crpProgram.acronym)! != ""]
                <li role="presentation" [#if (flagship_index)! == 0]class="active" [/#if]><a href="#${(flagship.crpProgram.acronym)!}-${index}" aria-controls="${(flagship.crpProgram.acronym)!}" role="tab" data-toggle="tab">${(flagship.crpProgram.acronym)!}</a></li>
            [/#if]
      [/#list]
      </ul>
     
      <div class="tab-content">
        [#list liaisonInstitutions as flagship]
        <div role="tabpanel" [#if (flagship_index)! == 0] class="tab-pane active" [#else]class="tab-pane" [/#if] id="${(flagship.crpProgram.acronym)!}-${index}" style="overflow-y: scroll; max-height: 510px;">
         [#--  <p>this is a ${(flagship.crpProgram.acronym)!}</p> --]
          [#-- 
           [#list sloTargetList[index].targetCases as slo]
             [@contributionListComponent element=slo targetIndex=index flagship="${(flagship.crpProgram.acronym)!}" /]  
          [/#list] 
           --]
        </div>
        [/#list]
      </div>


          </div>
       
      </div>
     [/#if]


    </div>
    <div class="to-disabled-box" style="display:${element.hasEvidence?string('none', 'block')}">
      <div class="disabled-box"></div>
    <div class="evidenceList">
      [#list element.targetCases as evidence]
       [@sloContribution name="" element=evidence indexSlo=index index=(evidence?index)  /]
      [/#list]
    </div>
  </div>

  [#if editable]
    <div class="btn-addEvidence bigAddButton text-center" style="display:${(element.hasEvidence)?string('none', 'block')}"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add contribution</div>
  [/#if]
    
    
  </div>
[/#macro]


[#macro contributionListComponent element={}  targetIndex=-1 flagship=""]



[#if flagship == element.liaisonInstitution.acronym]
<br>
<div style="background-color: rgb(250, 250, 250); border-radius: 10px; padding: 7px; position: relative;">
  <div class="leftHead  sm">
    <!--<span class="index">12</span>-->
    <span class="index indexSloContribution">...</span>
    [#-- <span class="elementId">Id: ${(element.id)!"New"}</span>--]
  </div>
  <br>
       <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px; margin-top: 10px;">Geographic scope:</p>
        [#list element.geographicScopes as geographicScope]
        <p> - ${geographicScope.repIndGeographicScope.name}<p> 
        [/#list]

        <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px; margin-top: 10px;">Regions:</p>
        [#list element.geographicRegions as region]
        <p> - ${region.locElement.name}<p> 
        [/#list]

        [#--
        <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px; margin-top: 10px;">Country(ies):</p>
        [#list element.countriesIds as countrieId]
        <p> - ${countrieId.......}<p> 
        [/#list]
        --]

       <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px;">Brief summary of new evidence of CGIAR contribution:</p>
       <p>${(element.briefSummary)!}</p>
   
       <p style="font-weight: 700; margin-bottom: 0px; padding-bottom: 0px;">Expected additional contribution before end of 2022 (if not already fully covered)</p>
       <p>${(element.additionalContribution)!}</p>
</div>
[/#if]
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


[#macro sloContribution element cssClass="" name="" indexSlo=0 index=0 isTemplate=false ]
[#local ccname = "sloTargets[${indexSlo}].targetCases[${index}]" /]

<div class="slo-contribution-section ${cssClass}" style="margin-top: 10px; padding-top: 20px;">

  <input type="hidden" name="${ccname}.id" value="${(element.id)!}" />
  
  <div class="leftHead  sm">
    <!--<span class="index">12</span>-->
    <span class="index indexSloContribution">${(index+1)}</span>
    [#-- <span class="elementId">Id: ${(element.id)!"New"}</span>--]
  </div>

  [#if editable]
    <div class="btn-removeEvidence removeElement sm" title="Remove Evidence"></div>
  [/#if]
  [#if !PMU] [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/][/#if]
  [#if PMU]
    [#if actualPhaseAR2021 && submission]
      [#assign qaIncluded = (!(element.isQAIncluded))!false]
      <div class="containerTitleElements">
        <span id="isCheckedAR-${(element.id)!''}" style="display: none;">${(qaIncluded)?c}</span>
        <button type="button" class="${qaIncluded?then('removeARButton', 'includeARButton')} qaStatus-button" name="${ccname}-button">${qaIncluded?then('Remove from QA', 'Include in QA')}</button>
        <input type="hidden" name="${ccname}.isQAIncluded" class="onoffswitch-radio"  value="${(!qaIncluded)?c}" />
        [#if qaIncluded]
          <div class="sloContainerTitleStatusMessage">
            <div id="containerQAStatus-${(element.id)!''}" class="pendingForReview-mode text-center animated flipInX">
              <p>
                [@s.text name="annualReport2018.policies.table2.pendingForReview"][/@s.text]
              </p>
            </div> 
          </div>
        [/#if]
    </div>
    [/#if]
  [/#if]
  [@arMacros.deliverableGeographicScope name="${ccname}" element=element /]
  <hr>
  [#-- Brief summary of new evidence of CGIAR contribution to relevant targets for this CRP (with citation) --]
  <br>
  <div class="form-group TA_summaryEvidence">
  [#if !PMU] [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/][/#if]
    [@customForm.textArea name="${ccname}.briefSummaryShow" value=element.briefSummary i18nkey="${customLabel}.summaryEvidence" className="limitWords-150 tumaco" help="${customLabel}.summaryEvidence2020.help" helpIcon=false required=true editable=editable allowTextEditor=!isTemplate /]

    <div style="display:none">
    [@customForm.textArea name="${ccname}.briefSummary" value=element.briefSummary i18nkey="${customLabel}.summaryEvidence" className="limitWords-150 briefSummaryTAHidden" help="${customLabel}.summaryEvidence2020.help" helpIcon=false required=true editable=editable  /]
    </div>
  [#-- FP Synthesis table --]
  [#if PMU]
    [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableSloTargetBriefSummary" list=otherContributions columns=["briefSummary"] crpProgramField="reportSynthesisSrfProgress.reportSynthesis.liaisonInstitution.crpProgram" showTitle=false showHeader=false showEmptyRows=false /]
  [/#if]
  </div>
  [#-- Expected additional contribution before end of 2022 (if not already fully covered). --]
  <br>
  <span id="actualPhase" style="display: none;">${action.isSelectedPhaseAR2021()?c}</span>
  <div class="form-group TA_additionalContribution">
  [#if !PMU] [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/][/#if]
    <div style="display:none">
      [@customForm.textArea name="" value=element.additionalContribution i18nkey="${customLabel}.additionalContribution" className="limitWords-100 tumaco" help="${customLabel}.additionalContribution.help" helpIcon=false required=false editable=editable  allowTextEditor=!isTemplate /]
    </div>
    
    [@customForm.textArea name="${ccname}.additionalContribution" value=element.additionalContribution i18nkey="${customLabel}.additionalContribution" className="limitWords-100 additionalContributionTAHidden" help="${customLabel}.additionalContribution.help" helpIcon=false required=false editable=editable /]
    
  
    [#-- FP Synthesis table --]
  [#if PMU]
    [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableSloTargetBriefSummary" list=otherContributions columns=["additionalContribution"] crpProgramField="reportSynthesisSrfProgress.reportSynthesis.liaisonInstitution.crpProgram" showTitle=false showHeader=false showEmptyRows=false /]
  [/#if]
  </div>
</div>
[/#macro]


