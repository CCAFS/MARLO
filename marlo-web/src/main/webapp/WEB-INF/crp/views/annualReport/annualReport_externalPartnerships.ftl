[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
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

[#assign customName= "reportSynthesis.reportSynthesisExternalPartnership" /]
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
      [#-- POWB Menu --]
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
          
            [#-- Summarize highlights, value added and points to improve/learning points from this year on external partnerships --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.highlights" i18nkey="${customLabel}.summarizeHighlights" help="${customLabel}.summarizeHighlights.help" className="" helpIcon=false required=true editable=editable /]
            </div>
            
            [#-- Flagships - External Partnerships Synthesis --]
            [#if PMU]
            <div class="form-group margin-panel">
              <div class="viewMoreSyntesis-block" >
                [@tableFlagshipSynthesis tableName="tablePartnerships" list=[{},{},{},{}] columns=["summarizeHighlights"] /]
                <div class="viewMoreSyntesis closed"></div>
              </div>
            </div>
            [/#if]
  
            [#-- Table G: Projects Key Partnerships --]
            <h4 class="simpleTitle">[@customForm.text name="${customLabel}.tableG.title" param="${currentCycleYear}" /]</h4>
            <div class="form-group margin-panel">
              [#if flagship]
                [@tableGMacro name="${customName}.partnershipsValue" list=partnerShipList /]
              [#else]
                <div class="viewMoreSyntesis-block" >
                  [@tableGMacro name="" list=flagshipPlannedList isPMU=PMU /]
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
          [#list columns as column]<th> [@s.text name="${customLabel}.${tableName}.column${column_index}" /] </th>[/#list]
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
                    ${item[column]} 
                  [#else]
                    <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                  [/#if]
                </td>
              [/#list]
            </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="3"><i>No flagships loaded...</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableGMacro name="" list=[] isPMU=false ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId" width="0%">[@s.text name="${customLabel}.tableG.projectId" /]</th>
        <th id="tb-phase" width="20%">[@s.text name="${customLabel}.tableG.phase" /]</th>
        <th id="tb-type" width="11%">[@s.text name="${customLabel}.tableG.type" /]</th>
        <th id="tb-geographicScope" width="24%">[@s.text name="${customLabel}.tableG.geoScope" /]</th>
        <th id="tb-mainArea" width="34%">[@s.text name="${customLabel}.tableG.mainArea" /]</th>
        [#if !isPMU]
          <th id="tb-include" width="0%">[@s.text name="${customLabel}.tableG.include" /]</th>
        [/#if]
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
          [#-- Project ID --]
          <td class="tb-projectId text-center">
            <p>${(element.projectPartner.institution.acronymName)!'--'}</p>
            <a href="${URL}" target="_blank"><i><small>(From P${(element.projectPartner.project.id)!''})</small></i></a>
          </td>
          [#-- Phase of research --]
          <td class="text-center">
          [#if element.partnershipResearchPhases?has_content]
            ${element.partnershipResearchPhases} ssdadsadas
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
          [#-- Include in AR--]
          [#if !isPMU]
          <td class="plannedStudiesCheckbox text-center">
            [@customForm.checkmark id="keyPartnership-${item_index}" name="${customName}" value="${(element.id)!''}" checked=((!powbSynthesis.powbEvidence.studiesIds?seq_contains(element.id))!true) editable=editable /]
          </td>
          [/#if]
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