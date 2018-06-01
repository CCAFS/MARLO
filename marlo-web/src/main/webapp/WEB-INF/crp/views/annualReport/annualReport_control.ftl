[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
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
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "annualReport.${currentStage}" /]
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
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC3.title" /]</h4>
            
            
            [#assign chartData = [
                {"name":"Stage 1: end of research phase ",    "value": "89"},
                {"name":"Stage 2: end of piloting phase",     "value": "6"},
                {"name":"Stage 3: available for uptake",      "value": "7"},
                {"name":"Stage 4: uptake by next user",       "value": "45"}
              ] 
            /]
            <div class="form-group row">
              <div class="col-md-4">
                [#-- Total of CRP Innovations --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3.totalInnovations" /]</label>
                  <span>256</span>
                </div>
              </div>
              <div class="col-md-8">
                [#-- Chart 2 --]
                <div id="chart2" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC3.chart2.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC3.chart2.1" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                    [/#list]
                  </ul>
                </div> 
              </div>
            </div>
            
            [#-- Table D-2: List of CRP Innovations in 2017  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.innovationsTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              [@tableD2InnovationsMacro list=[{},{},{},{}] /]
            </div>
            
            [#-- Data --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.data" i18nkey="${customLabel}.indicatorC3.data" help="${customLabel}.indicatorC3.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- Comments/Analysis --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.indicatorC3.comments" help="${customLabel}.indicatorC3.comments.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
          
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

[#------------------------------------  MACROS  ------------------------------------------]

[#macro tableD2InnovationsMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="${customLabel}.innovationsTable.titleInnovation" /]</th>
        <th id="tb-title">[@s.text name="${customLabel}.innovationsTable.stageInnovation" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.innovationsTable.degreeInnovation" /]</th>
        <th id="tb-organization-type">[@s.text name="${customLabel}.innovationsTable.contributionCRP" /]</th>
        <th id="tb-stage">[@s.text name="${customLabel}.innovationsTable.geoScope" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        <tr>
          [#-- Title of Innovation --]
          <td class="tb-id text-center">
            [#if item.crp?has_content]
              ${item.title}
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          [#-- Stage of Innovation --]
          <td class="">
          [#if item.crp?has_content]
            ${item.title}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Degree of Innovation --]
          <td class="">
          [#if item.type?has_content]
            ${item.type}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Contribution of CRP--]
          <td class="text-center">
          [#if item.type?has_content]
            ${item.type}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if item.stage?has_content]
            ${item.stage}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="5">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]

