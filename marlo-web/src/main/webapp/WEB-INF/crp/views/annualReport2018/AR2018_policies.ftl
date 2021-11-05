[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "components-font-awesome", "trumbowyg" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",  
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20211105a",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20211103a"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20210225"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"table2.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
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
            [#-- Table 2: Condensed list of policy contributions --]
            <div class="form-group">
              [#assign guideSheetURL = "https://docs.google.com/document/d/1KcNKtAdexpISekoVaAKPmHK223ohkqgb/edit?rtpof=true&sd=true" /]
              <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #I1 Policies  -  Guideline </a> </small>
            </div>
            <br />
            <div class="form-group row">
              <div class="col-md-5">
                <div class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorI1.totalPolicies" /]</label><br />
                  <span class="totalNumber">${(((total)!0)?number?string(",##0"))!0}</span>
                </div>
                [#-- Chart 7 - Stage of Maturity --][#--
                <div  class="chartBox simpleBox">
                Policies Level of Maturity
                <center><img src="${baseUrlCdn}/global/images/ComingSoon-charts.png" height="130"></center>
                </div>--]
                 <div id="chart7" class="chartBox simpleBox">
                   <ul class="chartData" style="display:none">
                    <li>
                      <span></span>
                      [#list (policiesByRepIndStageProcessDTOs)![] as data]
                        [#if data.repIndStageProcess.name?contains("Stage")]    
                            <span>${data.repIndStageProcess.name}</span>
                            <span class="json">{"role":"annotation"}</span> 
                        [/#if]                    
                      [/#list] 
                    </li>
                    <li>
                      <span></span>
                      [#list (policiesByRepIndStageProcessDTOs)![] as data]
                        [#if data.repIndStageProcess.name?contains("Stage")]
                          <span class="number">${data.projectPolicies?size}</span> 
                          <span>${data.projectPolicies?size}</span>
                        [/#if]  
                      [/#list]
                    </li>
                    [#--
                    <li>
                      <span></span>
                      <span></span>
                      <span class="json">{"role":"annotation"}</span>
                      <span class="json">{"role":"style"}</span>
                    </li>
                       
                    [#list (policiesByRepIndStageProcessDTOs)![] as data]
                      <li>
                        <span>${data.repIndStageProcess.name}</span>
                        <span class="number">${data.projectPolicies?size}</span>
                        <span>${data.projectPolicies?size}</span>
                      </li>
                    [/#list]
                    --]
                  </ul>
                </div> 
              </div>
              
              <div class="col-md-7">
              [#--<div class="chartBox simpleBox">
              Policies by Type
                <center><img src="${baseUrlCdn}/global/images/ComingSoon-charts.png" height="180"></center>
              </div>--]
                [#-- Chart 6 - Policies by type --]
                 <div id="chart6" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                                        
                    [#list (policiesByRepIndInvestimentTypeDTOs)![] as data]
                      <li>
                          <span>${(data.repIndPolicyInvestimentType.name)!}</span>
                          <span class="number">${data.projectPolicies?size}</span>
                          <span>#1773b8</span>
                          <span>${data.projectPolicies?size}</span>
                      </li> 
                    [/#list]          
                  </ul>
                </div> 
              </div>
            </div>
              
            <div class="form-group">
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              
              [#-- Button --]
              <button type="button" class="btn btn-default btn-xs pull-right" data-toggle="modal" data-target="#modal-policies">
                 <span class="glyphicon glyphicon-fullscreen"></span> See Full table 2
              </button>
              [#-- Modal --]
              <div class="modal fade" id="modal-policies" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog modal-lg" role="document">
                  <div class="modal-content">
                    <div class="modal-header">
                      <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      <h4 class="modal-title" id="myModalLabel">[@s.text name="${customLabel}.title" /]</h4>
                    </div>
                    <div class="modal-body">
                      [#-- Full table --]
                      <div class="viewMoreSyntesisTable-block">
                        [@table2ListOfPolicies list=(projectPolicies)![] expanded=true/]
                      </div>
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                  </div>
                </div>
              </div>
              
              [#-- Table --]
              <div class="tablePolicies-block">
                [@table2ListOfPolicies list=(projectPolicies)![] expanded=false/]
              </div>
            </div>
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]


[#------------------------------------  MACROS  ------------------------------------------]


[#macro table2ListOfPolicies list=[]  id="" expanded=false]
  [#local crossCuttingMarkers = [{ "Gender", "Youth", "CapDev", "Climate Change"}] /]
  [#if expanded]
    [#local rows = 2 /]
  [#else]
    [#local rows = 1 /]
  [/#if]
  <table id="tableA" class="table table-bordered">
    <thead>
      <tr>
        <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.name" /]</th>
        <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.description" /]</th>
        <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.maturity" /]</th>
        <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.subIDOs" /]</th>
        [#if expanded]
          <th class="text-center" colspan="4">[@s.text name="${customLabel}.table2.crossCutting" /]</th>
          [#--  <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.type" /]</th>--]
          <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.whose" /]</th>
          <th class="text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.geoScope" /]</th>
          <th class="text-center" rowspan="${rows}">Evidence(s)</th>
        [/#if]
        [#if !expanded]
          [#-- Complete Status    --]
          <th class="col-md-1 text-center no-sort" rowspan="${rows}">[@s.text name="${customLabel}.table2.missingFields" /]</th>
          [#if PMU]
            <th class="col-md-1 text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.includeAR" /]
            <br>
            <button type="button" class="selectAllCheckPolicies" id="selectAllPolicies" style="color: #1da5ce; font-style: italic; font-weight: 500; background-color: #F9F9F9; border-bottom: none; outline: none">Select All</button>
            </th>
            [#if actualPhaseAR2021 && submission]
             <th class="col-md-1 text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.QA" /]</th>
            [/#if]
          [/#if]
        [/#if]        
      </tr>
      [#if expanded]
      <tr>
        <th class="text-center"> <small>Gender</small> </th>
        <th class="text-center"> <small>Youth</small> </th>
        <th class="text-center"> <small>CapDev</small> </th>
        <th class="text-center"> <small>Climate Change</small> </th>
      </tr>
      [/#if]
    </thead>
    <tbody>
    [#if list?has_content]
      [#list list as item]
        [#local url][@s.url namespace="/projects" action="${(crpSession)!}/policy"][@s.param name='policyID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- Title --]
          <td class="">
            [@utils.tableText value=(item.composedName)!"" /]
            [#if item.project??]<br /> <small>(From Project P${item.project.id})</small> [/#if]
            
            [#if PMU]
              <br />
              <div class="form-group">
              [#list (item.selectedFlahsgips)![] as liason]
                <span class="programTag" style="border-color:${(liason.crpProgram.color)!'#444'}" title="${(liason.composedName)!}">${(liason.acronym)!}</span>
              [/#list]
              </div>
            [/#if]
            [#if !expanded] [@oicrPopup element=item /] [/#if]
            <a href="${url}" target="_blank" class="pull-right mt-1">[@s.text name="${customLabel}.table2.linkToPolicy" /] <span class="glyphicon glyphicon-new-window"></span></a>
          </td>
          [#-- Description --]
          <td class="text-center">[@utils.tableText value=(item.projectPolicyInfo.description)!"" /]</td>         
          [#-- Level of Maturity --]
          <td class="text-center">[@utils.tableText value=(item.projectPolicyInfo.repIndStageProcess.name)!"" /]</td>
          [#-- Sub-IDOs --]
          <td> <small>[@utils.tableList list=(item.subIdos)![]  displayFieldName="srfSubIdo.description" /]</small> </td>
          [#if expanded]
            [#-- Gender --]
            <td class="text-center">
              [#local marker = getMarker(item, "Gender") ]
              <p class="dacMarker level-${(marker.id)!""}" title="${(marker.powbName)!""}">${(marker.acronym)!""}</p> 
            </td>
            [#-- Youth --]
            <td class="text-center"> 
              [#local marker = getMarker(item, "Youth") ]
              <p class="dacMarker level-${(marker.id)!""}" title="${(marker.powbName)!""}">${(marker.acronym)!""}</p>
            </td>
            [#-- CapDev --]
            <td class="text-center"> 
              [#local marker = getMarker(item, "CapDev") ]
              <p class="dacMarker level-${(marker.id)!""}" title="${(marker.powbName)!""}">${(marker.acronym)!""}</p>
            </td>
            [#-- Climate Change --]
            <td class="text-center"> 
              [#local marker = getMarker(item, "Climate Change") ]
              <p class="dacMarker level-${(marker.id)!""}" title="${(marker.powbName)!""}">${(marker.acronym)!""}</p>
            </td>
            [#-- Policy Type
            <td>[@utils.tableText value=(item.projectPolicyInfo)!"" /]</td>
             --]
            [#-- Owners--]
            <td class="col-md-1">[@utils.tableList list=(item.owners)![]  displayFieldName="repIndPolicyType.name" nobr=true /]</td>
            [#-- Geographic Scope--]
            <td class="col-md-1">
              <div class="">
                <strong>[@utils.tableList list=(item.geographicScopes)![]  displayFieldName="repIndGeographicScope.name" nobr=true /]</strong>
              </div>
              <div class="">
                [@utils.tableList list=(item.regions)![]  displayFieldName="locElement.composedName" showEmpty=false nobr=true /]
              </div>
              <div class="">
                [@utils.tableList list=(item.countries)![]  displayFieldName="locElement.name" showEmpty=false nobr=true /]
              </div>
            </td>
            <td>
              [#list (item.evidences)![] as item]
                [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.projectExpectedStudy.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
                <p>
                  <a href="${summaryPDF}" class="btn btn-default btn-xs" target="_blank" style="text-decoration: none;" title="${item.projectExpectedStudy.composedName}">
                    <img src="${baseUrlCdn}/global/images/pdf.png" height="20"  /> ${item.projectExpectedStudy.composedIdentifier}
                  </a>
                </p>
              [#else]
                <span class="text-nowrap">Not defined</span> 
              [/#list]
            </td>
          [/#if]
          
          [#if !expanded]
            [#-- Complete Status--]
            <td class="text-center">
              [#-- Is Complete --]
              [#assign isPolicyComplete = action.isPolicyComplete(item.id, actualPhase.id)!false /]
              [#if isPolicyComplete]
                <span class="glyphicon glyphicon-ok-sign mf-icon check" title="Complete"></span> 
              [#else]
                <span class="glyphicon glyphicon-exclamation-sign mf-icon" title="Incomplete"></span> 
              [/#if]   
            </td>
            [#if PMU]
              [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.policiesIds?seq_contains(item.id))!true) /]
              <td class="text-center">
                [@customForm.checkmark id="policy-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.policiesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                <div id="isCheckedAR-${item.id}" style="display: none">${isChecked?string('1','0')}</div>
              </td>
              [#if actualPhaseAR2021 && submission]
                <td id="QAStatusIcon-${item.id}" class="text-center">
                  [#if isChecked]
                    <i style="font-weight: normal;opacity:0.8;">[@s.text name="annualReport2018.policies.table2.pendingForReview"/]</i>
                  [#else]
                    <i style="font-weight: normal;opacity:0.8;">[@s.text name="annualReport2018.policies.table2.notInluded"/]</i>
                  [/#if]
                </td>
              [/#if]
            [/#if]
          [/#if]
        </tr>
      [/#list]
    [#else]
      <div class="totalViewMoreNumber" style="display: none">${(list?has_content)?string('1','0')}</div>
      <tr>
        [#if !expanded]
         [#-- Complete Status
         <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
         --]
         <td class="text-center" colspan="5"><i>No entries added yet.</i></td>
        [#else]
         <td class="text-center" colspan="12"><i>No entries added yet.</i></td>
        [/#if]
      </tr>
    [/#if]
    </tbody>
  </table>

[/#macro]

[#macro oicrPopup element tiny=false]
  [#local totalContributions = (element.evidences?size)!0 ]
  
  [#if element.evidences?has_content]
    <br /> 
    <button type="button" class="policiesOicrsButton btn btn-default btn-xs" data-toggle="modal" data-target="#policiesOicrs-${element.id}">
      <span class="icon-20 project"></span> <strong>${totalContributions}</strong> [#if !tiny][@s.text name="${customLabel}.table2.linkToOicrs" /][/#if]
    </button>
    <!-- Modal -->
    <div class="modal fade" id="policiesOicrs-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [@s.text name="${customLabel}.table2.policiesOicrs" /]
            </h4>
          </div>
          <div class="modal-body">
            <div class="">            
              [#-- OICRs --]
              <h4 class="simpleTitle">[@s.text name="${customLabel}.table2.policiesOicrs.oicrs" /]</h4>
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th id="ids">[@s.text name="${customLabel}.table2.policiesOicrs.id" /]</th>
                      <th id="policyTitles">[@s.text name="${customLabel}.table2.policiesOicrs.oicrName" /]</th>
                      <th id="policyMaturityLevel">[@s.text name="${customLabel}.table2.policiesOicrs.maturityStage" /]</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list element.evidences as oicr]
                      [#local oicrUrl][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${oicr.projectExpectedStudy.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      <tr>
                        <th scope="row" class="col-md-1">${oicr.projectExpectedStudy.id}</th>
                        <td>${(oicr.projectExpectedStudy.composedName)!'Untitled'}</td>
                        <td>${(oicr.projectExpectedStudy.projectExpectedStudyInfo.repIndStageStudy.name)!'Undefined'}</td>
                        <td class="col-md-2 text-center"> <a href="${oicrUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                      </tr>
                      [/#list]
                  </tbody>
                </table>           
            </div>
          </div>
          <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
        </div>
      </div>
    </div>
  [/#if]
[/#macro]

[#function getMarker element name]
  [#list (element.crossCuttingMarkers)![] as ccm]
    [#if ccm.cgiarCrossCuttingMarker.name == name]
      [#return (ccm.repIndGenderYouthFocusLevel)!{} ]
    [/#if]
  [/#list]
  [#return {} ]
[/#function]