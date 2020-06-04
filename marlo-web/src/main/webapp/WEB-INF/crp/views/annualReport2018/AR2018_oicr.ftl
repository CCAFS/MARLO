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
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20190621"] /]

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
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
            
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
            <th class="col-md-1 text-center no-sort">[@s.text name="${customLabel}.${name}.missingFields" /]</th>
            <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
            [#local isFromProject = (item.project??)!false]
            [#if isFromProject]
              [#local url][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [#else]
              [#local url][@s.url namespace="/studies" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            [/#if]
            
            [#-- Is new --]
            [#local isNew = (action.isEvidenceNew(item.id)) /]
            
            [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
            <tr>
              <td>
                [#-- Report Tag --]
                [#if isNew] <span class="label label-info">[@s.text name="global.new" /]</span> [/#if]
              
                [@utils.tableText value=(item.composedName)!"" /]
                
                [#if isFromProject]<br /> <small>(From Project P${item.project.id})</small> [/#if]
                [#-- OICR Contributions --][#--
                [#if !expanded] <div class="pull-right">[@oicrContributions element=item tiny=true /] [/#if]</div>
                --]
                [#if PMU]
                <br />
                <div class="form-group">
                  [#list (item.selectedFlahsgips)![] as liason]
                    <span class="programTag" style="border-color:${(liason.crpProgram.color)!'#444'}" title="${(liason.composedName)!}">${(liason.acronym)!}</span>
                  [/#list]
                </div>              
                [/#if]
                [#-- OICR Contributions --]
                [#if !expanded] [@oicrContributions element=item /] [/#if]
                
                <a href="${url}" target="_blank" class="pull-right">[@s.text name="${customLabel}.${name}.linkToOicr" /] <span class="glyphicon glyphicon-new-window"></span></a>
              </td>
              <td>[@utils.tableText value=(item.projectExpectedStudyInfo.repIndStageStudy.name)!"" /]</td>
              <td class="text-center">[@utils.tableText value=(item.projectExpectedStudyInfo.evidenceTag.name)!"" /]</td>
             [#if expanded]
              <td>[@utils.tableList list=(item.srfTargets)![] displayFieldName="srfSloIndicator.title" /]</td>
              <td>[@utils.tableList list=(item.subIdos)![] displayFieldName="srfSubIdo.description" /]</td>
              <td> <a href="${summaryPDF}" target="_blank"><img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" /></a>  </td>
             [/#if]
             [#if !expanded]
               <td class="text-center">
               [#assign isStudyComplete  = action.isStudyComplete(item.id, actualPhase.id)!false /]
               [#if isStudyComplete ]
                  <span class="glyphicon glyphicon-ok-sign mf-icon check" title="Complete"></span> 
                [#else]
                  <span class="glyphicon glyphicon-exclamation-sign mf-icon" title="Incomplete"></span> 
              [/#if] 
             [/#if]  
              </td>
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

[#macro oicrContributions element tiny=false]
  [#local policiesContributions = (action.getPolicies(element.id, actualPhase.id))![] ]
  [#local innovationsContributions = (action.getInnovations(element.id, actualPhase.id))![] ]
  [#local totalContributions = (policiesContributions?size +innovationsContributions?size)!0 ]
  
  [#if policiesContributions?has_content || innovationsContributions?has_content]
    <br /> 
    <button type="button" class="outcomesContributionButton btn btn-default btn-xs" data-toggle="modal" data-target="#oicrContributions-${element.id}">
      <span class="icon-20 project"></span> <strong>${totalContributions}</strong> [#if !tiny][@s.text name="${customLabel}.table3.linkToPoliciesAndInnovations" /][/#if]
    </button>
    <!-- Modal -->
    <div class="modal fade" id="oicrContributions-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [@s.text name="${customLabel}.table3.outcomeContributions" /]
            </h4>
          </div>
          <div class="modal-body">
            <div class="">            
              [#-- Policies --]
              [#if policiesContributions?has_content] 
                <h4 class="simpleTitle">[@s.text name="${customLabel}.table3.outcomeContributions.policies" /]</h4>
                  <table class="table table-bordered">
                    <thead>
                      <tr>
                        <th id="ids">[@s.text name="${customLabel}.table3.outcomeContributions.id" /]</th>
                        <th id="policyTitles">[@s.text name="${customLabel}.table3.outcomeContributions.policyName" /]</th>
                       [#--<th id="policyType">[@s.text name="project.projectPolicyList.type" /]</th>--]
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      [#list policiesContributions as policy]
                        [#local policyUrl][@s.url namespace="/projects" action="${(crpSession)!}/policy"][@s.param name='policyID']${policy.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                        <tr>
                          <th scope="row" class="col-md-1">${policy.id}</th>
                          <td>${(policy.projectPolicyInfo.title)!'Untitled'}</td>
                           [#--<td>${(p.projectPolicyInfo.policyType.name?capitalize)!'none'}</td>--]
                          <td class="col-md-2 text-center"> <a href="${policyUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                        </tr>
                        [/#list]
                    </tbody>
                  </table>
              [/#if]
              
              [#-- innovation --]
              [#if innovationsContributions?has_content] 
                <h4 class="simpleTitle">[@s.text name="${customLabel}.table3.outcomeContributions.innovations" /]</h4>              
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th id="ids">[@s.text name="${customLabel}.table3.outcomeContributions.id" /]</th>
                      <th id="innovationTitles" >[@s.text name="${customLabel}.table3.outcomeContributions.innovationName" /]</th>
                      [#--<th id="innovationType">[@s.text name="project.innovationList.type" /]</th>--]
                      [#--<th id="innovationRole" >[@s.text name="project.innovationList.role" /]</th>--]
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list innovationsContributions as innovation]
                      [#local innovationUrl][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${innovation.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      <tr>
                        <th scope="row" class="col-md-1">${innovation.id}</th>
                        <td>${(innovation.projectInnovationInfo.title)!'Untitled'}</td>
                        [#--<td>${(i.innovationInfo.innovationType.name?capitalize)!'none'}</td>
                        <td class="col-md-6">${(i.projectInnovationInfo.title)!'Untitled'}</td>--]
                        <td class="col-md-2 text-center"> <a href="${innovationUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                      </tr>
                      [/#list]
                  </tbody>
                </table>
              [/#if]            
            </div>
          </div>
          <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
        </div>
      </div>
    </div>
  [/#if]
[/#macro]