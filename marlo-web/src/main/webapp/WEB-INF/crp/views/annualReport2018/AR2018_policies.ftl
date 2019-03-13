[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "components-font-awesome", "trumbowyg" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
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
                  <span class="animated infinite bounce">${(((projectPolicies?size)!0)?number?string(",##0"))!0}</span>
                </div>
                [#-- Chart 7 - Level of maturity --]
                <div id="chart7" class="chartBox simpleBox">
                  [#assign chartData = [
                      {"name":"Level 1",  "value": "4"},
                      {"name":"Level 2",  "value": "5"},
                      {"name":"Level 3",  "value": "2"}
                    ] /] 
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.chart13" /]</span>
                      <span>[@s.text name="${customLabel}.chart13" /]</span>
                    </li>
                    [#list chartData as data]
                      <li><span>${data.name}</span><span class="number">${data.value}</span></li>
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
                      [#if (data.repIndOrganizationType?has_content && (data.projectPolicies?has_content))!false]
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
                      <h4 class="modal-title" id="myModalLabel"></h4>
                    </div>
                    <div class="modal-body">
                      [#-- Full table --]
                      [@table2ListOfPolicies list=(projectPolicies)![] expanded=true/]
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                  </div>
                </div>
              </div>
              
              [#-- Table --]
              [@table2ListOfPolicies list=(projectPolicies)![] expanded=false/]
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

  <table id="tableA" class="table table-bordered">
    <thead>
      <tr>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.name" /]</th>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.maturity" /]</th>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.subIDOs" /]</th>
        [#if expanded]
        <th class="text-center" colspan="4">[@s.text name="${customLabel}.table2.crossCutting" /]</th>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.type" /]</th>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.whose" /]</th>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.geoScope" /]</th>
        <th class="text-center" rowspan="2">Evidence</th>
        [/#if]
        [#if !expanded]
        <th class="col-md-1 text-center" rowspan="2">[@s.text name="${customLabel}.table2.includeAR" /]</th>
        [/#if]
        
      </tr>
      [#if expanded]
      <tr>
        <th class="text-center"> Gender </th>
        <th class="text-center"> Youth </th>
        <th class="text-center"> CapDev</th>
        <th class="text-center"> Climate Change </th>
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
                <span class="programTag" style="border-color:${(liason.crpProgram.color)!'#fff'}" title="${(liason.composedName)!}">${(liason.acronym)!}</span>
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
              [#list (item.crossCuttingMarkers)![] as ccm]${ccm}[/#list]
              <p class="dacMarker level-2" title="0 - Not Targeted"> 0 </p>  
            </td>
            [#-- Youth --]
            <td class="text-center"> <p class="dacMarker level-2" title="0 - Not Targeted">0</p> </td>
            [#-- CapDev --]
            <td class="text-center"> <p class="dacMarker level-3" title="1 - Significant">0</p> </td>
            [#-- Climate Change --]
            <td class="text-center"> <p class="dacMarker level-4" title="2 - Principal">0</p> </td>
            [#-- Policy Type --]
            <td>[@utils.tableText value=(item.projectPolicyInfo.repIndOrganizationType.name)!"" /]</td>
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
            <td>[@utils.tableList list=(item.evidences)![]  displayFieldName="projectExpectedStudy.id" nobr=true /]</td>
          [/#if]
          [#if !expanded]
          <td class="text-center">
            [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.policiesIds?seq_contains(item.id))!true) /]
            [@customForm.checkmark id="policy-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.policiesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
          </td>
          [/#if]
        </tr>
      [/#list]
    [#else]
      
    [/#if]
    </tbody>
  </table>

[/#macro]

[#function getMarker element name]
  [#list (element.crossCuttingMarkers)![] as ccm]
    [#if ccm.cgiarCrossCuttingMarker.name == name]
      [#return (ccm.repIndGenderYouthFocusLevel.powbName)!'0 - Not Targeted' ]
    [/#if]
  [/#list]
  [#return "0 - Not Targeted" ]
[/#function]