[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [   
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" 
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"table3.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
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
          
            [#-- Table 3: List of Outcome/Impact Case Reports --]
            <div class="form-group">

              [@listOfOutcomeImpactCaseReports name="table3" list=(projectExpectedStudies)![] /]
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

[#macro listOfOutcomeImpactCaseReports name list=[]  isPMU=false ]


  <div class="form-group">
    [@customForm.helpLabel name="${customLabel}.help" showIcon=false editable=editable/]
    <table class="annual-report-table table-border">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.outcomeTitle" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.maturityLevel" /] </th>
          <th class="col-md-2 text-center"> [@s.text name="${customLabel}.${name}.status" /] </th>
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          <tr>
            <td>[@utils.tableText value=(item.composedName)!"" /]</td>
            <td>[@utils.tableText value=(item.projectExpectedStudyInfo.repIndStageStudy.name)!"" /]</td>
            <td class="text-center">[@utils.tableText value=(item.projectExpectedStudyInfo.status.name)!"" /]</td>
            <td class="text-center">
              [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.studiesIds?seq_contains(item.id))!true) /]
              [@customForm.checkmark id="study-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.studiesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/] 
            </td>
          </tr>
          [/#list]
        [#else]
          <tr>
            <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]