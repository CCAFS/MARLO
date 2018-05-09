[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${powbSynthesisID}" /]
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
        
          [#-- Summarize highlights, value added and points to improve/learning points from this year on external partnerships --]
          <div class="form-group margin-panel">
            [@customForm.textArea name="${customName}.summarizeHighlights" i18nkey="${customLabel}.summarizeHighlights" help="${customLabel}.summarizeHighlights.help" className="" helpIcon=false required=true editable=editable /]
          </div>
          
          [#-- Flagships - External Partnerships Synthesis --]
          [#if PMU]
          <div class="form-group margin-panel">
            <h4 class="subTitle headTitle">[@s.text name="${customName}.table.title" /]</h4>
            
            <hr />
            [@tableFlagshipsMacro /]
          </div>
          [/#if]

          [#-- Table G: Projects Key Partnerships --]
          [#if flagship]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header">
              <h4 class="subTitle headTitle">[@s.text name="${customName}.tableG.title" /]</h4>
            </div>
            <hr />
            <label>[@s.text name="${customName}.includeLabel" /]:</label>
            [@tableGMacro/]
          </div>
          [/#if]
        
        </div>
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro tableGMacro ]
  <table class="annual-report-table">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId" width="0%">[@s.text name="evidenceRelevant.tablePlannedStudies.projectId" /]</th>
        <th id="tb-phase" width="20%">[@s.text name="evidenceRelevant.tablePlannedStudies.plannedTopic" /]</th>
        <th id="tb-type" width="11%">[@s.text name="evidenceRelevant.tablePlannedStudies.geographicScope" /]</th>
        <th id="tb-geographicScope" width="24%">[@s.text name="evidenceRelevant.tablePlannedStudies.relevant" /]</th>
        <th id="tb-mainArea" width="34%">[@s.text name="evidenceRelevant.tablePlannedStudies.comments" /]</th>
        <th id="tb-include" width="0%">[@s.text name="evidenceRelevant.tablePlannedStudies.include" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if popUpProjects?has_content]
      [#list popUpProjects as popUp]
        [#if popUp.project.id?has_content]
            [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${(popUp.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#local wordCutterMaxPos=180]
          [#-- 
            [#local tsURL][@s.url namespace="/projects" action="${(crpSession)!}/expectedStudies"][@s.param name='projectID']${(popUp.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          --]
        <tr>
          [#-- Project ID --]
          <td class="tb-projectId text-center">
            <a href="${pURL}" target="_blank">P${(popUp.project.id)!''}</a>
          </td>
          [#-- Phase of research --]
          <td>
          [#if popUp.topicStudy?has_content]
            [#if popUp.topicStudy?length gt wordCutterMaxPos]
              <div title="${(popUp.topicStudy)!''}">
            [/#if]
              <a href="${tsURL}" target="_blank">[@utilities.wordCutter string="${(popUp.topicStudy)!''}" maxPos=wordCutterMaxPos /]</a>
            [#if popUp.topicStudy?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Partner type --]
          <td class="text-center">
          [#if popUp.scopeName?has_content]
            ${popUp.scopeName}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="relevantSubIDO">
          [#if popUp.scopeName?has_content]
            ${popUp.scopeName}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Main area of partnership --]
          <td class="comments">
          [#if popUp.comments?has_content]
            [#if popUp.comments?length gt wordCutterMaxPos]
              <div title="${(popUp.comments)!''}">
            [/#if]
              [@utilities.wordCutter string="${(popUp.comments)!''}" maxPos=wordCutterMaxPos /]
            [#if popUp.comments?length gt wordCutterMaxPos]
              </div>
            [/#if]
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Include --]
          <td class="plannedStudiesCheckbox text-center">
          [#if editable]
            [@customForm.checkBoxFlat id="${(popUp.id)!''}" name="powbSynthesis.powbEvidence.plannedStudiesValue" value="${(popUp.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(popUp.id))!true)/]
          [#else]
            [#-- If does no have permissions --]
            [#if powbSynthesis.powbEvidence.studiesIds?seq_contains(popUp.id)]<p class="checked"></p>[/#if]
          [/#if]
          </td>
        </tr>
        [/#if]
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

[#macro tableFlagshipsMacro ]
  <div class="">
    <table class="annual-report-table table table-bordered">
      <thead>
        <tr class="subHeader">
          <th> [@s.text name="annualReport.externalPartnerships.table.flagship" /] </th>
          <th> [@s.text name="annualReport.externalPartnerships.table.externalPartnerships" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if false]
          [#list flagships as liaisonInstitution]
            <tr>
              <td>[#-- <span class="programTag" style="border-color:${(liaisonInstitution.crpProgram.color)!'#fff'}">${liaisonInstitution.crpProgram.acronym}</span> --]</td>
              <td>[#if false] ${liaisonInstitution.externalPartnerships} [#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>[@s.text name="annualReport.externalPartnerships.table.void" /]</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]