[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "components-font-awesome" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js",
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"annualReport", "nameSpace":"annualReport", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}", "nameSpace":"annualReport", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/crp/views/annualReport/macros-annualReport.ftl" as annualReport /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisIndicatorGeneral" /]
[#assign synthesisIndicators= (reportSynthesis.reportSynthesisIndicatorGeneral.synthesisIndicators)![] /]
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
            [#assign guideSheetURL = "https://drive.google.com/file/d/1jwFtj2wSM1ZN6dS_EnITcFgm9E-WC1KH/view" /]
            <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #I3 Policies - Guideline </a> </small>
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorI3.title" /]</h4>
            
            <div class="form-group row">
              <div class="col-md-12">
                [#-- Chart 1 --]
                <div id="chart1" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorI3.chart1.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorI3.chart1.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list (organizationTypeByStudiesDTOs)![] as data]
                      [#if (data.projectExpectedStudies?has_content)!false]
                      <li>
                        <span>${(data.repIndOrganizationType.name)!}</span>
                        <span class="number">${data.projectExpectedStudies?size}</span>
                        <span>#4285f4</span>
                        <span>${data.projectExpectedStudies?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div> 
              </div>
            </div>
            
            [#-- Outcomes/Impacts involved in policy/investments --]
            <div class="form-group margin-panel">
              <h4 class="subTitle headTitle">[@s.text name="${customLabel}.indicatorI3.policyTable" /]</h4>
              <hr />
              <div class="viewMoreSyntesis-block" >
                [@tableOutcomesMacro list=projectExpectedStudies /]
                
              </div>
            </div>
            
            [#-- Information -  Indicator C3  --]
            [@annualReport.indicatorInformation name="${customName}.synthesisIndicators" list=synthesisIndicators index=0 id="indicatorI3" label="${customLabel}" editable=editable && PMU /]
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#if PMU]
            [#include "/WEB-INF/crp/views/annualReport/buttons-annualReport.ftl" /]
          [/#if]
        [/@s.form] 
      </div> 
    </div>
  [/#if]
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro tableOutcomesMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="text-left">[@s.text name="${customLabel}.table.studiesTitle" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.table.policy" /]</th>
        <th id="tb-organization-type">[@s.text name="${customLabel}.table.implementingType" /]</th>
        <th id="tb-stage">[@s.text name="${customLabel}.table.stage" /]</th>
        <th id="tb-geoScope">[@s.text name="${customLabel}.table.geoScope" /]</th>
        <th id=""> Links to evidence </th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#if (item.project.id??)!false]
          [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${(item.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#else]
          [#local URL][@s.url namespace="/studies" action="${(crpSession)!}/study"][@s.param name='expectedID']${(item.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [/#if]
        <tr>
          [#-- Title --]
          <td class="">
            [#if item.composedName?has_content]
              <a href="${URL}" target="_blank">${item.composedName}</a>
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
            [#-- Project ID --]
            [#if (item.project.id??)!false] <br /><i style="opacity:0.5">(From Project P${(item.project.id)!})</i> [/#if]
          </td>
          [#-- Policy/Investment Type --]
          <td class="">
          [#if item.projectExpectedStudyInfo.repIndPolicyInvestimentType?has_content]
            ${item.projectExpectedStudyInfo.repIndPolicyInvestimentType.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Implementing Organization Type --]
          <td class="text-center">
          [#if item.projectExpectedStudyInfo.repIndOrganizationType?has_content]
            ${item.projectExpectedStudyInfo.repIndOrganizationType.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Stage --]
          <td class="text-center">
          [#if item.projectExpectedStudyInfo.repIndStageProcess?has_content]
            ${item.projectExpectedStudyInfo.repIndStageProcess.id}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic Scope --]
          <td class="text-center">
          [#if item.projectExpectedStudyInfo.repIndGeographicScope?has_content]
            ${item.projectExpectedStudyInfo.repIndGeographicScope.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          <td class="text-center">
            [#local linkEvidenceURL][@s.url namespace="/projects" action="${(crpSession)!}/studySummary"][@s.param name='studyID']${(item.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            <a href="${linkEvidenceURL}" target="_blank"><i class="fas fa-link" style="color: #2196F3;"></i></a>
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