[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "components-font-awesome" ] /]
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
  {"label":"table4.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
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
          
           [#-- Table 4: Condensed list of innovations --]
            <div class="form-group">
                [#assign guideSheetURL = "https://drive.google.com/file/d/1JvceA0bdvqS5Een056ctL7zJr3hidToe/view" /]
                <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #C1 Innovations -  Guideline </a> </small>
            </div>
            
            <div class="form-group row">
              <div class="col-md-4">
              [#assign projectInnovationInfos = 12 /]
                [#-- Total of CRP Innovations --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC1.totalInnovations" /]</label><br />
                  <span>${(projectInnovationInfos)!}</span>
                </div>
              </div>
            </div>
            
            <div class="form-group row">
              [#-- Chart 8 - Innovations by Type --]
              <div class="col-md-5">
                [#assign partnershipsByGeographicScopeDTO = [
                      { 
                        "repIndGeographicScope": {
                          "name": "Name"
                        },
                        "projectPartnerPartnerships": [1, 2, 3]
                      },
                      { 
                        "repIndGeographicScope": {
                          "name": "Name 2"
                        },
                        "projectPartnerPartnerships": [1, 2, 3, 4]
                      },
                      { 
                        "repIndGeographicScope": {
                          "name": "Name 3"
                        },
                        "projectPartnerPartnerships": [1, 2, 3, 4, 5]
                      }
                    ] /]
                <div id="chart8" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart4.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart4.1" /]</span>
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
              
              [#-- Chart 9 - Innovations by Stage --]
              <div class="col-md-7">
                [#assign innovationsByStageDTO = [
                      { 
                        "repIndStageInnovation": {
                          "name": "Name"
                        },
                        "projectInnovationInfos": [1, 2, 3]
                      },
                      { 
                        "repIndStageInnovation": {
                          "name": "Name 2"
                        },
                        "projectInnovationInfos": [1, 2, 3, 4]
                      },
                      { 
                        "repIndStageInnovation": {
                          "name": "Name 3"
                        },
                        "projectInnovationInfos": [1, 2, 3, 4, 5]
                      }
                    ] /]
                <div id="chart9" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart9.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart9.1" /]</span>
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
            
            <div class="form-group">
                [@innovationsTable name="table4" list=(projectInnovations)![]  /]
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


[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro innovationsTable name list=[] ]


  <div class="form-group viewMoreSyntesisTable-block">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.innovationTitle" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.stage" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.degree" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.contribution" /] </th>
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          <tr>
            <td>[@utils.tableText value=(item.composedName)!"" /]</td>
            <td>[@utils.tableText value=(item.projectInnovationInfo.repIndStageInnovation.name)!"" /]</td>
            <td>[@utils.tableText value=(item.projectInnovationInfo.repIndDegreeInnovation.name)!"" /]</td>
            <td>[@utils.tableText value=(item.projectInnovationInfo.repIndContributionOfCrp.name)!"" /]</td>
            <td class="text-center">
              [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.innovationsIds?seq_contains(item.id))!true) /]
              [@customForm.checkmark id="innovation-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.innovationsValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/] 
            </td>
          </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="5"><i>No entries added yet.</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]