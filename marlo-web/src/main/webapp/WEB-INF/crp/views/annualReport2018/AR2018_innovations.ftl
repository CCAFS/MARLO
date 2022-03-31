[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "font-awesome" ] /]
[#assign customJS = [
  "https://www.gstatic.com/charts/loader.js", 
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20220328a",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js?20211103a"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20210225"] /]

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
          <span id="actualCrpID" style="display: none;">${(action.getCurrentCrp().id)!-1}</span>
          <span id="actualPhase" style="display: none;">${(action.isSelectedPhaseAR2021())?c}</span>
          <span id="isSubmitted" style="display: none;">${submission?c}</span>
          [#assign actualPhaseAR2021 = action.isSelectedPhaseAR2021()!false]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
          
           [#-- Table 4: Condensed list of innovations --]
            <div class="form-group">
              [#assign guideSheetURL = "https://drive.google.com/file/d/1JvceA0bdvqS5Een056ctL7zJr3hidToe/view" /]
              <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #C1 Innovations -  Guideline </a> </small>
            </div>
            
            <div class="form-group row">
              <div class="col-md-4">
                [#-- Total of CRP Innovations --]
                <div id="" class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorC1.totalInnovations" /]</label><br />
                  <span class="totalNumber">${(total)!}</span>
                </div>
              </div>
            </div>
            
            <div class="form-group row">
              [#-- Chart 8 - Innovations by Type --]
              <div class="col-md-6">
                <div id="chart8" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart4.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart4.1" /]</span>
                    </li>
                    [#list (innovationsByTypeDTO)![] as data]
                      [#if data.projectInnovationInfos?has_content]
                      <li>
                        <span>${data.repIndInnovationType.name}</span>
                        <span class="number">${data.projectInnovationInfos?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div>
              </div>
              
              
              [#-- Chart 9 - Innovations by Stage --]
              <div class="col-md-6">
                 <div id="chart9" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart9.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorC1.chart9.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list (innovationsByStageDTO)![] as data]
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
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              
              [#-- Buttons --]
              <div class="form-group btn-group btn-group-sm pull-right" role="group" aria-label="...">
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#modal-innovations"><span class="glyphicon glyphicon-fullscreen"></span> See Full Table 4</button>
              </div>
    
              [#-- Modal --]
              <div class="modal fade" id="modal-innovations" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog modal-lg" role="document">
                  <div class="modal-content">
                    <div class="modal-header">
                      <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      <h4 class="modal-title" id="myModalLabel"></h4>
                    </div>
                    <div class="modal-body">
                      [#-- Full table --]
                      [@innovationsTable name="table4" list=(projectInnovations)![]  expanded=true/]
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                  </div>
                </div>
              </div>
              
              [#-- Table --]
              [@innovationsTable name="table4" list=(projectInnovations)![]  expanded=false /]
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

[#macro innovationsTable name list=[] expanded=false]


  <div class="form-group tableNoPaginator-block">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.innovationTitle" /] </th>
          [#if expanded]
            <th class="text-center"> Description of the innovation </th>
          [/#if]
          <th class="text-center"> [@s.text name="${customLabel}.${name}.type" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.stage" /] </th>
          [#if expanded]
            <th class="text-center"> Description of Stage reached </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.organization" /] </th>
            <th class="text-center"> Top five contributing partners</th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.geoScope" /] </th>
            <th class="text-center col-md-1"> [@s.text name="${customLabel}.${name}.evidence" /] </th>
            [#--<th class="text-center"></th>--]
          [/#if]
          [#if !expanded]
            <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.missingFields" /] </th>
            [#if PMU]
              <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] 
              <br>
              <button type="button" class="selectAllCheckInnovations" id="selectAllInnovations" style="color: #1da5ce; font-style: italic; font-weight: 500; background-color: #F9F9F9; border-bottom: none; outline: none">Select All</button>
              [#--  <span class="selectAllCheckInnovations">[@customForm.checkmark id="selectAllInnovations" name="selectAllInnovations" value="false" checked=false editable=editable centered=true/]</span>  --]
              </th>
              [#if actualPhaseAR2021 && submission]
                <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.QA" /]</th>
              [/#if]
            [/#if]
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          [#local marloUrl][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${item.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          [#local publicUrl][@s.url namespace="/summaries" action='${(crpSession)!}/projectInnovationSummary'][@s.param name='innovationID']${item.id?c}[/@s.param][@s.param name='phaseID']${(actualPhase.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          [#local isStageFour = (item.projectInnovationInfo.repIndStageInnovation.id == 4)!false]
          <tr>
            [#-- 1. Title of innovation--]
            <td>
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
              [#local hasOicr = ((item.projectInnovationInfo.projectExpectedStudy?has_content)!false) /]
              [#if !expanded && isStageFour && hasOicr]
                <br />
                [#local oicrUrl][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${item.projectInnovationInfo.projectExpectedStudy.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                <span>[@s.text name="${customLabel}.${name}.linkToOicr" /] <a href="${oicrUrl}" target="_blank">${(item.projectInnovationInfo.projectExpectedStudy.composedName)!'Untitled'}</span></a>
              [/#if]
              [#if !expanded] [@oicrPopup element=item isStageFour=isStageFour/] [/#if]
              <div class="container-links">
                <div data-toggle="tooltip" title="[@s.text name="${customLabel}.${name}.linkToMARLOInnovation" /]">
                  <a href="${marloUrl}" target="_blank" class="pull-right"> <span class="fa fa-external-link"></span></a>
                </div>
                <div data-toggle="tooltip" title="[@s.text name="${customLabel}.${name}.linkToPublicInnovation" /]">
                  <a href="${publicUrl}" target="_blank" class="pull-right"> <span class="fa fa-file-pdf-o pdfIcon file"></span></a>
                </div>
              </div>
            </td>
            [#-- 3. Description of Innovation  --]
            [#if expanded]
              <td>[@utils.tableText value=(item.projectInnovationInfo.narrative)!"" /]</td>
            [/#if]
            [#-- 4. Innovation Type  --]
            <td>[@utils.tableText value=(item.projectInnovationInfo.repIndInnovationType.name)!"" /]</td>
            [#-- 5. Stage of Innovation --]
            <td>[@utils.tableText value=(item.projectInnovationInfo.repIndStageInnovation.name)!"" /]</td>
            [#if expanded]
              [#-- 6. Description of stage reached --]
              <td class="urlify">[@utils.tableText value=(item.projectInnovationInfo.descriptionStage)!"" /]</td>
              [#-- 7. Lead Organization/ entity --]
              <td>
                [#if (item.projectInnovationInfo.clearLead)!false] 
                  <p><i>[@s.text name="projectInnovations.clearLead" /]</i></p>
                  [@utils.tableText value=(item.projectInnovationInfo.leadOrganization.name)!"" emptyText="" /]
                [#else]
                  [@utils.tableText value=(item.projectInnovationInfo.leadOrganization.name)!"" /]
                [/#if] 
              </td>
              [#-- 8. Top five contributing partners/ entities to this stage --]
              <td>[@utils.tableList list=(item.contributingOrganizations)![] displayFieldName="institution.composedName" /]</td>
              [#-- 9. Geographic Scope --]
              <td>
                <div class=""><strong>[@utils.tableList list=(item.geographicScopes)![]  displayFieldName="repIndGeographicScope.name" nobr=true /]</strong></div>
                <div class="">[@utils.tableList list=(item.regions)![]  displayFieldName="locElement.composedName" showEmpty=false nobr=false /]</div>
                <div class="">[@utils.tableList list=(item.countries)![]  displayFieldName="locElement.name" showEmpty=false nobr=false /]</div>
              </td>
              [#-- 10. Evidence for Innovation --]
              <td class="text-center">
                [#if isStageFour]
                 [#list (item.studies)![] as item]
                  [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(item.projectInnovationInfo.projectExpectedStudy.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
                  <p>
                    <a href="${summaryPDF}" class="btn btn-default btn-xs" target="_blank" style="text-decoration: none;" title="${(item.projectExpectedStudy.composedName)!''}">
                      <img src="${baseUrlCdn}/global/images/pdf.png" height="20"  /> ${(item.projectExpectedStudy.composedIdentifier)!''}
                    </a>
                  </p>
                  [/#list]
                [#else]                
                  [#local innovationEvidence = (item.projectInnovationInfo.evidenceLink)!""/]
                  [#if innovationEvidence?has_content]
                    <a target="_blank" href="${innovationEvidence}"><span class="glyphicon glyphicon-link"></span></a>
                  [#else]
                    <span class="glyphicon glyphicon-link" title="Not defined"></span>
                  [/#if]
                [/#if]
              </td>
              [#--<td class="text-center">
                <a href="[@s.url namespace="/summaries" action='${(crpSession)!}/projectInnovationSummary'][@s.param name='innovationID']${item.id?c}[/@s.param][@s.param name='phaseID']${(item.projectInnovationInfo.phase.id)!''}[/@s.param][/@s.url]" target="__BLANK">
                  <img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
                </a>
              </td>--]
            [/#if]
            [#if !expanded]
              [#-- 11. Innovation Completion--]
              <td class="text-center">
                [#assign isInnovationComplete = action.isInnovationComplete(item.id, actualPhase.id)!false /]
                [#if  isInnovationComplete]
                  <span class="glyphicon glyphicon-ok-sign mf-icon check" title="Complete"></span> 
                [#else]
                  <span class="glyphicon glyphicon-exclamation-sign mf-icon" title="Incomplete"></span> 
                [/#if]
              </td>
              [#-- 12. Included in AR? --]
              [#if PMU]
                [#local isChecked = ((!reportSynthesis.reportSynthesisFlagshipProgress.innovationsIds?seq_contains(item.id))!true) /]
                <td class="text-center">
                  [#--local canBeAddedToAR = ((action.canBeAddedToAR(item.id, actualPhase.id))!false)]
                  <div data-toggle="tooltip" [#if !canBeAddedToAR]title="[@s.text name="annualReport2018.innovations.table4.cannotBeAddedToAR" /]"[/#if]>
                    [@customForm.checkmark id="innovation-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.innovationsValue" value="${(item.id)!''}" checked=isChecked editable=(editable&&canBeAddedToAR) centered=true/]
                  </div>--]
                  [@customForm.checkmark id="innovation-${(item.id)!}" name="reportSynthesis.reportSynthesisFlagshipProgress.innovationsValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/] 
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
          <tr>
            <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro oicrPopup element tiny=false isStageFour=false]
  [#local totalContributions = (element.studies?size)!0 ]
  
  [#if element.studies?has_content && isStageFour]
    <br /> 
    <button type="button" class="innovationsOicrsButton btn btn-default btn-xs" data-toggle="modal" data-target="#innovationsOicrs-${element.id}">
      <span class="icon-20 project"></span> <strong>${totalContributions}</strong> [#if !tiny][@s.text name="${customLabel}.table4.linkToOicrs" /][/#if]
    </button>
    <!-- Modal -->
    <div class="modal fade" id="innovationsOicrs-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [@s.text name="${customLabel}.table4.innovationsOicrs" /]
            </h4>
          </div>
          <div class="modal-body">
            <div class="">            
              [#-- OICRs --]
              <h4 class="simpleTitle">[@s.text name="${customLabel}.table4.innovationsOicrs.oicrs" /]</h4>
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th id="ids">[@s.text name="${customLabel}.table4.innovationsOicrs.id" /]</th>
                      <th id="policyTitles">[@s.text name="${customLabel}.table4.innovationsOicrs.oicrName" /]</th>
                      <th id="policyMaturityLevel">[@s.text name="${customLabel}.table4.innovationsOicrs.maturityLevel" /]</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    [#list element.studies as oicr]
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