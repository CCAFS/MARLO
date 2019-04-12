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
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

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
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
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
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#-- Table 2: Condensed list of policy contributions --]
            <div class="form-group">
              [#assign guideSheetURL = "https://drive.google.com/file/d/1GYLsseeZOOXF9zXNtpUtE1xeh2gx3Vw2/view" /]
              <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #I1 Policies  -  Guideline </a> </small>
            </div>
            <br />
            <div class="form-group row">
              <div class="col-md-4">
                <div class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorI1.totalPolicies" /]</label><br />
                  <span class="animated infinite bounce">${(((total)!0)?number?string(",##0"))!0}</span>
                </div>
                [#-- Chart 7 - Level of maturity --]
                <div id="chart7" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span> </span>
                      <span> </span>
                    </li>
                    [#list (policiesByRepIndStageProcessDTOs)![] as data]
                      <li><span>${data.repIndStageProcess.name}</span><span class="number">${data.projectPolicies?size}</span></li>
                    [/#list]
                  </ul>
                </div> 
              </div>
              <div class="col-md-8">
                [#-- Chart 6 - Organizations designing/promulgating the policy --]
                <div id="chart6" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list (policiesByOrganizationTypeDTOs)![] as data]
                      [#assign policiesSize = (data.projectPolicies?size) /]
                      [#if  policiesSize > 0]
                      <li>
                        <span>${(data.repIndOrganizationType.name)!}</span>
                        <span class="number">${data.projectPolicies?size}</span>
                        <span>#4285f4</span>
                        <span>${data.projectPolicies?size}</span>
                      </li>
                      [/#if]
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
              <div class="viewMoreSyntesisTable-block">
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
        <th class="col-md-1 text-center" rowspan="${rows}">[@s.text name="${customLabel}.table2.includeAR" /]</th>
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
            
            <a href="${url}" target="_blank" class="pull-right"><span class="glyphicon glyphicon-new-window"></span></a>
          </td>
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
                    <img src="${baseUrl}/global/images/pdf.png" height="20"  /> ${item.projectExpectedStudy.composedIdentifier}
                  </a>
                </p>
              [#else]
                <span class="text-nowrap">Not defined</span> 
              [/#list]
            </td>
          [/#if]
          [#if !expanded]
          <td class="text-center">
            [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.policiesIds?seq_contains(item.id))!true) /]
            <div class="hidden">${isChecked?string}</div>
            [@customForm.checkmark id="policy-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.policiesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
          </td>
          [/#if]
        </tr>
      [/#list]
    [#else]
      <tr>
        [#if !expanded]
         <td class="text-center" colspan="4"><i>No entries added yet.</i></td>
        [#else]
         <td class="text-center" colspan="11"><i>No entries added yet.</i></td>
        [/#if]
      </tr>
    [/#if]
    </tbody>
  </table>

[/#macro]

[#function getMarker element name]
  [#list (element.crossCuttingMarkers)![] as ccm]
    [#if ccm.cgiarCrossCuttingMarker.name == name]
      [#return (ccm.repIndGenderYouthFocusLevel)!{} ]
    [/#if]
  [/#list]
  [#return {} ]
[/#function]