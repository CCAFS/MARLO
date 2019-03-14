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
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js" 
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
              [#-- Button --]
              <button type="button" class="btn btn-default btn-xs pull-right" data-toggle="modal" data-target="#modal-oicr">
                 <span class="glyphicon glyphicon-fullscreen"></span> See Full table 3
              </button>
              [#-- Modal --]
              <div class="modal fade" id="modal-oicr" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog modal-lg" role="document">
                  <div class="modal-content">
                    <div class="modal-header">
                      <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      <h4 class="modal-title" id="myModalLabel"></h4>
                    </div>
                    <div class="modal-body">
                      [#-- Full table --]
                      [@listOfOutcomeImpactCaseReports name="table3" list=(projectExpectedStudies)![] expanded=true /]
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                  </div>
                </div>
              </div>
              [#-- Table --]
              [@listOfOutcomeImpactCaseReports name="table3" list=(projectExpectedStudies)![] expanded=false/]
              
              
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

[#macro listOfOutcomeImpactCaseReports name list=[]  isPMU=false expanded=false ]


  <div class="form-group viewMoreSyntesisTable-block">
    [@customForm.helpLabel name="${customLabel}.help" showIcon=false editable=editable/]
    <table class="annual-report-table table-border">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.outcomeTitle" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.maturityLevel" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.status" /] </th>
          [#if expanded]
          <th> [@s.text name="${customLabel}.${name}.srfTargets" /] </th>
          <th> [@s.text name="${customLabel}.${name}.subIdos" /] </th>
          <th></th>
          [/#if]
          [#if !expanded]
          <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local url][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
            <tr>
              <td>
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
              <td>[@utils.tableText value=(item.projectExpectedStudyInfo.repIndStageStudy.name)!"" /]</td>
              <td class="text-center">[@utils.tableText value=(item.projectExpectedStudyInfo.status.name)!"" /]</td>
             [#if expanded]
              <td>[@utils.tableList list=(item.srfTargets)![] displayFieldName="srfSloIndicator.title" /]</td>
              <td>[@utils.tableList list=(item.subIdos)![] displayFieldName="srfSubIdo.description" /]</td>
              <td> <a href="${summaryPDF}" target="_blank"><img src="${baseUrl}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" /></a>  </td>
             [/#if]
             [#if !expanded]
              <td class="text-center">
                [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.studiesIds?seq_contains(item.id))!true) /]
                [@customForm.checkmark id="study-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.studiesValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/] 
              </td>
             [/#if]
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