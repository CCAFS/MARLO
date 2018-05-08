[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "components-font-awesome" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="annualReport.${currentStage}.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/annualReport/submenu-annualReport.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/annualReport/menu-annualReport.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/annualReport/messages-annualReport.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
      
        [#assign customName= "annualReport.${currentStage}" /]
        [#assign customLabel= "annualReport.${currentStage}" /]
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="${customName}.title" /]</h3>
        <div class="borderBox">
          [#-- Overall CRP progress towards SLOs --]
          <div class="form-group">
            [@customForm.textArea name="${customName}.overallProgress" i18nkey="${customLabel}.overallProgress" help="${customLabel}.overallProgress.help" className="" helpIcon=false required=true editable=editable /]
          </div>
          
          [#-- Summaries of outcome case studies --]
          <div class="form-group">
            [@customForm.textArea name="${customName}.summariesOutcomes" i18nkey="${customLabel}.summariesOutcomes" help="${customLabel}.summariesOutcomes.help" className="" helpIcon=false required=true editable=editable /]
          </div>
        
          [#-- Flagships - Synthesis  --]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">Flagships - Synthesis progress towards SLOs and Outcome</h4>
            [@tableCRPProgressMacro list=[{},{},{},{}] /]
          </div>
          [/#if]
          
          <hr />
          
          [#-- Table A-1: Evidence on progress towards the SLOs (sphere of interest)  --]
          [#if flagship]
          <div class="form-group">
            <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.evidenceProgress" /]</h4>
            [@customForm.helpLabel name="${customLabel}.evidenceProgress.help" showIcon=false editable=editable/]
            
            <div class="block-selectedSLOs">
              <div class="form-group">
                [#assign selectedSLOs = [
                  { "name": "SLO Target title #1"} 
                ] /]
                [#if selectedSLOs?has_content]
                  [#list selectedSLOs as slo][@sloTargetMacro name="${customName}.selectedSLOs" element=slo index=slo_index /][/#list]
                [/#if]
              </div>
              <hr />
              <div class="dottedBox">
                [@customForm.select name="" className="setSelect2" i18nkey="${customLabel}.selectSLOTarget" listName="selectSLOTarget" keyFieldName="id"  displayFieldName="name" required=true /]
              </div>
            </div>
            
          </div>
          [/#if]
          
          [#-- Flagships - Synthesis  --]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="${customLabel}.evidenceProgress" /]</h4>
            [@tableSLOSynthesisProgressMacro list=[{},{},{},{}] /]
          </div>
          [/#if]
          
          [#-- Table A-2: List of New Outcome Case Studies from This Reporting Year (Sphere of Influence)  --]
          <div class="form-group">
            <h4 class="subTitle headTitle annualReport-table">[@s.text name="${customLabel}.listOutcomes" /]</h4>
            [@customForm.helpLabel name="${customLabel}.listOutcomes.help" showIcon=false editable=editable/]
            [@tableOutcomesCaseStudiesMacro name="${customName}.selectedStudies" list=[{},{},{},{}] /]
          </div>
          
          <hr />
          
        
        </div>
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableOutcomesCaseStudiesMacro name list ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1"> [@s.text name="${customLabel}.table.projectId" /] </th>
          <th> [@s.text name="${customLabel}.table.outcomeCaseStudy" /] </th>
          <th> [@s.text name="${customLabel}.table.subIDO" /] </th>
          <th> [@s.text name="${customLabel}.table.crossCuttingIssues" /] </th>
          <th> [@s.text name="${customLabel}.table.evidenceLink" /] </th>
          <th> [@s.text name="${customLabel}.table.includeAR" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list??]
          [#list list as item]
            [#local customName = "${name}[${item_index}]" /]
            <tr>
              <td>P${item_index+10}</td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td><i class="fas fa-link"></i></td>
              <td>[@customForm.checkBoxFlat id="studyCheck-${item_index}" name="${customName}.value" label="" value="true" editable=editable checked=true cssClass="" /]</td>
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

[#macro tableSLOSynthesisProgressMacro list ]
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
        [#if list??]
          [#list list as item]
            <tr>
              <td><span class="programTag" style="border-color:${(item.crpProgram.color)!'#fff'}">${(item.crpProgram.acronym)!}</span></td>              
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

[#macro tableCRPProgressMacro list ]
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
        [#if list??]
          [#list list as item]
            <tr>
              <td><span class="programTag" style="border-color:${(item.crpProgram.color)!'#fff'}">${(item.crpProgram.acronym)!}</span></td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              <td>[#if false] [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
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
  [#local customName = "${name}.[${index}]" /]
  [#local customClass = "sloTarget" /]
  <div id="${customClass}-${isTemplate?string('template', index)}" class="simpleBox ${customClass}">
    [#-- Hidden Inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}" />
    [#-- Remove button --]
    [#if editable]<div class="removeElement sm removeIcon removeElementType-${customClass}" title="Remove"></div>[/#if] 
    [#-- SLO Target --]
    <div class="form-group grayBox"> <strong>SLO Target </strong> <br />${element.name}</div>
    [#-- Brief summary of new evidence of CGIAR contribution to relevant targets for this CRP (with citation) --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.summaryNewEvidence" i18nkey="${customLabel}.summaryNewEvidence" className="" required=true editable=editable /]
    </div>
    [#-- Expected additional contribution before end of 2022 (if not already fully covered). --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.additionalContribution" i18nkey="${customLabel}.additionalContribution" className="" required=false editable=editable /]
    </div>
  </div>
[/#macro]