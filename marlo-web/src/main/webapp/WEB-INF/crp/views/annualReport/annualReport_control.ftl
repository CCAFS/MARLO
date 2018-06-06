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
        
          [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          
          [#-- CGIAR Indicator #C1: Number of Innovations   --]
          <div class="borderBox">
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC1.title" /]</h4>
            <div class="form-group row">
              <div class="col-md-4">
                [#-- Total of CRP Innovations --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC1.totalInnovations" /]</label><br />
                  <span>256</span>
                </div>
              </div>
              <div class="col-md-8">
                [#-- Chart 2 --]
                <div id="chart2" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Stage 1: end of research phase ",    "value": "89"},
                      {"name":"Stage 2: end of piloting phase",     "value": "6"},
                      {"name":"Stage 3: available for uptake",      "value": "7"},
                      {"name":"Stage 4: uptake by next user",       "value": "45"}
                    ] 
                  /]
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart2.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart2.1" /]</span>
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
              [@customForm.textArea name="${customName}.data" i18nkey="${customLabel}.indicatorC1.data" help="${customLabel}.indicatorC1.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- Comments/Analysis --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.indicatorC1.comments" help="${customLabel}.indicatorC1.comments.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
          </div>
          
          [#--  CGIAR Indicator #C2: Partnerships --]
          <div class="borderBox">
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC2.title" /]</h4>
            <div class="form-group row">
              <div class="col-md-7">
                [#-- Chart 3 --]
                <div id="chart3" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"CGIAR",                              "value": "89"},
                      {"name":"Academic and Research",              "value": "6"},
                      {"name":"Development organizations",          "value": "7"},
                      {"name":"NARES/NARS",                         "value": "45"},
                      {"name":"CBOs and farmers' groups",           "value": "56"},
                      {"name":"Private sector",                     "value": "5"},
                      {"name":"Foundations and Financial Institutions", "value": "2"},
                      {"name":"Government",                         "value": "7"},
                      {"name":"Bilateral and Donor governments",    "value": "7"},
                      {"name":"Multilateral",                       "value": "45"},
                      {"name":"Other",                              "value": "23"}
                    ] 
                  /]
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart3.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart3.1" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                    [/#list]
                  </ul>
                </div>
              </div>
              <div class="col-md-5">
                [#-- Chart 4 --]
                <div id="chart4" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Global",         "value": "89"},
                      {"name":"Multinational",  "value": "6"},
                      {"name":"National",       "value": "7"},
                      {"name":"Sub-National",   "value": "45"}
                    ] 
                  /] 
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart4.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart4.1" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                    [/#list]
                  </ul>
                </div>
              </div>
            </div>
            <div class="form-group row">
              <div class="col-md-7">
                [#-- Chart 5 --]
                <div id="chart5" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Phase 1: Discovery/Proof of concept",    "value": "50"},
                      {"name":"Phase 2: Piloting",                      "value": "16"},
                      {"name":"Phase 3: Scaling up and scaling out",    "value": "17"}
                    ] 
                  /]
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart5.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart5.1" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                    [/#list]
                  </ul>
                </div>
              </div>
              <div class="col-md-5">
                [#-- Total of partnerships--]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC2.totalPartnerships" /]</label><br />
                  <span>556</span>
                </div>
              </div>
            </div>
            
            [#-- Table G: Projects Key Partnerships  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.partnershipsTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              [@tableGKeyPartnershipsMacro list=[{},{},{},{}] /]
            </div>
            
            [#-- Data --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.data" i18nkey="${customLabel}.indicatorC2.data" help="${customLabel}.indicatorC2.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- Comments/Analysis --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.indicatorC2.comments" help="${customLabel}.indicatorC2.comments.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
          
          </div>
          
          [#--  CGIAR Indicator #C3. Number of Direct Participants in CGIAR Activities (Incorporating CGIAR Indicator #C4. Number of People Trained) --]
          <div class="borderBox">
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC3C4.title" /]</h4>
            <div class="form-group row">
              <div class="col-md-6">
                [#-- Total of participants estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.totalParticipants" /]</label><br />
                  <span class="animated infinite bounce">556</span>
                </div>
              </div>
              <div class="col-md-6">
                [#-- Percentage of female  estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.percentageFemale" /]</label><br />
                  <span>556</span>
                </div>
              </div>
            </div>
            <div class="form-group row">
              <div class="col-md-6">
                [#-- Percentage of Youth estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.percentageYouth" /]</label><br />
                  <span>556</span>
                </div>
              </div>
              <div class="col-md-6">
                [#-- Formal training estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.formalTraining" /]</label><br />
                  <span>556</span>
                </div>
              </div>
            </div>
            
            [#-- Projects Activities/Events  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.activitiesEventsTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              [@tableParticipantsTrainingsMacro list=[{},{},{},{}] /]
            </div>
            
            [#-- Data - Indicator C3 --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.data" i18nkey="${customLabel}.indicatorC3.data" help="${customLabel}.indicatorC3.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- Comments/Analysis - Indicator C3  --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.indicatorC3.comments" help="${customLabel}.indicatorC3.comments.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- Data - Indicator C4 --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.data" i18nkey="${customLabel}.indicatorC4.data" help="${customLabel}.indicatorC4.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            
            [#-- Comments/Analysis - Indicator C4  --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.indicatorC4.comments" help="${customLabel}.indicatorC4.comments.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
          </div>
          
          [#--  CGIAR Indicator #C5: Number of CGIAR research papers published in peer reviewed journals  --]
          <div class="borderBox">
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC5.title" /]</h4>
            <div class="form-group row">
              <div class="col-md-6">
                [#-- Chart 6 --]
                <div id="chart6" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Open Acess",   "value": "71"},
                      {"name":"Limited",      "value": "6"}
                    ] 
                  /] 
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC5.chart6.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC5.chart6.1" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                    [/#list]
                  </ul>
                </div>
              </div>
              <div class="col-md-6">
                [#-- Chart 7 --]
                <div id="chart7" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Yes",   "value": "36"},
                      {"name":"No",    "value": "28"}
                    ] 
                  /] 
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC5.chart7.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC5.chart7.1" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
                    [/#list]
                  </ul>
                </div>
              </div>
            </div>

            [#-- Data - Indicator C5 --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.data" i18nkey="${customLabel}.indicatorC5.data" help="${customLabel}.indicatorC5.data.help" paramText="${(actualPhase.year)!}" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
            [#-- Comments/Analysis - Indicator C5  --]
            <div class="form-group margin-panel">
              [@customForm.textArea name="${customName}.comments" i18nkey="${customLabel}.indicatorC5.comments" help="${customLabel}.indicatorC5.comments.help" className="" helpIcon=false required=true editable=editable && PMU /]
            </div>
          </div>
          
          [#if PMU]
          [#-- Section Buttons & hidden inputs--]
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

[#macro tableGKeyPartnershipsMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="${customLabel}.partnershipsTable.projectID" /]</th>
        <th id="tb-title">[@s.text name="${customLabel}.partnershipsTable.researchPhase" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.partnershipsTable.partnerType" /]</th>
        <th id="tb-organization-type">[@s.text name="${customLabel}.partnershipsTable.geoScope" /]</th>
        <th id="tb-stage">[@s.text name="${customLabel}.partnershipsTable.mainPartnership" /]</th>
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


[#macro tableParticipantsTrainingsMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-id">[@s.text name="${customLabel}.activitiesEventsTable.activityEvent" /]</th>
        <th id="tb-title">[@s.text name="${customLabel}.activitiesEventsTable.type" /]</th>
        <th id="tb-type">[@s.text name="${customLabel}.activitiesEventsTable.totalParticipants" /]</th>
        <th id="tb-organization-type">[@s.text name="${customLabel}.activitiesEventsTable.typeParticipants" /]</th>
        <th id="tb-stage">[@s.text name="${customLabel}.activitiesEventsTable.scope" /]</th>
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