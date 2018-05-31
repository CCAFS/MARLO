[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "components-font-awesome" ] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisCrpProgress" /]
[#assign customLabel= "annualReport.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
  
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
    
    <div class="row">
      [#-- Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
      </div> 
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- Overall CRP progress towards SLOs --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.overallProgress" i18nkey="${customLabel}.overallProgress" help="${customLabel}.overallProgress.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Summaries of outcome case studies --]
            <div class="form-group">
              [@customForm.textArea name="${customName}.summaries" i18nkey="${customLabel}.summariesOutcomes" help="${customLabel}.summariesOutcomes.help" className="" helpIcon=false required=true editable=editable /]
            </div>
          
            [#-- Flagships - Synthesis  --]
            [#if PMU]
            <div class="form-group">
              <h4 class="subTitle headTitle">Flagships - Synthesis progress towards SLOs and Outcome</h4>
              <div class="viewMoreSyntesis-block" >
                  [@tableCRPProgressMacro list=flagshipCrpProgress /]
                  <div class="viewMoreSyntesis closed"></div>
                </div>
            </div>
            [/#if]
            
            <hr />
            
            [#-- Table A-1: Evidence on progress towards the SLOs (sphere of interest)  --]
            [#if flagship]
            <div class="form-group">
              <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.evidenceProgress" /]</h4>
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
            
            [#-- Flagships - Synthesis  --]
            [#if PMU]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.evidenceProgress" /]</h4>
              <div class="viewMoreSyntesis-block" >
                  [@tableSLOSynthesisProgressMacro list=fpSynthesisTable /]
                  <div class="viewMoreSyntesis closed"></div>
                </div>
            </div>
            [/#if]
            
            <hr />
            
            [#-- Table A-2: List of New Outcome Case Studies from This Reporting Year (Sphere of Influence)  --]
            <div class="form-group">
              <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.listOutcomes" /]</h4>
              [@customForm.helpLabel name="${customLabel}.listOutcomes.help" showIcon=false editable=editable/]
              [#if flagship]
                [@tableOutcomesCaseStudiesMacro name="${customName}.plannedStudiesValue" list=studiesList /]
              [#else]
                <div class="viewMoreSyntesis-block" >
                  [@tableOutcomesCaseStudiesMacro name="" list=flagshipPlannedList isPMU=true /]
                  <div class="viewMoreSyntesis closed"></div>
                </div>
              [/#if]  
            </div>
            
            
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
        [/@s.form] 
      </div> 
    </div>
    
    [#-- Templates --]
    [@sloTargetMacro name="${customName}.sloTargets" element={} index=-1 isTemplate=true /]
  
  [/#if]
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableOutcomesCaseStudiesMacro name list=[]  isPMU=false ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          [#-- <th class="col-md-1"> [@s.text name="${customLabel}.table.projectId" /] </th> --]
          <th> [@s.text name="${customLabel}.table.outcomeCaseStudy" /] </th>
          <th> [@s.text name="${customLabel}.table.subIDO" /] </th>
          <th class="col-md-3"> [@s.text name="${customLabel}.table.crossCuttingIssues" /] </th>
          [#if !isPMU]<th class="col-md-1"> [@s.text name="${customLabel}.table.includeAR" /] </th>[/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#if isPMU]
              [#local projectExpectedStudy = item.projectExpectedStudy]
            [#else]
              [#local projectExpectedStudy = item]
            [/#if]
            [#local customName = "${name}" /]
            [#if (item.project.id??)!false]
              [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${(projectExpectedStudy.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#else]
              [#local URL][@s.url namespace="/studies" action="${(crpSession)!}/study"][@s.param name='expectedID']${(projectExpectedStudy.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [/#if]
            <tr>
              <td> 
                [#-- Title --]
                <a href="${URL}" target="_blank"> 
                  [#if ((projectExpectedStudy.projectExpectedStudyInfo.title)?has_content)!false] ${projectExpectedStudy.projectExpectedStudyInfo.title}[#else]Untitled[/#if]
                </a>
                [#-- Project ID --]
                [#if (projectExpectedStudy.project.id??)!false] <br /><i style="opacity:0.5">(From Project P${(projectExpectedStudy.project.id)!})</i> [/#if]
                [#-- Flagships --]
                [#if isPMU]
                  <div class="clearfix"></div>
                  [#list liaisonInstitutions as liaisonInstitution]
                    <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${(liaisonInstitution.crpProgram.acronym)!}</span>
                  [/#list]
                [/#if]
              </td>
              <td>
                [#if ((projectExpectedStudy.subIdos)?has_content)!false]
                  <ul>
                  [#list projectExpectedStudy.subIdos as ido]
                    <li>${(ido.srfSubIdo.description)!}</li>
                  [/#list]
                  </ul>
                [#else]
                  <i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              <td>
                [#local additional]
                  [#if (projectExpectedStudy.projectExpectedStudyInfo.genderLevel??)!false]
                    <strong>Gender:</strong>  
                    ${(projectExpectedStudy.projectExpectedStudyInfo.genderLevel.name)!} <br />
                    <small>${(projectExpectedStudy.projectExpectedStudyInfo.describeGender)!} </small><br />
                  [/#if] 
                  [#if (projectExpectedStudy.projectExpectedStudyInfo.youthLevel??)!false]
                    <strong>Youth: </strong>  
                    ${(projectExpectedStudy.projectExpectedStudyInfo.youthLevel.name)!} <br />
                    <small>${(projectExpectedStudy.projectExpectedStudyInfo.describeYouth)!}</small> <br />
                  [/#if] 
                  [#if (projectExpectedStudy.projectExpectedStudyInfo.capdevLevel??)!false]
                    <strong>CapDev: </strong> 
                    ${(projectExpectedStudy.projectExpectedStudyInfo.capdevLevel.name)!} <br />
                    <small>${(projectExpectedStudy.projectExpectedStudyInfo.describeCapdev)!} </small><br />
                  [/#if]
                [/#local]
                [#if additional?has_content ]${additional}[#else]<i class="text-center" style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]
              </td>
              [#-- <td class="text-center"><a href="#"><i class="fas fa-link"></i></a></td> --]
              [#if !isPMU]
              <td class="text-center">
                [@customForm.checkBoxFlat id="studyCheck-${item_index}" name="${customName}" label="" value="${projectExpectedStudy.id}" editable=editable checked=(!reportSynthesis.reportSynthesisCrpProgress.studiesIds?seq_contains(projectExpectedStudy.id))!false cssClass="" /]
              </td>
              [/#if]
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableSLOSynthesisProgressMacro list=[] ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1"> [@s.text name="${customLabel}.table.flagship" /] </th>
          <th> [@s.text name="${customLabel}.table.sloTarget" /] </th>
          <th> [@s.text name="${customLabel}.table.summarieEvidence" /] </th>
          <th> [@s.text name="${customLabel}.table.expectedContribution" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            <tr>
              <td> 
              <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span></td>              
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="4"><i>No flagships loaded..</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableCRPProgressMacro list=[] ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1"> [@s.text name="${customLabel}.table.flagship" /] </th>
          <th> [@s.text name="${customLabel}.table.progressSLO" /] </th>
          <th> [@s.text name="${customLabel}.table.summaries" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local crpProgram = (item.reportSynthesis.liaisonInstitution.crpProgram)!{} ]
            <tr>
              <td><span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span></td>
              <td>[#if (item.overallProgress?has_content)!false] ${item.overallProgress} [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td>[#if (item.summaries?has_content)!false] ${item.summaries} [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>No flagships loaded..</i></td>
          </tr>
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
      [@customForm.textArea name="${customName}.birefSummary" value="${(element.birefSummary)!}" i18nkey="${customLabel}.summaryNewEvidence" className="" required=true editable=editable /]
    </div>
    [#-- Expected additional contribution before end of 2022 (if not already fully covered). --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.additionalContribution" value="${(element.additionalContribution)!}" i18nkey="${customLabel}.additionalContribution" className="" required=false editable=editable /]
    </div>
  </div>
[/#macro]