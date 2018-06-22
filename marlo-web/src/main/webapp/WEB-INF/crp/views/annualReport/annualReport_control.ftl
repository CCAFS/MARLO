[#ftl]
[#assign title = "Annual Report" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
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
        
          [#-- Section Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          
          [#-- CGIAR Indicator #C1: Number of Innovations   --]
          <div class="borderBox">
            [#assign guideSheetURL = "https://drive.google.com/file/d/1LsVAVX60ALyr95BG0SKlK8J8_wibYz_c/view" /]
            <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #C1 Innovations -  Guideline </a> </small>
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC1.title" /] 
            </h4>
            <div class="form-group row">
              <div class="col-md-4">
                [#-- Total of CRP Innovations --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC1.totalInnovations" /]</label><br />
                  <span>${(projectInnovationInfos?size)!0}</span>
                </div>
              </div>
              <div class="col-md-8">
                [#-- Chart 2 --]
                <div id="chart2" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart2.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart2.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list innovationsByStageDTO as data]
                      [#if data.projectInnovationInfos?has_content]
                      <li>
                        <span>${data.repIndStageInnovation.name}</span>
                        <span class="number">${data.projectInnovationInfos?size}</span>
                        <span>#27ae60</span>
                        <span>${data.projectInnovationInfos?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div> 
              </div>
            </div>
            
            [#-- Table D-2: List of CRP Innovations in 2017  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.innovationsTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              <div class="viewMoreSyntesis-block" >
                [@tableD2InnovationsMacro list=(projectInnovationInfos)![] /]
                
              </div>
            </div>
            
            [#-- Information -  Indicator C1  --]
            [@annualReport.indicatorInformation name="${customName}.synthesisIndicators" list=synthesisIndicators index=0 id="indicatorC1" label="${customLabel}" editable=editable && PMU /]
            
          </div>
          
          [#--  CGIAR Indicator #C2: Partnerships --]
          <div class="borderBox">
            [#assign guideSheetURL = "https://drive.google.com/file/d/1rmVYYuo8yi4P0mq9zC9s_xeqoSurH4vd/view" /]
            <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #C2 Partnerships - Guideline </a> </small>
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC2.title" /]
            </h4>
            <div class="form-group row">
              <div class="col-md-7">
                [#-- Chart 3 --]
                <div id="chart3" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart3.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart3.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list partnershipsByRepIndOrganizationTypeDTOs as data]
                      [#if data.projectPartnerPartnerships?has_content]
                      <li>
                        <span>${data.repIndOrganizationType.name}</span>
                        <span class="number">${data.projectPartnerPartnerships?size}</span>
                        <span>#f39c12</span>
                        <span>${data.projectPartnerPartnerships?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div>
              </div>
              <div class="col-md-5">
                [#-- Chart 4 --]
                <div id="chart4" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart4.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart4.1" /]</span>
                    </li>
                    [#list partnershipsByGeographicScopeDTO as data]
                      [#if data.projectPartnerPartnerships?has_content]
                      <li>
                        <span>${data.repIndGeographicScope.name}</span>
                        <span class="number">${data.projectPartnerPartnerships?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div>
              </div>
            </div>
            <div class="form-group row">
              <div class="col-md-7">
                [#-- Chart 5 --]
                <div id="chart5" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart5.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC2.chart5.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list partnershipsByPhaseDTO as data]
                      [#if data.projectPartnerPartnerships?has_content]
                      <li>
                        <span>${data.repIndPhaseResearchPartnership.name}</span>
                        <span class="number">${data.projectPartnerPartnerships?size}</span>
                        <span>#e67e22</span>
                        <span>${data.projectPartnerPartnerships?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div>
              </div>
              <div class="col-md-5">
                [#-- Total of partnerships--]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC2.totalPartnerships" /]</label><br />
                  <span>${projectPartnerPartnerships?size}</span>
                </div>
              </div>
            </div>
            
            [#-- Table G: Projects Key Partnerships  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.partnershipsTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              <div class="viewMoreSyntesis-block" >
                [@tableGKeyPartnershipsMacro list=(projectPartnerPartnerships)![] /]
                
              </div>
            </div>
            
            [#-- Information -  Indicator C2  --]
            [@annualReport.indicatorInformation name="${customName}.synthesisIndicators" list=synthesisIndicators index=1 id="indicatorC2" label="${customLabel}" editable=editable && PMU /]
            
          
          </div>
          
          [#--  CGIAR Indicator #C3. Number of Direct Participants in CGIAR Activities (Incorporating CGIAR Indicator #C4. Number of People Trained) --]
          <div class="borderBox">
            [#assign guideSheetURL = "https://drive.google.com/file/d/1ZuZ-Ka4Bf9Bab42CCGeJvGkzWUYV4ytA/view" /]
            <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #C3 Participants + #C4 Trainees - Guideline </a> </small>
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC3C4.title" /]
            </h4>
            <div class="form-group row">
              <div class="col-md-4">
                [#-- Total of participants estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.totalParticipants" /]</label><br />
                  <span class="animated infinite bounce">${(totalParticipants?number?string(",##0"))!0}</span>
                </div>
              </div>
              <div class="col-md-4">
                [#-- Percentage of female estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.percentageFemale" /]</label><br />
                  <span>${(percentageFemales)!0}% </span>
                </div>
              </div>
              <div class="col-md-4">
                [#-- Formal training estimated/counted --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC3C4.formalTraining" /]</label><br />
                  <span>${(totalParticipantFormalTraining?number?string(",##0"))!0}</span>
                </div>
              </div>
            </div>
            
            [#-- Projects Activities/Events  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.activitiesEventsTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              <div class="viewMoreSyntesis-block" >
                [@tableParticipantsTrainingsMacro list=(deliverableParticipants)![] /]
              </div>
            </div>
            
            [#-- Information -  Indicator C3  --]
            [@annualReport.indicatorInformation name="${customName}.synthesisIndicators" list=synthesisIndicators index=2 id="indicatorC3" label="${customLabel}" editable=editable && PMU /]
            
            [#-- Information -  Indicator C4  --]
            [@annualReport.indicatorInformation name="${customName}.synthesisIndicators" list=synthesisIndicators index=3 id="indicatorC4" label="${customLabel}" editable=editable && PMU /]
            
          </div>
          
          [#--  CGIAR Indicator #C5: Number of CGIAR research papers published in peer reviewed journals  --]
          <div class="borderBox">
            [#assign guideSheetURL = "https://drive.google.com/file/d/1CZU7MLmXIVCBFFtlmB19eHFrldTI-dG4/view" /]
            <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #C5 Peer review papers - Guideline </a> </small>
            <h4 class="sectionSubTitle">[@s.text name="${customLabel}.indicatorC5.title" /]
            </h4>
            <div class="form-group row">
              <div class="col-md-6">
                [#-- Chart 6 --]
                <div id="chart6" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Open Acess",   "value": "${totalOpenAccess}"},
                      {"name":"Limited",      "value": "${totalLimited}"}
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
                      {"name":"Yes",   "value": "${totalIsis}"},
                      {"name":"No",    "value": "${totalNoIsis}"}
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

            [#-- Journal Articles  --]
            <div class="form-group">
              <h4 class="subTitle headTitle">[@customForm.text name="${customLabel}.journalArticlesTable.title" param="${currentCycleYear}"/]</h4>
              <hr />
              <div class="viewMoreSyntesis-block" >
                [@tableJournalArticlesMacro list=(deliverableInfos)![] /]
                
              </div>
            </div>
            
            [#-- Information -  Indicator C5  --]
            [@annualReport.indicatorInformation name="${customName}.synthesisIndicators" list=synthesisIndicators index=4 id="indicatorC5" label="${customLabel}" editable=editable && PMU /]
            
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
        [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${(item.projectInnovation.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title of Innovation --]
          <td class="">
            <a href="${URL}" target="_blank">
              [#if item.title?has_content]
                ${item.title}
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
              [/#if]
              [#-- Project ID --]
              [#if (item.projectInnovation.project.id??)!false] <br /><i style="opacity:0.5">(From Project P${(item.projectInnovation.project.id)!})</i> [/#if]
            </a>
          </td>
          [#-- Stage of Innovation --]
          <td class="">
          [#if item.repIndStageInnovation?has_content]
            ${item.repIndStageInnovation.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Degree of Innovation --]
          <td class="">
          [#if item.repIndDegreeInnovation?has_content]
            ${item.repIndDegreeInnovation.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Contribution of CRP--]
          <td class="text-center">
          [#if item.repIndContributionOfCrp?has_content]
            ${item.repIndContributionOfCrp.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if item.repIndGeographicScope?has_content]
            ${item.repIndGeographicScope.name}
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
        <th id="tb-id">[@s.text name="${customLabel}.partnershipsTable.partner" /]</th>
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
        [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/partners"][@s.param name='projectID']${(item.projectPartner.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title of Innovation --]
          <td class="tb-id text-center">
            <a href="${URL}" target="_blank">
              [#-- Partner Name --]
              ${(item.projectPartner.institution.acronymName)!'--'}
              [#-- Project ID --]
              <br /><i style="opacity:0.5"><small>(From P${(item.projectPartner.project.id)!''})</small></i>
            </a>
          </td>
          [#-- Phase of research --]
          <td class="">
            [#if item.partnershipResearchPhases?has_content]
              [#list item.partnershipResearchPhases as reseacrhPhase]
                <span>${reseacrhPhase.repIndPhaseResearchPartnership.name}</span><br />
              [/#list]
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          [#-- Partner Type --]
          <td class="">
            [#if item.projectPartner.institution.institutionType.repIndOrganizationType?has_content]
              ${item.projectPartner.institution.institutionType.repIndOrganizationType.name}
            [#else]
              <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
            [/#if]
          </td>
          <td class="">
          [#-- Geographic scope --]
          [#if item.geographicScope?has_content]
            ${item.geographicScope.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Main Area --]
          <td class="">
          [#if item.mainArea?has_content]
            ${item.mainArea}
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
        <th id="tb-type">Number of females</th>
        <th id="tb-organization-type">[@s.text name="${customLabel}.activitiesEventsTable.typeParticipants" /]</th>
        <th id="tb-stage">[@s.text name="${customLabel}.activitiesEventsTable.scope" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title of Innovation --]
          <td class="">
            <a href="${URL}" target="_blank">
              [#if item.eventActivityName?has_content]
                ${item.eventActivityName}
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
              [/#if]
              [#-- Deliverable ID --]
              <br /><i style="opacity:0.5"><small>(From D${(item.deliverable.id)!''})</small></i>
            </a>
          </td>
          [#-- Activity Type --]
          <td class="">
          [#if item.repIndTypeActivity?has_content]
            ${item.repIndTypeActivity.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Total Participants --]
          <td class="text-center">
            ${(item.participants?number?string(",##0"))!0}
          </td>
          [#-- Number of females --]
          <td class="text-center">
            ${(item.females?number?string(",##0"))!0}
          </td>
          [#-- Type of participants --]
          <td class="text-center">
          [#if item.repIndTypeParticipant?has_content]
            ${item.repIndTypeParticipant.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Geographic scope --]
          <td class="text-center">
          [#if item.repIndGeographicScope?has_content]
            ${item.repIndGeographicScope.name}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
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

[#macro tableJournalArticlesMacro list ]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="">Title</th>
        <th id="">Status</th>
        <th id="">Responsable</th>
        <th id="">Journal</th>
        <th id="">Open Access</th>
        <th id="">ISI Journal</th>
      </tr>
    </thead>
    <tbody>
    [#-- Loading --]
    [#if list?has_content]
      [#list list as item]
        [#local hasProject = (item.deliverable.project.id??)!false ]
        [#if hasProject]
          [#local URL][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [#else]
          [#local URL][@s.url namespace="/publications" action="${(crpSession)!}/publication"][@s.param name='deliverableID']${(item.deliverable.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        [/#if]
        <tr>
          [#-- Title --]
          <td class="">
            <a href="${URL}" target="_blank">
              [#if item.title?has_content]
                ${item.title}
              [#else]
                <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
              [/#if]
              [#-- Deliverable ID --]
              <br /><i style="opacity:0.5"><small>(From D${(item.deliverable.id)!''} [#if hasProject], P${(item.deliverable.project.id)!''}[/#if])</small></i>
            </a>
          </td>
          [#-- Deliverable Status --]
          <td>
            [#local statusName = item.getStatusName(actualPhase) ]
            [#if statusName?has_content]
              ${statusName}
            [#else]
              [#if !hasProject]Complete[/#if]
            [/#if]
          </td>
          [#-- Responsable --]
          <td class="">
          [#if item.deliverable.responsiblePartner?has_content]
            ${(item.deliverable.responsiblePartner.composedName)!}
          [#else]
            <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
          [/#if]
          </td>
          [#-- Journal  --]
          <td>
            ${(item.deliverable.publication.journal?string)!''}
          </td>
          [#-- Open Access --]
          <td class="text-center">
            <span style="display:none">${(item.deliverable.dissemination.isOpenAccess?string)!'false'}</span>
            <img src="${baseUrl}/global/images/openAccess-${(item.deliverable.dissemination.isOpenAccess?string)!'false'}.png" alt="" />
          </td>
          [#-- Journal ISI --]
          <td class="text-center">
            <span style="display:none">${(item.deliverable.publication.isiPublication?string)!'false'}</span>
            <img src="${baseUrl}/global/images/checked-${(item.deliverable.publication.isiPublication?string)!'false'}.png" alt="" />
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