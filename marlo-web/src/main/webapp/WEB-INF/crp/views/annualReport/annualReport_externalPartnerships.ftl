[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
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
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
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
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
          
            [#-- Summarize highlights, value added and points to improve/learning points from this year on external partnerships --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.summarizeHighlights" i18nkey="${customLabel}.summarizeHighlights" help="${customLabel}.summarizeHighlights.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Flagships - External Partnerships Synthesis --]
            [#if PMU]
            <div class="form-group margin-panel">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.table.title" /]</h4>
              
              <hr />
              [@tableFlagshipsMacro list=[{},{},{},{}] /]
            </div>
            [/#if]
  
            [#-- Table G: Projects Key Partnerships --]
            [#if flagship]
            <div class="form-group margin-panel">
              <div class="evidence-plannedStudies-header">
                <h4 class="subTitle headTitle">[@s.text name="${customLabel}.tableG.title" /]</h4>
              </div>
              <hr />
              <label>[@s.text name="${customLabel}.includeLabel" /]:</label>
              [@tableGMacro list=[{},{},{},{}] /]
            </div>
            [/#if]
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if]
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableFlagshipsMacro list ]
  <div class="">
    <table class="annual-report-table table table-bordered">
      <thead>
        <tr class="subHeader">
          <th width="20%" > [@s.text name="${customLabel}.table.flagship" /] </th>
          <th width="80%" > [@s.text name="${customLabel}.table.externalPartnerships" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            <tr>
              [#-- Flagship --]
              <td>
                [#if item.liaisonInstitution?has_content]
                <span class="programTag" style="border-color:${(item.liaisonInstitution.crpProgram.color)!'#fff'}">${item.liaisonInstitution.crpProgram.acronym!''}</span>
                [/#if]
              </td>
              [#-- External Partnerships --]
              <td class="text-center">
              [#if item.externalPartnerships?has_content] 
                ${item.externalPartnerships!''} 
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
              [/#if]
              </td>
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>[@s.text name="${customLabel}.table.void" /]</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableGMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId" width="0%">[@s.text name="${customLabel}.tableG.projectId" /]</th>
        <th id="tb-phase" width="20%">[@s.text name="${customLabel}.tableG.phase" /]</th>
        <th id="tb-type" width="11%">[@s.text name="${customLabel}.tableG.type" /]</th>
        <th id="tb-geographicScope" width="24%">[@s.text name="${customLabel}.tableG.geoScope" /]</th>
        <th id="tb-mainArea" width="34%">[@s.text name="${customLabel}.tableG.mainArea" /]</th>
        <th id="tb-include" width="0%">[@s.text name="${customLabel}.tableG.include" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${(item.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#local wordCutterMaxPos=180]
        [#-- 
          [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/expectedStudies"][@s.param name='projectID']${(popUp.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        --]
        <tr>
          [#-- Project ID --]
          <td class="tb-projectId text-center">
            <a href="${pURL}" target="_blank">P${(item.project.id)!''}</a>
          </td>
          [#-- Phase of research --]
          <td class="text-center">
          [#if item.phase?has_content]
            ${item.phase}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Partner type --]
          <td class="text-center">
          [#if item.type?has_content]
            ${item.type}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if item.scope?has_content]
            ${item.scope}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Main area of partnership --]
          <td class="text-center">
          [#if item.area?has_content]
            ${item.area}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Include in AR--]
          <td class="plannedStudiesCheckbox text-center">
          [#if editable]
            [@customForm.checkBoxFlat id="${(item.id)!''}" name="item.name" value="${(item.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(item.id))!true)/]
          [#else]
            [#-- If does no have permissions --]
            [#if powbSynthesis.powbEvidence.studiesIds?seq_contains(item.id)]<p class="checked"></p>[/#if]
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